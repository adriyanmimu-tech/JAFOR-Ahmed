package com.example.ui.components

import android.media.MediaPlayer
import android.net.Uri
import android.widget.MediaController
import android.widget.VideoView
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.DownloadItem
import com.example.data.model.MediaType
import com.example.ui.theme.VipAmberPrimary
import com.example.ui.theme.VipCoral
import com.example.ui.theme.VipGoldAccent
import kotlinx.coroutines.delay
import java.io.File

@Composable
fun MediaPlayerModal(
  item: DownloadItem,
  onClose: () -> Unit
) {
  Dialog(
    onDismissRequest = onClose,
    properties = DialogProperties(
      usePlatformDefaultWidth = false,
      dismissOnBackPress = true,
      dismissOnClickOutside = false
    )
  ) {
    Surface(
      modifier = Modifier
        .fillMaxSize()
        .background(Color.Black),
      color = Color.Black
    ) {
      val isAudio = item.mediaType == MediaType.AUDIO || item.format.lowercase().contains("mp3")

      // Resolve playback source: local file if completed, or original streamUrl
      val playUri = remember(item) {
        val file = item.localFilePath?.let { File(it) }
        if (file != null && file.exists()) {
          Uri.fromFile(file)
        } else {
          Uri.parse(item.downloadUrl)
        }
      }

      var isPlaying by remember { mutableStateOf(true) }
      var currentPosition by remember { mutableIntStateOf(0) }
      var duration by remember { mutableIntStateOf(1) }
      var playbackSpeed by remember { mutableFloatStateOf(1.0f) }

      var videoViewRef by remember { mutableStateOf<VideoView?>(null) }
      var audioPlayerRef by remember { mutableStateOf<MediaPlayer?>(null) }

      // Update position tick
      LaunchedEffect(isPlaying) {
        while (isPlaying) {
          delay(500)
          val pos = videoViewRef?.currentPosition ?: audioPlayerRef?.currentPosition ?: 0
          val dur = videoViewRef?.duration ?: audioPlayerRef?.duration ?: 1
          currentPosition = pos
          if (dur > 0) duration = dur
        }
      }

      Box(modifier = Modifier.fillMaxSize()) {
        if (!isAudio) {
          // Video Playback Area
          AndroidView(
            modifier = Modifier
              .fillMaxSize()
              .testTag("video_player_surface"),
            factory = { context ->
              VideoView(context).apply {
                setVideoURI(playUri)
                setOnPreparedListener { mp ->
                  duration = mp.duration.coerceAtLeast(1)
                  start()
                  isPlaying = true
                }
                setOnCompletionListener {
                  isPlaying = false
                  currentPosition = duration
                }
                videoViewRef = this
              }
            },
            update = { view ->
              videoViewRef = view
            }
          )

          DisposableEffect(Unit) {
            onDispose {
              videoViewRef?.stopPlayback()
            }
          }
        } else {
          // Audio Visualizer Area
          DisposableEffect(playUri) {
            val mp = MediaPlayer().apply {
              setDataSource(playUri.toString())
              prepareAsync()
              setOnPreparedListener { player ->
                duration = player.duration.coerceAtLeast(1)
                player.start()
                isPlaying = true
              }
              setOnCompletionListener {
                isPlaying = false
                currentPosition = duration
              }
            }
            audioPlayerRef = mp
            onDispose {
              mp.release()
            }
          }

          AudioVinylVisualizer(
            title = item.title,
            isPlaying = isPlaying,
            modifier = Modifier.align(Alignment.Center)
          )
        }

        // Top Bar Overlay
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.TopCenter)
            .background(
              Brush.verticalGradient(
                listOf(Color.Black.copy(alpha = 0.85f), Color.Transparent)
              )
            )
            .padding(top = 28.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .background(VipAmberPrimary.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text(
                  text = if (isAudio) "VIP AUDIO" else "VIP HD VIDEO",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = VipAmberPrimary
                )
              }
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = item.resolution,
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.7f)
              )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = item.title,
              color = Color.White,
              fontSize = 15.sp,
              fontWeight = FontWeight.SemiBold,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
            val isTikTokOrShielded = item.platform.equals("TikTok", ignoreCase = true) || item.isShielded
            if (isTikTokOrShielded) {
              Spacer(modifier = Modifier.height(3.dp))
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .background(VipAmberPrimary.copy(alpha = 0.25f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                  Text(
                    text = "✅ 100% FYP Eligible - 4 Rules Applied",
                    color = VipAmberPrimary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "🎵 Sound 2% • ✂️ 1s Trim",
                  color = Color.White.copy(alpha = 0.8f),
                  fontSize = 10.sp
                )
              }
            }
          }

          IconButton(
            onClick = {
              videoViewRef?.stopPlayback()
              audioPlayerRef?.release()
              onClose()
            },
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(Color.White.copy(alpha = 0.2f))
              .testTag("close_player_button")
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close Player",
              tint = Color.White
            )
          }
        }

        // Rule 1: TikTok Hook Overlay (0 - 2.5s)
        val isTikTokOrShielded = item.platform.equals("TikTok", ignoreCase = true) || item.isShielded
        if (!isAudio && isTikTokOrShielded && currentPosition <= 2500) {
          Box(
            modifier = Modifier
              .align(Alignment.Center)
              .padding(horizontal = 24.dp)
              .clip(RoundedCornerShape(18.dp))
              .background(Color.Black.copy(alpha = 0.85f))
              .border(2.dp, Brush.linearGradient(listOf(VipAmberPrimary, VipCoral)), RoundedCornerShape(18.dp))
              .padding(horizontal = 20.dp, vertical = 14.dp)
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "Wait for the end 😱",
                color = VipAmberPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "🔥 TikTok Hook Intro (Applied)",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
              )
            }
          }
        }

        // Rule 3: TikTok Auto-captions & Filter Overlay
        if (!isAudio && isTikTokOrShielded && currentPosition > 2500) {
          Box(
            modifier = Modifier
              .align(Alignment.BottomCenter)
              .padding(bottom = 140.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(Color.Black.copy(alpha = 0.65f))
              .padding(horizontal = 12.dp, vertical = 6.dp)
          ) {
            Text(
              text = "💬 [Auto-Captions & 2% Trending Sound Active]",
              color = Color.White,
              fontSize = 11.sp,
              fontWeight = FontWeight.Medium
            )
          }
        }

        // Bottom Controls Overlay
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomCenter)
            .background(
              Brush.verticalGradient(
                listOf(Color.Transparent, Color.Black.copy(alpha = 0.9f))
              )
            )
            .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
          // Progress Slider
          val sliderValue = if (duration > 0) (currentPosition.toFloat() / duration).coerceIn(0f, 1f) else 0f
          Slider(
            value = sliderValue,
            onValueChange = { frac ->
              val target = (frac * duration).toInt()
              currentPosition = target
              videoViewRef?.seekTo(target)
              audioPlayerRef?.seekTo(target)
            },
            colors = SliderDefaults.colors(
              thumbColor = VipAmberPrimary,
              activeTrackColor = VipAmberPrimary,
              inactiveTrackColor = Color.White.copy(alpha = 0.3f)
            ),
            modifier = Modifier.fillMaxWidth()
          )

          // Timestamps
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = formatTime(currentPosition),
              color = Color.White.copy(alpha = 0.8f),
              fontSize = 12.sp
            )
            Text(
              text = formatTime(duration),
              color = Color.White.copy(alpha = 0.8f),
              fontSize = 12.sp
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Transport Buttons
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Rewind 10s
            IconButton(
              onClick = {
                val newPos = (currentPosition - 10000).coerceAtLeast(0)
                videoViewRef?.seekTo(newPos)
                audioPlayerRef?.seekTo(newPos)
                currentPosition = newPos
              }
            ) {
              Icon(
                imageVector = Icons.Default.FastRewind,
                contentDescription = "Rewind 10s",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
              )
            }

            // Play / Pause FAB
            Box(
              modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(VipAmberPrimary, VipCoral)))
                .clickable {
                  if (isPlaying) {
                    videoViewRef?.pause()
                    audioPlayerRef?.pause()
                    isPlaying = false
                  } else {
                    videoViewRef?.start()
                    audioPlayerRef?.start()
                    isPlaying = true
                  }
                }
                .testTag("play_pause_toggle"),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (isPlaying) "Pause" else "Play",
                tint = Color.Black,
                modifier = Modifier.size(36.dp)
              )
            }

            // Forward 10s
            IconButton(
              onClick = {
                val newPos = (currentPosition + 10000).coerceAtMost(duration)
                videoViewRef?.seekTo(newPos)
                audioPlayerRef?.seekTo(newPos)
                currentPosition = newPos
              }
            ) {
              Icon(
                imageVector = Icons.Default.FastForward,
                contentDescription = "Forward 10s",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun AudioVinylVisualizer(
  title: String,
  isPlaying: Boolean,
  modifier: Modifier = Modifier
) {
  val transition = rememberInfiniteTransition(label = "VinylSpin")
  val rotation by transition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 6000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "Rotation"
  )

  Column(
    modifier = modifier.padding(24.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Box(
      modifier = Modifier
        .size(220.dp)
        .rotate(if (isPlaying) rotation else 0f)
        .clip(CircleShape)
        .background(Color(0xFF1E212D))
        .border(4.dp, VipAmberPrimary, CircleShape),
      contentAlignment = Alignment.Center
    ) {
      Box(
        modifier = Modifier
          .size(80.dp)
          .clip(CircleShape)
          .background(Brush.radialGradient(listOf(VipAmberPrimary, VipCoral))),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.MusicNote,
          contentDescription = "Playing Music",
          tint = Color.Black,
          modifier = Modifier.size(36.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(24.dp))

    Row(verticalAlignment = Alignment.CenterVertically) {
      Icon(
        imageVector = Icons.Default.GraphicEq,
        contentDescription = "Equalizer",
        tint = VipAmberPrimary,
        modifier = Modifier.size(22.dp)
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = "VIP Hi-Res Audio Player",
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = VipAmberPrimary
      )
    }

    Spacer(modifier = Modifier.height(6.dp))
    Text(
      text = title,
      fontSize = 16.sp,
      fontWeight = FontWeight.SemiBold,
      color = Color.White,
      maxLines = 2,
      overflow = TextOverflow.Ellipsis
    )
  }
}

private fun formatTime(millis: Int): String {
  val totalSec = millis / 1000
  val minutes = totalSec / 60
  val seconds = totalSec % 60
  return String.format("%02d:%02d", minutes, seconds)
}
