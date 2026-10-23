package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.DownloadItem
import com.example.data.protection.FypShieldOptions
import com.example.ui.theme.VipAmberPrimary
import com.example.ui.theme.VipCoral
import com.example.ui.theme.VipGoldAccent

@Composable
fun FypShieldModal(
  item: DownloadItem,
  options: FypShieldOptions,
  isDark: Boolean,
  onToggleOption: (Int) -> Unit,
  onSelectAll: () -> Unit,
  onApply: (FypShieldOptions) -> Unit,
  onDismiss: () -> Unit
) {
  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(26.dp),
      color = if (isDark) Color(0xFF131522) else Color(0xFFFFFFFF),
      modifier = Modifier
        .fillMaxWidth()
        .border(1.5.dp, VipAmberPrimary, RoundedCornerShape(26.dp))
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(VipAmberPrimary.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Shield",
                tint = VipAmberPrimary,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "FYP Anti-Copyright Suite",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
              )
              Text(
                text = "100% Original with 4 FYP Protection Rules",
                fontSize = 11.sp,
                color = VipAmberPrimary,
                fontWeight = FontWeight.Medium
              )
            }
          }

          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close")
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = "Video: ${item.title.take(35)}...",
          fontSize = 12.sp,
          color = if (isDark) Color(0xFFA5ABB8) else Color(0xFF6B7280)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Golden Rules Quick Tip
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (isDark) Color(0x22FFB300) else Color(0x18FFB300))
            .padding(horizontal = 10.dp, vertical = 8.dp)
        ) {
          Text(
            text = "💡 Golden Rules: When uploading to TikTok, add a 1s intro hook, 1-3% trending sound, in-app captions, and trim the last second.",
            fontSize = 11.sp,
            color = VipAmberPrimary,
            fontWeight = FontWeight.Medium,
            lineHeight = 15.sp
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Option 1: New Hook Intro & Hash
        ShieldOptionCheckboxRow(
          title = "1. Add Hook Intro & Unique Salt",
          description = "Injects a unique intro hook signature and cryptographic binary salt into the video file.",
          isChecked = options.md5HashScramble,
          onToggle = { onToggleOption(1) },
          isDark = isDark
        )

        // Option 2: Trending Audio Mix
        ShieldOptionCheckboxRow(
          title = "2. Trending Audio Layer Mix (1-3%)",
          description = "Shifts background frequency layer to prevent audio Content-ID duplication.",
          isChecked = options.audioPitchShift,
          onToggle = { onToggleOption(2) },
          isDark = isDark
        )

        // Option 3: TikTok Text & Auto-caption Filter
        ShieldOptionCheckboxRow(
          title = "3. TikTok Text & Filter Signature",
          description = "Adds caption layer metadata to present the video as fresh, original creator content.",
          isChecked = options.microZoomCrop,
          onToggle = { onToggleOption(3) },
          isDark = isDark
        )

        // Option 4: Trim Last 1 Second
        ShieldOptionCheckboxRow(
          title = "4. Auto-Trim Last 1 Second",
          description = "Modifies frame count and ending duration so it never matches previous uploads.",
          isChecked = options.microSpeedShift,
          onToggle = { onToggleOption(4) },
          isDark = isDark
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Action Buttons
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Button(
            onClick = onSelectAll,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = if (isDark) Color(0x33FFB300) else Color(0x22FFA000)
            ),
            modifier = Modifier.weight(1f)
          ) {
            Text(
              text = "Select All (4/4)",
              fontSize = 12.sp,
              color = VipAmberPrimary,
              fontWeight = FontWeight.Bold
            )
          }

          Button(
            onClick = { onApply(options) },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = VipAmberPrimary),
            modifier = Modifier.weight(1.5f)
          ) {
            Text(
              text = "Apply Shield (${options.activeOptionsCount})",
              fontSize = 13.sp,
              color = Color.Black,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }
  }
}

@Composable
private fun ShieldOptionCheckboxRow(
  title: String,
  description: String,
  isChecked: Boolean,
  onToggle: () -> Unit,
  isDark: Boolean
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp)
      .clip(RoundedCornerShape(12.dp))
      .background(if (isChecked) (if (isDark) Color(0x1AFFB300) else Color(0x14FFA000)) else Color.Transparent)
      .clickable { onToggle() }
      .padding(horizontal = 8.dp, vertical = 6.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Checkbox(
      checked = isChecked,
      onCheckedChange = { onToggle() },
      colors = CheckboxDefaults.colors(
        checkedColor = VipAmberPrimary,
        checkmarkColor = Color.Black,
        uncheckedColor = if (isDark) Color.Gray else Color.LightGray
      )
    )
    Spacer(modifier = Modifier.width(6.dp))
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = title,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = if (isChecked) VipAmberPrimary else MaterialTheme.colorScheme.onBackground
      )
      Text(
        text = description,
        fontSize = 11.sp,
        color = if (isDark) Color(0xFFA5ABB8) else Color(0xFF6B7280),
        lineHeight = 14.sp
      )
    }
  }
}
