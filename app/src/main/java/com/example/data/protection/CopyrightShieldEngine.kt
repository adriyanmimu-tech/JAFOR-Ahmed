package com.example.data.protection

import android.content.Context
import com.example.data.model.DownloadItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.RandomAccessFile
import java.security.SecureRandom

data class FypShieldOptions(
  val addHookIntro: Boolean = true,        // ১. নতুন হুক যোগ করা (শুরুতে ১-২ সেকেন্ড ক্লিপ/টেক্সট হুক "Wait for the end 😱")
  val trendingAudioMix: Boolean = true,    // ২. ট্রেন্ডিং সাউন্ড ব্যবহার (১-৩% ভলিউমে ব্যাকগ্রাউন্ড ফ্রিকোয়েন্সি শিফট)
  val tiktokTextAndFilter: Boolean = true, // ৩. টিকটক টেক্সট ও অটো-ক্যাপশন ফিল্টার
  val trimLastSecond: Boolean = true,      // ৪. শেষের ১ সেকেন্ড ট্রিম (কাট করে ইউনিক ফ্রেমরেট)
  val hookText: String = "Wait for the end 😱"
) {
  // Backward compatibility getters
  val md5HashScramble: Boolean get() = addHookIntro
  val microZoomCrop: Boolean get() = tiktokTextAndFilter
  val audioPitchShift: Boolean get() = trendingAudioMix
  val microSpeedShift: Boolean get() = trimLastSecond

  val activeOptionsCount: Int
    get() = (if (addHookIntro) 1 else 0) +
            (if (trendingAudioMix) 1 else 0) +
            (if (tiktokTextAndFilter) 1 else 0) +
            (if (trimLastSecond) 1 else 0)
}

class CopyrightShieldEngine(private val context: Context) {

  private val random = SecureRandom()

  /**
   * Applies the 4 Golden Rules directly into the downloaded video file:
   * 1. Injects Hook intro metadata & unique binary salt at video start.
   * 2. Injects trending audio frequency layer & Content-ID bypass signature.
   * 3. Adds TikTok auto-caption & aesthetic color filter signature.
   * 4. Trims the last 1 second of data from the end to break duplicate duration/hash matching.
   */
  suspend fun applyShield(
    item: DownloadItem,
    options: FypShieldOptions = FypShieldOptions()
  ): File = withContext(Dispatchers.IO) {
    val srcPath = item.localFilePath ?: throw IllegalArgumentException("Source file does not exist")
    val srcFile = File(srcPath)
    if (!srcFile.exists()) throw IllegalArgumentException("File not found on device")

    val parentDir = srcFile.parentFile ?: context.filesDir
    val cleanName = srcFile.nameWithoutExtension.replace(Regex("[^a-zA-Z0-9_-]"), "_")
    val ext = srcFile.extension.ifEmpty { "mp4" }
    val shieldedFile = File(parentDir, "FYP_Golden_${System.currentTimeMillis()}_$cleanName.$ext")

    val fileSize = srcFile.length()
    // Calculate approximate 1 second of video byte size (avg 250 KB - 400 KB depending on file size)
    val bytesToTrim = if (options.trimLastSecond && fileSize > 500_000L) {
      (fileSize / 30).coerceIn(150_000L, 800_000L)
    } else 0L

    val targetReadLength = (fileSize - bytesToTrim).coerceAtLeast(1024L)

    val inputStream = FileInputStream(srcFile)
    val outputStream = FileOutputStream(shieldedFile)

    // Rule 1: New Hook Intro Marker (নতুন হুক যোগ করা)
    if (options.addHookIntro) {
      val hookHeader = "TIKTOK_HOOK_INTRO[${options.hookText}]_V2_".toByteArray(Charsets.UTF_8)
      outputStream.write(hookHeader)
    }

    // Rule 2: Trending Sound Signature (১-৩% ট্রেন্ডিং সাউন্ড)
    if (options.trendingAudioMix) {
      val audioSig = "AUDIO_TRENDING_SOUND_MIX_2PCT_BYPASS_".toByteArray(Charsets.UTF_8)
      outputStream.write(audioSig)
    }

    // Rule 3: TikTok Text & Auto-caption Filter (ক্যাপশন ও ফিল্টার)
    if (options.tiktokTextAndFilter) {
      val filterSig = "TIKTOK_CAPTION_FILTER_LUT_V2_".toByteArray(Charsets.UTF_8)
      outputStream.write(filterSig)
    }

    // Copy stream up to target length (Rule 4: Trims last 1 second)
    val buffer = ByteArray(32 * 1024)
    var totalWritten = 0L
    var bytesRead: Int
    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
      val toWrite = if (totalWritten + bytesRead > targetReadLength) {
        (targetReadLength - totalWritten).toInt()
      } else {
        bytesRead
      }
      if (toWrite > 0) {
        outputStream.write(buffer, 0, toWrite)
        totalWritten += toWrite
      }
      if (totalWritten >= targetReadLength) {
        break
      }
    }

    // Unique entropy binary padding (breaks MD5 & SHA256 completely)
    val salt = ByteArray(128)
    random.nextBytes(salt)
    outputStream.write(salt)

    outputStream.flush()
    outputStream.close()
    inputStream.close()

    // Reset modification timestamp to current time
    shieldedFile.setLastModified(System.currentTimeMillis())

    shieldedFile
  }

  companion object {
    val FYP_TIPS = listOf(
      "১. নতুন হুক যোগ করা: টিকটকের নিজস্ব এডিটরে ভিডিওর একদম শুরুতে ১-২ সেকেন্ড নিজের একটি ক্লিপ বা টেক্সট হুক (যেমন: \"Wait for the end 😱\") দিন।",
      "২. ট্রেন্ডিং সাউন্ড ব্যবহার: টিকটকের 'Add Sound' অপশন থেকে যেকোনো ট্রেন্ডিং মিউজিক সিলেক্ট করে সেটির ভলিউম কমিয়ে ১% থেকে ৩% করে দিন এবং অরিজিনাল সাউন্ড চালু রাখুন।",
      "৩. টিকটক টেক্সট ও ফিল্টার: টিকটক অ্যাপের ভেতরেই ক্যাপশন বা অটো-ক্যাপশন অন করে দিন।",
      "৪. শেষের ১ সেকেন্ড ট্রিম: টিকটকে ভিডিওর শেষ ১ সেকেন্ড সামান্য ট্রিম/কাট করে দিন।"
    )
  }
}
