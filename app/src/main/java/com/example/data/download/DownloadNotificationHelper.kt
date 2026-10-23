package com.example.data.download

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.FileProvider
import com.example.MainActivity
import java.io.File

/**
 * High-performance, low-overhead system notification manager for video downloads.
 * Shows sleek progress notifications during downloads and actionable completion notifications
 * with direct 'Open Video' support.
 */
class DownloadNotificationHelper(private val context: Context) {

  companion object {
    const val CHANNEL_PROGRESS_ID = "snapvid_download_progress"
    const val CHANNEL_COMPLETED_ID = "snapvid_download_completed"
  }

  private val notificationManager = NotificationManagerCompat.from(context)

  init {
    createNotificationChannels()
  }

  private fun createNotificationChannels() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val progressChannel = NotificationChannel(
        CHANNEL_PROGRESS_ID,
        "Active Downloads",
        NotificationManager.IMPORTANCE_LOW
      ).apply {
        description = "Shows real-time download progress and speed"
        setShowBadge(false)
      }

      val completedChannel = NotificationChannel(
        CHANNEL_COMPLETED_ID,
        "Completed Downloads",
        NotificationManager.IMPORTANCE_DEFAULT
      ).apply {
        description = "Alerts when media download completes successfully"
        setShowBadge(true)
        enableVibration(true)
      }

      val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
      manager?.createNotificationChannel(progressChannel)
      manager?.createNotificationChannel(completedChannel)
    }
  }

  fun updateProgress(downloadId: Long, title: String, percent: Int, speedText: String) {
    try {
      val notificationId = (downloadId % Int.MAX_VALUE).toInt()

      // Tap notification opens app
      val appIntent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
      }
      val pendingIntent = PendingIntent.getActivity(
        context,
        notificationId,
        appIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
      )

      val notification = NotificationCompat.Builder(context, CHANNEL_PROGRESS_ID)
        .setSmallIcon(android.R.drawable.stat_sys_download)
        .setContentTitle("Downloading: $title")
        .setContentText("$percent% • $speedText")
        .setProgress(100, percent.coerceIn(0, 100), percent <= 0)
        .setOngoing(true)
        .setOnlyAlertOnce(true)
        .setContentIntent(pendingIntent)
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .build()

      notificationManager.notify(notificationId, notification)
    } catch (_: SecurityException) {
      // Permission not yet granted on Android 13+
    } catch (_: Exception) {}
  }

  fun notifySuccess(downloadId: Long, title: String, filePath: String, mimeType: String) {
    try {
      val notificationId = (downloadId % Int.MAX_VALUE).toInt()

      val file = File(filePath)
      val openIntent = if (file.exists()) {
        try {
          val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
          )
          Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, mimeType)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
          }
        } catch (_: Exception) {
          Intent(context, MainActivity::class.java)
        }
      } else {
        Intent(context, MainActivity::class.java)
      }

      val pendingIntent = PendingIntent.getActivity(
        context,
        notificationId + 10000,
        openIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
      )

      val notification = NotificationCompat.Builder(context, CHANNEL_COMPLETED_ID)
        .setSmallIcon(android.R.drawable.stat_sys_download_done)
        .setContentTitle("Download Completed!")
        .setContentText(title)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .addAction(android.R.drawable.ic_media_play, "Play Video", pendingIntent)
        .build()

      // Cancel progress notification and show completed notification
      notificationManager.cancel(notificationId)
      notificationManager.notify(notificationId + 10000, notification)
    } catch (_: SecurityException) {
      // Permission not yet granted
    } catch (_: Exception) {}
  }

  fun notifyError(downloadId: Long, title: String, errorMsg: String) {
    try {
      val notificationId = (downloadId % Int.MAX_VALUE).toInt()

      val notification = NotificationCompat.Builder(context, CHANNEL_COMPLETED_ID)
        .setSmallIcon(android.R.drawable.stat_notify_error)
        .setContentTitle("Download Failed")
        .setContentText("$title ($errorMsg)")
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .build()

      notificationManager.cancel(notificationId)
      notificationManager.notify(notificationId + 20000, notification)
    } catch (_: SecurityException) {} catch (_: Exception) {}
  }

  fun cancelNotification(downloadId: Long) {
    try {
      val notificationId = (downloadId % Int.MAX_VALUE).toInt()
      notificationManager.cancel(notificationId)
    } catch (_: Exception) {}
  }
}
