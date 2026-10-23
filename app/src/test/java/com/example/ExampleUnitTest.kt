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
  fun qualityOptions_onlyShowDistinctAvailableStreams() {
    val extractor = SocialMediaExtractor()
    val sameStream = "https://cdn.example/video.mp4?token=sample"

    val options = extractor.buildMultiResolutionQualities(
      hdStream = sameStream,
      sdStream = sameStream,
      audioStream = sameStream,
      platform = "TikTok"
    )

    assertEquals(1, options.size)
    assertEquals(sameStream, options.single().downloadUrl)
    assertTrue(options.single().isVipBest)
    assertTrue(options.none { it.resolution.contains("4K", ignoreCase = true) })
  }

  @Test
  fun qualityOptions_recommendBestAndKeepDistinctManualChoices() {
    val extractor = SocialMediaExtractor()
    val bestStream = "https://cdn.example/video-hd.mp4"
    val standardStream = "https://cdn.example/video-sd.mp4"

    val options = extractor.buildMultiResolutionQualities(
      hdStream = bestStream,
      sdStream = standardStream,
      platform = "TikTok"
    )
    val videoOptions = options.filter { it.mediaType == MediaType.VIDEO }

    assertEquals(2, videoOptions.size)
    assertEquals(bestStream, videoOptions.first().downloadUrl)
    assertTrue(videoOptions.first().isVipBest)
    assertEquals(standardStream, videoOptions.last().downloadUrl)
    assertTrue(videoOptions.none { it.resolution.contains("4K", ignoreCase = true) })
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
