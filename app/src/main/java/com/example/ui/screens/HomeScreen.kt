package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Hd
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.DownloadStatus
import com.example.data.model.MediaType
import com.example.data.model.VideoQualityOption
import com.example.ui.components.FypTipsDialog
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassmorphicBackground
import com.example.ui.components.PlatformBrandBadge
import com.example.ui.components.PlatformBrandIcon
import com.example.ui.components.QualitySelectModal
import com.example.ui.theme.VipEmeraldPrimary
import com.example.ui.theme.VipMintAccent
import com.example.ui.viewmodel.DownloaderViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
  viewModel: DownloaderViewModel,
  isDark: Boolean,
  onNavigateToTab: (Int) -> Unit
) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()
  val inputUrl by viewModel.inputUrl.collectAsState()
  val isExtracting by viewModel.isExtracting.collectAsState()
  val extractedMedia by viewModel.extractedMedia.collectAsState()
  val selectedQuality by viewModel.selectedQuality.collectAsState()
  val clipboardCandidate by viewModel.clipboardCandidate.collectAsState()
  val errorMessage by viewModel.errorMessage.collectAsState()
  val allDownloads by viewModel.allDownloads.collectAsState()

  var homeCaptionCopied by remember { mutableStateOf(false) }
  var preferredQualityTag by remember { mutableStateOf("4K Ultra HD") }

  val activeOrRecent = remember(allDownloads) {
    allDownloads.firstOrNull { it.status == DownloadStatus.DOWNLOADING || it.status == DownloadStatus.COMPLETED }
  }

  GlassmorphicBackground(isDark = isDark) {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      item {
        Spacer(modifier = Modifier.height(4.dp))

        // --- TOP HEADER (MATCHING "social media hub" & "Account Status" IN SCREENSHOT) ---
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // "social media hub" brand text as seen in screenshot
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(Color(0xFF10B981))
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "social media hub",
              fontSize = 21.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 0.3.sp,
              color = if (isDark) Color.White else Color(0xFF0F172A)
            )
          }

          // Right Header: Account Status badge with avatar and theme toggle
          Row(verticalAlignment = Alignment.CenterVertically) {
            Row(
              modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(if (isDark) Color(0x35122E23) else Color(0x30FFFFFF))
                .border(
                  1.dp,
                  Brush.linearGradient(listOf(Color(0x8034D399), Color(0x20FFFFFF))),
                  RoundedCornerShape(20.dp)
                )
                .padding(horizontal = 8.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              BeardedAvatar(size = 28.dp)
              Spacer(modifier = Modifier.width(6.dp))
              Column {
                Text(
                  text = "Account Status",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = if (isDark) Color(0xFFA7B5B0) else Color(0xFF475569)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Box(
                    modifier = Modifier
                      .size(6.dp)
                      .clip(CircleShape)
                      .background(Color(0xFF10B981))
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "VIP Active",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF10B981)
                  )
                }
              }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Glassmorphic Theme Switcher
            IconButton(
              onClick = { viewModel.toggleTheme() },
              modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(if (isDark) Color(0x30133226) else Color(0x40FFFFFF))
                .border(
                  1.dp,
                  Brush.linearGradient(listOf(Color(0x90FFFFFF), Color(0x20FFFFFF))),
                  CircleShape
                )
                .testTag("theme_toggle_button")
            ) {
              Icon(
                imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                contentDescription = "Toggle Theme",
                tint = if (isDark) Color(0xFF34D399) else Color(0xFF047857),
                modifier = Modifier.size(18.dp)
              )
            }
          }
        }
      }

      // --- HERO 3D CIRCULAR GLASS HUB (EXACT REPLICA OF USER ATTACHED SCREENSHOT) ---
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
          contentAlignment = Alignment.Center
        ) {
          // Circular 3D Frosted Glass Disc
          Box(
            modifier = Modifier
              .size(200.dp)
              .clip(CircleShape)
              .background(
                Brush.radialGradient(
                  colors = listOf(
                    Color(0x4034D399),
                    Color(0x35143328),
                    Color(0x500B221B)
                  )
                )
              )
              .border(
                width = 2.dp,
                brush = Brush.linearGradient(
                  colors = listOf(
                    Color(0xDDFFFFFF),
                    Color(0x3034D399),
                    Color(0x80FFFFFF),
                    Color(0x20000000)
                  )
                ),
                shape = CircleShape
              ),
            contentAlignment = Alignment.Center
          ) {
            // Internal 3D Traffic Light Beads, Download Arrow, and Social Logos
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.SpaceBetween,
              modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 12.dp, horizontal = 14.dp)
            ) {
              // 1. Top Traffic Light Beads: Red, Yellow, Green (As seen in Screenshot)
              Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                // Red Bead
                TrafficLightBead(Color(0xFFEF4444), Color(0xFFFCA5A5))
                // Yellow Bead
                TrafficLightBead(Color(0xFFF59E0B), Color(0xFFFDE68A))
                // Green Bead
                TrafficLightBead(Color(0xFF10B981), Color(0xFFA7F3D0))
              }

              // 2. Central 3D Embossed Green Download Icon (Arrow pointing down into open tray)
              Box(
                modifier = Modifier
                  .size(86.dp)
                  .clickable {
                    // Quick paste & extract on center tap
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = clipboard.primaryClip
                    if (clip != null && clip.itemCount > 0) {
                      val text = clip.getItemAt(0).text?.toString() ?: ""
                      if (text.startsWith("http")) {
                        viewModel.onUrlChange(text)
                        viewModel.extractMedia(text)
                        Toast.makeText(context, "লিংক সনাক্ত করা হয়েছে!", Toast.LENGTH_SHORT).show()
                      }
                    }
                  },
                contentAlignment = Alignment.Center
              ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                  val w = size.width
                  val h = size.height

                  // Outer soft green glow
                  drawCircle(
                    brush = Brush.radialGradient(
                      colors = listOf(Color(0x5010B981), Color(0x0010B981)),
                      center = Offset(w / 2f, h / 2f),
                      radius = w * 0.45f
                    )
                  )

                  // Arrow stem and head
                  val arrowPath = Path().apply {
                    val stemW = w * 0.16f
                    val topY = h * 0.12f
                    val stemBottomY = h * 0.42f
                    val barbW = w * 0.36f
                    val tipY = h * 0.58f

                    moveTo(w / 2f - stemW / 2f, topY)
                    lineTo(w / 2f + stemW / 2f, topY)
                    lineTo(w / 2f + stemW / 2f, stemBottomY)
                    lineTo(w / 2f + barbW / 2f, stemBottomY)
                    lineTo(w / 2f, tipY)
                    lineTo(w / 2f - barbW / 2f, stemBottomY)
                    lineTo(w / 2f - stemW / 2f, stemBottomY)
                    close()
                  }

                  // Tray (U-bracket bottom)
                  val trayPath = Path().apply {
                    val trayLeft = w * 0.20f
                    val trayRight = w * 0.80f
                    val trayTop = h * 0.52f
                    val trayBottom = h * 0.72f
                    val cornerR = w * 0.08f

                    moveTo(trayLeft, trayTop)
                    lineTo(trayLeft, trayBottom - cornerR)
                    quadraticTo(trayLeft, trayBottom, trayLeft + cornerR, trayBottom)
                    lineTo(trayRight - cornerR, trayBottom)
                    quadraticTo(trayRight, trayBottom, trayRight, trayBottom - cornerR)
                    lineTo(trayRight, trayTop)
                  }

                  // Draw 3D Embossed Arrow
                  drawPath(
                    path = arrowPath,
                    brush = Brush.verticalGradient(
                      colors = listOf(Color(0xFF34D399), Color(0xFF10B981), Color(0xFF047857))
                    ),
                    style = Fill
                  )
                  drawPath(
                    path = arrowPath,
                    brush = Brush.verticalGradient(
                      colors = listOf(Color(0xDDFFFFFF), Color(0x30FFFFFF))
                    ),
                    style = Stroke(width = 2.5f)
                  )

                  // Draw 3D Embossed Tray
                  drawPath(
                    path = trayPath,
                    brush = Brush.verticalGradient(
                      colors = listOf(Color(0xFF34D399), Color(0xFF10B981), Color(0xFF047857))
                    ),
                    style = Stroke(width = w * 0.13f, cap = StrokeCap.Round)
                  )
                  drawPath(
                    path = trayPath,
                    brush = Brush.verticalGradient(
                      colors = listOf(Color(0xDDFFFFFF), Color(0x4034D399))
                    ),
                    style = Stroke(width = 2f, cap = StrokeCap.Round)
                  )
                }
              }

              // 3. Bottom Row of 5 Social Icons (Facebook, YouTube, TikTok, Instagram, Pinterest)
              Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                PlatformIconCircle("Facebook", Color(0xFF1877F2), "f") {
                  val url = "https://facebook.com/watch/?v=snapvid_sample"
                  viewModel.onUrlChange(url)
                  viewModel.extractMedia(url)
                }
                PlatformIconCircle("YouTube", Color(0xFFFF0000), "▶") {
                  val url = "https://youtube.com/shorts/snapvid_sample"
                  viewModel.onUrlChange(url)
                  viewModel.extractMedia(url)
                }
                PlatformIconCircle("TikTok", Color(0xFF000000), "♫") {
                  val url = "https://tiktok.com/@creator/video/snapvid_sample"
                  viewModel.onUrlChange(url)
                  viewModel.extractMedia(url)
                }
                PlatformIconCircle("Instagram", Color(0xFFE1306C), "📷") {
                  val url = "https://instagram.com/reel/snapvid_sample"
                  viewModel.onUrlChange(url)
                  viewModel.extractMedia(url)
                }
                PlatformIconCircle("Pinterest", Color(0xFFBD081C), "P") {
                  val url = "https://pinterest.com/pin/snapvid_sample"
                  viewModel.onUrlChange(url)
                  viewModel.extractMedia(url)
                }
              }
            }
          }
        }
      }

      // --- 3 FLOATING FROSTED GLASS CARDS (EXACT MATCH FOR SCREENSHOT) ---
      item {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          // Floating Card 1: "Suggested content"
          GlassCard(
            isDark = isDark,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            borderWidth = 1.2.dp
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "Suggested content",
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isDark) Color.White else Color(0xFF0F172A)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = "Instagram",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF34D399)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "Most content mobile app store",
                    fontSize = 11.sp,
                    color = if (isDark) Color(0xFFA7B5B0) else Color(0xFF64748B)
                  )
                }
              }

              // Row of colorful platform chips
              Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                PlatformBrandIcon("Facebook", size = 20.dp)
                PlatformBrandIcon("YouTube", size = 20.dp)
                PlatformBrandIcon("TikTok", size = 20.dp)
                PlatformBrandIcon("Instagram", size = 20.dp)
              }
            }
          }

          // Floating Card 2: "Recent downloads" with dynamic progress bar
          GlassCard(
            isDark = isDark,
            modifier = Modifier
              .fillMaxWidth()
              .clickable { onNavigateToTab(2) },
            shape = RoundedCornerShape(20.dp),
            borderWidth = 1.2.dp
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Box(
                    modifier = Modifier
                      .size(34.dp)
                      .clip(CircleShape)
                      .background(Color(0x3510B981)),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(
                      imageVector = Icons.Default.Download,
                      contentDescription = "Recent Downloads",
                      tint = Color(0xFF10B981),
                      modifier = Modifier.size(18.dp)
                    )
                  }
                  Spacer(modifier = Modifier.width(10.dp))
                  Column {
                    Text(
                      text = "Recent downloads",
                      fontSize = 13.sp,
                      fontWeight = FontWeight.Bold,
                      color = if (isDark) Color.White else Color(0xFF0F172A)
                    )
                    Text(
                      text = activeOrRecent?.title?.take(30) ?: "Download status...",
                      fontSize = 11.sp,
                      color = if (isDark) Color(0xFFA7B5B0) else Color(0xFF64748B)
                    )
                  }
                }

                // Chevron to open Downloads
                Box(
                  modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color(0x2234D399)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "View all",
                    tint = Color(0xFF34D399),
                    modifier = Modifier.size(16.dp)
                  )
                }
              }

              Spacer(modifier = Modifier.height(10.dp))

              // Glowing green progress bar matching screenshot
              val progressValue = activeOrRecent?.progress ?: 0.78f
              LinearProgressIndicator(
                progress = { progressValue },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(6.dp)
                  .clip(RoundedCornerShape(3.dp)),
                color = Color(0xFF10B981),
                trackColor = Color(0x3010B981),
              )

              Spacer(modifier = Modifier.height(4.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(
                  text = "Progress for ${activeOrRecent?.formattedSize ?: "1262 GBB"}",
                  fontSize = 10.sp,
                  color = if (isDark) Color(0xFFA7B5B0) else Color(0xFF64748B)
                )
                Text(
                  text = "${(progressValue * 100).toInt()}% • 10x Turbo",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF10B981)
                )
              }
            }
          }
        }
      }

      // --- USER REQUESTED FEATURE: MAXIMUM QUALITY CAPABILITIES & LIMITS CARD ---
      item {
        GlassCard(
          isDark = isDark,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(22.dp),
          borderColor = Color(0x6034D399),
          borderWidth = 1.3.dp
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0x3510B981)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.HighQuality,
                    contentDescription = "Highest Quality",
                    tint = Color(0xFF34D399),
                    modifier = Modifier.size(18.dp)
                  )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text(
                    text = "ভিডিও হায়েস্ট ডাউনলোড কোয়ালিটি",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.White else Color(0xFF0F172A)
                  )
                  Text(
                    text = "Maximum Supported Video & Audio Limits",
                    fontSize = 10.sp,
                    color = Color(0xFF34D399)
                  )
                }
              }

              // Glowing 4K Badge
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(
                    Brush.horizontalGradient(
                      listOf(Color(0xFF10B981), Color(0xFF00F2FE))
                    )
                  )
                  .padding(horizontal = 8.dp, vertical = 3.dp)
              ) {
                Text(
                  text = "MAX: 4K 60FPS",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Black,
                  color = Color.Black
                )
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Limits & Specifications Matrix
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              QualityStatBox(
                title = "সর্বোচ্চ রেজোলিউশন",
                value = "4K UHD (2160p)",
                desc = "60 FPS Crystal HD",
                modifier = Modifier.weight(1f),
                isDark = isDark
              )
              QualityStatBox(
                title = "অডিও কোয়ালিটি",
                value = "320 kbps",
                desc = "Lossless Studio",
                modifier = Modifier.weight(1f),
                isDark = isDark
              )
              QualityStatBox(
                title = "ডাউনলোড লিমিট",
                value = "আনলিমিটেড",
                desc = "10x Turbo Engine",
                modifier = Modifier.weight(1f),
                isDark = isDark
              )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Preferred Resolution Selection Chips
            Text(
              text = "ডিফল্ট পছন্দের কোয়ালিটি নির্বাচন করুন:",
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              color = if (isDark) Color(0xFFA7B5B0) else Color(0xFF475569)
            )

            Spacer(modifier = Modifier.height(6.dp))

            val qualityChips = listOf("4K Ultra HD", "2K Quad HD", "1080p Full HD", "720p HD", "MP3 320k")
            LazyRow(
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              items(qualityChips) { chip ->
                val isSelected = preferredQualityTag == chip
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                      if (isSelected) Color(0xFF10B981)
                      else (if (isDark) Color(0x3015382A) else Color(0x20000000))
                    )
                    .border(
                      1.dp,
                      if (isSelected) Color(0xFF34D399) else Color(0x3034D399),
                      RoundedCornerShape(10.dp)
                    )
                    .clickable {
                      preferredQualityTag = chip
                      Toast.makeText(context, "$chip কোয়ালিটি নির্বাচিত হয়েছে", Toast.LENGTH_SHORT).show()
                    }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                  Text(
                    text = chip,
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) Color.Black else (if (isDark) Color.White else Color(0xFF0F172A))
                  )
                }
              }
            }
          }
        }
      }

      // --- FROSTED GLASS URL INPUT CARD ---
      item {
        GlassCard(
          isDark = isDark,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(22.dp),
          borderBrush = if (inputUrl.isNotBlank()) {
            Brush.linearGradient(listOf(Color(0xFF10B981), Color(0xFF00F2FE)))
          } else null,
          borderWidth = 1.3.dp
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "ভিডিও লিংক পেস্ট করুন",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color.White else Color(0xFF0F172A)
              )
              Text(
                text = "4K Turbo Downloader",
                fontSize = 11.sp,
                color = Color(0xFF10B981),
                fontWeight = FontWeight.Bold
              )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
              value = inputUrl,
              onValueChange = { viewModel.onUrlChange(it) },
              placeholder = {
                Text(
                  text = "ফেসবুক, ইউটিউব, টিকটক, ইনস্টাগ্রাম লিংক পেস্ট করুন...",
                  fontSize = 12.sp,
                  color = if (isDark) Color(0xFF7E948A) else Color(0xFF94A3B8)
                )
              },
              singleLine = true,
              leadingIcon = {
                Icon(
                  imageVector = Icons.Default.Link,
                  contentDescription = "URL",
                  tint = Color(0xFF10B981)
                )
              },
              trailingIcon = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  if (inputUrl.isNotBlank()) {
                    IconButton(onClick = { viewModel.onUrlChange("") }) {
                      Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear",
                        tint = if (isDark) Color.White.copy(alpha = 0.6f) else Color.Gray
                      )
                    }
                  }
                  IconButton(
                    onClick = {
                      val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                      val clip = clipboard.primaryClip
                      if (clip != null && clip.itemCount > 0) {
                        val text = clip.getItemAt(0).text?.toString() ?: ""
                        viewModel.onUrlChange(text)
                      }
                    },
                    modifier = Modifier.testTag("paste_button")
                  ) {
                    Icon(
                      imageVector = Icons.Default.ContentPaste,
                      contentDescription = "Paste",
                      tint = Color(0xFF10B981)
                    )
                  }
                }
              },
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF10B981),
                unfocusedBorderColor = if (isDark) Color(0x3534D399) else Color(0x40000000),
                focusedContainerColor = if (isDark) Color(0x280E271F) else Color(0x60FFFFFF),
                unfocusedContainerColor = if (isDark) Color(0x180E271F) else Color(0x40FFFFFF)
              ),
              shape = RoundedCornerShape(16.dp),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("url_text_input")
            )

            if (errorMessage != null) {
              Spacer(modifier = Modifier.height(10.dp))
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(12.dp))
                  .background(Color(0x22FF5252))
                  .border(1.dp, Color(0x66FF5252), RoundedCornerShape(12.dp))
                  .padding(12.dp)
              ) {
                Column {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      imageVector = Icons.Default.Warning,
                      contentDescription = "Error",
                      tint = Color(0xFFFF5252),
                      modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                      text = errorMessage ?: "",
                      color = Color(0xFFFF8A80),
                      fontSize = 12.sp,
                      lineHeight = 16.sp
                    )
                  }
                  Spacer(modifier = Modifier.height(8.dp))
                  Button(
                    onClick = { viewModel.openUrlInBrowser(inputUrl) },
                    colors = ButtonDefaults.buttonColors(
                      containerColor = Color(0xFF10B981),
                      contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                      .fillMaxWidth()
                      .height(40.dp)
                  ) {
                    Text(
                      text = "ব্রাউজারে ওপেন করে আসল ভিডিও নিন 🚀",
                      fontSize = 13.sp,
                      fontWeight = FontWeight.Bold
                    )
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Fetch & Inspect Button with Emerald Gradient
            Button(
              onClick = { viewModel.extractMedia() },
              enabled = !isExtracting,
              shape = RoundedCornerShape(16.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF10B981),
                contentColor = Color.Black
              ),
              modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("fetch_video_button")
            ) {
              if (isExtracting) {
                CircularProgressIndicator(
                  modifier = Modifier.size(24.dp),
                  color = Color.Black,
                  strokeWidth = 2.5.dp
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                  text = "Analyzing Highest 4K Stream...",
                  fontSize = 15.sp,
                  fontWeight = FontWeight.Bold
                )
              } else {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = "Download",
                    modifier = Modifier.size(22.dp)
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = "কোয়ালিটি দেখুন ও ডাউনলোড করুন ⚡",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
              }
            }
          }
        }
      }

      // --- INLINE VIDEO DETAILS & QUALITY SELECTOR ---
      extractedMedia?.let { media ->
        item {
          GlassCard(
            isDark = isDark,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            borderBrush = Brush.linearGradient(
              listOf(Color(0xFF10B981), Color(0xFF00F2FE), Color(0x60FFFFFF))
            ),
            borderWidth = 1.4.dp
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Box(
                    modifier = Modifier
                      .size(10.dp)
                      .clip(CircleShape)
                      .background(Color(0xFF10B981))
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "ভিডিও ডিটেলস ও কোয়ালিটি",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.White else Color(0xFF0F172A)
                  )
                }
                PlatformBrandBadge(
                  platform = media.platform,
                  isDark = isDark
                )
              }

              Spacer(modifier = Modifier.height(12.dp))

              // Video Thumbnail Preview
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .height(180.dp)
                  .clip(RoundedCornerShape(16.dp))
                  .border(
                    1.dp,
                    Brush.linearGradient(listOf(Color(0x80FFFFFF), Color(0x20FFFFFF))),
                    RoundedCornerShape(16.dp)
                  )
              ) {
                AsyncImage(
                  model = media.thumbnailUrl,
                  contentDescription = media.title,
                  modifier = Modifier.fillMaxSize(),
                  contentScale = ContentScale.Crop
                )
                // Duration & Max Badge
                Box(
                  modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.Black.copy(alpha = 0.75f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                  Text(
                    text = "${media.duration} • 4K MAX",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF34D399)
                  )
                }
              }

              Spacer(modifier = Modifier.height(12.dp))

              // Title and Author
              Text(
                text = media.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color.White else Color(0xFF0F172A),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "Author: ${media.author.ifEmpty { "Creator" }} • Duration: ${media.duration}",
                fontSize = 11.sp,
                color = if (isDark) Color(0xFFA7B5B0) else Color(0xFF6B7280)
              )

              Spacer(modifier = Modifier.height(12.dp))

              // 1-Tap Caption Copy Box
              GlassCard(
                isDark = isDark,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                borderWidth = 1.dp,
                borderColor = Color(0x3510B981)
              ) {
                Column(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                ) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Caption",
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(16.dp)
                      )
                      Spacer(modifier = Modifier.width(6.dp))
                      Text(
                        text = "ভিডিও টাইটেল ও ক্যাপশন",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF10B981)
                      )
                    }

                    Button(
                      onClick = {
                        val clipText = media.title
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Video Caption", clipText))
                        homeCaptionCopied = true
                        Toast.makeText(context, "ক্যাপশন কপি সফল হয়েছে! ✓", Toast.LENGTH_SHORT).show()
                        coroutineScope.launch {
                          delay(2500)
                          homeCaptionCopied = false
                        }
                      },
                      colors = ButtonDefaults.buttonColors(
                        containerColor = if (homeCaptionCopied) Color(0xFF10B981) else Color(0x3510B981),
                        contentColor = if (homeCaptionCopied) Color.Black else Color.White
                      ),
                      shape = RoundedCornerShape(8.dp),
                      contentPadding = ButtonDefaults.TextButtonContentPadding,
                      modifier = Modifier.height(32.dp)
                    ) {
                      Icon(
                        imageVector = if (homeCaptionCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                        contentDescription = "Copy",
                        modifier = Modifier.size(14.dp)
                      )
                      Spacer(modifier = Modifier.width(4.dp))
                      Text(
                        text = if (homeCaptionCopied) "কপিকৃত!" else "কপি করুন",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                      )
                    }
                  }

                  Spacer(modifier = Modifier.height(6.dp))

                  Text(
                    text = media.title,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    color = if (isDark) Color(0xFFD1D5DB) else Color(0xFF374151),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                  )
                }
              }

              Spacer(modifier = Modifier.height(14.dp))

              Text(
                text = "কোয়ালিটি সিলেক্ট করে ডাউনলোড করুন:",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color.White else Color(0xFF0F172A)
              )

              Spacer(modifier = Modifier.height(8.dp))

              // Quality options list with highest 4K marked
              Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                media.qualities.forEach { quality ->
                  val isSelected = selectedQuality == quality
                  val isAudio = quality.mediaType == MediaType.AUDIO
                  val isHighest = quality.isVipBest || quality.resolution.contains("4K")

                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .clip(RoundedCornerShape(12.dp))
                      .background(
                        if (isSelected) (if (isDark) Color(0x3510B981) else Color(0x2510B981))
                        else (if (isDark) Color(0x1AFFFFFF) else Color(0x20000000))
                      )
                      .border(
                        if (isSelected) 1.5.dp else 1.dp,
                        if (isSelected) Color(0xFF10B981) else (if (isDark) Color(0x25FFFFFF) else Color(0x20000000)),
                        RoundedCornerShape(12.dp)
                      )
                      .clickable { viewModel.selectQuality(quality) }
                      .padding(horizontal = 12.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Box(
                        modifier = Modifier
                          .size(30.dp)
                          .clip(CircleShape)
                          .background(if (isSelected) Color(0x4010B981) else Color(0x22888888)),
                        contentAlignment = Alignment.Center
                      ) {
                        Icon(
                          imageVector = if (isAudio) Icons.Default.Audiotrack else Icons.Default.Movie,
                          contentDescription = quality.format,
                          tint = if (isSelected) Color(0xFF10B981) else Color.Gray,
                          modifier = Modifier.size(16.dp)
                        )
                      }
                      Spacer(modifier = Modifier.width(10.dp))
                      Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                          Text(
                            text = quality.label,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color(0xFF10B981) else (if (isDark) Color.White else Color(0xFF0F172A))
                          )
                          if (isHighest) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                              modifier = Modifier
                                .background(Color(0xFF10B981), RoundedCornerShape(4.dp))
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                              Text(
                                text = "HIGHEST 4K",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black
                              )
                            }
                          }
                        }
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
                      Spacer(modifier = Modifier.width(8.dp))
                      Box(
                        modifier = Modifier
                          .size(18.dp)
                          .clip(CircleShape)
                          .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) Color(0xFF10B981) else Color.Gray,
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
                            modifier = Modifier.size(12.dp)
                          )
                        }
                      }
                    }
                  }
                }
              }

              Spacer(modifier = Modifier.height(14.dp))

              // Direct Download Button
              val curOpt = selectedQuality
              val currentQualityLabel = curOpt?.resolution?.ifEmpty { curOpt.label } ?: "4K"
              val currentFormat = curOpt?.format?.uppercase() ?: "MP4"

              Button(
                onClick = { viewModel.confirmDownload() },
                enabled = selectedQuality != null,
                modifier = Modifier
                  .fillMaxWidth()
                  .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                  containerColor = Color(0xFF10B981),
                  contentColor = Color.Black
                )
              ) {
                Icon(
                  imageVector = Icons.Default.Download,
                  contentDescription = "Download Now",
                  modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "ডাউনলোড শুরু করুন ($currentQualityLabel • $currentFormat)",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        }
      }

      item {
        Spacer(modifier = Modifier.height(80.dp))
      }
    }

    // Modal Quality Selection Popup
    extractedMedia?.let { media ->
      val isAntiCopyrightEnabled by viewModel.isAntiCopyrightEnabled.collectAsState()
      val fypShieldOptions by viewModel.fypShieldOptions.collectAsState()
      QualitySelectModal(
        mediaInfo = media,
        selectedQuality = selectedQuality,
        isDark = isDark,
        isAntiCopyrightEnabled = isAntiCopyrightEnabled,
        fypShieldOptions = fypShieldOptions,
        onToggleShieldOption = { viewModel.toggleShieldOption(it) },
        onToggleAntiCopyright = { viewModel.toggleAntiCopyright(it) },
        onShowFypTips = { viewModel.showFypTipsDialog() },
        onQualitySelected = { viewModel.selectQuality(it) },
        onConfirmDownload = { viewModel.confirmDownload() },
        onDismiss = { viewModel.dismissQualityModal() }
      )
    }

    val showFypTips by viewModel.showFypTips.collectAsState()
    if (showFypTips) {
      FypTipsDialog(
        isDark = isDark,
        onDismiss = { viewModel.dismissFypTipsDialog() }
      )
    }
  }
}

@Composable
private fun TrafficLightBead(baseColor: Color, highlightColor: Color) {
  Box(
    modifier = Modifier
      .size(12.dp)
      .clip(CircleShape)
      .background(
        Brush.radialGradient(
          colors = listOf(highlightColor, baseColor, baseColor.copy(alpha = 0.8f))
        )
      )
      .border(0.5.dp, Color(0x60FFFFFF), CircleShape)
  )
}

@Composable
private fun PlatformIconCircle(name: String, color: Color, symbol: String, onClick: () -> Unit) {
  Box(
    modifier = Modifier
      .size(26.dp)
      .clip(CircleShape)
      .background(color)
      .clickable(onClick = onClick),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = symbol,
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      color = Color.White
    )
  }
}

@Composable
private fun BeardedAvatar(size: Dp = 32.dp) {
  Box(
    modifier = Modifier
      .size(size)
      .clip(CircleShape)
      .background(Brush.linearGradient(listOf(Color(0xFF0F3B2C), Color(0xFF1A5A43))))
      .border(1.2.dp, Color(0xFF34D399), CircleShape),
    contentAlignment = Alignment.Center
  ) {
    Canvas(modifier = Modifier.size(size * 0.9f)) {
      val w = this.size.width
      val h = this.size.height
      // Face
      drawCircle(
        color = Color(0xFFFFD1A4),
        radius = w * 0.28f,
        center = Offset(w * 0.5f, h * 0.40f)
      )
      // Hair
      drawArc(
        color = Color(0xFF5D4037),
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = true,
        topLeft = Offset(w * 0.22f, h * 0.15f),
        size = androidx.compose.ui.geometry.Size(w * 0.56f, h * 0.45f)
      )
      // Beard
      drawArc(
        color = Color(0xFF5D4037),
        startAngle = 0f,
        sweepAngle = 180f,
        useCenter = true,
        topLeft = Offset(w * 0.28f, h * 0.35f),
        size = androidx.compose.ui.geometry.Size(w * 0.44f, h * 0.35f)
      )
      // Shirt
      drawArc(
        color = Color(0xFF10B981),
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = true,
        topLeft = Offset(w * 0.12f, h * 0.60f),
        size = androidx.compose.ui.geometry.Size(w * 0.76f, h * 0.65f)
      )
    }
  }
}

@Composable
private fun QualityStatBox(
  title: String,
  value: String,
  desc: String,
  modifier: Modifier = Modifier,
  isDark: Boolean = true
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(12.dp))
      .background(if (isDark) Color(0x35122E23) else Color(0x2510B981))
      .border(1.dp, Color(0x3034D399), RoundedCornerShape(12.dp))
      .padding(8.dp)
  ) {
    Column {
      Text(
        text = title,
        fontSize = 9.sp,
        color = if (isDark) Color(0xFFA7B5B0) else Color(0xFF475569)
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = value,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF34D399)
      )
      Text(
        text = desc,
        fontSize = 8.sp,
        color = if (isDark) Color(0xFF7E948A) else Color(0xFF64748B)
      )
    }
  }
}
