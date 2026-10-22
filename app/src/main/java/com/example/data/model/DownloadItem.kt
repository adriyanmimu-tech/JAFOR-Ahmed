package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class DownloadStatus {
  PENDING,
  DOWNLOADING,
  PAUSED,
  COMPLETED,
  FAILED
}

enum class MediaType {
  VIDEO,
  AUDIO
}

@Entity(tableName = "downloads")
data class DownloadItem(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val title: String,
  val originalUrl: String,
  val downloadUrl: String,
  val thumbnailUrl: String,
  val platform: String,
  val mediaType: MediaType = MediaType.VIDEO,
  val resolution: String, // e.g. "1080p", "720p", "MP3 320kbps"
  val format: String, // "mp4" or "mp3"
  val totalBytes: Long = 0,
  val downloadedBytes: Long = 0,
  val speedText: String = "", // e.g. "3.5 MB/s"
  val status: DownloadStatus = DownloadStatus.PENDING,
  val localFilePath: String? = null,
  val duration: String = "",
  val author: String = "",
  val isShielded: Boolean = false, // Anti-Copyright & FYP Shield applied
  val createdAt: Long = System.currentTimeMillis()
) {
  val progress: Float
    get() = if (totalBytes > 0) (downloadedBytes.toFloat() / totalBytes).coerceIn(0f, 1f) else 0f

  val progressPercent: Int
    get() = (progress * 100).toInt()

  val formattedSize: String
    get() = formatFileSize(totalBytes)

  companion object {
    fun formatFileSize(bytes: Long): String {
      if (bytes <= 0) return "Unknown"
      val kb = bytes / 1024.0
      val mb = kb / 1024.0
      val gb = mb / 1024.0
      return when {
        gb >= 1.0 -> String.format("%.2f GB", gb)
        mb >= 1.0 -> String.format("%.1f MB", mb)
        else -> String.format("%.0f KB", kb)
      }
    }
  }
}

data class VideoQualityOption(
  val label: String,          // e.g. "1080p Full HD"
  val resolution: String,     // "1080p"
  val format: String,         // "MP4" or "MP3"
  val mediaType: MediaType,
  val estimatedSize: String,  // e.g. "48 MB"
  val downloadUrl: String,
  val bitrate: String = "",
  val isVipBest: Boolean = false
)

data class ExtractedMediaInfo(
  val originalUrl: String,
  val title: String,
  val author: String,
  val duration: String,
  val thumbnailUrl: String,
  val platform: String,
  val qualities: List<VideoQualityOption>
)
