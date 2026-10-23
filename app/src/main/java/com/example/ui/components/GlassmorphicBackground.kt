package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Animated Social Media Icons Background
 * Replaces the static green circles/spheres with an interactive, lively floating
 * animated field of social media platform icons (YouTube, TikTok, Facebook, Instagram,
 * WhatsApp, Twitter/X, Pinterest, Snapchat, Reddit) drifting smoothly.
 */
@Composable
fun GlassmorphicBackground(
  isDark: Boolean = true,
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit
) {
  val baseGradient = if (isDark) {
    Brush.verticalGradient(
      listOf(
        Color(0xFF06090D),
        Color(0xFF0B1218),
        Color(0xFF070B10)
      )
    )
  } else {
    Brush.verticalGradient(
      listOf(
        Color(0xFFF1F5F9),
        Color(0xFFE2E8F0),
        Color(0xFFF8FAFC)
      )
    )
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(baseGradient)
  ) {
    // --- ANIMATED FLOATING SOCIAL MEDIA ICONS LAYER ---
    SocialMediaFloatingAnimationLayer(isDark = isDark)

    // Foreground Content
    content()
  }
}

private data class SocialFloatConfig(
  val platform: String,
  val xFraction: Float,
  val yFraction: Float,
  val sizeDp: Dp,
  val floatDistY: Dp,
  val floatDistX: Dp,
  val durationMs: Int,
  val delayMs: Int,
  val brandColor: Color,
  val maxRotation: Float = 14f
)

@Composable
private fun SocialMediaFloatingAnimationLayer(isDark: Boolean) {
  val iconsList = listOf(
    SocialFloatConfig(
      platform = "youtube",
      xFraction = 0.10f,
      yFraction = 0.08f,
      sizeDp = 44.dp,
      floatDistY = 16.dp,
      floatDistX = 8.dp,
      durationMs = 3800,
      delayMs = 0,
      brandColor = Color(0xFFFF0000),
      maxRotation = 10f
    ),
    SocialFloatConfig(
      platform = "tiktok",
      xFraction = 0.84f,
      yFraction = 0.12f,
      sizeDp = 40.dp,
      floatDistY = 20.dp,
      floatDistX = 10.dp,
      durationMs = 4200,
      delayMs = 400,
      brandColor = Color(0xFF00F2FE),
      maxRotation = -12f
    ),
    SocialFloatConfig(
      platform = "instagram",
      xFraction = 0.48f,
      yFraction = 0.18f,
      sizeDp = 42.dp,
      floatDistY = 14.dp,
      floatDistX = 6.dp,
      durationMs = 3400,
      delayMs = 800,
      brandColor = Color(0xFFE1306C),
      maxRotation = 8f
    ),
    SocialFloatConfig(
      platform = "facebook",
      xFraction = 0.16f,
      yFraction = 0.32f,
      sizeDp = 46.dp,
      floatDistY = 18.dp,
      floatDistX = 12.dp,
      durationMs = 4600,
      delayMs = 200,
      brandColor = Color(0xFF1877F2),
      maxRotation = -8f
    ),
    SocialFloatConfig(
      platform = "whatsapp",
      xFraction = 0.85f,
      yFraction = 0.36f,
      sizeDp = 42.dp,
      floatDistY = 22.dp,
      floatDistX = 8.dp,
      durationMs = 3900,
      delayMs = 600,
      brandColor = Color(0xFF25D366),
      maxRotation = 12f
    ),
    SocialFloatConfig(
      platform = "twitter",
      xFraction = 0.08f,
      yFraction = 0.52f,
      sizeDp = 38.dp,
      floatDistY = 16.dp,
      floatDistX = 10.dp,
      durationMs = 4400,
      delayMs = 1000,
      brandColor = Color(0xFF1DA1F2),
      maxRotation = -10f
    ),
    SocialFloatConfig(
      platform = "pinterest",
      xFraction = 0.88f,
      yFraction = 0.58f,
      sizeDp = 40.dp,
      floatDistY = 20.dp,
      floatDistX = 12.dp,
      durationMs = 4100,
      delayMs = 300,
      brandColor = Color(0xFFBD081C),
      maxRotation = 14f
    ),
    SocialFloatConfig(
      platform = "snapchat",
      xFraction = 0.22f,
      yFraction = 0.70f,
      sizeDp = 40.dp,
      floatDistY = 18.dp,
      floatDistX = 8.dp,
      durationMs = 4800,
      delayMs = 700,
      brandColor = Color(0xFFFFFC00),
      maxRotation = -14f
    ),
    SocialFloatConfig(
      platform = "reddit",
      xFraction = 0.78f,
      yFraction = 0.76f,
      sizeDp = 38.dp,
      floatDistY = 14.dp,
      floatDistX = 10.dp,
      durationMs = 3600,
      delayMs = 500,
      brandColor = Color(0xFFFF4500),
      maxRotation = 10f
    ),
    SocialFloatConfig(
      platform = "youtube",
      xFraction = 0.12f,
      yFraction = 0.88f,
      sizeDp = 36.dp,
      floatDistY = 16.dp,
      floatDistX = 8.dp,
      durationMs = 4300,
      delayMs = 900,
      brandColor = Color(0xFFFF0000),
      maxRotation = -8f
    ),
    SocialFloatConfig(
      platform = "instagram",
      xFraction = 0.86f,
      yFraction = 0.92f,
      sizeDp = 38.dp,
      floatDistY = 18.dp,
      floatDistX = 10.dp,
      durationMs = 4000,
      delayMs = 400,
      brandColor = Color(0xFFE1306C),
      maxRotation = 12f
    ),
    SocialFloatConfig(
      platform = "tiktok",
      xFraction = 0.50f,
      yFraction = 0.90f,
      sizeDp = 36.dp,
      floatDistY = 14.dp,
      floatDistX = 6.dp,
      durationMs = 3700,
      delayMs = 1100,
      brandColor = Color(0xFF00F2FE),
      maxRotation = -6f
    )
  )

  BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
    val totalWidth = maxWidth
    val totalHeight = maxHeight

    iconsList.forEach { item ->
      FloatingSocialIcon(
        config = item,
        totalWidth = totalWidth,
        totalHeight = totalHeight,
        isDark = isDark
      )
    }
  }
}

@Composable
private fun FloatingSocialIcon(
  config: SocialFloatConfig,
  totalWidth: Dp,
  totalHeight: Dp,
  isDark: Boolean
) {
  val infiniteTransition = rememberInfiniteTransition(label = "social_${config.platform}_${config.xFraction}")

  val floatY by infiniteTransition.animateFloat(
    initialValue = -config.floatDistY.value,
    targetValue = config.floatDistY.value,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = config.durationMs, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "floatY"
  )

  val driftX by infiniteTransition.animateFloat(
    initialValue = -config.floatDistX.value,
    targetValue = config.floatDistX.value,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = (config.durationMs * 1.3f).toInt(), easing = LinearOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "driftX"
  )

  val rotation by infiniteTransition.animateFloat(
    initialValue = -config.maxRotation,
    targetValue = config.maxRotation,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = (config.durationMs * 1.5f).toInt(), easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "rotation"
  )

  val scale by infiniteTransition.animateFloat(
    initialValue = 0.94f,
    targetValue = 1.06f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = (config.durationMs * 1.2f).toInt(), easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "scale"
  )

  val alphaPulse by infiniteTransition.animateFloat(
    initialValue = if (isDark) 0.65f else 0.55f,
    targetValue = if (isDark) 0.90f else 0.85f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = config.durationMs, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "alpha"
  )

  val posX = (totalWidth * config.xFraction) - (config.sizeDp / 2) + driftX.dp
  val posY = (totalHeight * config.yFraction) - (config.sizeDp / 2) + floatY.dp

  Box(
    modifier = Modifier
      .offset(x = posX, y = posY)
      .size(config.sizeDp + 16.dp)
      .scale(scale)
      .rotate(rotation)
      .alpha(alphaPulse),
    contentAlignment = Alignment.Center
  ) {
    // Soft Ambient Brand Aura Glow behind icon
    Box(
      modifier = Modifier
        .size(config.sizeDp + 12.dp)
        .clip(CircleShape)
        .background(
          Brush.radialGradient(
            colors = listOf(
              config.brandColor.copy(alpha = if (isDark) 0.40f else 0.25f),
              Color.Transparent
            )
          )
        )
    )

    // Glass Orb Capsule
    Box(
      modifier = Modifier
        .size(config.sizeDp)
        .clip(CircleShape)
        .background(
          if (isDark) Color(0x3516202A) else Color(0x70FFFFFF)
        )
        .border(
          width = 1.2.dp,
          brush = Brush.linearGradient(
            colors = listOf(
              if (isDark) Color(0x60FFFFFF) else Color(0x90FFFFFF),
              config.brandColor.copy(alpha = 0.5f),
              if (isDark) Color(0x20000000) else Color(0x20FFFFFF)
            )
          ),
          shape = CircleShape
        ),
      contentAlignment = Alignment.Center
    ) {
      PlatformBrandIcon(
        platform = config.platform,
        size = config.sizeDp * 0.62f
      )
    }
  }
}
