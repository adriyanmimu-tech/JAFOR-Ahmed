package com.example.ui.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.components.GlassCard
import com.example.ui.theme.VipAmberDark
import com.example.ui.theme.VipAmberPrimary
import com.example.ui.theme.VipCoral
import com.example.ui.theme.VipGoldAccent
import com.example.ui.viewmodel.DownloaderViewModel

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun BrowserScreen(
  viewModel: DownloaderViewModel,
  isDark: Boolean,
  onDownloadUrlDetected: (String) -> Unit
) {
  var currentUrl by remember { mutableStateOf("https://www.tiktok.com") }
  var inputUrlText by remember { mutableStateOf("https://www.tiktok.com") }
  var webViewRef by remember { mutableStateOf<WebView?>(null) }
  var loadProgress by remember { mutableIntStateOf(0) }
  var isPageLoading by remember { mutableStateOf(false) }

  val bookmarks = listOf(
    Bookmark("YouTube", "https://m.youtube.com", Color(0xFFFF0000)),
    Bookmark("TikTok", "https://www.tiktok.com", Color(0xFF00F2FE)),
    Bookmark("Instagram", "https://www.instagram.com", Color(0xFFE1306C)),
    Bookmark("Facebook", "https://www.facebook.com", Color(0xFF1877F2)),
    Bookmark("X / Twitter", "https://x.com", Color(0xFF1DA1F2)),
    Bookmark("Pinterest", "https://www.pinterest.com", Color(0xFFBD081C)),
    Bookmark("Reddit", "https://www.reddit.com", Color(0xFFFF4500))
  )

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      Spacer(modifier = Modifier.height(10.dp))

      // Navigation Bar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(
          onClick = {
            if (webViewRef?.canGoBack() == true) webViewRef?.goBack()
          },
          modifier = Modifier.size(36.dp)
        ) {
          Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Back",
            tint = MaterialTheme.colorScheme.onBackground
          )
        }

        IconButton(
          onClick = {
            if (webViewRef?.canGoForward() == true) webViewRef?.goForward()
          },
          modifier = Modifier.size(36.dp)
        ) {
          Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = "Forward",
            tint = MaterialTheme.colorScheme.onBackground
          )
        }

        IconButton(
          onClick = { webViewRef?.reload() },
          modifier = Modifier.size(36.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = "Reload",
            tint = VipAmberPrimary
          )
        }

        Spacer(modifier = Modifier.width(6.dp))

        // URL Field
        OutlinedTextField(
          value = inputUrlText,
          onValueChange = { inputUrlText = it },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = VipAmberPrimary,
            unfocusedBorderColor = if (isDark) Color(0x33FFB300) else Color(0x33FF8F00),
            focusedContainerColor = if (isDark) Color(0x22171B2A) else Color(0xFFF9F9F9),
            unfocusedContainerColor = if (isDark) Color(0x1A171B2A) else Color(0xFFFAFAFA)
          ),
          trailingIcon = {
            IconButton(
              onClick = {
                val formatted = if (!inputUrlText.startsWith("http://") && !inputUrlText.startsWith("https://")) {
                  "https://$inputUrlText"
                } else inputUrlText
                currentUrl = formatted
                webViewRef?.loadUrl(formatted)
              }
            ) {
              Icon(
                imageVector = Icons.Default.Language,
                contentDescription = "Go",
                tint = VipAmberPrimary
              )
            }
          },
          modifier = Modifier
            .weight(1f)
            .height(50.dp)
        )
      }

      // Quick Bookmark Shortcuts
      LazyRow(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(bookmarks) { bookmark ->
          GlassCard(
            isDark = isDark,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.clickable {
              inputUrlText = bookmark.url
              currentUrl = bookmark.url
              webViewRef?.loadUrl(bookmark.url)
            }
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(8.dp)
                  .clip(CircleShape)
                  .background(bookmark.color)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = bookmark.name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground
              )
            }
          }
        }
      }

      // Loading indicator
      if (isPageLoading && loadProgress < 100) {
        LinearProgressIndicator(
          progress = { loadProgress / 100f },
          modifier = Modifier
            .fillMaxWidth()
            .height(3.dp),
          color = VipAmberPrimary,
          trackColor = Color.Transparent
        )
      }

      // Web View Container
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
          .padding(bottom = 80.dp)
      ) {
        AndroidView(
          modifier = Modifier.fillMaxSize(),
          factory = { context ->
            WebView(context).apply {
              settings.javaScriptEnabled = true
              settings.domStorageEnabled = true
              settings.loadWithOverviewMode = true
              settings.useWideViewPort = true
              settings.userAgentString = "Mozilla/5.0 (Linux; Android 13; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"

              webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                  super.onPageStarted(view, url, favicon)
                  isPageLoading = true
                  url?.let {
                    currentUrl = it
                    inputUrlText = it
                  }
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                  super.onPageFinished(view, url)
                  isPageLoading = false
                  url?.let {
                    currentUrl = it
                    inputUrlText = it
                  }
                }
              }

              webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                  loadProgress = newProgress
                  if (newProgress >= 100) isPageLoading = false
                }
              }

              loadUrl(currentUrl)
              webViewRef = this
            }
          },
          update = { view ->
            webViewRef = view
          }
        )
      }
    }

    // Floating VIP Sniff & Download FAB
    FloatingActionButton(
      onClick = {
        val detectedUrl = webViewRef?.url ?: currentUrl
        viewModel.onUrlChange(detectedUrl)
        viewModel.extractMedia(detectedUrl)
        viewModel.setTab(0) // Return to home to display the quality modal
      },
      containerColor = VipAmberPrimary,
      contentColor = Color.Black,
      shape = CircleShape,
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(end = 20.dp, bottom = 96.dp)
        .testTag("sniff_download_fab")
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = Icons.Default.Download,
          contentDescription = "Download Current Page Video",
          modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Sniff & Download",
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp
        )
      }
    }
  }
}

private data class Bookmark(
  val name: String,
  val url: String,
  val color: Color
)
