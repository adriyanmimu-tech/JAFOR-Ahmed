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
        val targetDir = if (item.mediaType == MediaType.VIDEO) {
          context.getExternalFilesDir(Environment.DIRECTORY_MOVIES) ?: context.filesDir
        } else {
          context.getExternalFilesDir(Environment.DIRECTORY_MUSIC) ?: context.filesDir
        }
        if (!targetDir.exists()) targetDir.mkdirs()

        val cleanTitle = item.title.replace(Regex("[^a-zA-Z0-9._-]"), "_").take(40)
        val extension = if (item.format.lowercase().contains("mp3") || item.mediaType == MediaType.AUDIO) "mp3" else "mp4"
        val outputFile = File(targetDir, "VidMax_${cleanTitle}_${item.id}.$extension")

        val request = Request.Builder()
          .url(item.downloadUrl)
          .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
          .build()

        client.newCall(request).execute().use { response ->
          if (!response.isSuccessful) {
            downloadDao.updateProgress(id, 0, 0, "Server Error", DownloadStatus.FAILED)
            return@use
          }

          val body = response.body ?: throw Exception("Empty response body")
          val contentLength = body.contentLength().takeIf { it > 0 } ?: (item.totalBytes.takeIf { it > 0 } ?: 25_000_000L)

          val inputStream: InputStream = body.byteStream()
          val outputStream = FileOutputStream(outputFile)

          val buffer = ByteArray(8 * 1024)
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
              lastTime = now
              bytesSinceLastTime = 0L
            }
          }

          if (item.isShielded) {
            val salt = ByteArray(64)
            java.security.SecureRandom().nextBytes(salt)
            outputStream.write(salt)
          }

          outputStream.flush()
          outputStream.close()

          downloadDao.markCompleted(id, DownloadStatus.COMPLETED, outputFile.absolutePath)
        }
      } catch (e: Exception) {
        if (isActive) {
          downloadDao.updateProgress(id, 0, 0, "Error: ${e.localizedMessage ?: "Failed"}", DownloadStatus.FAILED)
        }
      } finally {
        activeJobs.remove(id)
      }
    }

    activeJobs[id] = job
  }
}
