package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Hd
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.model.ExtractedMediaInfo
import com.example.data.model.MediaType
import com.example.data.model.VideoQualityOption
import com.example.ui.theme.VipAmberDark
import com.example.ui.theme.VipAmberPrimary
import com.example.ui.theme.VipCoral
import com.example.ui.theme.VipCyanAccent
import com.example.ui.theme.VipElectricCyan
import com.example.ui.theme.VipGoldAccent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()
  var isCaptionCopied by remember { mutableStateOf(false) }
  val availableQualities = remember(mediaInfo.qualities) {
    mediaInfo.qualities
      .filter { it.downloadUrl.startsWith("http", ignoreCase = true) }
      .distinctBy { it.mediaType to it.downloadUrl }
  }
  val recommendedQuality = availableQualities.firstOrNull {
    it.mediaType == MediaType.VIDEO && it.isVipBest
  } ?: availableQualities.firstOrNull {
    it.mediaType == MediaType.VIDEO
  } ?: availableQualities.firstOrNull()

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.95f)
        .fillMaxHeight(0.92f)
        .clip(RoundedCornerShape(26.dp))
        .border(
          width = 1.4.dp,
          brush = Brush.linearGradient(
            listOf(Color(0xEEFFFFFF), Color(0x4034D399), Color(0x80FFFFFF), Color(0x15000000))
          ),
          shape = RoundedCornerShape(26.dp)
        ),
      color = if (isDark) Color(0xF4071813) else Color(0xF6FFFFFF),
      tonalElevation = 12.dp
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp)
      ) {
        // Modal Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(Color(0xFF10B981), Color(0xFF34D399)))),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Download,
                contentDescription = "SnapVid Downloader",
                tint = Color.Black,
                modifier = Modifier.size(22.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Media Details & Quality",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color.White else Color(0xFF0F172A),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
              Text(
                text = "Source: ${mediaInfo.platform}",
                fontSize = 11.sp,
                color = Color(0xFF34D399),
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

        Spacer(modifier = Modifier.height(10.dp))

        // DEDICATED TITLE & CAPTION BOX WITH 1-TAP COPY (PREMIUM SCREEN CARD)
        PremiumGlassCard(
          isDark = isDark,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(18.dp),
          borderWidth = 1.2.dp
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp)
          ) {
            // Media Preview Row
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(width = 84.dp, height = 72.dp)
                  .clip(RoundedCornerShape(12.dp))
                  .background(Color(0xFF1E2338))
              ) {
                AsyncImage(
                  model = mediaInfo.thumbnailUrl,
                  contentDescription = "Media Thumbnail",
                  modifier = Modifier.fillMaxWidth(),
                  contentScale = ContentScale.Crop
                )
                // Duration Overlay
                Box(
                  modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(4.dp)
                    .background(Color.Black.copy(alpha = 0.8f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                  Text(
                    text = mediaInfo.duration,
                    color = Color.White,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
              }

              Spacer(modifier = Modifier.width(12.dp))

              // Creator and Platform Pills
              Column(modifier = Modifier.weight(1f)) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                  PlatformBrandBadge(
                    platform = mediaInfo.platform,
                    isDark = isDark
                  )
                  Box(
                    modifier = Modifier
                      .background(VipElectricCyan.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                      .padding(horizontal = 6.dp, vertical = 2.dp)
                  ) {
                    Text(
                      text = mediaInfo.author,
                      fontSize = 11.sp,
                      color = VipElectricCyan,
                      fontWeight = FontWeight.SemiBold
                    )
                  }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = "✓ Original Audio & High Fidelity",
                  fontSize = 11.sp,
                  color = Color(0xFF10B981),
                  fontWeight = FontWeight.Medium
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Dedicated Full Title / Caption Display Box
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(if (isDark) Color(0x33090B14) else Color(0xFFF1F5F9))
                .border(1.dp, if (isDark) Color(0x333B4261) else Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                .padding(10.dp)
            ) {
              Column {
                Text(
                  text = "Original Video Title & Caption:",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF10B981)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = mediaInfo.title,
                  fontSize = 13.sp,
                  color = if (isDark) Color(0xFFF1F5F9) else Color(0xFF1E293B),
                  lineHeight = 18.sp,
                  maxLines = 4,
                  overflow = TextOverflow.Ellipsis
                )
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Copy Title & Caption 1-Tap Action Button
            Button(
              onClick = {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Video Caption", mediaInfo.title)
                clipboard.setPrimaryClip(clip)
                isCaptionCopied = true
                Toast.makeText(context, "Video caption copied to clipboard! 📋", Toast.LENGTH_SHORT).show()
                coroutineScope.launch {
                  delay(2500)
                  isCaptionCopied = false
                }
              },
              colors = ButtonDefaults.buttonColors(
                containerColor = if (isCaptionCopied) Color(0xFF10B981) else Color(0x3510B981),
                contentColor = if (isCaptionCopied) Color.Black else Color(0xFF10B981)
              ),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.fillMaxWidth().height(38.dp)
            ) {
              Icon(
                imageVector = if (isCaptionCopied) Icons.Default.CheckCircle else Icons.Default.ContentCopy,
                contentDescription = "Copy Caption",
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = if (isCaptionCopied) "✓ Caption Copied!" else "📋 Copy Title & Caption",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // INTERACTIVE QUALITY SELECTION
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Select Quality:",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = if (isDark) Color.White else Color(0xFF0F172A)
          )
          Text(
            text = if (selectedQuality?.isVipBest == true) "Best available selected" else "Choose below",
            fontSize = 11.sp,
            color = Color(0xFF10B981),
            fontWeight = FontWeight.SemiBold
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Quality Options List (Premium Screen Cards)
        LazyColumn(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f, fill = true),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          if (availableQualities.isEmpty()) {
            item {
              Text(
                text = "No downloadable quality was found for this link.",
                fontSize = 12.sp,
                color = if (isDark) Color(0xFFA5ABB8) else Color(0xFF6B7280),
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 12.dp)
              )
            }
          } else {
            items(availableQualities.size) { index ->
              val quality = availableQualities[index]
              val isSelected = selectedQuality == quality

              ModernQualityCard(
                quality = quality,
                isSelected = isSelected,
                isRecommended = quality == recommendedQuality,
                isDark = isDark,
                onClick = { onQualitySelected(quality) }
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // VIP Anti-Copyright & FYP Shield Toggle (Premium Screen Card)
        PremiumGlassCard(
          isDark = isDark,
          isSelected = isAntiCopyrightEnabled,
          shape = RoundedCornerShape(16.dp),
          borderWidth = if (isAntiCopyrightEnabled) 1.5.dp else 1.dp,
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.weight(1f)
            ) {
              Box(
                modifier = Modifier
                  .size(28.dp)
                  .clip(CircleShape)
                  .background(if (isAntiCopyrightEnabled) Color(0x3510B981) else Color(0x22888888)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Lock,
                  contentDescription = "Anti-Copyright",
                  tint = if (isAntiCopyrightEnabled) Color(0xFF10B981) else Color.Gray,
                  modifier = Modifier.size(16.dp)
                )
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = "FYP & Anti-Copyright Shield",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isAntiCopyrightEnabled) Color(0xFF10B981) else MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "Tips",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF10B981),
                    modifier = Modifier
                      .background(Color(0x3010B981), RoundedCornerShape(4.dp))
                      .clickable { onShowFypTips() }
                      .padding(horizontal = 5.dp, vertical = 1.dp)
                  )
                }
                Text(
                  text = "Optional • Toggle on or off",
                  fontSize = 9.sp,
                  color = if (isDark) Color(0xFFA5ABB8) else Color(0xFF6B7280),
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
              }
            }

            Switch(
              checked = isAntiCopyrightEnabled,
              onCheckedChange = onToggleAntiCopyright,
              colors = SwitchDefaults.colors(
                checkedThumbColor = Color.Black,
                checkedTrackColor = Color(0xFF10B981),
                uncheckedThumbColor = Color.LightGray,
                uncheckedTrackColor = Color.DarkGray
              )
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Main Confirm Download Button (3D Embossed Emerald Glass Button)
        val currentQuality = selectedQuality?.takeIf { it.downloadUrl.startsWith("http", ignoreCase = true) }
        val currentQualityLabel = currentQuality?.resolution?.ifEmpty { currentQuality.label } ?: "Choose quality"
        val currentFormat = selectedQuality?.format?.uppercase() ?: "MP4"
        val currentSize = selectedQuality?.estimatedSize?.let { " • $it" } ?: ""

        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
              Brush.horizontalGradient(
                listOf(Color(0xFF10B981), Color(0xFF34D399), Color(0xFF059669))
              )
            )
            .border(
              width = 1.2.dp,
              brush = Brush.linearGradient(
                listOf(Color(0xDDFFFFFF), Color(0x4034D399), Color(0x80FFFFFF))
              ),
              shape = RoundedCornerShape(16.dp)
            )
            .clickable(enabled = currentQuality != null, onClick = onConfirmDownload)
            .testTag("confirm_download_button"),
          contentAlignment = Alignment.Center
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            Icon(
              imageVector = Icons.Default.Download,
              contentDescription = "Download Now",
              tint = Color.Black,
              modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = if (currentQuality != null) "Download • $currentQualityLabel • $currentFormat$currentSize" else "No quality available",
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = Color.Black
            )
          }
        }
      }
    }
  }
}

@Composable
private fun ModernQualityCard(
  quality: VideoQualityOption,
  isSelected: Boolean,
  isRecommended: Boolean,
  isDark: Boolean,
  onClick: () -> Unit
) {
  val isAudio = quality.mediaType == MediaType.AUDIO

  PremiumGlassCard(
    isDark = isDark,
    isSelected = isSelected,
    shape = RoundedCornerShape(16.dp),
    borderWidth = if (isSelected) 1.5.dp else 1.dp,
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 11.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(
              if (isSelected) Color(0xFF10B981).copy(alpha = 0.25f)
              else (if (isDark) Color(0x22FFFFFF) else Color(0x20000000))
            )
            .border(
              0.8.dp,
              if (isSelected) Color(0xFF34D399) else Color(0x30FFFFFF),
              CircleShape
            ),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = if (isAudio) Icons.Default.Audiotrack else Icons.Default.Movie,
            contentDescription = quality.format,
            tint = if (isSelected) Color(0xFF34D399) else (if (isDark) Color.White.copy(alpha = 0.8f) else Color.DarkGray),
            modifier = Modifier.size(18.dp)
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = quality.label,
              fontSize = 13.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
              color = if (isSelected) Color(0xFF10B981) else (if (isDark) Color.White else Color(0xFF0F172A))
            )
            if (isRecommended) {
              Spacer(modifier = Modifier.width(6.dp))
              Box(
                modifier = Modifier
                  .background(Color(0xFF10B981), RoundedCornerShape(4.dp))
                  .padding(horizontal = 5.dp, vertical = 1.5.dp)
              ) {
                Text(
                  text = "BEST AVAILABLE",
                  fontSize = 8.sp,
                  fontWeight = FontWeight.Black,
                  color = Color.Black
                )
              }
            }
          }
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "${quality.format} • ${quality.bitrate}",
            fontSize = 10.sp,
            color = if (isDark) Color(0xFFA7B5B0) else Color(0xFF6B7280)
          )
        }
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = quality.estimatedSize,
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          color = if (isSelected) Color(0xFF10B981) else (if (isDark) Color(0xFFB5BAC9) else Color(0xFF475569))
        )
        Spacer(modifier = Modifier.width(10.dp))
        Box(
          modifier = Modifier
            .size(20.dp)
            .clip(CircleShape)
            .border(
              width = if (isSelected) 2.dp else 1.dp,
              color = if (isSelected) Color(0xFF10B981) else Color(0x60FFFFFF),
              shape = CircleShape
            )
            .background(if (isSelected) Color(0xFF10B981) else Color.Transparent),
          contentAlignment = Alignment.Center
        ) {
          if (isSelected) {
            Icon(
              imageVector = Icons.Default.Check,
              contentDescription = "Selected",
              tint = Color.Black,
              modifier = Modifier.size(13.dp)
            )
          }
        }
      }
    }
  }
}

