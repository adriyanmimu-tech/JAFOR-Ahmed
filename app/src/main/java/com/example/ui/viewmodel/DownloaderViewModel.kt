package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.download.DownloadEngine
import com.example.data.local.AppDatabase
import com.example.data.model.DownloadItem
import com.example.data.model.DownloadStatus
import com.example.data.model.ExtractedMediaInfo
import com.example.data.model.VideoQualityOption
import com.example.data.network.SocialMediaExtractor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DownloaderViewModel(application: Application) : AndroidViewModel(application) {

  private val database = AppDatabase.getDatabase(application)
  private val downloadDao = database.downloadDao()
  private val downloadEngine = DownloadEngine(application, downloadDao)
  private val extractor = SocialMediaExtractor()

  val allDownloads: StateFlow<List<DownloadItem>> = downloadDao.getAllDownloads()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  private val _inputUrl = MutableStateFlow("")
  val inputUrl: StateFlow<String> = _inputUrl.asStateFlow()

  private val _isExtracting = MutableStateFlow(false)
  val isExtracting: StateFlow<Boolean> = _isExtracting.asStateFlow()

  private val _extractedMedia = MutableStateFlow<ExtractedMediaInfo?>(null)
  val extractedMedia: StateFlow<ExtractedMediaInfo?> = _extractedMedia.asStateFlow()

  private val _clipboardCandidate = MutableStateFlow<String?>(null)
  val clipboardCandidate: StateFlow<String?> = _clipboardCandidate.asStateFlow()

  private val _selectedQuality = MutableStateFlow<VideoQualityOption?>(null)
  val selectedQuality: StateFlow<VideoQualityOption?> = _selectedQuality.asStateFlow()

  private val _errorMessage = MutableStateFlow<String?>(null)
  val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

  private val _isAntiCopyrightEnabled = MutableStateFlow(false) // Pure original 1080p master quality by default
  val isAntiCopyrightEnabled: StateFlow<Boolean> = _isAntiCopyrightEnabled.asStateFlow()

  private val _fypShieldOptions = MutableStateFlow(com.example.data.protection.FypShieldOptions())
  val fypShieldOptions: StateFlow<com.example.data.protection.FypShieldOptions> = _fypShieldOptions.asStateFlow()

  private val _selectedItemForShield = MutableStateFlow<DownloadItem?>(null)
  val selectedItemForShield: StateFlow<DownloadItem?> = _selectedItemForShield.asStateFlow()

  private val _showFypTips = MutableStateFlow(false)
  val showFypTips: StateFlow<Boolean> = _showFypTips.asStateFlow()

  private val _shieldStatusMessage = MutableStateFlow<String?>(null)
  val shieldStatusMessage: StateFlow<String?> = _shieldStatusMessage.asStateFlow()

  private val copyrightShieldEngine = com.example.data.protection.CopyrightShieldEngine(application)

  private val _isDarkTheme = MutableStateFlow(true)
  val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

  private val _activeTab = MutableStateFlow(0)
  val activeTab: StateFlow<Int> = _activeTab.asStateFlow()

  private val _activePlayingItem = MutableStateFlow<DownloadItem?>(null)
  val activePlayingItem: StateFlow<DownloadItem?> = _activePlayingItem.asStateFlow()

  private val _browserUrlToLoad = MutableStateFlow<String?>(null)
  val browserUrlToLoad: StateFlow<String?> = _browserUrlToLoad.asStateFlow()

  private val _fileLocationDialogItem = MutableStateFlow<DownloadItem?>(null)
  val fileLocationDialogItem: StateFlow<DownloadItem?> = _fileLocationDialogItem.asStateFlow()

  fun onUrlChange(newUrl: String) {
    _inputUrl.value = newUrl
    _errorMessage.value = null
  }

  fun toggleTheme() {
    _isDarkTheme.value = !_isDarkTheme.value
  }

  fun setTab(index: Int) {
    _activeTab.value = index
  }

  fun openUrlInBrowser(url: String) {
    val clean = url.trim()
    if (clean.isNotBlank()) {
      _browserUrlToLoad.value = clean
      _activeTab.value = 1
    }
  }

  fun onBrowserUrlLoaded() {
    _browserUrlToLoad.value = null
  }

  fun showFileLocation(item: DownloadItem) {
    _fileLocationDialogItem.value = item
  }

  fun dismissFileLocation() {
    _fileLocationDialogItem.value = null
  }


  fun checkClipboard(clipboardContent: String?) {
    if (clipboardContent.isNullOrBlank()) return
    val text = clipboardContent.trim()
    if ((text.startsWith("http://") || text.startsWith("https://")) &&
      text != _inputUrl.value &&
      text != _clipboardCandidate.value
    ) {
      _clipboardCandidate.value = text
    }
  }

  fun dismissClipboardCandidate() {
    _clipboardCandidate.value = null
  }

  fun acceptClipboardCandidate() {
    _clipboardCandidate.value?.let { candidate ->
      _inputUrl.value = candidate
      _clipboardCandidate.value = null
      extractMedia(candidate)
    }
  }

  fun extractMedia(overrideUrl: String? = null) {
    val targetUrl = (overrideUrl ?: _inputUrl.value).trim()
    if (targetUrl.isBlank()) {
      _errorMessage.value = "Please paste or enter a valid video link"
      return
    }

    if (!targetUrl.startsWith("http://") && !targetUrl.startsWith("https://")) {
      _errorMessage.value = "Link must start with http:// or https://"
      return
    }

    viewModelScope.launch {
      _isExtracting.value = true
      _errorMessage.value = null
      try {
        val media = extractor.extractMedia(targetUrl)
        _extractedMedia.value = media
        val availableQualities = media.qualities.filter {
          it.downloadUrl.startsWith("http://", ignoreCase = true) ||
            it.downloadUrl.startsWith("https://", ignoreCase = true)
        }
        _selectedQuality.value = availableQualities.firstOrNull {
          it.mediaType == com.example.data.model.MediaType.VIDEO && it.isVipBest
        } ?: availableQualities.firstOrNull {
          it.mediaType == com.example.data.model.MediaType.VIDEO
        } ?: availableQualities.firstOrNull()
        _showQualityModal.value = true
      } catch (e: Exception) {
        _errorMessage.value = "Could not parse link: ${e.localizedMessage ?: "Unknown error"}"
      } finally {
        _isExtracting.value = false
      }
    }
  }

  fun handleDownloadButtonClick() {
    val currentUrl = _inputUrl.value.trim()
    val media = _extractedMedia.value
    if (media != null && media.originalUrl.trim() == currentUrl) {
      _showQualityModal.value = true
    } else {
      extractMedia()
    }
  }

  private val _showQualityModal = MutableStateFlow(false)
  val showQualityModal: StateFlow<Boolean> = _showQualityModal.asStateFlow()

  fun openQualityModal() {
    _showQualityModal.value = true
  }

  fun dismissQualityModal() {
    _showQualityModal.value = false
  }

  fun resetMedia() {
    _extractedMedia.value = null
    _selectedQuality.value = null
    _showQualityModal.value = false
    _inputUrl.value = ""
    _errorMessage.value = null
  }

  fun selectQuality(quality: VideoQualityOption) {
    _selectedQuality.value = quality
  }

  fun downloadWithQuality(quality: VideoQualityOption) {
    _selectedQuality.value = quality
    confirmDownload()
  }

  fun downloadDirectHd() {
    val media = _extractedMedia.value ?: return
    val hd = media.qualities.firstOrNull { it.mediaType == com.example.data.model.MediaType.VIDEO && it.isVipBest }
      ?: media.qualities.firstOrNull { it.mediaType == com.example.data.model.MediaType.VIDEO && it.resolution.contains("1080", ignoreCase = true) }
      ?: media.qualities.firstOrNull { it.mediaType == com.example.data.model.MediaType.VIDEO }
    if (hd != null) {
      downloadWithQuality(hd)
    }
  }

  fun downloadDirectSd() {
    val media = _extractedMedia.value ?: return
    val sd = media.qualities.firstOrNull {
      it.mediaType == com.example.data.model.MediaType.VIDEO &&
        (it.resolution.contains("720", ignoreCase = true) || it.resolution.contains("480", ignoreCase = true)) &&
        !it.isVipBest
    }
      ?: media.qualities.filter { it.mediaType == com.example.data.model.MediaType.VIDEO }.getOrNull(1)
      ?: media.qualities.firstOrNull { it.mediaType == com.example.data.model.MediaType.VIDEO }
    if (sd != null) {
      downloadWithQuality(sd)
    }
  }

  fun downloadDirectAudio() {
    val media = _extractedMedia.value ?: return
    val audio = media.qualities.firstOrNull { it.mediaType == com.example.data.model.MediaType.AUDIO }
      ?: media.qualities.firstOrNull()
    if (audio != null) {
      downloadWithQuality(audio)
    }
  }

  fun confirmDownload() {
    val media = _extractedMedia.value ?: return
    val quality = _selectedQuality.value ?: return

    viewModelScope.launch {
      val isTikTok = media.platform.equals("TikTok", ignoreCase = true)
      val shouldShield = _isAntiCopyrightEnabled.value
      val finalTitle = if (shouldShield && isTikTok && !media.title.contains("FYP")) {
        "${media.title} [FYP 4x Rules]"
      } else {
        media.title
      }

      val newItem = DownloadItem(
        title = finalTitle,
        originalUrl = media.originalUrl,
        downloadUrl = quality.downloadUrl,
        thumbnailUrl = media.thumbnailUrl,
        platform = media.platform,
        mediaType = quality.mediaType,
        resolution = quality.resolution,
        format = quality.format.lowercase(),
        author = media.author,
        duration = media.duration,
        isShielded = shouldShield,
        status = DownloadStatus.DOWNLOADING,
        speedText = if (shouldShield) "Applying optional shield..." else "Starting..."
      )

      downloadEngine.startDownload(newItem)
      dismissQualityModal()
      _activeTab.value = 2 // Switch directly to Downloads tab so user sees their progress!
    }
  }

  fun toggleAntiCopyright(enabled: Boolean) {
    _isAntiCopyrightEnabled.value = enabled
  }

  fun showFypTipsDialog() {
    _showFypTips.value = true
  }

  fun dismissFypTipsDialog() {
    _showFypTips.value = false
  }

  fun toggleShieldOption(optionIndex: Int) {
    val current = _fypShieldOptions.value
    _fypShieldOptions.value = when (optionIndex) {
      1 -> current.copy(addHookIntro = !current.addHookIntro)
      2 -> current.copy(trendingAudioMix = !current.trendingAudioMix)
      3 -> current.copy(tiktokTextAndFilter = !current.tiktokTextAndFilter)
      4 -> current.copy(trimLastSecond = !current.trimLastSecond)
      else -> current
    }
  }

  fun selectAllShieldOptions() {
    _fypShieldOptions.value = com.example.data.protection.FypShieldOptions(
      addHookIntro = true,
      trendingAudioMix = true,
      tiktokTextAndFilter = true,
      trimLastSecond = true
    )
  }

  fun openShieldModal(item: DownloadItem) {
    _selectedItemForShield.value = item
  }

  fun closeShieldModal() {
    _selectedItemForShield.value = null
  }

  fun shieldExistingVideo(
    item: DownloadItem,
    options: com.example.data.protection.FypShieldOptions = _fypShieldOptions.value
  ) {
    viewModelScope.launch {
      try {
        _shieldStatusMessage.value = "Applying ${options.activeOptionsCount}/4 FYP Anti-Copyright Protections..."
        val shieldedFile = copyrightShieldEngine.applyShield(item, options)
        val shieldedItem = item.copy(
          id = 0,
          title = "${item.title} [4x FYP Shielded]",
          localFilePath = shieldedFile.absolutePath,
          isShielded = true,
          status = DownloadStatus.COMPLETED,
          createdAt = System.currentTimeMillis()
        )
        downloadDao.insertDownload(shieldedItem)
        _shieldStatusMessage.value = "All 4 Protections Applied! 100% Unique & FYP Ready."
        _selectedItemForShield.value = null
      } catch (e: Exception) {
        _shieldStatusMessage.value = "Could not shield video: ${e.localizedMessage}"
      }
    }
  }

  fun clearShieldStatus() {
    _shieldStatusMessage.value = null
  }

  fun pauseDownload(id: Long) {
    downloadEngine.pauseDownload(id)
  }

  fun resumeDownload(id: Long) {
    downloadEngine.resumeDownload(id)
  }

  fun cancelDownload(id: Long) {
    downloadEngine.cancelDownload(id)
  }

  fun deleteDownload(item: DownloadItem) {
    viewModelScope.launch {
      downloadDao.deleteDownload(item)
    }
  }

  fun openPlayer(item: DownloadItem) {
    _activePlayingItem.value = item
  }

  fun closePlayer() {
    _activePlayingItem.value = null
  }
}
