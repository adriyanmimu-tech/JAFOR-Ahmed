package com.example

import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.runtime.LaunchedEffect
import androidx.core.content.ContextCompat
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.data.model.DownloadStatus
import com.example.ui.components.MediaPlayerModal
import com.example.ui.screens.BrowserScreen
import com.example.ui.screens.DownloadsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.theme.VidMaxVipTheme
import com.example.ui.theme.VipAmberDark
import com.example.ui.theme.VipAmberPrimary
import com.example.ui.theme.VipCardBorderDark
import com.example.ui.theme.VipCardBorderLight
import com.example.ui.viewmodel.DownloaderViewModel

class MainActivity : ComponentActivity() {

  private val viewModel: DownloaderViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      val isDark by viewModel.isDarkTheme.collectAsState()
      val activeTab by viewModel.activeTab.collectAsState()
      val activePlayingItem by viewModel.activePlayingItem.collectAsState()
      val allDownloads by viewModel.allDownloads.collectAsState()

      val activeCount = allDownloads.count {
        it.status == DownloadStatus.DOWNLOADING || it.status == DownloadStatus.PAUSED
      }

      val lifecycleOwner = LocalLifecycleOwner.current
      DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
          if (event == Lifecycle.Event.ON_RESUME) {
            checkClipboard()
          }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
          lifecycleOwner.lifecycle.removeObserver(observer)
        }
      }

      val context = androidx.compose.ui.platform.LocalContext.current
      val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
      ) { /* Permission handled gracefully */ }

      LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
          if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
          }
        }
      }

      VidMaxVipTheme(darkTheme = isDark) {
        // Back press handling
        BackHandler(enabled = activeTab != 0) {
          viewModel.setTab(0)
        }

        Scaffold(
          modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
          bottomBar = {
            VipBottomNavigationBar(
              activeTab = activeTab,
              activeDownloadCount = activeCount,
              isDark = isDark,
              onSelectTab = { viewModel.setTab(it) }
            )
          }
        ) { innerPadding ->
          Box(
            modifier = Modifier
              .fillMaxSize()
              .padding(innerPadding)
          ) {
            Crossfade(targetState = activeTab, label = "TabTransition") { tab ->
              when (tab) {
                0 -> HomeScreen(
                  viewModel = viewModel,
                  isDark = isDark,
                  onNavigateToTab = { viewModel.setTab(it) }
                )
                1 -> BrowserScreen(
                  viewModel = viewModel,
                  isDark = isDark,
                  onDownloadUrlDetected = { url ->
                    viewModel.onUrlChange(url)
                    viewModel.extractMedia(url)
                    viewModel.setTab(0)
                  }
                )
                2 -> DownloadsScreen(
                  viewModel = viewModel,
                  isDark = isDark,
                  onPlayItem = { viewModel.openPlayer(it) }
                )
              }
            }

            // In-app Video & Audio Player Modal
            activePlayingItem?.let { playingItem ->
              MediaPlayerModal(
                item = playingItem,
                onClose = { viewModel.closePlayer() }
              )
            }
          }
        }
      }
    }
  }

  private fun checkClipboard() {
    try {
      val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
      val clip = clipboard.primaryClip
      if (clip != null && clip.itemCount > 0) {
        val text = clip.getItemAt(0).text?.toString()
        viewModel.checkClipboard(text)
      }
    } catch (_: Exception) {}
  }
}

@Composable
fun VipBottomNavigationBar(
  activeTab: Int,
  activeDownloadCount: Int,
  isDark: Boolean,
  onSelectTab: (Int) -> Unit
) {
  val surfaceBrush = if (isDark) {
    Brush.verticalGradient(
      listOf(
        Color(0x551E2B42),
        Color(0x350F1A2D)
      )
    )
  } else {
    Brush.verticalGradient(
      listOf(
        Color(0xCCFFFFFF),
        Color(0x99F0F4FA)
      )
    )
  }

  val borderBrush = Brush.horizontalGradient(
    if (isDark) listOf(Color(0x99FFFFFF), Color(0x5000F2FE), Color(0x3010B981), Color(0x70FFFFFF))
    else listOf(Color(0xE0FFFFFF), Color(0x6000F2FE), Color(0x90FFFFFF))
  )

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .navigationBarsPadding()
      .padding(horizontal = 18.dp, vertical = 6.dp)
      .clip(RoundedCornerShape(26.dp))
      .background(surfaceBrush)
      .border(1.3.dp, borderBrush, RoundedCornerShape(26.dp))
  ) {
    // Top specular highlight line
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(1.dp)
        .background(
          Brush.horizontalGradient(
            listOf(
              Color.Transparent,
              Color(0x80FFFFFF),
              Color(0x9900F2FE),
              Color(0x80FFFFFF),
              Color.Transparent
            )
          )
        )
    )

    NavigationBar(
      containerColor = Color.Transparent,
      tonalElevation = 0.dp,
      modifier = Modifier
        .fillMaxWidth()
        .height(64.dp)
        .testTag("vip_bottom_nav_bar")
    ) {
      // Tab 0: Home / Downloader
      NavigationBarItem(
        selected = activeTab == 0,
        onClick = { onSelectTab(0) },
        icon = {
          Icon(
            imageVector = if (activeTab == 0) Icons.Filled.Home else Icons.Outlined.Home,
            contentDescription = "Home",
            modifier = Modifier.size(24.dp)
          )
        },
        label = {
          Text(
            text = "Downloader",
            fontSize = 11.sp,
            fontWeight = if (activeTab == 0) FontWeight.Bold else FontWeight.Medium
          )
        },
        colors = NavigationBarItemDefaults.colors(
          selectedIconColor = Color.Black,
          selectedTextColor = Color(0xFF00F2FE),
          indicatorColor = Color(0xFF00F2FE),
          unselectedIconColor = if (isDark) Color(0xFFA5ABB8) else Color(0xFF6B7280),
          unselectedTextColor = if (isDark) Color(0xFFA5ABB8) else Color(0xFF6B7280)
        )
      )

      // Tab 1: Browser / Explorer
      NavigationBarItem(
        selected = activeTab == 1,
        onClick = { onSelectTab(1) },
        icon = {
          Icon(
            imageVector = if (activeTab == 1) Icons.Filled.Language else Icons.Outlined.Language,
            contentDescription = "Browser",
            modifier = Modifier.size(24.dp)
          )
        },
        label = {
          Text(
            text = "Browser",
            fontSize = 11.sp,
            fontWeight = if (activeTab == 1) FontWeight.Bold else FontWeight.Medium
          )
        },
        colors = NavigationBarItemDefaults.colors(
          selectedIconColor = Color.Black,
          selectedTextColor = Color(0xFF00F2FE),
          indicatorColor = Color(0xFF00F2FE),
          unselectedIconColor = if (isDark) Color(0xFFA5ABB8) else Color(0xFF6B7280),
          unselectedTextColor = if (isDark) Color(0xFFA5ABB8) else Color(0xFF6B7280)
        )
      )

      // Tab 2: Downloads Manager & Player
      NavigationBarItem(
        selected = activeTab == 2,
        onClick = { onSelectTab(2) },
        icon = {
          BadgedBox(
            badge = {
              if (activeDownloadCount > 0) {
                Badge(containerColor = Color(0xFF00F2FE)) {
                  Text("$activeDownloadCount", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
              }
            }
          ) {
            Icon(
              imageVector = if (activeTab == 2) Icons.Filled.Folder else Icons.Outlined.Folder,
              contentDescription = "Downloads",
              modifier = Modifier.size(24.dp)
            )
          }
        },
        label = {
          Text(
            text = "Downloads",
            fontSize = 11.sp,
            fontWeight = if (activeTab == 2) FontWeight.Bold else FontWeight.Medium
          )
        },
        colors = NavigationBarItemDefaults.colors(
          selectedIconColor = Color.Black,
          selectedTextColor = Color(0xFF00F2FE),
          indicatorColor = Color(0xFF00F2FE),
          unselectedIconColor = if (isDark) Color(0xFFA5ABB8) else Color(0xFF6B7280),
          unselectedTextColor = if (isDark) Color(0xFFA5ABB8) else Color(0xFF6B7280)
        )
      )
    }
  }
}

