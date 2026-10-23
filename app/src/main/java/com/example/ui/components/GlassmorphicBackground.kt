package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke

/**
 * 3D Glassmorphic Background with glowing 3D Toruses and Spheres
 * as seen in reference screenshots. Provides the rich depth and light
 * diffusion that makes frosted glass panels truly pop.
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
        Color(0xFF080C14), // Deep obsidian
        Color(0xFF0D1322),
        Color(0xFF060910)
      )
    )
  } else {
    Brush.verticalGradient(
      listOf(
        Color(0xFF0284C7), // Sky blue backdrop inspired by Screenshot 2
        Color(0xFF0EA5E9),
        Color(0xFF38BDF8)
      )
    )
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(baseGradient)
  ) {
    // 3D Geometry Canvas
    Canvas(modifier = Modifier.fillMaxSize()) {
      val w = size.width
      val h = size.height

      if (isDark) {
        // --- 1. Top Right 3D Emerald Sphere ---
        val sphere1Center = Offset(w * 0.85f, h * 0.16f)
        val sphere1Radius = w * 0.22f
        // Soft ambient aura
        drawCircle(
          brush = Brush.radialGradient(
            colors = listOf(Color(0x5510B981), Color(0x0010B981)),
            center = sphere1Center,
            radius = sphere1Radius * 1.8f
          ),
          radius = sphere1Radius * 1.8f,
          center = sphere1Center
        )
        // 3D Sphere shading
        drawCircle(
          brush = Brush.radialGradient(
            colors = listOf(
              Color(0xFF6EE7B7), // Highlight specular
              Color(0xFF10B981), // Emerald body
              Color(0xFF047857), // Midtone
              Color(0xFF064E3B), // Shadow
              Color(0xFF022C22)
            ),
            center = Offset(sphere1Center.x - sphere1Radius * 0.35f, sphere1Center.y - sphere1Radius * 0.35f),
            radius = sphere1Radius * 1.2f
          ),
          radius = sphere1Radius,
          center = sphere1Center
        )

        // --- 2. Top-Center Floating Small Sphere ---
        val smallSphereCenter = Offset(w * 0.65f, h * 0.05f)
        val smallSphereRadius = w * 0.09f
        drawCircle(
          brush = Brush.radialGradient(
            colors = listOf(
              Color(0xFF6EE7B7),
              Color(0xFF10B981),
              Color(0xFF064E3B)
            ),
            center = Offset(smallSphereCenter.x - smallSphereRadius * 0.35f, smallSphereCenter.y - smallSphereRadius * 0.35f),
            radius = smallSphereRadius
          ),
          radius = smallSphereRadius,
          center = smallSphereCenter
        )

        // --- 3. Mid-Right Floating Sphere ---
        val midSphereCenter = Offset(w * 0.92f, h * 0.45f)
        val midSphereRadius = w * 0.08f
        drawCircle(
          brush = Brush.radialGradient(
            colors = listOf(
              Color(0xFFA7F3D0),
              Color(0xFF10B981),
              Color(0xFF064E3B)
            ),
            center = Offset(midSphereCenter.x - midSphereRadius * 0.35f, midSphereCenter.y - midSphereRadius * 0.35f),
            radius = midSphereRadius
          ),
          radius = midSphereRadius,
          center = midSphereCenter
        )

        // --- 4. Left-Center 3D Torus (Ring) ---
        val torus1Center = Offset(w * 0.08f, h * 0.42f)
        val torus1Radius = w * 0.26f
        val torus1Stroke = w * 0.08f
        // Ambient aura
        drawCircle(
          brush = Brush.radialGradient(
            colors = listOf(Color(0x4010B981), Color(0x0010B981)),
            center = torus1Center,
            radius = torus1Radius * 1.5f
          ),
          radius = torus1Radius * 1.5f,
          center = torus1Center
        )
        // 3D Ring Body
        drawCircle(
          brush = Brush.sweepGradient(
            colors = listOf(
              Color(0xFF34D399),
              Color(0xFF10B981),
              Color(0xFF047857),
              Color(0xFF064E3B),
              Color(0xFF10B981),
              Color(0xFF34D399)
            ),
            center = torus1Center
          ),
          radius = torus1Radius,
          center = torus1Center,
          style = Stroke(width = torus1Stroke)
        )
        // Specular inner edge
        drawCircle(
          color = Color(0x80A7F3D0),
          radius = torus1Radius - torus1Stroke / 2f + 1f,
          center = torus1Center,
          style = Stroke(width = 2f)
        )

        // --- 5. Bottom 3D Giant Torus (Ring) (As seen in Screenshot 1) ---
        val torus2Center = Offset(w * 0.62f, h * 0.78f)
        val torus2Radius = w * 0.36f
        val torus2Stroke = w * 0.12f
        // Soft aura
        drawCircle(
          brush = Brush.radialGradient(
            colors = listOf(Color(0x4510B981), Color(0x0010B981)),
            center = torus2Center,
            radius = torus2Radius * 1.6f
          ),
          radius = torus2Radius * 1.6f,
          center = torus2Center
        )
        // 3D Ring
        drawCircle(
          brush = Brush.sweepGradient(
            colors = listOf(
              Color(0xFF6EE7B7),
              Color(0xFF10B981),
              Color(0xFF064E3B),
              Color(0xFF022C22),
              Color(0xFF047857),
              Color(0xFF34D399),
              Color(0xFF6EE7B7)
            ),
            center = torus2Center
          ),
          radius = torus2Radius,
          center = torus2Center,
          style = Stroke(width = torus2Stroke)
        )

        // --- 6. Ambient Electric Cyan & Amber glows for futuristic depth ---
        drawCircle(
          brush = Brush.radialGradient(
            colors = listOf(Color(0x2500F2FE), Color(0x0000F2FE)),
            center = Offset(w * 0.2f, h * 0.15f),
            radius = w * 0.5f
          ),
          radius = w * 0.5f,
          center = Offset(w * 0.2f, h * 0.15f)
        )
      } else {
        // Light Theme: Cyan, Sky Blue, and Soft White Glowing Spheres
        val sphere1Center = Offset(w * 0.85f, h * 0.20f)
        val sphere1Radius = w * 0.25f
        drawCircle(
          brush = Brush.radialGradient(
            colors = listOf(
              Color(0x80FFFFFF),
              Color(0x3038BDF8),
              Color(0x000284C7)
            ),
            center = sphere1Center,
            radius = sphere1Radius * 1.5f
          ),
          radius = sphere1Radius * 1.5f,
          center = sphere1Center
        )

        drawCircle(
          brush = Brush.radialGradient(
            colors = listOf(
              Color(0x6000F2FE),
              Color(0x0000F2FE)
            ),
            center = Offset(w * 0.2f, h * 0.7f),
            radius = w * 0.4f
          ),
          radius = w * 0.4f,
          center = Offset(w * 0.2f, h * 0.7f)
        )
      }
    }

    content()
  }
}
