package com.example.ui.components

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Hd
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.model.ExtractedMediaInfo
import com.example.data.model.MediaType
import com.example.data.model.VideoQualityOption
import com.example.ui.theme.VipAmberDark
import com.example.ui.theme.VipAmberPrimary
import com.example.ui.theme.VipCoral
import com.example.ui.theme.VipGoldAccent

@Composable
fun QualitySelectModal(
  mediaInfo: ExtractedMediaInfo,
  selectedQuality: VideoQualityOption?,
  isDark: Boolean,
  isAntiCopyrightEnabled: Boolean,
  fypShieldOptions: com.example.data.protection.FypShieldOptions,
  onToggleShieldOption: (Int) -> Unit,
  onToggleAntiCopyright: (Boolean) -> Unit,
  onShowFypTips: () -> Unit,
  onQualitySelected: (VideoQualityOption) -> Unit,
  onConfirmDownload: () -> Unit,
  onDismiss: () -> Unit
) {
  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.95f)
        .clip(RoundedCornerShape(28.dp))
        .border(
          width = 1.dp,
          color = if (isDark) Color(0x66FFB300) else Color(0x44FF8F00),
          shape = RoundedCornerShape(28.dp)
        ),
      color = if (isDark) Color(0xFF131522) else Color(0xFFFFFFFF),
      tonalElevation = 8.dp
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(VipAmberPrimary, VipCoral))),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "VIP Quality",
                tint = Color.Black,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "VIP Media Downloader",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color.White else Color(0xFF1A1A1A)
              )
              Text(
                text = "Detected: ${mediaInfo.platform}",
                fontSize = 12.sp,
                color = VipAmberPrimary,
                fontWeight = FontWeight.Medium
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("close_quality_modal")
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = if (isDark) Color.White.copy(alpha = 0.7f) else Color.Black.copy(alpha = 0.6f)
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Thumbnail & Title Card
        GlassCard(
          isDark = isDark,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(width = 84.dp, height = 72.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.DarkGray)
            ) {
              AsyncImage(
                model = mediaInfo.thumbnailUrl,
                contentDescription = "Media Thumbnail",
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.Crop
              )
              // Platform badge overlay
              Box(
                modifier = Modifier
                  .align(Alignment.BottomStart)
                  .padding(4.dp)
                  .background(Color.Black.copy(alpha = 0.75f), RoundedCornerShape(4.dp))
                  .padding(horizontal = 4.dp, vertical = 2.dp)
              ) {
                Text(
                  text = mediaInfo.duration,
                  color = Color.White,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.SemiBold
                )
              }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = mediaInfo.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isDark) Color.White else Color(0xFF1E202B),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = mediaInfo.author,
                fontSize = 12.sp,
                color = if (isDark) Color(0xFF9EA3B0) else Color(0xFF757575)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = "Select Quality & Format",
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = if (isDark) Color(0xFFDCDFEA) else Color(0xFF333333)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Options List
        LazyColumn(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f, fill = false),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          // Video section header
          item {
            Text(
              text = "VIDEO (MP4)",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = VipAmberPrimary,
              letterSpacing = 1.sp,
              modifier = Modifier.padding(vertical = 2.dp)
            )
          }

          val videoQualities = mediaInfo.qualities.filter { it.mediaType == MediaType.VIDEO }
          items(videoQualities.size) { index ->
            val quality = videoQualities[index]
            val isSelected = selectedQuality == quality

            QualityRowItem(
              quality = quality,
              isSelected = isSelected,
              isDark = isDark,
              onClick = { onQualitySelected(quality) }
            )
          }

          // Audio section header
          item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "AUDIO (MP3)",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = VipCoral,
              letterSpacing = 1.sp,
              modifier = Modifier.padding(vertical = 2.dp)
            )
          }

          val audioQualities = mediaInfo.qualities.filter { it.mediaType == MediaType.AUDIO }
          items(audioQualities.size) { index ->
            val quality = audioQualities[index]
            val isSelected = selectedQuality == quality

            QualityRowItem(
              quality = quality,
              isSelected = isSelected,
              isDark = isDark,
              onClick = { onQualitySelected(quality) }
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // VIP Anti-Copyright & FYP Shield Toggle
        GlassCard(
          isDark = isDark,
          borderColor = if (isAntiCopyrightEnabled) VipAmberPrimary else (if (isDark) Color(0x335A6282) else Color(0xFFDDDDDD)),
          borderWidth = if (isAntiCopyrightEnabled) 1.5.dp else 1.dp,
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.weight(1f)
            ) {
              Box(
                modifier = Modifier
                  .size(34.dp)
                  .clip(CircleShape)
                  .background(if (isAntiCopyrightEnabled) VipAmberPrimary.copy(alpha = 0.2f) else Color(0x22888888)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Lock,
                  contentDescription = "Anti-Copyright",
                  tint = if (isAntiCopyrightEnabled) VipAmberPrimary else Color.Gray,
                  modifier = Modifier.size(18.dp)
                )
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = "FYP & Anti-Copyright Shield",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isAntiCopyrightEnabled) VipAmberPrimary else MaterialTheme.colorScheme.onBackground
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "💡 ৪টি রুলস",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = VipAmberPrimary,
                    modifier = Modifier
                      .background(VipAmberPrimary.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                      .clickable { onShowFypTips() }
                      .padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
                Text(
                  text = "Bypasses TikTok 'Not eligible for FYP' by scrambling file hash & metadata",
                  fontSize = 11.sp,
                  color = if (isDark) Color(0xFFA5ABB8) else Color(0xFF6B7280),
                  lineHeight = 14.sp
                )
              }
            }

            androidx.compose.material3.Switch(
              checked = isAntiCopyrightEnabled,
              onCheckedChange = onToggleAntiCopyright,
              colors = androidx.compose.material3.SwitchDefaults.colors(
                checkedThumbColor = Color.Black,
                checkedTrackColor = VipAmberPrimary,
                uncheckedThumbColor = Color.LightGray,
                uncheckedTrackColor = Color.DarkGray
              )
            )
          }

          if (isAntiCopyrightEnabled) {
            Spacer(modifier = Modifier.height(6.dp))
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 6.dp),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              MiniShieldOptionChip(
                label = "১. MD5 হ্যাশ",
                isActive = fypShieldOptions.md5HashScramble,
                onClick = { onToggleShieldOption(1) },
                isDark = isDark,
                modifier = Modifier.weight(1f)
              )
              MiniShieldOptionChip(
                label = "২. ফ্রেম ক্রপ",
                isActive = fypShieldOptions.microZoomCrop,
                onClick = { onToggleShieldOption(2) },
                isDark = isDark,
                modifier = Modifier.weight(1f)
              )
            }
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 2.dp),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              MiniShieldOptionChip(
                label = "৩. অডিও শিফট",
                isActive = fypShieldOptions.audioPitchShift,
                onClick = { onToggleShieldOption(3) },
                isDark = isDark,
                modifier = Modifier.weight(1f)
              )
              MiniShieldOptionChip(
                label = "৪. স্পিড ১.০২x",
                isActive = fypShieldOptions.microSpeedShift,
                onClick = { onToggleShieldOption(4) },
                isDark = isDark,
                modifier = Modifier.weight(1f)
              )
            }
            Spacer(modifier = Modifier.height(4.dp))
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Download Action Button
        Button(
          onClick = onConfirmDownload,
          enabled = selectedQuality != null,
          modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .testTag("confirm_download_button"),
          shape = RoundedCornerShape(16.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = VipAmberPrimary,
            contentColor = Color.Black
          )
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            Icon(
              imageVector = Icons.Default.Download,
              contentDescription = "Download Now",
              modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            val sizeSuffix = selectedQuality?.estimatedSize?.let { " ($it)" } ?: ""
            val formatLabel = selectedQuality?.format ?: "MP4"
            Text(
              text = "Download $formatLabel$sizeSuffix",
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }
  }
}

@Composable
private fun QualityRowItem(
  quality: VideoQualityOption,
  isSelected: Boolean,
  isDark: Boolean,
  onClick: () -> Unit
) {
  val borderColor = when {
    isSelected -> VipAmberPrimary
    isDark -> Color(0x335A6282)
    else -> Color(0xFFE0E0E0)
  }

  val bgModifier = when {
    isSelected -> Modifier.background(
      if (isDark) Color(0x33FFB300) else Color(0x22FFA000)
    )
    else -> Modifier.background(
      if (isDark) Color(0x1F22283C) else Color(0xFFF7F7F7)
    )
  }

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .then(bgModifier)
      .border(if (isSelected) 1.5.dp else 1.dp, borderColor, RoundedCornerShape(12.dp))
      .clickable(onClick = onClick)
      .padding(horizontal = 14.dp, vertical = 10.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Icon(
        imageVector = if (quality.mediaType == MediaType.VIDEO) Icons.Default.Movie else Icons.Default.Audiotrack,
        contentDescription = quality.format,
        tint = if (isSelected) VipAmberPrimary else (if (isDark) Color(0xFFA5ABC0) else Color(0xFF666666)),
        modifier = Modifier.size(20.dp)
      )
      Spacer(modifier = Modifier.width(10.dp))
      Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = quality.label,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) VipAmberPrimary else (if (isDark) Color.White else Color(0xFF222222))
          )
          if (quality.isVipBest) {
            Spacer(modifier = Modifier.width(6.dp))
            Box(
              modifier = Modifier
                .background(VipGoldAccent.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                .border(0.5.dp, VipGoldAccent, RoundedCornerShape(4.dp))
                .padding(horizontal = 5.dp, vertical = 1.dp)
            ) {
              Text(
                text = "VIP BEST",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = VipAmberDark
              )
            }
          }
        }
        Text(
          text = "${quality.format} • ${quality.bitrate}",
          fontSize = 11.sp,
          color = if (isDark) Color(0xFF888E9F) else Color(0xFF888888)
        )
      }
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
      Text(
        text = quality.estimatedSize,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = if (isSelected) VipAmberPrimary else (if (isDark) Color(0xFFB5BAC9) else Color(0xFF555555))
      )
      Spacer(modifier = Modifier.width(8.dp))
      Icon(
        imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.CheckCircle,
        contentDescription = "Selected",
        tint = if (isSelected) VipAmberPrimary else Color.Transparent,
        modifier = Modifier.size(18.dp)
      )
    }
  }
}

@Composable
private fun MiniShieldOptionChip(
  label: String,
  isActive: Boolean,
  onClick: () -> Unit,
  isDark: Boolean,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(8.dp))
      .background(if (isActive) VipAmberPrimary.copy(alpha = 0.2f) else (if (isDark) Color(0x221C2030) else Color(0xFFEEEEEE)))
      .border(
        width = 0.8.dp,
        color = if (isActive) VipAmberPrimary else (if (isDark) Color(0x33555D75) else Color(0xFFCCCCCC)),
        shape = RoundedCornerShape(8.dp)
      )
      .clickable(onClick = onClick)
      .padding(horizontal = 6.dp, vertical = 5.dp),
    contentAlignment = Alignment.Center
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Box(
        modifier = Modifier
          .size(6.dp)
          .clip(CircleShape)
          .background(if (isActive) VipAmberPrimary else Color.Gray)
      )
      Spacer(modifier = Modifier.width(4.dp))
      Text(
        text = label,
        fontSize = 10.sp,
        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
        color = if (isActive) VipAmberPrimary else MaterialTheme.colorScheme.onBackground
      )
    }
  }
}
