package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Authentic brand icons for social media platforms drawn via Canvas
 * for zero APK overhead, extreme crispness, and pixel-perfect brand identity.
 */
@Composable
fun PlatformBrandIcon(
  platform: String,
  modifier: Modifier = Modifier,
  size: Dp = 24.dp
) {
  val cleanPlatform = platform.trim().lowercase()

  Box(
    modifier = modifier.size(size),
    contentAlignment = Alignment.Center
  ) {
    when {
      cleanPlatform.contains("facebook") || cleanPlatform.contains("fb") -> {
        FacebookIcon(size = size)
      }
      cleanPlatform.contains("tiktok") || cleanPlatform.contains("tik") -> {
        TikTokIcon(size = size)
      }
      cleanPlatform.contains("instagram") || cleanPlatform.contains("insta") || cleanPlatform.contains("ig") -> {
        InstagramIcon(size = size)
      }
      cleanPlatform.contains("youtube") || cleanPlatform.contains("yt") || cleanPlatform.contains("shorts") -> {
        YouTubeIcon(size = size)
      }
      cleanPlatform.contains("twitter") || cleanPlatform.contains("x") -> {
        XTwitterIcon(size = size)
      }
      cleanPlatform.contains("pinterest") || cleanPlatform.contains("pin") -> {
        PinterestIcon(size = size)
      }
      cleanPlatform.contains("reddit") -> {
        RedditIcon(size = size)
      }
      cleanPlatform.contains("snapchat") || cleanPlatform.contains("snap") -> {
        SnapchatIcon(size = size)
      }
      cleanPlatform.contains("vimeo") -> {
        VimeoIcon(size = size)
      }
      cleanPlatform.contains("whatsapp") || cleanPlatform.contains("wa") -> {
        WhatsAppIcon(size = size)
      }
      else -> {
        GenericPlatformIcon(size = size)
      }
    }
  }
}

/**
 * Official WhatsApp Green Icon
 */
@Composable
fun WhatsAppIcon(size: Dp) {
  Canvas(modifier = Modifier.size(size)) {
    val radius = this.size.minDimension / 2f
    val center = Offset(this.size.width / 2f, this.size.height / 2f)

    // WhatsApp Emerald Green Circle
    drawCircle(
      color = Color(0xFF25D366),
      radius = radius,
      center = center
    )

    val w = this.size.width
    val h = this.size.height

    // Speech bubble with tail
    val bubblePath = Path().apply {
      val bubbleRadius = w * 0.30f
      val bubbleCenter = Offset(w * 0.5f, h * 0.47f)
      addOval(androidx.compose.ui.geometry.Rect(
        center = bubbleCenter,
        radius = bubbleRadius
      ))
      moveTo(w * 0.36f, h * 0.68f)
      lineTo(w * 0.22f, h * 0.77f)
      lineTo(w * 0.29f, h * 0.60f)
      close()
    }
    drawPath(bubblePath, Color.White)

    // Inner green telephone handset
    val phonePath = Path().apply {
      moveTo(w * 0.40f, h * 0.38f)
      cubicTo(w * 0.38f, h * 0.40f, w * 0.38f, h * 0.44f, w * 0.42f, h * 0.50f)
      cubicTo(w * 0.46f, h * 0.56f, w * 0.50f, h * 0.58f, w * 0.54f, h * 0.56f)
      lineTo(w * 0.58f, h * 0.52f)
      lineTo(w * 0.54f, h * 0.46f)
      lineTo(w * 0.51f, h * 0.48f)
      cubicTo(w * 0.49f, h * 0.49f, w * 0.47f, h * 0.47f, w * 0.45f, h * 0.45f)
      lineTo(w * 0.42f, h * 0.42f)
      lineTo(w * 0.44f, h * 0.39f)
      close()
    }
    drawPath(phonePath, Color(0xFF25D366))
  }
}

/**
 * Official Facebook "f" logo inside royal blue circle
 */
@Composable
fun FacebookIcon(size: Dp) {
  Canvas(modifier = Modifier.size(size)) {
    val radius = this.size.minDimension / 2f
    val center = Offset(this.size.width / 2f, this.size.height / 2f)

    // Facebook Blue Circle
    drawCircle(
      color = Color(0xFF1877F2),
      radius = radius,
      center = center
    )

    // Bold "f"
    val w = this.size.width
    val h = this.size.height

    val path = Path().apply {
      val stemLeft = w * 0.44f
      val stemRight = w * 0.60f
      val crossTop = h * 0.40f
      val crossBottom = h * 0.52f
      val crossLeft = w * 0.30f
      val crossRight = w * 0.74f

      // Start from bottom stem
      moveTo(stemLeft, h * 0.95f)
      lineTo(stemRight, h * 0.95f)
      lineTo(stemRight, crossBottom)
      lineTo(crossRight, crossBottom)
      lineTo(crossRight * 0.96f, crossTop)
      lineTo(stemRight, crossTop)
      lineTo(stemRight, h * 0.30f)
      // Top hook curve
      cubicTo(
        stemRight, h * 0.18f,
        w * 0.68f, h * 0.14f,
        w * 0.82f, h * 0.14f
      )
      lineTo(w * 0.82f, h * 0.28f)
      cubicTo(
        w * 0.74f, h * 0.28f,
        stemLeft, h * 0.29f,
        stemLeft, h * 0.36f
      )
      lineTo(stemLeft, crossTop)
      lineTo(crossLeft, crossTop)
      lineTo(crossLeft, crossBottom)
      lineTo(stemLeft, crossBottom)
      close()
    }
    drawPath(path, Color.White)
  }
}

/**
 * Official TikTok Logo with the signature Cyan & Red offset musical note
 */
@Composable
fun TikTokIcon(size: Dp) {
  Canvas(modifier = Modifier.size(size)) {
    val radius = this.size.minDimension / 2f
    val center = Offset(this.size.width / 2f, this.size.height / 2f)

    // TikTok black background
    drawCircle(
      color = Color(0xFF010101),
      radius = radius,
      center = center
    )

    val w = this.size.width
    val h = this.size.height

    fun drawNote(offsetX: Float, offsetY: Float, color: Color) {
      val path = Path().apply {
        // Base musical note note-head oval
        val headCx = w * 0.40f + offsetX
        val headCy = h * 0.68f + offsetY
        val rx = w * 0.18f
        val ry = h * 0.14f

        moveTo(headCx + rx, headCy)
        cubicTo(
          headCx + rx, headCy + ry,
          headCx - rx, headCy + ry,
          headCx - rx, headCy
        )
        cubicTo(
          headCx - rx, headCy - ry,
          headCx + rx * 0.6f, headCy - ry * 0.8f,
          headCx + rx * 0.5f, headCy - ry * 0.2f
        )
        // Stem going up
        val stemX = w * 0.52f + offsetX
        lineTo(stemX, h * 0.25f + offsetY)
        // Flag going right & up
        cubicTo(
          w * 0.58f + offsetX, h * 0.36f + offsetY,
          w * 0.72f + offsetX, h * 0.38f + offsetY,
          w * 0.82f + offsetX, h * 0.38f + offsetY
        )
        lineTo(w * 0.82f + offsetX, h * 0.26f + offsetY)
        cubicTo(
          w * 0.70f + offsetX, h * 0.26f + offsetY,
          w * 0.62f + offsetX, h * 0.16f + offsetY,
          w * 0.62f + offsetX, h * 0.14f + offsetY
        )
        lineTo(w * 0.44f + offsetX, h * 0.14f + offsetY)
        lineTo(w * 0.44f + offsetX, h * 0.58f + offsetY)
        cubicTo(
          w * 0.38f + offsetX, h * 0.55f + offsetY,
          headCx - rx * 0.2f, h * 0.56f + offsetY,
          headCx - rx, headCy
        )
        close()
      }
      drawPath(path, color)
    }

    // Cyan chromatic aberration offset
    drawNote(-w * 0.035f, -h * 0.02f, Color(0xFF00F2FE))
    // Magenta chromatic aberration offset
    drawNote(w * 0.035f, h * 0.02f, Color(0xFFFE2C55))
    // Crisp White main body
    drawNote(0f, 0f, Color.White)
  }
}

/**
 * Official Instagram Gradient Rounded Camera Icon
 */
@Composable
fun InstagramIcon(size: Dp) {
  Canvas(modifier = Modifier.size(size)) {
    val w = this.size.width
    val h = this.size.height

    // Instagram Iconic vibrant gradient
    val igGradient = Brush.linearGradient(
      colors = listOf(
        Color(0xFF833AB4), // Purple
        Color(0xFFFD1D1D), // Crimson
        Color(0xFFFCB045)  // Warm Gold / Orange
      ),
      start = Offset(0f, h),
      end = Offset(w, 0f)
    )

    drawRoundRect(
      brush = igGradient,
      topLeft = Offset(0f, 0f),
      size = Size(w, h),
      cornerRadius = CornerRadius(w * 0.28f, h * 0.28f)
    )

    // Outer Camera Rounded Rectangle (White line)
    val strokeWidth = w * 0.08f
    val pad = w * 0.22f
    drawRoundRect(
      color = Color.White,
      topLeft = Offset(pad, pad),
      size = Size(w - 2 * pad, h - 2 * pad),
      cornerRadius = CornerRadius(w * 0.18f, h * 0.18f),
      style = Stroke(width = strokeWidth)
    )

    // Center Lens Circle
    drawCircle(
      color = Color.White,
      radius = w * 0.16f,
      center = Offset(w / 2f, h / 2f),
      style = Stroke(width = strokeWidth)
    )

    // Flash dot at top right
    drawCircle(
      color = Color.White,
      radius = strokeWidth * 0.65f,
      center = Offset(w * 0.68f, h * 0.32f),
      style = Fill
    )
  }
}

/**
 * Official YouTube Red rounded rectangle with white triangle play button
 */
@Composable
fun YouTubeIcon(size: Dp) {
  Canvas(modifier = Modifier.size(size)) {
    val w = this.size.width
    val h = this.size.height

    // Red pill container
    drawRoundRect(
      color = Color(0xFFFF0000),
      topLeft = Offset(0f, h * 0.12f),
      size = Size(w, h * 0.76f),
      cornerRadius = CornerRadius(w * 0.25f, h * 0.25f)
    )

    // Centered White Equilateral Play Triangle
    val triangle = Path().apply {
      val left = w * 0.40f
      val right = w * 0.68f
      val top = h * 0.35f
      val bottom = h * 0.65f
      val centerY = h * 0.50f

      moveTo(left, top)
      lineTo(right, centerY)
      lineTo(left, bottom)
      close()
    }
    drawPath(triangle, Color.White)
  }
}

/**
 * Official X / Twitter Black Circle with White Stencil 𝕏
 */
@Composable
fun XTwitterIcon(size: Dp) {
  Canvas(modifier = Modifier.size(size)) {
    val radius = this.size.minDimension / 2f
    val center = Offset(this.size.width / 2f, this.size.height / 2f)

    drawCircle(
      color = Color(0xFF000000),
      radius = radius,
      center = center
    )

    val w = this.size.width
    val h = this.size.height

    // Draw the X shape lines
    val strokeWidth = w * 0.10f
    // Top-left to bottom-right
    drawLine(
      color = Color.White,
      start = Offset(w * 0.28f, h * 0.28f),
      end = Offset(w * 0.72f, h * 0.72f),
      strokeWidth = strokeWidth * 1.3f
    )
    // Top-right to bottom-left
    drawLine(
      color = Color.White,
      start = Offset(w * 0.72f, h * 0.28f),
      end = Offset(w * 0.28f, h * 0.72f),
      strokeWidth = strokeWidth
    )
  }
}

/**
 * Official Pinterest Red Circle with White Script "P"
 */
@Composable
fun PinterestIcon(size: Dp) {
  Canvas(modifier = Modifier.size(size)) {
    val radius = this.size.minDimension / 2f
    val center = Offset(this.size.width / 2f, this.size.height / 2f)

    drawCircle(
      color = Color(0xFFE60023),
      radius = radius,
      center = center
    )

    val w = this.size.width
    val h = this.size.height

    // Draw "P"
    val pPath = Path().apply {
      moveTo(w * 0.44f, h * 0.88f)
      lineTo(w * 0.48f, h * 0.20f)
      cubicTo(
        w * 0.68f, h * 0.20f,
        w * 0.76f, h * 0.32f,
        w * 0.72f, h * 0.46f
      )
      cubicTo(
        w * 0.68f, h * 0.58f,
        w * 0.52f, h * 0.60f,
        w * 0.44f, h * 0.54f
      )
      close()
    }
    drawPath(pPath, Color.White, style = Stroke(width = w * 0.10f))
  }
}

/**
 * Official Vimeo Cyan Circle with White "v"
 */
@Composable
fun VimeoIcon(size: Dp) {
  Canvas(modifier = Modifier.size(size)) {
    val radius = this.size.minDimension / 2f
    val center = Offset(this.size.width / 2f, this.size.height / 2f)

    drawCircle(
      color = Color(0xFF1AB7EA),
      radius = radius,
      center = center
    )

    val w = this.size.width
    val h = this.size.height

    val vPath = Path().apply {
      moveTo(w * 0.26f, h * 0.35f)
      lineTo(w * 0.48f, h * 0.70f)
      lineTo(w * 0.74f, h * 0.35f)
    }
    drawPath(vPath, Color.White, style = Stroke(width = w * 0.12f))
  }
}

/**
 * Fallback Generic Video Icon
 */
@Composable
fun GenericPlatformIcon(size: Dp) {
  Canvas(modifier = Modifier.size(size)) {
    val radius = this.size.minDimension / 2f
    val center = Offset(this.size.width / 2f, this.size.height / 2f)

    drawCircle(
      color = Color(0xFF374151),
      radius = radius,
      center = center
    )

    val w = this.size.width
    val h = this.size.height

    // Play icon
    val triangle = Path().apply {
      moveTo(w * 0.40f, h * 0.32f)
      lineTo(w * 0.68f, h * 0.50f)
      lineTo(w * 0.40f, h * 0.68f)
      close()
    }
    drawPath(triangle, Color.White)
  }
}

/**
 * Official Reddit Orange Circle with White Snoo Face
 */
@Composable
fun RedditIcon(size: Dp) {
  Canvas(modifier = Modifier.size(size)) {
    val radius = this.size.minDimension / 2f
    val center = Offset(this.size.width / 2f, this.size.height / 2f)

    drawCircle(
      color = Color(0xFFFF4500),
      radius = radius,
      center = center
    )

    val w = this.size.width
    val h = this.size.height

    // Snoo head oval
    val headRect = Path().apply {
      addOval(
        androidx.compose.ui.geometry.Rect(
          left = w * 0.22f,
          top = h * 0.36f,
          right = w * 0.78f,
          bottom = h * 0.76f
        )
      )
    }
    drawPath(headRect, Color.White)

    // Left ear
    drawCircle(
      color = Color.White,
      radius = w * 0.08f,
      center = Offset(w * 0.22f, h * 0.44f)
    )
    // Right ear
    drawCircle(
      color = Color.White,
      radius = w * 0.08f,
      center = Offset(w * 0.78f, h * 0.44f)
    )

    // Antenna line
    drawLine(
      color = Color.White,
      start = Offset(w * 0.50f, h * 0.36f),
      end = Offset(w * 0.62f, h * 0.22f),
      strokeWidth = w * 0.05f
    )
    // Antenna tip
    drawCircle(
      color = Color.White,
      radius = w * 0.055f,
      center = Offset(w * 0.66f, h * 0.22f)
    )

    // Orange eyes
    drawCircle(
      color = Color(0xFFFF4500),
      radius = w * 0.05f,
      center = Offset(w * 0.38f, h * 0.54f)
    )
    drawCircle(
      color = Color(0xFFFF4500),
      radius = w * 0.05f,
      center = Offset(w * 0.62f, h * 0.54f)
    )
  }
}

/**
 * Official Snapchat Yellow Icon with White Ghost
 */
@Composable
fun SnapchatIcon(size: Dp) {
  Canvas(modifier = Modifier.size(size)) {
    val radius = this.size.minDimension / 2f
    val center = Offset(this.size.width / 2f, this.size.height / 2f)

    drawCircle(
      color = Color(0xFFFFFC00),
      radius = radius,
      center = center
    )

    val w = this.size.width
    val h = this.size.height

    val ghost = Path().apply {
      moveTo(w * 0.50f, h * 0.24f)
      cubicTo(w * 0.36f, h * 0.24f, w * 0.32f, h * 0.38f, w * 0.32f, h * 0.52f)
      // left arm
      cubicTo(w * 0.24f, h * 0.52f, w * 0.22f, h * 0.58f, w * 0.28f, h * 0.62f)
      cubicTo(w * 0.34f, h * 0.64f, w * 0.34f, h * 0.66f, w * 0.32f, h * 0.74f)
      // bottom wavy skirt
      cubicTo(w * 0.38f, h * 0.70f, w * 0.44f, h * 0.76f, w * 0.50f, h * 0.72f)
      cubicTo(w * 0.56f, h * 0.76f, w * 0.62f, h * 0.70f, w * 0.68f, h * 0.74f)
      // right arm
      cubicTo(w * 0.66f, h * 0.66f, w * 0.66f, h * 0.64f, w * 0.72f, h * 0.62f)
      cubicTo(w * 0.78f, h * 0.58f, w * 0.76f, h * 0.52f, w * 0.68f, h * 0.52f)
      cubicTo(w * 0.68f, h * 0.38f, w * 0.64f, h * 0.24f, w * 0.50f, h * 0.24f)
      close()
    }
    drawPath(ghost, Color.White)
    drawPath(ghost, Color.Black.copy(alpha = 0.75f), style = Stroke(width = w * 0.035f))
  }
}

/**
 * 3D Glass Bubble Platform Badge (inspired directly by reference screenshot)
 * Features a spherical translucent frosted glass bubble with specular rim highlight,
 * light refraction, and brand icon inside.
 */
@Composable
fun GlassBubblePlatformBadge(
  platform: String,
  modifier: Modifier = Modifier,
  bubbleSize: Dp = 56.dp,
  iconSize: Dp = 30.dp,
  isSelected: Boolean = false,
  onClick: (() -> Unit)? = null
) {
  val clickModifier = if (onClick != null) {
    Modifier.clickable(onClick = onClick)
  } else {
    Modifier
  }

  Box(
    modifier = modifier
      .size(bubbleSize)
      .clip(CircleShape)
      .then(clickModifier),
    contentAlignment = Alignment.Center
  ) {
    // 3D Glass Sphere Canvas
    Canvas(modifier = Modifier.matchParentSize()) {
      val w = this.size.width
      val h = this.size.height
      val r = w / 2f
      val c = Offset(r, r)

      // Base translucent sphere body
      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(
            if (isSelected) Color(0x6000F2FE) else Color(0x35FFFFFF),
            if (isSelected) Color(0x4000B4D8) else Color(0x18FFFFFF),
            Color(0x0A000000)
          ),
          center = Offset(r * 0.8f, r * 0.7f),
          radius = r
        ),
        radius = r,
        center = c
      )

      // Specular highlight rim (outer glass border)
      drawCircle(
        brush = Brush.linearGradient(
          colors = if (isSelected) {
            listOf(
              Color(0xFF00F2FE),
              Color(0x6000F2FE),
              Color(0xFF4FACFE)
            )
          } else {
            listOf(
              Color(0xE6FFFFFF),
              Color(0x25FFFFFF),
              Color(0x80FFFFFF)
            )
          },
          start = Offset(0f, 0f),
          end = Offset(w, h)
        ),
        radius = r - 1.2f,
        center = c,
        style = Stroke(width = if (isSelected) 2.5f else 1.8f)
      )

      // Top-left glossy reflection arc
      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(
            Color(0x90FFFFFF),
            Color(0x00FFFFFF)
          ),
          center = Offset(r * 0.65f, r * 0.45f),
          radius = r * 0.45f
        ),
        radius = r * 0.45f,
        center = Offset(r * 0.65f, r * 0.45f)
      )
    }

    // Centered Platform Brand Icon
    PlatformBrandIcon(platform = platform, size = iconSize)
  }
}

/**
 * Platform badge with official icon and brand name
 */
@Composable
fun PlatformBrandBadge(
  platform: String,
  modifier: Modifier = Modifier,
  isDark: Boolean = true
) {
  val cleanPlatform = platform.trim().ifEmpty { "Video" }

  Row(
    modifier = modifier
      .background(
        if (isDark) Color(0x331F2438) else Color(0x15000000),
        shape = RoundedCornerShape(8.dp)
      )
      .border(
        0.8.dp,
        if (isDark) Color(0x33FFFFFF) else Color(0x22000000),
        shape = RoundedCornerShape(8.dp)
      )
      .padding(horizontal = 8.dp, vertical = 4.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    PlatformBrandIcon(platform = cleanPlatform, size = 18.dp)
    Spacer(modifier = Modifier.width(6.dp))
    Text(
      text = cleanPlatform,
      fontSize = 12.sp,
      fontWeight = FontWeight.Bold,
      color = if (isDark) Color.White else Color.Black
    )
  }
}

