package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.GlassFrostDark
import com.example.ui.theme.GlassFrostLight
import com.example.ui.theme.VipCardBorderDark
import com.example.ui.theme.VipCardBorderLight

@Composable
fun GlassCard(
  modifier: Modifier = Modifier,
  shape: Shape = RoundedCornerShape(20.dp),
  isDark: Boolean = true,
  borderWidth: Dp = 1.dp,
  borderColor: Color = if (isDark) VipCardBorderDark else VipCardBorderLight,
  content: @Composable BoxScope.() -> Unit
) {
  val surfaceColor = if (isDark) {
    Color(0x2E1C2030) // Translucent deep frosted glass
  } else {
    Color(0xDDFFFFFF)
  }

  Box(
    modifier = modifier
      .clip(shape)
      .background(surfaceColor)
      .border(BorderStroke(borderWidth, borderColor), shape),
    content = content
  )
}

@Composable
fun VipAmberGlowBrush(isDark: Boolean = true): Brush {
  return if (isDark) {
    Brush.radialGradient(
      colors = listOf(
        Color(0x33FFB300),
        Color(0x15FF7043),
        Color(0x000C0D14)
      )
    )
  } else {
    Brush.radialGradient(
      colors = listOf(
        Color(0x22FFA000),
        Color(0x08FF7043),
        Color(0x00FAF9F6)
      )
    )
  }
}
