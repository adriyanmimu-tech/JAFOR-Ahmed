package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Authentic 3D Glassmorphism Card with frosted translucent surface,
 * specular highlight edge gradient, and top-edge light reflection.
 */
@Composable
fun GlassCard(
  modifier: Modifier = Modifier,
  shape: Shape = RoundedCornerShape(22.dp),
  isDark: Boolean = true,
  borderWidth: Dp = 1.2.dp,
  borderBrush: Brush? = null,
  borderColor: Color? = null,
  tintColor: Color? = null,
  showTopSheen: Boolean = true,
  content: @Composable BoxScope.() -> Unit
) {
  val surfaceBrush = if (tintColor != null) {
    Brush.linearGradient(
      colors = listOf(
        tintColor.copy(alpha = 0.35f),
        tintColor.copy(alpha = 0.12f)
      ),
      start = Offset(0f, 0f),
      end = Offset(400f, 600f)
    )
  } else if (isDark) {
    Brush.linearGradient(
      colors = listOf(
        Color(0x331C2638), // Translucent frosty navy-slate
        Color(0x1F0D1524),
        Color(0x2E162032)
      ),
      start = Offset(0f, 0f),
      end = Offset(600f, 800f)
    )
  } else {
    Brush.linearGradient(
      colors = listOf(
        Color(0xE6FFFFFF),
        Color(0xC8F4F7FC)
      ),
      start = Offset(0f, 0f),
      end = Offset(400f, 600f)
    )
  }

  val defaultBorderBrush = if (isDark) {
    Brush.linearGradient(
      colors = listOf(
        Color(0x99FFFFFF), // Top-left specular highlight
        Color(0x18FFFFFF),
        Color(0x4500F2FE), // Soft cyan glass refraction
        Color(0x15FFFFFF)
      ),
      start = Offset(0f, 0f),
      end = Offset(500f, 700f)
    )
  } else {
    Brush.linearGradient(
      colors = listOf(
        Color(0xB3FFFFFF),
        Color(0x33000000),
        Color(0x66FFFFFF)
      ),
      start = Offset(0f, 0f),
      end = Offset(400f, 600f)
    )
  }

  val finalBorderStroke = when {
    borderBrush != null -> BorderStroke(borderWidth, borderBrush)
    borderColor != null -> BorderStroke(borderWidth, borderColor)
    else -> BorderStroke(borderWidth, defaultBorderBrush)
  }

  Box(
    modifier = modifier
      .clip(shape)
      .background(surfaceBrush)
      .border(finalBorderStroke, shape)
  ) {
    // Top specular sheen line for 3D realism
    if (showTopSheen && isDark) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(1.dp)
          .background(
            Brush.horizontalGradient(
              listOf(
                Color.Transparent,
                Color(0x60FFFFFF),
                Color(0x80FFFFFF),
                Color(0x20FFFFFF),
                Color.Transparent
              )
            )
          )
      )
    }

    content()
  }
}

@Composable
fun VipAmberGlowBrush(isDark: Boolean = true): Brush {
  return if (isDark) {
    Brush.radialGradient(
      colors = listOf(
        Color(0x33FFB300),
        Color(0x1500F2FE),
        Color(0x000C0D14)
      )
    )
  } else {
    Brush.radialGradient(
      colors = listOf(
        Color(0x22FFA000),
        Color(0x0800F2FE),
        Color(0x00FAF9F6)
      )
    )
  }
}
