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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Hd
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.ui.components.PremiumGlassCard
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

  LaunchedEffect(Unit) {
    try {
      val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
      val clip = clipboard.primaryClip
      if (clip != null && clip.itemCount > 0) {
        val text = clip.getItemAt(0).text?.toString()
        viewModel.checkClipboard(text)
      }
    } catch (_: Exception) {}
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
                        Toast.makeText(context, "Link detected!", Toast.LENGTH_SHORT).show()
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
        }
      }

      // --- SMART AUTO-CLIPBOARD DETECTION BANNER ---
      if (clipboardCandidate != null) {
        item {
          val candidateUrl = clipboardCandidate ?: ""
          val platformName = when {
            candidateUrl.contains("tiktok.com", true) -> "TikTok"
            candidateUrl.contains("youtube.com", true) || candidateUrl.contains("youtu.be", true) -> "YouTube"
            candidateUrl.contains("instagram.com", true) -> "Instagram"
            candidateUrl.contains("facebook.com", true) || candidateUrl.contains("fb.watch", true) -> "Facebook"
            candidateUrl.contains("twitter.com", true) || candidateUrl.contains("x.com", true) -> "Twitter"
            candidateUrl.contains("pinterest.com", true) || candidateUrl.contains("pin.it", true) -> "Pinterest"
            candidateUrl.contains("reddit.com", true) -> "Reddit"
            candidateUrl.contains("snapchat.com", true) -> "Snapchat"
            candidateUrl.contains("whatsapp", true) -> "WhatsApp"
            else -> "Social Media"
          }

          GlassCard(
            isDark = isDark,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            borderColor = Color(0xFF10B981),
            borderWidth = 1.5.dp
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(
                  modifier = Modifier.weight(1f),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  PlatformBrandIcon(platform = platformName, size = 26.dp)
                  Spacer(modifier = Modifier.width(10.dp))
                  Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Text(
                        text = "Link detected in clipboard!",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF10B981)
                      )
                      Spacer(modifier = Modifier.width(6.dp))
                      Box(
                        modifier = Modifier
                          .clip(RoundedCornerShape(4.dp))
                          .background(Color(0x3010B981))
                          .padding(horizontal = 6.dp, vertical = 2.dp)
                      ) {
                        Text(
                          text = platformName,
                          fontSize = 9.sp,
                          fontWeight = FontWeight.Bold,
                          color = Color(0xFF34D399)
                        )
                      }
                    }
                    Text(
                      text = candidateUrl,
                      fontSize = 11.sp,
                      color = if (isDark) Color(0xFFA7B5B0) else Color(0xFF64748B),
                      maxLines = 1,
                      overflow = TextOverflow.Ellipsis
                    )
                  }
                }

                IconButton(
                  onClick = { viewModel.dismissClipboardCandidate() },
                  modifier = Modifier.size(28.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss",
                    tint = if (isDark) Color(0x90FFFFFF) else Color(0x90000000),
                    modifier = Modifier.size(16.dp)
                  )
                }
              }

              Spacer(modifier = Modifier.height(10.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Button(
                  onClick = { viewModel.acceptClipboardCandidate() },
                  modifier = Modifier
                    .weight(1f)
                    .height(40.dp),
                  shape = RoundedCornerShape(10.dp),
                  colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF10B981),
                    contentColor = Color.Black
                  )
                ) {
                  Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "Download Now",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                  )
                }

                OutlinedButton(
                  onClick = { viewModel.dismissClipboardCandidate() },
                  modifier = Modifier.height(40.dp),
                  shape = RoundedCornerShape(10.dp),
                  border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isDark) Color(0x40FFFFFF) else Color(0x30000000)
                  )
                ) {
                  Text(
                    text = "Dismiss",
                    fontSize = 12.sp,
                    color = if (isDark) Color.White else Color.Black
                  )
                }
              }
            }
          }
        }
      }

      // --- FROSTED GLASS URL INPUT CARD ---
      // --- FROSTED GLASS URL INPUT CARD (PREMIUM SCREEN CARD) ---
      item {
        PremiumGlassCard(
          isDark = isDark,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(24.dp),
          borderWidth = 1.4.dp,
          isSelected = inputUrl.isNotBlank()
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(18.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(9.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF10B981))
                )
                Spacer(modifier = Modifier.width(7.dp))
                Text(
                  text = "Paste Video Link",
                  fontSize = 15.sp,
                  fontWeight = FontWeight.Bold,
                  letterSpacing = 0.2.sp,
                  color = if (isDark) Color.White else Color(0xFF0F172A)
                )
              }
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(if (isDark) Color(0x3510B981) else Color(0x2010B981))
                  .border(0.8.dp, Color(0xFF34D399), RoundedCornerShape(6.dp))
                  .padding(horizontal = 8.dp, vertical = 3.dp)
              ) {
                Text(
                  text = "Smart Video Downloader",
                  fontSize = 10.sp,
                  color = Color(0xFF10B981),
                  fontWeight = FontWeight.Bold
                )
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
              value = inputUrl,
              onValueChange = { viewModel.onUrlChange(it) },
              placeholder = {
                Text(
                  text = "Paste Facebook, YouTube, TikTok, Instagram link...",
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

            // Quick Feature Badges
            Spacer(modifier = Modifier.height(10.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              listOf("✓ Best available quality", "✓ Manual quality choice", "✓ Audio options", "✓ FYP toggle").forEach { feature ->
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isDark) Color(0x2034D399) else Color(0x1510B981))
                    .border(0.6.dp, Color(0x4034D399), RoundedCornerShape(20.dp))
                    .padding(horizontal = 7.dp, vertical = 2.dp)
                ) {
                  Text(
                    text = feature,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isDark) Color(0xFFA7F3D0) else Color(0xFF047857)
                  )
                }
              }
            }

            if (errorMessage != null) {
              Spacer(modifier = Modifier.height(12.dp))
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(14.dp))
                  .background(Color(0x22FF5252))
                  .border(1.dp, Color(0x66FF5252), RoundedCornerShape(14.dp))
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
                      text = "Open in Browser & Sniff Media 🚀",
                      fontSize = 13.sp,
                      fontWeight = FontWeight.Bold
                    )
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Fetch & Inspect Button with 3D Embossed Emerald Gradient
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
                .clickable(enabled = !isExtracting) { viewModel.extractMedia() }
                .testTag("fetch_video_button"),
              contentAlignment = Alignment.Center
            ) {
              if (isExtracting) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = Color.Black,
                    strokeWidth = 2.5.dp
                  )
                  Spacer(modifier = Modifier.width(10.dp))
                  Text(
                    text = "Finding available video quality...",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                  )
                }
              } else {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = "Download",
                    tint = Color.Black,
                    modifier = Modifier.size(22.dp)
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = "View Qualities & Download ⚡",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                  )
                }
              }
            }
          }
        }
      }

      // --- INLINE VIDEO DETAILS & QUALITY SELECTOR (PREMIUM SCREEN CARDS) ---
      extractedMedia?.let { media ->
        item {
          PremiumGlassCard(
            isDark = isDark,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            borderWidth = 1.4.dp
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
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
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = "Media Details & Quality Options",
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

              Spacer(modifier = Modifier.height(14.dp))

              // Video Thumbnail Preview
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .height(180.dp)
                  .clip(RoundedCornerShape(16.dp))
                  .border(
                    1.2.dp,
                    Brush.linearGradient(listOf(Color(0x90FFFFFF), Color(0x3034D399), Color(0x20FFFFFF))),
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
                    .background(Color.Black.copy(alpha = 0.8f))
                    .border(0.5.dp, Color(0xFF34D399), RoundedCornerShape(6.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                  Text(
                    text = media.duration,
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
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Author: ${media.author.ifEmpty { "Creator" }} • Duration: ${media.duration}",
                fontSize = 11.sp,
                color = if (isDark) Color(0xFFA7B5B0) else Color(0xFF6B7280)
              )

              Spacer(modifier = Modifier.height(14.dp))

              // 1-Tap Caption Copy Box (Frosted Glass)
              PremiumGlassCard(
                isDark = isDark,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                borderWidth = 1.dp
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
                        text = "Video Title & Caption",
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
                        Toast.makeText(context, "Caption copied to clipboard! ✓", Toast.LENGTH_SHORT).show()
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
                        text = if (homeCaptionCopied) "Copied!" else "Copy",
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

              Spacer(modifier = Modifier.height(16.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "Select Quality & Format:",
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isDark) Color.White else Color(0xFF0F172A)
                )
                Text(
                  text = "Best available quality",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF10B981)
                )
              }

              Spacer(modifier = Modifier.height(10.dp))

              // Quality options list: each item is a sleek PremiumGlassCard
              Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                media.qualities.forEach { quality ->
                  val isSelected = selectedQuality == quality
                  val isAudio = quality.mediaType == MediaType.AUDIO
                  val isRecommended = quality.isVipBest

                  PremiumGlassCard(
                    isDark = isDark,
                    isSelected = isSelected,
                    shape = RoundedCornerShape(16.dp),
                    borderWidth = if (isSelected) 1.5.dp else 1.dp,
                    modifier = Modifier
                      .fillMaxWidth()
                      .clickable { viewModel.selectQuality(quality) }
                  ) {
                    Row(
                      modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
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
                              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
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
              }

              Spacer(modifier = Modifier.height(16.dp))

              // Direct Download Button (3D Embossed Emerald Glass Button)
              val curOpt = selectedQuality
              val currentQualityLabel = curOpt?.resolution?.ifEmpty { curOpt.label } ?: "Choose quality"
              val currentFormat = curOpt?.format?.uppercase() ?: "MP4"

              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .height(50.dp)
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
                  .clickable(enabled = selectedQuality != null) { viewModel.confirmDownload() },
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
                    modifier = Modifier.size(20.dp)
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = "Start Download ($currentQualityLabel • $currentFormat)",
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

