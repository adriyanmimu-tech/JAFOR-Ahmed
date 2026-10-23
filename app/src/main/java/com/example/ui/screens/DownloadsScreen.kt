package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.data.model.DownloadItem
import com.example.data.model.DownloadStatus
import com.example.data.model.MediaType
import com.example.ui.components.GlassCard
import com.example.ui.components.PlatformBrandIcon
import com.example.ui.theme.VipAmberDark
import com.example.ui.theme.VipAmberPrimary
import com.example.ui.theme.VipCoral
import com.example.ui.theme.VipGoldAccent
import com.example.ui.viewmodel.DownloaderViewModel
import java.io.File

@Composable
fun DownloadsScreen(
  viewModel: DownloaderViewModel,
  isDark: Boolean,
  onPlayItem: (DownloadItem) -> Unit
) {
  val context = LocalContext.current
  val allDownloads by viewModel.allDownloads.collectAsState()

  var selectedFilter by remember { mutableStateOf("All") } // "All", "Videos", "Audio", "Active"
  var searchQuery by remember { mutableStateOf("") }

  val filteredDownloads = remember(allDownloads, selectedFilter, searchQuery) {
    allDownloads.filter { item ->
      val matchesFilter = when (selectedFilter) {
        "Videos" -> item.mediaType == MediaType.VIDEO && item.status == DownloadStatus.COMPLETED
        "Audio" -> item.mediaType == MediaType.AUDIO && item.status == DownloadStatus.COMPLETED
        "Active" -> item.status == DownloadStatus.DOWNLOADING || item.status == DownloadStatus.PAUSED || item.status == DownloadStatus.PENDING
        else -> true
      }
      val matchesSearch = searchQuery.isBlank() || item.title.contains(searchQuery, ignoreCase = true)
      matchesFilter && matchesSearch
    }
  }

  val activeItems = remember(allDownloads) {
    allDownloads.filter { it.status == DownloadStatus.DOWNLOADING || it.status == DownloadStatus.PAUSED || it.status == DownloadStatus.PENDING }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(horizontal = 16.dp)
  ) {
    Spacer(modifier = Modifier.height(16.dp))

    // Top Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "Download Manager",
          fontSize = 22.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onBackground
        )
        Text(
          text = "${allDownloads.count { it.status == DownloadStatus.COMPLETED }} completed • ${activeItems.size} active",
          fontSize = 12.sp,
          color = if (isDark) Color(0xFFA5ABB8) else Color(0xFF6B7280)
        )
      }

      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(12.dp))
          .background(VipAmberPrimary.copy(alpha = 0.15f))
          .padding(horizontal = 10.dp, vertical = 6.dp)
      ) {
        Text(
          text = "VIP VAULT",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = VipAmberPrimary
        )
      }
    }

    Spacer(modifier = Modifier.height(6.dp))

    Spacer(modifier = Modifier.height(14.dp))

    // Search bar
    OutlinedTextField(
      value = searchQuery,
      onValueChange = { searchQuery = it },
      placeholder = { Text("Search your downloads...", fontSize = 13.sp) },
      leadingIcon = {
        Icon(
          imageVector = Icons.Default.Search,
          contentDescription = "Search",
          tint = VipAmberPrimary
        )
      },
      trailingIcon = {
        if (searchQuery.isNotBlank()) {
          IconButton(onClick = { searchQuery = "" }) {
            Icon(Icons.Default.Close, contentDescription = "Clear")
          }
        }
      },
      shape = RoundedCornerShape(14.dp),
      colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = VipAmberPrimary,
        unfocusedBorderColor = if (isDark) Color(0x33FFB300) else Color(0x33FF8F00),
        focusedContainerColor = if (isDark) Color(0x22171B2A) else Color(0xFFF9F9F9),
        unfocusedContainerColor = if (isDark) Color(0x1A171B2A) else Color(0xFFFAFAFA)
      ),
      modifier = Modifier
        .fillMaxWidth()
        .height(52.dp)
    )


    val shieldMessage by viewModel.shieldStatusMessage.collectAsState()
    if (shieldMessage != null) {
      Spacer(modifier = Modifier.height(10.dp))
      GlassCard(
        isDark = isDark,
        borderColor = VipAmberPrimary,
        borderWidth = 1.dp,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon(
              imageVector = Icons.Default.Lock,
              contentDescription = "Shield Status",
              tint = VipAmberPrimary,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = shieldMessage ?: "",
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium,
              color = MaterialTheme.colorScheme.onBackground
            )
          }
          IconButton(
            onClick = { viewModel.clearShieldStatus() },
            modifier = Modifier.size(24.dp)
          ) {
            Icon(Icons.Default.Close, contentDescription = "Close", modifier = Modifier.size(16.dp))
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Filter Chips
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      val filters = listOf("All", "Videos", "Audio", "Active")
      filters.forEach { filter ->
        val isSelected = selectedFilter == filter
        FilterChip(
          selected = isSelected,
          onClick = { selectedFilter = filter },
          label = { Text(filter, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = VipAmberPrimary,
            selectedLabelColor = Color.Black,
            containerColor = if (isDark) Color(0x221A1D2C) else Color(0xFFEEEEEE),
            labelColor = MaterialTheme.colorScheme.onBackground
          ),
          border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = isSelected,
            borderColor = if (isDark) Color(0x33555D75) else Color(0xFFDDDDDD),
            selectedBorderColor = VipAmberPrimary
          ),
          shape = RoundedCornerShape(10.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // List of Downloads
    if (filteredDownloads.isEmpty()) {
      // Empty state
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f),
        contentAlignment = Alignment.Center
      ) {
        GlassCard(
          isDark = isDark,
          modifier = Modifier.fillMaxWidth(0.9f)
        ) {
          Column(
            modifier = Modifier.padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(
              imageVector = Icons.Default.FolderOpen,
              contentDescription = "No Downloads",
              tint = VipAmberPrimary,
              modifier = Modifier.size(54.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = if (searchQuery.isNotBlank()) "No files match \"$searchQuery\"" else "No Downloads Yet",
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "Paste any link from Instagram, TikTok, Facebook or X on the Home tab to download instantly in high quality.",
              fontSize = 13.sp,
              color = if (isDark) Color(0xFFA5ABB8) else Color(0xFF6B7280),
              lineHeight = 18.sp
            )
          }
        }
      }
    } else {
      LazyColumn(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        items(filteredDownloads, key = { it.id }) { item ->
          DownloadItemRow(
            item = item,
            isDark = isDark,
            onPlay = { onPlayItem(item) },
            onOpenInGallery = { openInPhoneGallery(context, item) },
            onShowLocation = { viewModel.showFileLocation(item) },
            onPause = { viewModel.pauseDownload(item.id) },
            onResume = { viewModel.resumeDownload(item.id) },
            onCancel = { viewModel.cancelDownload(item.id) },
            onDelete = { viewModel.deleteDownload(item) },
            onShield = { viewModel.openShieldModal(item) },
            onShare = { shareMedia(context, item) }
          )
        }
        item {
          Spacer(modifier = Modifier.height(80.dp))
        }
      }
    }

    val selectedItemForShield by viewModel.selectedItemForShield.collectAsState()
    val fypShieldOptions by viewModel.fypShieldOptions.collectAsState()
    selectedItemForShield?.let { shieldItem ->
      com.example.ui.components.FypShieldModal(
        item = shieldItem,
        options = fypShieldOptions,
        isDark = isDark,
        onToggleOption = { viewModel.toggleShieldOption(it) },
        onSelectAll = { viewModel.selectAllShieldOptions() },
        onApply = { options -> viewModel.shieldExistingVideo(shieldItem, options) },
        onDismiss = { viewModel.closeShieldModal() }
      )
    }

    val fileLocationDialogItem by viewModel.fileLocationDialogItem.collectAsState()
    fileLocationDialogItem?.let { locItem ->
      FileLocationDialog(
        item = locItem,
        isDark = isDark,
        onDismiss = { viewModel.dismissFileLocation() },
        onOpenInGallery = {
          openInPhoneGallery(context, locItem)
          viewModel.dismissFileLocation()
        }
      )
    }
  }
}


@Composable
private fun DownloadItemRow(
  item: DownloadItem,
  isDark: Boolean,
  onPlay: () -> Unit,
  onOpenInGallery: () -> Unit,
  onShowLocation: () -> Unit,
  onPause: () -> Unit,
  onResume: () -> Unit,
  onCancel: () -> Unit,
  onDelete: () -> Unit,
  onShield: () -> Unit,
  onShare: () -> Unit
) {
  val isActive = item.status == DownloadStatus.DOWNLOADING || item.status == DownloadStatus.PAUSED || item.status == DownloadStatus.PENDING

  GlassCard(
    isDark = isDark,
    shape = RoundedCornerShape(16.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Thumbnail or Media Icon
        Box(
          modifier = Modifier
            .size(width = 68.dp, height = 60.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF222533))
            .clickable(enabled = !isActive) { onPlay() },
          contentAlignment = Alignment.Center
        ) {
          if (item.thumbnailUrl.isNotBlank()) {
            AsyncImage(
              model = item.thumbnailUrl,
              contentDescription = "Thumbnail",
              contentScale = ContentScale.Crop,
              modifier = Modifier.fillMaxSize()
            )
          } else {
            Icon(
              imageVector = if (item.mediaType == MediaType.VIDEO) Icons.Default.Movie else Icons.Default.Audiotrack,
              contentDescription = "Icon",
              tint = VipAmberPrimary,
              modifier = Modifier.size(28.dp)
            )
          }

          if (!isActive) {
            Box(
              modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.65f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Play",
                tint = VipAmberPrimary,
                modifier = Modifier.size(20.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Title and Info
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = item.title,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Spacer(modifier = Modifier.height(3.dp))
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .background(VipAmberPrimary.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                .padding(horizontal = 5.dp, vertical = 1.dp)
            ) {
              Text(
                text = item.resolution.ifBlank { item.format.uppercase() },
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = VipAmberDark
              )
            }
            if (item.isShielded) {
              Spacer(modifier = Modifier.width(6.dp))
              Box(
                modifier = Modifier
                  .background(Color(0xFF2E7D32).copy(alpha = 0.25f), RoundedCornerShape(4.dp))
                  .border(0.5.dp, Color(0xFF4CAF50), RoundedCornerShape(4.dp))
                  .padding(horizontal = 5.dp, vertical = 1.dp)
              ) {
                Text(
                  text = "🛡️ FYP READY",
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF81C784)
                )
              }
            }
            Spacer(modifier = Modifier.width(6.dp))
            PlatformBrandIcon(platform = item.platform, size = 16.dp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = item.platform,
              fontSize = 11.sp,
              fontWeight = FontWeight.Medium,
              color = if (isDark) Color(0xFFCAD0E0) else Color(0xFF4B5563)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "•",
              fontSize = 11.sp,
              color = if (isDark) Color(0xFF9EA3B0) else Color(0xFF757575)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (item.totalBytes > 0) item.formattedSize else "${item.speedText}",
              fontSize = 11.sp,
              color = if (isDark) Color(0xFF9EA3B0) else Color(0xFF757575)
            )
          }

          // Gallery & Files indicator for completed items
          if (!isActive && item.status == DownloadStatus.COMPLETED) {
            Spacer(modifier = Modifier.height(3.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Saved in Gallery",
                tint = Color(0xFF00E676),
                modifier = Modifier.size(11.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "Saved to Gallery & Movies/VidMax",
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF00E676)
              )
            }
          }
        }

        // Action Buttons
        if (isActive) {
          if (item.status == DownloadStatus.DOWNLOADING) {
            IconButton(onClick = onPause) {
              Icon(
                imageVector = Icons.Default.Pause,
                contentDescription = "Pause",
                tint = VipAmberPrimary
              )
            }
          } else {
            IconButton(onClick = onResume) {
              Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Resume",
                tint = VipAmberPrimary
              )
            }
          }
          IconButton(onClick = onCancel) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Cancel",
              tint = Color(0xFFFF5252)
            )
          }
        } else {
          // Completed row actions: Gallery, Folder, VIP Play, Share, Shield, Delete
          IconButton(onClick = onOpenInGallery) {
            Icon(
              imageVector = Icons.Default.PhotoLibrary,
              contentDescription = "Open in Gallery",
              tint = VipAmberPrimary,
              modifier = Modifier.size(20.dp)
            )
          }
          IconButton(onClick = onShowLocation) {
            Icon(
              imageVector = Icons.Default.Folder,
              contentDescription = "File Location",
              tint = if (isDark) Color.White.copy(alpha = 0.85f) else Color.Black.copy(alpha = 0.75f),
              modifier = Modifier.size(20.dp)
            )
          }
          if (item.mediaType == MediaType.VIDEO && !item.isShielded) {
            IconButton(onClick = onShield) {
              Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Shield for FYP",
                tint = VipAmberPrimary,
                modifier = Modifier.size(20.dp)
              )
            }
          }
          IconButton(onClick = onPlay) {
            Icon(
              imageVector = Icons.Default.PlayArrow,
              contentDescription = "Play",
              tint = VipAmberPrimary
            )
          }
          IconButton(onClick = onShare) {
            Icon(
              imageVector = Icons.Default.Share,
              contentDescription = "Share",
              tint = if (isDark) Color.White.copy(alpha = 0.7f) else Color.Black.copy(alpha = 0.6f)
            )
          }
          IconButton(onClick = onDelete) {
            Icon(
              imageVector = Icons.Default.Delete,
              contentDescription = "Delete",
              tint = if (isDark) Color.White.copy(alpha = 0.5f) else Color.Black.copy(alpha = 0.4f)
            )
          }
        }
      }

      // Progress bar for active downloads
      if (isActive) {
        Spacer(modifier = Modifier.height(10.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = if (item.status == DownloadStatus.DOWNLOADING) "Downloading: ${item.speedText}" else "Paused",
            fontSize = 11.sp,
            color = VipAmberPrimary,
            fontWeight = FontWeight.Medium
          )
          Text(
            text = "${item.progressPercent}%",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
          )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
          progress = { item.progress },
          modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(3.dp)),
          color = VipAmberPrimary,
          trackColor = if (isDark) Color(0x335A6282) else Color(0xFFDDDDDD)
        )
      }
    }
  }
}

private fun shareMedia(context: Context, item: DownloadItem) {
  val path = item.localFilePath
  if (path != null && File(path).exists()) {
    try {
      val file = File(path)
      val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
      val mimeType = if (item.mediaType == MediaType.VIDEO) "video/*" else "audio/*"
      val intent = Intent(Intent.ACTION_SEND).apply {
        type = mimeType
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra(Intent.EXTRA_TEXT, "Downloaded via VidMax VIP: ${item.title}")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
      }
      context.startActivity(Intent.createChooser(intent, "Share via"))
    } catch (_: Exception) {
      val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, "Downloaded: ${item.title}\nLink: ${item.originalUrl}")
      }
      context.startActivity(Intent.createChooser(intent, "Share Link"))
    }
  } else {
    val intent = Intent(Intent.ACTION_SEND).apply {
      type = "text/plain"
      putExtra(Intent.EXTRA_TEXT, "Downloaded: ${item.title}\nLink: ${item.originalUrl}")
    }
    context.startActivity(Intent.createChooser(intent, "Share Link"))
  }
}

private fun openInPhoneGallery(context: Context, item: DownloadItem) {
  val path = item.localFilePath
  if (path != null && File(path).exists()) {
    try {
      val file = File(path)
      val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
      val mimeType = if (item.mediaType == MediaType.VIDEO) "video/*" else "audio/*"
      val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, mimeType)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
      }
      context.startActivity(Intent.createChooser(intent, "Open in Gallery or Player"))
    } catch (_: Exception) {
      android.widget.Toast.makeText(context, "Could not open video player", android.widget.Toast.LENGTH_SHORT).show()
    }
  } else {
    android.widget.Toast.makeText(context, "File not found on device", android.widget.Toast.LENGTH_SHORT).show()
  }
}

@Composable
private fun FileLocationDialog(
  item: DownloadItem,
  isDark: Boolean,
  onDismiss: () -> Unit,
  onOpenInGallery: () -> Unit
) {
  val context = LocalContext.current
  androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
    GlassCard(
      isDark = isDark,
      borderColor = VipAmberPrimary,
      shape = RoundedCornerShape(20.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Folder,
              contentDescription = "Folder",
              tint = VipAmberPrimary,
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "File & Gallery Location",
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onBackground
            )
          }
          IconButton(onClick = onDismiss, modifier = Modifier.size(26.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Close")
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = item.title,
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.onBackground,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(10.dp))

        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (isDark) Color(0x3310121C) else Color(0xFFEEEEEE))
            .border(1.dp, if (isDark) Color(0x33555D75) else Color(0xFFCCCCCC), RoundedCornerShape(10.dp))
            .padding(10.dp)
        ) {
          Column {
            Text(
              text = "📁 Device Storage Path:",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = VipAmberPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = item.localFilePath ?: "Movies/VidMax",
              fontSize = 12.sp,
              color = if (isDark) Color(0xFFE2E8F0) else Color(0xFF1E293B)
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Button(
            onClick = {
              val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
              val clip = android.content.ClipData.newPlainText("File Path", item.localFilePath ?: "")
              clipboard.setPrimaryClip(clip)
              android.widget.Toast.makeText(context, "Path copied to clipboard!", android.widget.Toast.LENGTH_SHORT).show()
            },
            colors = ButtonDefaults.buttonColors(containerColor = if (isDark) Color(0x33FFFFFF) else Color(0x22000000)),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.weight(1f)
          ) {
            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.width(6.dp))
            Text("Copy Path", fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground)
          }

          Button(
            onClick = onOpenInGallery,
            colors = ButtonDefaults.buttonColors(containerColor = VipAmberPrimary),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.weight(1.3f)
          ) {
            Icon(Icons.Default.PhotoLibrary, contentDescription = "Gallery", modifier = Modifier.size(16.dp), tint = Color.Black)
            Spacer(modifier = Modifier.width(6.dp))
            Text("Open Gallery", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
          onClick = {
            item.localFilePath?.let { p ->
              android.media.MediaScannerConnection.scanFile(
                context,
                arrayOf(p),
                arrayOf(if (item.mediaType == MediaType.VIDEO) "video/mp4" else "audio/mpeg")
              ) { _, _ -> }
              android.widget.Toast.makeText(context, "Gallery and file scan refreshed!", android.widget.Toast.LENGTH_SHORT).show()
            }
          },
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Icon(Icons.Default.Refresh, contentDescription = "Refresh", modifier = Modifier.size(16.dp), tint = VipAmberPrimary)
          Spacer(modifier = Modifier.width(6.dp))
          Text("Refresh Gallery & File Scan", fontSize = 11.sp, color = VipAmberPrimary)
        }
      }
    }
  }
}

