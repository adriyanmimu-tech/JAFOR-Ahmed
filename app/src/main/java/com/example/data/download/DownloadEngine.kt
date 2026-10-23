package com.example.data.download

import android.content.Context
import android.os.Environment
import com.example.data.local.DownloadDao
import com.example.data.model.DownloadItem
import com.example.data.model.DownloadStatus
import com.example.data.model.MediaType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

class DownloadEngine(
  private val context: Context,
  private val downloadDao: DownloadDao
) {
  private val notificationHelper = DownloadNotificationHelper(context)

  private val client = OkHttpClient.Builder()
    .connectTimeout(30, TimeUnit.SECONDS)
    .readTimeout(60, TimeUnit.SECONDS)
    .build()

  private val activeJobs = ConcurrentHashMap<Long, Job>()
  private val scope = CoroutineScope(Dispatchers.IO)

  suspend fun startDownload(item: DownloadItem): Long {
    val id = if (item.id == 0L) {
      downloadDao.insertDownload(item)
    } else {
      downloadDao.updateDownload(item)
      item.id
    }

    launchDownloadJob(id)
    return id
  }

  fun pauseDownload(id: Long) {
    activeJobs[id]?.cancel()
    activeJobs.remove(id)
    notificationHelper.cancelNotification(id)
    scope.launch {
      downloadDao.updateProgress(id, downloadedBytes = 0, totalBytes = 0, speedText = "Paused", status = DownloadStatus.PAUSED)
    }
  }

  fun resumeDownload(id: Long) {
    launchDownloadJob(id)
  }

  fun cancelDownload(id: Long) {
    activeJobs[id]?.cancel()
    activeJobs.remove(id)
    notificationHelper.cancelNotification(id)
    scope.launch {
      downloadDao.deleteById(id)
    }
  }

  private fun launchDownloadJob(id: Long) {
    activeJobs[id]?.cancel()

    val job = scope.launch {
      val item = downloadDao.getDownloadById(id) ?: return@launch
      downloadDao.updateProgress(id, item.downloadedBytes, item.totalBytes, "Connecting...", DownloadStatus.DOWNLOADING)

      try {
        val isAudio = item.format.lowercase().contains("mp3") || item.mediaType == MediaType.AUDIO
        val extension = if (isAudio) "mp3" else "mp4"
        val mimeType = if (isAudio) "audio/mpeg" else "video/mp4"

        val cleanTitle = item.title.replace(Regex("[^a-zA-Z0-9._-]"), "_").take(35).ifEmpty { "Media" }
        val fileName = "VidMax_${cleanTitle}_${item.id}.$extension"

        // Determine destination folder:
        // Priority 1: Public Movies / Music folder -> Movies/VidMax/ or Music/VidMax/ (Accessible to Gallery & File Manager)
        // Priority 2: Public Downloads folder -> Download/VidMax/
        // Priority 3: External App Files Dir
        val outputFile = resolveOutputFile(isAudio, fileName)

        val request = Request.Builder()
          .url(item.downloadUrl)
          .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
          .build()

        client.newCall(request).execute().use { response ->
          if (!response.isSuccessful) {
            downloadDao.updateProgress(id, 0, 0, "Server Error (${response.code})", DownloadStatus.FAILED)
            return@use
          }

          val body = response.body ?: throw Exception("Empty response body")
          val contentLength = body.contentLength().takeIf { it > 0 } ?: (item.totalBytes.takeIf { it > 0 } ?: 25_000_000L)

          val inputStream: InputStream = body.byteStream()
          val outputStream = FileOutputStream(outputFile)

          val buffer = ByteArray(16 * 1024)
          var bytesRead: Int
          var totalRead = 0L
          var lastTime = System.currentTimeMillis()
          var bytesSinceLastTime = 0L

          while (inputStream.read(buffer).also { bytesRead = it } != -1) {
            if (!isActive) {
              outputStream.close()
              return@launch
            }
            outputStream.write(buffer, 0, bytesRead)
            totalRead += bytesRead
            bytesSinceLastTime += bytesRead

            val now = System.currentTimeMillis()
            if (now - lastTime >= 500) {
              val elapsedSec = (now - lastTime) / 1000.0
              val speedMb = (bytesSinceLastTime / (1024.0 * 1024.0)) / elapsedSec
              val speedText = String.format("%.1f MB/s", speedMb.coerceAtLeast(0.1))

              downloadDao.updateProgress(id, totalRead, contentLength, speedText, DownloadStatus.DOWNLOADING)
              val percent = ((totalRead * 100) / contentLength.coerceAtLeast(1L)).toInt()
              notificationHelper.updateProgress(id, cleanTitle, percent, speedText)
              lastTime = now
              bytesSinceLastTime = 0L
            }
          }

          // Apply the optional metadata marker only when the user enabled the shield.
          if (item.isShielded && !isAudio) {
            val salt = ByteArray(128)
            java.security.SecureRandom().nextBytes(salt)
            val payload = "TIKTOK_HOOK_INTRO_TRENDING_AUDIO_MIX_CAPTION_FILTER_TRIM_V2_" +
              java.util.Base64.getEncoder().encodeToString(salt)
            val mp4FreeBox = createMp4FreeBox(payload)
            outputStream.write(mp4FreeBox)
          }

          outputStream.flush()
          outputStream.close()

          // Reset file modified timestamp
          outputFile.setLastModified(System.currentTimeMillis())

          // Register in Android Gallery & MediaStore so video shows up immediately in phone Gallery & File Manager!
          registerMediaInSystemGallery(outputFile, cleanTitle, isAudio, mimeType)

          downloadDao.markCompleted(id, DownloadStatus.COMPLETED, outputFile.absolutePath)
          notificationHelper.notifySuccess(id, cleanTitle, outputFile.absolutePath, mimeType)
        }
      } catch (e: Exception) {
        if (isActive) {
          val errorMsg = e.localizedMessage ?: "Failed"
          downloadDao.updateProgress(id, 0, 0, "Error: $errorMsg", DownloadStatus.FAILED)
          val cleanTitle = item.title.take(30).ifEmpty { "Media" }
          notificationHelper.notifyError(id, cleanTitle, errorMsg)
        }
      } finally {
        activeJobs.remove(id)
      }
    }

    activeJobs[id] = job
  }

  private fun resolveOutputFile(isAudio: Boolean, fileName: String): File {
    try {
      val primaryType = if (isAudio) Environment.DIRECTORY_MUSIC else Environment.DIRECTORY_MOVIES
      val publicDir = Environment.getExternalStoragePublicDirectory(primaryType)
      val vidMaxFolder = File(publicDir, "VidMax")
      if (!vidMaxFolder.exists()) {
        vidMaxFolder.mkdirs()
      }
      if (vidMaxFolder.exists() && vidMaxFolder.canWrite()) {
        return File(vidMaxFolder, fileName)
      }
    } catch (_: Exception) {}

    try {
      val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
      val vidMaxFolder = File(downloadsDir, "VidMax")
      if (!vidMaxFolder.exists()) {
        vidMaxFolder.mkdirs()
      }
      if (vidMaxFolder.exists() && vidMaxFolder.canWrite()) {
        return File(vidMaxFolder, fileName)
      }
    } catch (_: Exception) {}

    // Fallback to external files dir
    val appDir = if (isAudio) {
      context.getExternalFilesDir(Environment.DIRECTORY_MUSIC) ?: context.filesDir
    } else {
      context.getExternalFilesDir(Environment.DIRECTORY_MOVIES) ?: context.filesDir
    }
    if (!appDir.exists()) appDir.mkdirs()
    return File(appDir, fileName)
  }

  /**
   * Registers media into Android's MediaStore and triggers MediaScannerConnection
   * to guarantee the video appears instantly in:
   * 1. Phone default Gallery (Google Photos, Samsung/Xiaomi Gallery)
   * 2. Phone File Manager (Internal Storage > Movies > VidMax)
   */
  private fun registerMediaInSystemGallery(
    file: File,
    title: String,
    isAudio: Boolean,
    mimeType: String
  ) {
    try {
      // 1. Android MediaStore insertion (API 29+ scoped storage support)
      val values = android.content.ContentValues().apply {
        put(if (isAudio) android.provider.MediaStore.Audio.Media.TITLE else android.provider.MediaStore.Video.Media.TITLE, title)
        put(if (isAudio) android.provider.MediaStore.Audio.Media.DISPLAY_NAME else android.provider.MediaStore.Video.Media.DISPLAY_NAME, file.name)
        put(if (isAudio) android.provider.MediaStore.Audio.Media.MIME_TYPE else android.provider.MediaStore.Video.Media.MIME_TYPE, mimeType)
        put(if (isAudio) android.provider.MediaStore.Audio.Media.DATE_ADDED else android.provider.MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
        put(if (isAudio) android.provider.MediaStore.Audio.Media.DATE_MODIFIED else android.provider.MediaStore.Video.Media.DATE_MODIFIED, System.currentTimeMillis() / 1000)
        put(if (isAudio) android.provider.MediaStore.Audio.Media.DATA else android.provider.MediaStore.Video.Media.DATA, file.absolutePath)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
          put(if (isAudio) android.provider.MediaStore.Audio.Media.RELATIVE_PATH else android.provider.MediaStore.Video.Media.RELATIVE_PATH, if (isAudio) "Music/VidMax" else "Movies/VidMax")
        }
      }

      val contentUri = if (isAudio) {
        android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
      } else {
        android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI
      }

      context.contentResolver.insert(contentUri, values)
    } catch (_: Exception) {}

    try {
      // 2. Trigger Android MediaScanner to index into phone's Gallery database
      android.media.MediaScannerConnection.scanFile(
        context,
        arrayOf(file.absolutePath),
        arrayOf(mimeType)
      ) { _, _ -> }
    } catch (_: Exception) {}

    try {
      // 3. Broadcast legacy media scan intent
      val scanIntent = android.content.Intent(android.content.Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
      scanIntent.data = android.net.Uri.fromFile(file)
      context.sendBroadcast(scanIntent)
    } catch (_: Exception) {}
  }

  /**
   * Encapsulates signature in an ISO MP4 standard "free" box.
   * Standard media decoders safely skip "free" boxes while changing the file hash.
   */
  private fun createMp4FreeBox(payload: String): ByteArray {
    val bytes = payload.toByteArray(Charsets.UTF_8)
    val boxSize = 8 + bytes.size
    val buffer = java.nio.ByteBuffer.allocate(boxSize)
    buffer.order(java.nio.ByteOrder.BIG_ENDIAN)
    buffer.putInt(boxSize)
    buffer.put("free".toByteArray(Charsets.US_ASCII))
    buffer.put(bytes)
    return buffer.array()
  }
}

