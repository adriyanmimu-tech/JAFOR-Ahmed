package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
  primary = VipAmberPrimary,
  onPrimary = Color(0xFF1F1200),
  primaryContainer = Color(0xFF4A3400),
  onPrimaryContainer = Color(0xFFFFE082),
  secondary = VipCoral,
  onSecondary = Color.White,
  secondaryContainer = Color(0xFF4E1D0C),
  onSecondaryContainer = Color(0xFFFFCCBC),
  tertiary = VipGoldAccent,
  onTertiary = Color(0xFF261900),
  background = VipObsidianBg,
  onBackground = TextPrimaryDark,
  surface = VipObsidianSurface,
  onSurface = TextPrimaryDark,
  surfaceVariant = VipObsidianSurfaceVariant,
  onSurfaceVariant = TextSecondaryDark,
  outline = Color(0xFF3B4055),
  outlineVariant = VipCardBorderDark
)

private val LightColorScheme = lightColorScheme(
  primary = VipAmberDark,
  onPrimary = Color.White,
  primaryContainer = Color(0xFFFFECB3),
  onPrimaryContainer = Color(0xFF4A3400),
  secondary = Color(0xFFD84315),
  onSecondary = Color.White,
  secondaryContainer = Color(0xFFFFCCBC),
  onSecondaryContainer = Color(0xFF3E1400),
  tertiary = VipAmberPrimary,
  onTertiary = Color(0xFF261900),
  background = VipLightBg,
  onBackground = TextPrimaryLight,
  surface = VipLightSurface,
  onSurface = TextPrimaryLight,
  surfaceVariant = VipLightSurfaceVariant,
  onSurfaceVariant = TextSecondaryLight,
  outline = Color(0xFFBDBDBD),
  outlineVariant = VipCardBorderLight
)

@Composable
fun VidMaxVipTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
