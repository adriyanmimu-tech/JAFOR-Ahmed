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
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.MediaType
import com.example.ui.components.FypTipsDialog
import com.example.ui.components.GlassBubblePlatformBadge
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassmorphicBackground
import com.example.ui.components.PlatformBrandBadge
import com.example.ui.components.PlatformBrandIcon
import com.example.ui.components.QualitySelectModal
import com.example.ui.theme.VipAmberDark
import com.example.ui.theme.VipAmberPrimary
import com.example.ui.theme.VipElectricCyan
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

  var homeCaptionCopied by remember { mutableStateOf(false) }
  var selectedPlatformFilter by remember { mutableStateOf("Facebook") }

  // 3D Glassmorphic Background with glowing 3D spheres & toruses
  GlassmorphicBackground(isDark = isDark) {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      item {
        Spacer(modifier = Modifier.height(6.dp))

        // --- MODERN SNAPVID TOP HEADER ---
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            // Frosted glass logo badge with 3D gradient
            Box(
              modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(
                  Brush.linearGradient(
                    listOf(
                      Color(0x8000F2FE),
                      Color(0x4010B981),
                      Color(0x30FFFFFF)
                    )
                  )
                )
                .border(
                  1.5.dp,
                  Brush.linearGradient(
                    listOf(Color.White, Color(0x30FFFFFF), Color(0xFF00F2FE))
                  ),
                  CircleShape
                ),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Download,
                contentDescription = "SnapVid",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "SnapVid",
                  fontSize = 22.sp,
                  fontWeight = FontWeight.Black,
                  letterSpacing = 0.5.sp,
                  color = if (isDark) Color.White else Color(0xFF0F172A)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                      Brush.horizontalGradient(
                        listOf(Color(0xFF00F2FE), Color(0xFF10B981))
                      )
                    )
                    .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                  Text(
                    text = "3D GLASS",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black
                  )
                }
              }
              Text(
                text = "Social Media Video & Audio Downloader",
                fontSize = 11.sp,
                color = if (isDark) Color(0xFFA5ABB8) else Color(0xFF64748B)
              )
            }
          }

          // Glassmorphic Theme Switcher
          IconButton(
            onClick = { viewModel.toggleTheme() },
            modifier = Modifier
              .size(42.dp)
              .clip(CircleShape)
              .background(if (isDark) Color(0x28FFFFFF) else Color(0x33FFFFFF))
              .border(
                1.2.dp,
                Brush.linearGradient(listOf(Color(0x80FFFFFF), Color(0x20FFFFFF))),
                CircleShape
              )
              .testTag("theme_toggle_button")
          ) {
            Icon(
              imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
              contentDescription = "Toggle Theme",
              tint = if (isDark) Color(0xFFFFD54F) else Color(0xFF0284C7)
            )
          }
        }
      }

      // --- HERO 3D GLASS CARD (INSPIRED DIRECTLY BY REFERENCE MOCKUPS) ---
      item {
        GlassCard(
          isDark = isDark,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(28.dp),
          borderWidth = 1.5.dp
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 18.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            // Central 3D Frosted Glass Download Arrow + Circular Play Ring
            Box(
              modifier = Modifier
                .size(160.dp),
              contentAlignment = Alignment.Center
            ) {
              // Canvas drawing the 3D translucent frosted glass download arrow & play ring
              Canvas(modifier = Modifier.matchParentSize()) {
                val w = size.width
                val h = size.height
                val center = Offset(w / 2f, h / 2f)
                val radius = w * 0.38f

                // Outer Frosted Glass Ring with gap at top
                val ringStroke = w * 0.085f
                val arcPath = Path().apply {
                  // Arc from 135 deg to 405 deg (leaves top open for arrow)
                  addArc(
                    androidx.compose.ui.geometry.Rect(
                      center.x - radius,
                      center.y - radius,
                      center.x + radius,
                      center.y + radius
                    ),
                    startAngleDegrees = 140f,
                    sweepAngleDegrees = 260f
                  )
                }

                // Frosted glass glow aura
                drawPath(
                  path = arcPath,
                  brush = Brush.radialGradient(
                    colors = listOf(
                      Color(0x6000F2FE),
                      Color(0x2010B981),
                      Color(0x00000000)
                    ),
                    center = center,
                    radius = radius * 1.4f
                  ),
                  style = Stroke(width = ringStroke * 1.5f, cap = StrokeCap.Round)
                )

                // Frosted Glass Ring Body
                drawPath(
                  path = arcPath,
                  brush = Brush.sweepGradient(
                    colors = listOf(
                      Color(0xD0FFFFFF),
                      Color(0x4000F2FE),
                      Color(0x70FFFFFF),
                      Color(0x3010B981),
                      Color(0xD0FFFFFF)
                    ),
                    center = center
                  ),
                  style = Stroke(width = ringStroke, cap = StrokeCap.Round)
                )

                // Center Play Button Triangle (Frosted 3D Glass)
                val playTriangle = Path().apply {
                  val tLeft = center.x - w * 0.10f
                  val tRight = center.x + w * 0.15f
                  val tTop = center.y - h * 0.14f
                  val tBottom = center.y + h * 0.14f

                  moveTo(tLeft, tTop)
                  lineTo(tRight, center.y)
                  lineTo(tLeft, tBottom)
                  close()
                }

                // Soft ambient glow behind play button
                drawPath(
                  path = playTriangle,
                  brush = Brush.radialGradient(
                    colors = listOf(Color(0x5000F2FE), Color(0x00000000)),
                    center = center,
                    radius = w * 0.25f
                  )
                )

                // Play button glass body
                drawPath(
                  path = playTriangle,
                  brush = Brush.linearGradient(
                    colors = listOf(
                      Color(0xE0FFFFFF),
                      Color(0x70FFFFFF),
                      Color(0x9000F2FE)
                    ),
                    start = Offset(center.x - w * 0.1f, center.y - h * 0.15f),
                    end = Offset(center.x + w * 0.15f, center.y + h * 0.15f)
                  ),
                  style = Fill
                )

                // Play button specular edge
                drawPath(
                  path = playTriangle,
                  brush = Brush.linearGradient(
                    colors = listOf(Color.White, Color(0x40FFFFFF))
                  ),
                  style = Stroke(width = 2.5f)
                )

                // 3D Download Arrow pointing down through the gap
                val arrowPath = Path().apply {
                  val arrowWidth = w * 0.15f
                  val arrowHeadWidth = w * 0.32f
                  val arrowTop = h * 0.05f
                  val arrowStemBottom = h * 0.38f
                  val arrowTip = h * 0.48f

                  // Arrow stem
                  moveTo(center.x - arrowWidth / 2f, arrowTop)
                  lineTo(center.x + arrowWidth / 2f, arrowTop)
                  lineTo(center.x + arrowWidth / 2f, arrowStemBottom)
                  // Arrow right barb
                  lineTo(center.x + arrowHeadWidth / 2f, arrowStemBottom)
                  // Arrow tip point
                  lineTo(center.x, arrowTip)
                  // Arrow left barb
                  lineTo(center.x - arrowHeadWidth / 2f, arrowStemBottom)
                  lineTo(center.x - arrowWidth / 2f, arrowStemBottom)
                  close()
                }

                // Arrow 3D glass body
                drawPath(
                  path = arrowPath,
                  brush = Brush.verticalGradient(
                    colors = listOf(
                      Color(0xF0FFFFFF),
                      Color(0x90E0F7FA),
                      Color(0xC000F2FE)
                    )
                  ),
                  style = Fill
                )

                // Arrow specular edge highlight
                drawPath(
                  path = arrowPath,
                  brush = Brush.linearGradient(
                    colors = listOf(Color.White, Color(0x30FFFFFF))
                  ),
                  style = Stroke(width = 2.5f)
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
              text = "1-Tap Social Downloader",
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold,
              color = if (isDark) Color.White else Color(0xFF0F172A)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "ট্যাপ করুন যেকোনো প্ল্যাটফর্মে অথবা লিংক পেস্ট করুন",
              fontSize = 12.sp,
              color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // FLOATING 3D GLASS BUBBLE PLATFORMS (AS SEEN IN SCREENSHOT 2)
            val bubblePlatforms = listOf(
              PlatformBubbleItem("Facebook", "https://facebook.com/watch/?v=snapvid_demo"),
              PlatformBubbleItem("TikTok", "https://tiktok.com/@creator/video/snapvid_demo"),
              PlatformBubbleItem("Instagram", "https://instagram.com/reel/snapvid_demo"),
              PlatformBubbleItem("YouTube", "https://youtube.com/shorts/snapvid_demo"),
              PlatformBubbleItem("X", "https://x.com/status/snapvid_demo"),
              PlatformBubbleItem("Snapchat", "https://snapchat.com/spotlight/snapvid_demo"),
              PlatformBubbleItem("Pinterest", "https://pinterest.com/pin/snapvid_demo"),
              PlatformBubbleItem("Reddit", "https://reddit.com/r/videos/comments/snapvid_demo")
            )

            LazyRow(
              horizontalArrangement = Arrangement.spacedBy(12.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              items(bubblePlatforms) { item ->
                val isSelected = selectedPlatformFilter == item.name
                Column(
                  horizontalAlignment = Alignment.CenterHorizontally,
                  modifier = Modifier.clickable {
                    selectedPlatformFilter = item.name
                    viewModel.onUrlChange(item.sampleUrl)
                    viewModel.extractMedia(item.sampleUrl)
                  }
                ) {
                  GlassBubblePlatformBadge(
                    platform = item.name,
                    bubbleSize = 52.dp,
                    iconSize = 26.dp,
                    isSelected = isSelected
                  )
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = item.name,
                    fontSize = 10.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) Color(0xFF00F2FE) else (if (isDark) Color(0xFFCBD5E1) else Color(0xFF334155))
                  )
                }
              }
            }
          }
        }
      }

      // --- CLIPBOARD AUTO-DETECT CARD ---
      item {
        AnimatedVisibility(
          visible = clipboardCandidate != null,
          enter = fadeIn() + expandVertically(),
          exit = fadeOut() + shrinkVertically()
        ) {
          clipboardCandidate?.let { candidate ->
            GlassCard(
              isDark = isDark,
              borderBrush = Brush.horizontalGradient(
                listOf(Color(0xFF00F2FE), Color(0xFF10B981))
              ),
              borderWidth = 1.5.dp,
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Row(
                  modifier = Modifier.weight(1f),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Box(
                    modifier = Modifier
                      .size(38.dp)
                      .clip(CircleShape)
                      .background(Color(0x3300F2FE)),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(
                      imageVector = Icons.Default.Link,
                      contentDescription = "Clipboard link",
                      tint = Color(0xFF00F2FE),
                      modifier = Modifier.size(20.dp)
                    )
                  }
                  Spacer(modifier = Modifier.width(10.dp))
                  Column {
                    Text(
                      text = "কপিকৃত ভিডিও লিংক পাওয়া গেছে!",
                      fontSize = 13.sp,
                      fontWeight = FontWeight.Bold,
                      color = if (isDark) Color.White else Color(0xFF0F172A)
                    )
                    Text(
                      text = candidate.take(35) + "...",
                      fontSize = 11.sp,
                      color = if (isDark) Color(0xFFA5ABB8) else Color(0xFF6B7280)
                    )
                  }
                }

                Row {
                  Button(
                    onClick = { viewModel.acceptClipboardCandidate() },
                    colors = ButtonDefaults.buttonColors(
                      containerColor = Color(0xFF00F2FE),
                      contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = ButtonDefaults.TextButtonContentPadding,
                    modifier = Modifier.testTag("accept_clipboard_button")
                  ) {
                    Text(
                      text = "ডাউনলোড",
                      fontSize = 12.sp,
                      fontWeight = FontWeight.Bold
                    )
                  }
                  Spacer(modifier = Modifier.width(6.dp))
                  IconButton(
                    onClick = { viewModel.dismissClipboardCandidate() },
                    modifier = Modifier.size(32.dp)
                  ) {
                    Icon(
                      imageVector = Icons.Default.Clear,
                      contentDescription = "Dismiss",
                      tint = if (isDark) Color.White.copy(alpha = 0.6f) else Color.Black.copy(alpha = 0.5f)
                    )
                  }
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
            Brush.linearGradient(listOf(Color(0xFF00F2FE), Color(0xFF10B981)))
          } else null,
          borderWidth = 1.3.dp
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
              Text(
                text = "Paste Video URL",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color.White else Color(0xFF0F172A)
              )
              Text(
                text = "SnapVid Engine",
                fontSize = 11.sp,
                color = Color(0xFF00F2FE),
                fontWeight = FontWeight.Bold
              )
            }
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
              value = inputUrl,
              onValueChange = { viewModel.onUrlChange(it) },
              placeholder = {
                Text(
                  text = "এখানে ফেসবুক, ইনস্টাগ্রাম, টিকটক লিংক পেস্ট করুন...",
                  fontSize = 13.sp,
                  color = if (isDark) Color(0xFF7E8498) else Color(0xFF94A3B8)
                )
              },
              singleLine = true,
              leadingIcon = {
                Icon(
                  imageVector = Icons.Default.Link,
                  contentDescription = "URL",
                  tint = Color(0xFF00F2FE)
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
                      tint = Color(0xFF00F2FE)
                    )
                  }
                }
              },
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF00F2FE),
                unfocusedBorderColor = if (isDark) Color(0x35FFFFFF) else Color(0x40000000),
                focusedContainerColor = if (isDark) Color(0x280B1220) else Color(0x60FFFFFF),
                unfocusedContainerColor = if (isDark) Color(0x180B1220) else Color(0x40FFFFFF)
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
                      containerColor = Color(0xFF00F2FE),
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

            // Fetch & Inspect Button with 3D Glass Gradient
            Button(
              onClick = { viewModel.extractMedia() },
              enabled = !isExtracting,
              shape = RoundedCornerShape(16.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF00F2FE),
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
                  text = "Analyzing Stream...",
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
                    text = "Inspect & Select Quality",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
              }
            }
          }
        }
      }

      // --- INLINE VIDEO DETAILS, CAPTION COPY BOX & QUALITY SELECTOR ---
      extractedMedia?.let { media ->
        item {
          GlassCard(
            isDark = isDark,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            borderBrush = Brush.linearGradient(
              listOf(Color(0xFF00F2FE), Color(0xFF10B981), Color(0x60FFFFFF))
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
                // Duration Chip
                Box(
                  modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.Black.copy(alpha = 0.75f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                  Text(
                    text = media.duration,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
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
                color = if (isDark) Color(0xFFA5ABB8) else Color(0xFF6B7280)
              )

              Spacer(modifier = Modifier.height(12.dp))

              // --- 1-TAP CAPTION COPY BOX ---
              GlassCard(
                isDark = isDark,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                borderWidth = 1.dp,
                borderColor = Color(0x3500F2FE)
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
                        tint = Color(0xFF00F2FE),
                        modifier = Modifier.size(16.dp)
                      )
                      Spacer(modifier = Modifier.width(6.dp))
                      Text(
                        text = "ভিডিও টাইটেল ও ক্যাপশন",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00F2FE)
                      )
                    }

                    // Copy Button with 1-Tap Feedback
                    Button(
                      onClick = {
                        val clipText = media.title
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("SnapVid Video Caption", clipText))
                        homeCaptionCopied = true
                        Toast.makeText(context, "ক্যাপশন কপি সফল হয়েছে! ✓", Toast.LENGTH_SHORT).show()
                        coroutineScope.launch {
                          delay(2500)
                          homeCaptionCopied = false
                        }
                      },
                      colors = ButtonDefaults.buttonColors(
                        containerColor = if (homeCaptionCopied) Color(0xFF10B981) else Color(0xFF00F2FE),
                        contentColor = Color.Black
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

                  Spacer(modifier = Modifier.height(8.dp))

                  // Caption Text Preview Box
                  Box(
                    modifier = Modifier
                      .fillMaxWidth()
                      .clip(RoundedCornerShape(8.dp))
                      .background(if (isDark) Color(0x30000000) else Color(0x10000000))
                      .padding(10.dp)
                  ) {
                    val fullText = media.title
                    Text(
                      text = fullText,
                      fontSize = 12.sp,
                      lineHeight = 16.sp,
                      color = if (isDark) Color(0xFFD1D5DB) else Color(0xFF374151),
                      maxLines = 3,
                      overflow = TextOverflow.Ellipsis
                    )
                  }
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

              // --- INLINE QUALITY SELECTION CARDS ---
              Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                media.qualities.forEach { quality ->
                  val isSelected = selectedQuality == quality
                  val isAudio = quality.mediaType == MediaType.AUDIO

                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .clip(RoundedCornerShape(12.dp))
                      .background(
                        if (isSelected) (if (isDark) Color(0x4000F2FE) else Color(0x3000F2FE))
                        else (if (isDark) Color(0x1AFFFFFF) else Color(0x20000000))
                      )
                      .border(
                        if (isSelected) 1.5.dp else 1.dp,
                        if (isSelected) Color(0xFF00F2FE) else (if (isDark) Color(0x25FFFFFF) else Color(0x20000000)),
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
                          .background(if (isSelected) Color(0xFF00F2FE).copy(alpha = 0.25f) else Color(0x22888888)),
                        contentAlignment = Alignment.Center
                      ) {
                        Icon(
                          imageVector = if (isAudio) Icons.Default.Audiotrack else Icons.Default.Movie,
                          contentDescription = quality.format,
                          tint = if (isSelected) Color(0xFF00F2FE) else Color.Gray,
                          modifier = Modifier.size(16.dp)
                        )
                      }
                      Spacer(modifier = Modifier.width(10.dp))
                      Column {
                        Text(
                          text = quality.label,
                          fontSize = 13.sp,
                          fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                          color = if (isSelected) Color(0xFF00F2FE) else (if (isDark) Color.White else Color(0xFF0F172A))
                        )
                        Text(
                          text = "${quality.format} • ${quality.bitrate}",
                          fontSize = 10.sp,
                          color = if (isDark) Color(0xFFA5ABB8) else Color(0xFF6B7280)
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
                      Spacer(modifier = Modifier.width(8.dp))
                      Box(
                        modifier = Modifier
                          .size(18.dp)
                          .clip(CircleShape)
                          .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) Color(0xFF00F2FE) else Color.Gray,
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
              val currentQualityLabel = curOpt?.resolution?.ifEmpty { curOpt.label } ?: "HD"
              val currentFormat = curOpt?.format?.uppercase() ?: "MP4"

              Button(
                onClick = { viewModel.confirmDownload() },
                enabled = selectedQuality != null,
                modifier = Modifier
                  .fillMaxWidth()
                  .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                  containerColor = Color(0xFF00F2FE),
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

      // --- SUPPORTED PLATFORMS QUICK DEMO ROW ---
      item {
        Column(modifier = Modifier.fillMaxWidth()) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Supported Platforms & Instant Links",
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = if (isDark) Color.White else Color(0xFF0F172A)
            )
            Text(
              text = "1-Tap Demo",
              fontSize = 11.sp,
              color = Color(0xFF00F2FE),
              fontWeight = FontWeight.SemiBold
            )
          }
          Spacer(modifier = Modifier.height(8.dp))

          val platforms = listOf(
            PlatformDemoItem("Facebook", "https://facebook.com/watch/?v=snapvid_fb"),
            PlatformDemoItem("TikTok", "https://tiktok.com/@creator/video/snapvid_tt"),
            PlatformDemoItem("Instagram", "https://instagram.com/reel/snapvid_ig"),
            PlatformDemoItem("YouTube", "https://youtube.com/shorts/snapvid_yt"),
            PlatformDemoItem("X", "https://x.com/status/snapvid_x"),
            PlatformDemoItem("Snapchat", "https://snapchat.com/spotlight/snapvid_sc"),
            PlatformDemoItem("Pinterest", "https://pinterest.com/pin/snapvid_pin"),
            PlatformDemoItem("Reddit", "https://reddit.com/r/videos/comments/snapvid_rd")
          )

          LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            items(platforms) { platform ->
              GlassCard(
                isDark = isDark,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.clickable {
                  viewModel.onUrlChange(platform.sampleUrl)
                  viewModel.extractMedia(platform.sampleUrl)
                }
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  PlatformBrandIcon(platform = platform.name, size = 22.dp)
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = platform.name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isDark) Color.White else Color(0xFF0F172A)
                  )
                }
              }
            }
          }
        }
      }

      item {
        Spacer(modifier = Modifier.height(80.dp)) // Space for bottom dock
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

private data class PlatformBubbleItem(
  val name: String,
  val sampleUrl: String
)

private data class PlatformDemoItem(
  val name: String,
  val sampleUrl: String
)
