package com.example

import com.example.data.model.DownloadItem
import com.example.data.model.DownloadStatus
import com.example.data.model.MediaType
import com.example.data.network.SocialMediaExtractor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun platformDetection_recognizesSocialPlatforms() {
    val extractor = SocialMediaExtractor()
    assertEquals("Instagram", extractor.detectPlatform("https://www.instagram.com/reel/C7xyz123"))
    assertEquals("TikTok", extractor.detectPlatform("https://www.tiktok.com/@user/video/123456789"))
    assertEquals("Facebook", extractor.detectPlatform("https://fb.watch/m9xyz"))
    assertEquals("X / Twitter", extractor.detectPlatform("https://x.com/user/status/987654321"))
    assertEquals("Pinterest", extractor.detectPlatform("https://pinterest.com/pin/12345"))
    assertEquals("Vimeo", extractor.detectPlatform("https://vimeo.com/98765"))
  }

  @Test
  fun downloadItem_calculatesProgressAndSize() {
    val item = DownloadItem(
      title = "VIP Test Video",
      originalUrl = "https://example.com/video",
      downloadUrl = "https://example.com/video.mp4",
      thumbnailUrl = "",
      platform = "Instagram",
      mediaType = MediaType.VIDEO,
      resolution = "1080p",
      format = "mp4",
      totalBytes = 100_000_000L,
      downloadedBytes = 50_000_000L,
      status = DownloadStatus.DOWNLOADING
    )

    assertEquals(0.5f, item.progress, 0.01f)
    assertEquals(50, item.progressPercent)
    assertTrue(item.formattedSize.contains("MB"))
  }
}
