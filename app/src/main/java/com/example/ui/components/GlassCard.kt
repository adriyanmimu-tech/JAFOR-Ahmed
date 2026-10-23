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
        Color(0x3817352A), // Frosted translucent emerald glass
        Color(0x220B1F17),
        Color(0x301B3E31)
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
        Color(0xCCFFFFFF), // Top-left specular highlight
        Color(0x25FFFFFF),
        Color(0x6034D399), // Soft mint green glass reflection
        Color(0x20FFFFFF)
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

/**
 * Ultra-high-end Glassmorphism Premium Screen Card with multi-layer frosted depth,
 * specular rim reflections, and glowing emerald light aura matching the hero glass sphere.
 */
@Composable
fun PremiumGlassCard(
  modifier: Modifier = Modifier,
  shape: Shape = RoundedCornerShape(24.dp),
  isDark: Boolean = true,
  borderWidth: Dp = 1.4.dp,
  accentGlow: Color = Color(0xFF10B981),
  isSelected: Boolean = false,
  content: @Composable BoxScope.() -> Unit
) {
  val surfaceBrush = if (isDark) {
    if (isSelected) {
      Brush.linearGradient(
        colors = listOf(
          Color(0x45164E3D),
          Color(0x300F2A21),
          Color(0x401B4D3C)
        ),
        start = Offset(0f, 0f),
        end = Offset(700f, 900f)
      )
    } else {
      Brush.linearGradient(
        colors = listOf(
          Color(0x3D14382B),
          Color(0x240D241C),
          Color(0x35193F31)
        ),
        start = Offset(0f, 0f),
        end = Offset(700f, 900f)
      )
    }
  } else {
    if (isSelected) {
      Brush.linearGradient(
        colors = listOf(
          Color(0xF5ECFDF5),
          Color(0xEEFFFFFF),
          Color(0xF0D1FAE5)
        ),
        start = Offset(0f, 0f),
        end = Offset(500f, 700f)
      )
    } else {
      Brush.linearGradient(
        colors = listOf(
          Color(0xF5FFFFFF),
          Color(0xEBF4F7FC),
          Color(0xF2FFFFFF)
        ),
        start = Offset(0f, 0f),
        end = Offset(500f, 700f)
      )
    }
  }

  val specularBorderBrush = if (isSelected) {
    Brush.linearGradient(
      colors = listOf(
        Color(0xFF34D399),
        Color(0xFF10B981),
        Color(0xFF00F2FE),
        Color(0x90FFFFFF)
      ),
      start = Offset(0f, 0f),
      end = Offset(600f, 800f)
    )
  } else if (isDark) {
    Brush.linearGradient(
      colors = listOf(
        Color(0xEEFFFFFF), // Top-left specular sheen
        Color(0x3534D399), // Mint glass edge
        Color(0x99FFFFFF), // Mid sheen
        Color(0x10000000), // Shadow bevel
        Color(0x4034D399)  // Bottom glow
      ),
      start = Offset(0f, 0f),
      end = Offset(600f, 800f)
    )
  } else {
    Brush.linearGradient(
      colors = listOf(
        Color(0xFFFFFFFF),
        Color(0x6034D399),
        Color(0x40CBD5E1),
        Color(0x80FFFFFF)
      ),
      start = Offset(0f, 0f),
      end = Offset(500f, 700f)
    )
  }

  Box(
    modifier = modifier
      .clip(shape)
      .background(surfaceBrush)
      .border(BorderStroke(borderWidth, specularBorderBrush), shape)
  ) {
    // Top specular sheen reflection line for 3D physical glass depth
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(1.2.dp)
        .background(
          Brush.horizontalGradient(
            listOf(
              Color.Transparent,
              Color(0x60FFFFFF),
              Color(0xEEFFFFFF),
              Color(0x60FFFFFF),
              Color.Transparent
            )
          )
        )
    )

    content()
  }
}

