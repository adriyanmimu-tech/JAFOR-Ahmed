package com.example.ui.screens

import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.GlassCard
import com.example.ui.components.QualitySelectModal
import com.example.ui.theme.VipAmberDark
import com.example.ui.theme.VipAmberPrimary
import com.example.ui.theme.VipCoral
import com.example.ui.theme.VipGoldAccent
import com.example.ui.viewmodel.DownloaderViewModel

@Composable
fun HomeScreen(
  viewModel: DownloaderViewModel,
  isDark: Boolean,
  onNavigateToTab: (Int) -> Unit
) {
  val context = LocalContext.current
  val inputUrl by viewModel.inputUrl.collectAsState()
  val isExtracting by viewModel.isExtracting.collectAsState()
  val extractedMedia by viewModel.extractedMedia.collectAsState()
  val selectedQuality by viewModel.selectedQuality.collectAsState()
  val clipboardCandidate by viewModel.clipboardCandidate.collectAsState()
  val errorMessage by viewModel.errorMessage.collectAsState()
  val allDownloads by viewModel.allDownloads.collectAsState()

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      item {
        Spacer(modifier = Modifier.height(8.dp))
        // Top Header
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(VipAmberPrimary, VipCoral)))
                .border(1.dp, VipGoldAccent, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Download,
                contentDescription = "VidMax VIP",
                tint = Color.Black,
                modifier = Modifier.size(24.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "VidMax",
                  fontSize = 20.sp,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                  modifier = Modifier
                    .background(VipAmberPrimary, RoundedCornerShape(6.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                  Text(
                    text = "VIP PRO",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black
                  )
                }
              }
              Text(
                text = "All Social Media Downloader",
                fontSize = 12.sp,
                color = if (isDark) Color(0xFFA5ABB8) else Color(0xFF6B7280)
              )
            }
          }

          // Dark/Light Theme Switcher
          IconButton(
            onClick = { viewModel.toggleTheme() },
            modifier = Modifier
              .size(42.dp)
              .clip(CircleShape)
              .background(if (isDark) Color(0x33FFFFFF) else Color(0x15000000))
              .border(
                1.dp,
                if (isDark) Color(0x33FFB300) else Color(0x33FF8F00),
                CircleShape
              )
              .testTag("theme_toggle_button")
          ) {
            Icon(
              imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
              contentDescription = "Toggle Theme",
              tint = VipAmberPrimary
            )
          }
        }
      }

      // Hero Banner
      item {
        GlassCard(
          isDark = isDark,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(22.dp)
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(150.dp)
          ) {
            Image(
              painter = painterResource(id = R.drawable.hero_banner_1792680412351),
              contentDescription = "VIP Downloader Hero",
              modifier = Modifier.fillMaxSize(),
              contentScale = ContentScale.Crop
            )
            // Gradient Overlay for readability
            Box(
              modifier = Modifier
                .fillMaxSize()
                .background(
                  Brush.horizontalGradient(
                    listOf(
                      Color.Black.copy(alpha = 0.85f),
                      Color.Black.copy(alpha = 0.4f),
                      Color.Transparent
                    )
                  )
                )
            )

            Column(
              modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
              verticalArrangement = Arrangement.Center
            ) {
              Box(
                modifier = Modifier
                  .background(VipAmberPrimary.copy(alpha = 0.25f), RoundedCornerShape(6.dp))
                  .border(0.5.dp, VipAmberPrimary, RoundedCornerShape(6.dp))
                  .padding(horizontal = 8.dp, vertical = 2.dp)
              ) {
                Text(
                  text = "HIGH SPEED ENGINE",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = VipAmberPrimary
                )
              }
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "Download Any Video or Audio",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
              Text(
                text = "1080p, 720p HD & 320kbps MP3 Extractor",
                fontSize = 12.sp,
                color = Color(0xFFE2E4EB)
              )
            }
          }
        }
      }

      // Clipboard Auto-Detect Card
      item {
        AnimatedVisibility(
          visible = clipboardCandidate != null,
          enter = fadeIn() + expandVertically(),
          exit = fadeOut() + shrinkVertically()
        ) {
          clipboardCandidate?.let { candidate ->
            GlassCard(
              isDark = isDark,
              borderColor = VipAmberPrimary,
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
                      .background(VipAmberPrimary.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(
                      imageVector = Icons.Default.Link,
                      contentDescription = "Clipboard link",
                      tint = VipAmberPrimary,
                      modifier = Modifier.size(20.dp)
                    )
                  }
                  Spacer(modifier = Modifier.width(10.dp))
                  Column {
                    Text(
                      text = "Copied link detected!",
                      fontSize = 13.sp,
                      fontWeight = FontWeight.Bold,
                      color = MaterialTheme.colorScheme.onBackground
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
                    colors = ButtonDefaults.buttonColors(containerColor = VipAmberPrimary),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = ButtonDefaults.TextButtonContentPadding,
                    modifier = Modifier.testTag("accept_clipboard_button")
                  ) {
                    Text(
                      text = "Download",
                      fontSize = 12.sp,
                      fontWeight = FontWeight.Bold,
                      color = Color.Black
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

      // Link Input Glass Card
      item {
        GlassCard(
          isDark = isDark,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(22.dp)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(18.dp)
          ) {
            Text(
              text = "Paste Video or Audio URL",
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
              value = inputUrl,
              onValueChange = { viewModel.onUrlChange(it) },
              placeholder = {
                Text(
                  text = "Paste Instagram, TikTok, Facebook, X link...",
                  fontSize = 13.sp,
                  color = if (isDark) Color(0xFF7E8498) else Color(0xFF9E9E9E)
                )
              },
              singleLine = true,
              leadingIcon = {
                Icon(
                  imageVector = Icons.Default.Link,
                  contentDescription = "URL",
                  tint = VipAmberPrimary
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
                      tint = VipAmberPrimary
                    )
                  }
                }
              },
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = VipAmberPrimary,
                unfocusedBorderColor = if (isDark) Color(0x33FFB300) else Color(0x33FF8F00),
                focusedContainerColor = if (isDark) Color(0x22171B2A) else Color(0xFFF9F9F9),
                unfocusedContainerColor = if (isDark) Color(0x1A171B2A) else Color(0xFFFAFAFA)
              ),
              shape = RoundedCornerShape(16.dp),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("url_text_input")
            )

            if (errorMessage != null) {
              Spacer(modifier = Modifier.height(8.dp))
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.Warning,
                  contentDescription = "Error",
                  tint = Color(0xFFFF5252),
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = errorMessage ?: "",
                  color = Color(0xFFFF5252),
                  fontSize = 12.sp
                )
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Fetch & Inspect Button
            Button(
              onClick = { viewModel.extractMedia() },
              enabled = !isExtracting,
              shape = RoundedCornerShape(16.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = VipAmberPrimary,
                contentColor = Color.Black
              ),
              modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
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
                    text = "Inspect & Download",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
              }
            }
          }
        }
      }

      // Quick Platforms Row with Demo Links for Instant Testing
      item {
        Column(modifier = Modifier.fillMaxWidth()) {
          Text(
            text = "Supported Platforms & Instant Test Links",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
          )
          Spacer(modifier = Modifier.height(8.dp))

          val platforms = listOf(
            PlatformItem("YouTube", "https://youtube.com/shorts/vip_demo_sample", Color(0xFFFF0000)),
            PlatformItem("Instagram", "https://instagram.com/reel/vip_demo_sample", Color(0xFFE1306C)),
            PlatformItem("TikTok", "https://tiktok.com/@creator/video/vip_demo", Color(0xFF00F2FE)),
            PlatformItem("Facebook", "https://facebook.com/watch/?v=vip_demo", Color(0xFF1877F2)),
            PlatformItem("X / Twitter", "https://x.com/status/vip_clip_demo", Color(0xFF1DA1F2)),
            PlatformItem("Pinterest", "https://pinterest.com/pin/vip_demo", Color(0xFFBD081C)),
            PlatformItem("Vimeo", "https://vimeo.com/vip_demo_hd", Color(0xFF1AB7EA))
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
                  Box(
                    modifier = Modifier
                      .size(10.dp)
                      .clip(CircleShape)
                      .background(platform.accentColor)
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = platform.name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                  )
                }
              }
            }
          }
        }
      }

      // VIP Stats & Features Bar
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          // Stat 1: Total downloads
          GlassCard(
            isDark = isDark,
            modifier = Modifier.weight(1f)
          ) {
            Column(
              modifier = Modifier.padding(14.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Icon(
                imageVector = Icons.Default.FolderSpecial,
                contentDescription = "Downloads",
                tint = VipAmberPrimary,
                modifier = Modifier.size(24.dp)
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "${allDownloads.size} Files",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
              )
              Text(
                text = "Downloaded",
                fontSize = 11.sp,
                color = if (isDark) Color(0xFFA5ABB8) else Color(0xFF6B7280)
              )
            }
          }

          // Stat 2: High Speed
          GlassCard(
            isDark = isDark,
            modifier = Modifier.weight(1f)
          ) {
            Column(
              modifier = Modifier.padding(14.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Icon(
                imageVector = Icons.Default.Bolt,
                contentDescription = "Speed",
                tint = VipCoral,
                modifier = Modifier.size(24.dp)
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Ultra Speed",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
              )
              Text(
                text = "Multi-thread",
                fontSize = 11.sp,
                color = if (isDark) Color(0xFFA5ABB8) else Color(0xFF6B7280)
              )
            }
          }

          // Stat 3: 100% Secure
          GlassCard(
            isDark = isDark,
            modifier = Modifier.weight(1f)
          ) {
            Column(
              modifier = Modifier.padding(14.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Privacy",
                tint = VipGoldAccent,
                modifier = Modifier.size(24.dp)
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Private",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
              )
              Text(
                text = "No Watermark",
                fontSize = 11.sp,
                color = if (isDark) Color(0xFFA5ABB8) else Color(0xFF6B7280)
              )
            }
          }
        }
      }

      // TikTok FYP Golden Rules Card
      item {
        GlassCard(
          isDark = isDark,
          modifier = Modifier
            .fillMaxWidth()
            .clickable { viewModel.showFypTipsDialog() }
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
              Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Box(
                  modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(VipAmberPrimary, VipCoral))),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "FYP Rules",
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                  )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text(
                    text = "💡 ১০০% FYP এলিজিবল রুলস",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = VipAmberPrimary
                  )
                  Text(
                    text = "টিকটকে ভাইরাল হওয়ার ৪টি গোল্ডেন ট্রিক",
                    fontSize = 11.sp,
                    color = if (isDark) Color(0xFFA5ABB8) else Color(0xFF6B7280)
                  )
                }
              }

              Box(
                modifier = Modifier
                  .background(VipAmberPrimary.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                  .border(1.dp, VipAmberPrimary.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Text(
                  text = "বিস্তারিত",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = VipAmberPrimary
                )
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
              text = "অ্যাপের ভিডিও আপলোডে আর কখনো \"Not eligible\" আসবে না:",
              fontSize = 12.sp,
              color = if (isDark) Color(0xFFE2E8F0) else Color(0xFF334155),
              fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            GuideStepRow("১", "নতুন হুক: শুরুতে ১-২ সেকেন্ড নিজস্ব ক্লিপ বা টেক্সট হুক দিন", isDark)
            GuideStepRow("২", "ট্রেন্ডিং সাউন্ড: Add Sound থেকে মিউজিক ভলিউম ১%-৩% করুন", isDark)
            GuideStepRow("৩", "টিকটক টেক্সট: অ্যাপের অটো-ক্যাপশন বা ক্যাপশন অন রাখুন", isDark)
            GuideStepRow("৪", "শেষের ট্রিম: ভিডিওর শেষ ১ সেকেন্ড সামান্য ট্রিম/কাট করুন", isDark)
          }
        }
      }

      // Step-by-Step Guide
      item {
        GlassCard(
          isDark = isDark,
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp)
          ) {
            Text(
              text = "How to Download with VidMax VIP",
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = VipAmberPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))

            GuideStepRow("1", "Copy link from Instagram, TikTok, Facebook or X", isDark)
            GuideStepRow("2", "Paste URL or let clipboard auto-detect the video", isDark)
            GuideStepRow("3", "Choose MP4 (1080p, 720p, 480p) or MP3 (320kbps)", isDark)
            GuideStepRow("4", "Play directly with the built-in VIP HD Player", isDark)
          }
        }
      }

      item {
        Spacer(modifier = Modifier.height(80.dp)) // Padding for bottom bar
      }
    }

    // Extraction / Quality Selection Modal
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
      com.example.ui.components.FypTipsDialog(
        isDark = isDark,
        onDismiss = { viewModel.dismissFypTipsDialog() }
      )
    }
  }
}

@Composable
private fun GuideStepRow(stepNumber: String, text: String, isDark: Boolean) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .size(22.dp)
        .clip(CircleShape)
        .background(VipAmberPrimary.copy(alpha = 0.2f)),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = stepNumber,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = VipAmberPrimary
      )
    }
    Spacer(modifier = Modifier.width(10.dp))
    Text(
      text = text,
      fontSize = 13.sp,
      color = if (isDark) Color(0xFFCAD0E0) else Color(0xFF4B5563)
    )
  }
}

private data class PlatformItem(
  val name: String,
  val sampleUrl: String,
  val accentColor: Color
)
