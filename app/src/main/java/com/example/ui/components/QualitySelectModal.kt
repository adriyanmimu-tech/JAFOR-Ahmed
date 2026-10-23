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

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.95f)
        .clip(RoundedCornerShape(26.dp))
        .border(
          width = 1.3.dp,
          brush = Brush.linearGradient(
            listOf(Color(0xFF00F2FE), Color(0xFF10B981), Color(0x60FFFFFF))
          ),
          shape = RoundedCornerShape(26.dp)
        ),
      color = if (isDark) Color(0xF209101D) else Color(0xF8FFFFFF),
      tonalElevation = 10.dp
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(18.dp)
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
                .background(Brush.linearGradient(listOf(Color(0xFF00F2FE), Color(0xFF10B981)))),
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
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "ভিডিও ডিটেলস ও কোয়ালিটি",
                  fontSize = 16.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isDark) Color.White else Color(0xFF0F172A)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                  modifier = Modifier
                    .background(Color(0xFF00F2FE), RoundedCornerShape(6.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                  Text(
                    text = "SNAPVID PRO",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black
                  )
                }
              }
              Text(
                text = "উৎস: ${mediaInfo.platform} • হাই স্পিড ইন্সপেক্টর",
                fontSize = 11.sp,
                color = VipElectricCyan,
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

        // DEDICATED TITLE & CAPTION BOX WITH 1-TAP COPY (ব্যবহারকারীর বিশেষ চাহিদা অনুযায়ী)
        GlassCard(
          isDark = isDark,
          modifier = Modifier.fillMaxWidth(),
          borderColor = VipElectricCyan.copy(alpha = 0.4f),
          borderWidth = 1.dp,
          shape = RoundedCornerShape(18.dp)
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
                  text = "✓ অরিজিনাল সাউন্ড ও ফুল কোয়ালিটি",
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
                  text = "ভিডিওর আসল টাইটেল ও ক্যাপশন:",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = VipAmberPrimary
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
                Toast.makeText(context, "ভিডিওর ক্যাপশন ক্লিপবোর্ডে কপি হয়েছে! 📋", Toast.LENGTH_SHORT).show()
                coroutineScope.launch {
                  delay(2500)
                  isCaptionCopied = false
                }
              },
              colors = ButtonDefaults.buttonColors(
                containerColor = if (isCaptionCopied) Color(0xFF10B981) else VipAmberPrimary.copy(alpha = 0.2f),
                contentColor = if (isCaptionCopied) Color.White else VipAmberPrimary
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
                text = if (isCaptionCopied) "✓ টাইটেল ও ক্যাপশন কপি হয়েছে!" else "📋 টাইটেল ও ক্যাপশন কপি করুন",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // INTERACTIVE QUALITY SELECTION (ব্যবহারকারী যাতে সহজে কোয়ালিটি বেছে নিতে পারে)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "কোয়ালিটি সিলেক্ট করুন:",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = if (isDark) Color.White else Color(0xFF0F172A)
          )
          Text(
            text = "ট্যাপ করে বেছে নিন",
            fontSize = 11.sp,
            color = VipAmberPrimary,
            fontWeight = FontWeight.SemiBold
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Quality Options List
        LazyColumn(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f, fill = false),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          items(mediaInfo.qualities.size) { index ->
            val quality = mediaInfo.qualities[index]
            val isSelected = selectedQuality == quality

            ModernQualityCard(
              quality = quality,
              isSelected = isSelected,
              isDark = isDark,
              onClick = { onQualitySelected(quality) }
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

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
              .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.weight(1f)
            ) {
              Box(
                modifier = Modifier
                  .size(32.dp)
                  .clip(CircleShape)
                  .background(if (isAntiCopyrightEnabled) VipAmberPrimary.copy(alpha = 0.2f) else Color(0x22888888)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Lock,
                  contentDescription = "Anti-Copyright",
                  tint = if (isAntiCopyrightEnabled) VipAmberPrimary else Color.Gray,
                  modifier = Modifier.size(16.dp)
                )
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = "FYP & Anti-Copyright 4x Shield",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isAntiCopyrightEnabled) VipAmberPrimary else MaterialTheme.colorScheme.onBackground
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "৪টি ট্রিক",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = VipAmberPrimary,
                    modifier = Modifier
                      .background(VipAmberPrimary.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                      .clickable { onShowFypTips() }
                      .padding(horizontal = 5.dp, vertical = 1.dp)
                  )
                }
                Text(
                  text = "ইউনিক ফাইল হ্যাশ ও অ্যান্টি-কপিরাইট প্রোটেকশন",
                  fontSize = 10.sp,
                  color = if (isDark) Color(0xFFA5ABB8) else Color(0xFF6B7280),
                  lineHeight = 13.sp
                )
              }
            }

            Switch(
              checked = isAntiCopyrightEnabled,
              onCheckedChange = onToggleAntiCopyright,
              colors = SwitchDefaults.colors(
                checkedThumbColor = Color.Black,
                checkedTrackColor = VipAmberPrimary,
                uncheckedThumbColor = Color.LightGray,
                uncheckedTrackColor = Color.DarkGray
              )
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Main Confirm Download Button with Selected Resolution Tag
        val currentQualityLabel = selectedQuality?.resolution?.ifEmpty { selectedQuality.label } ?: "HD"
        val currentFormat = selectedQuality?.format?.uppercase() ?: "MP4"
        val currentSize = selectedQuality?.estimatedSize?.let { " • $it" } ?: ""

        Button(
          onClick = onConfirmDownload,
          enabled = selectedQuality != null,
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("confirm_download_button"),
          shape = RoundedCornerShape(16.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF00F2FE),
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
            Text(
              text = "ডাউনলোড শুরু করুন ($currentQualityLabel • $currentFormat$currentSize)",
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold
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
  isDark: Boolean,
  onClick: () -> Unit
) {
  val isAudio = quality.mediaType == MediaType.AUDIO
  val cardBorder = if (isSelected) {
    Brush.linearGradient(listOf(Color(0xFF00F2FE), Color(0xFF10B981)))
  } else {
    Brush.linearGradient(
      if (isDark) listOf(Color(0x333B4261), Color(0x1F2A3048)) else listOf(Color(0xFFE2E8F0), Color(0xFFCBD5E1))
    )
  }

  val containerBg = when {
    isSelected -> if (isDark) Color(0x3300F2FE) else Color(0x2200F2FE)
    else -> if (isDark) Color(0x1F161A2B) else Color(0xFFF8FAFC)
  }

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .background(containerBg)
      .border(if (isSelected) 1.5.dp else 1.dp, cardBorder, RoundedCornerShape(14.dp))
      .clickable(onClick = onClick)
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
            if (isSelected) Color(0xFF00F2FE).copy(alpha = 0.25f)
            else (if (isDark) Color(0x2222273D) else Color(0xFFE2E8F0))
          ),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = if (isAudio) Icons.Default.Audiotrack else Icons.Default.Movie,
          contentDescription = quality.format,
          tint = if (isSelected) Color(0xFF00F2FE) else (if (isDark) Color(0xFFA5ABC0) else Color(0xFF64748B)),
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
            color = if (isSelected) Color(0xFF00F2FE) else (if (isDark) Color.White else Color(0xFF0F172A))
          )
          if (quality.isVipBest) {
            Spacer(modifier = Modifier.width(6.dp))
            Box(
              modifier = Modifier
                .background(Color(0xFF00F2FE).copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                .border(0.5.dp, Color(0xFF00F2FE), RoundedCornerShape(4.dp))
                .padding(horizontal = 5.dp, vertical = 1.dp)
            ) {
              Text(
                text = "BEST",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00F2FE)
              )
            }
          }
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = "${quality.format} • ${quality.bitrate}",
          fontSize = 11.sp,
          color = if (isDark) Color(0xFF888E9F) else Color(0xFF64748B)
        )
      }
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
      Text(
        text = quality.estimatedSize,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = if (isSelected) Color(0xFF00F2FE) else (if (isDark) Color(0xFFB5BAC9) else Color(0xFF475569))
      )
      Spacer(modifier = Modifier.width(10.dp))
      Box(
        modifier = Modifier
          .size(20.dp)
          .clip(CircleShape)
          .border(
            width = if (isSelected) 2.dp else 1.2.dp,
            color = if (isSelected) Color(0xFF00F2FE) else (if (isDark) Color(0x665A6282) else Color(0xFF94A3B8)),
            shape = CircleShape
          )
          .background(if (isSelected) Color(0xFF00F2FE) else Color.Transparent),
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
