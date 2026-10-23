package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
  primary = VipEmeraldPrimary,
  onPrimary = Color.Black,
  primaryContainer = Color(0xFF064E3B),
  onPrimaryContainer = Color(0xFFA7F3D0),
  secondary = VipMintAccent,
  onSecondary = Color.Black,
  secondaryContainer = Color(0xFF047857),
  onSecondaryContainer = Color(0xFFD1FAE5),
  tertiary = VipElectricCyan,
  onTertiary = Color.Black,
  background = VipObsidianBg,
  onBackground = TextPrimaryDark,
  surface = VipObsidianSurface,
  onSurface = TextPrimaryDark,
  surfaceVariant = VipObsidianSurfaceVariant,
  onSurfaceVariant = TextSecondaryDark,
  outline = Color(0xFF264A3D),
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
