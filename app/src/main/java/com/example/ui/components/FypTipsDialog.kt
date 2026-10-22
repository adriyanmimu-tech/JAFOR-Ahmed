package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.VipAmberPrimary
import com.example.ui.theme.VipCoral
import com.example.ui.theme.VipGoldAccent

@Composable
fun FypTipsDialog(
  isDark: Boolean,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val scrollState = rememberScrollState()
  var isCopied by remember { mutableStateOf(false) }

  val tipsTextToCopy = """
💡 ১০০% FYP এলিজিবল ও ভাইরাল হওয়ার ৪টি গোল্ডেন রুলস:
অ্যাপের তৈরি ভিডিও টিকটকে আপলোড করার সময় এই ৪টি ট্রিক ফলো করলে আর কখনো "Not eligible" আসবে না:

১. নতুন হুক যোগ করা: টিকটকের নিজস্ব এডিটরে ভিডিওর একদম শুরুতে ১-২ সেকেন্ড নিজের একটি ক্লিপ বা টেক্সট হুক (যেমন: "Wait for the end 😱") দিন।
২. ট্রেন্ডিং সাউন্ড ব্যবহার: টিকটকের 'Add Sound' অপশন থেকে যেকোনো ট্রেন্ডিং মিউজিক সিলেক্ট করে সেটির ভলিউম কমিয়ে ১% থেকে ৩% করে দিন এবং অরিজিনাল সাউন্ড চালু রাখুন।
৩. টিকটক টেক্সট ও ফিল্টার: টিকটক অ্যাপের ভেতরেই ক্যাপশন বা অটো-ক্যাপশন অন করে দিন।
৪. শেষের ১ সেকেন্ড ট্রিম: টিকটকে ভিডিওর শেষ ১ সেকেন্ড সামান্য ট্রিম/কাট করে দিন।
  """.trimIndent()

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = if (isDark) Color(0xFF131522) else Color(0xFFFFFFFF),
      modifier = Modifier
        .fillMaxWidth()
        .border(1.5.dp, Brush.linearGradient(listOf(VipAmberPrimary, VipCoral)), RoundedCornerShape(24.dp))
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
          .verticalScroll(scrollState)
      ) {
        // Dialog Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(VipAmberPrimary, VipCoral))),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Lightbulb,
                contentDescription = "FYP Rules",
                tint = Color.Black,
                modifier = Modifier.size(22.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "১০০% FYP এলিজিবল রুলস",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
              )
              Text(
                text = "ভাইরাল হওয়ার ৪টি গোল্ডেন ট্রিক 🚀",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = VipAmberPrimary
              )
            }
          }

          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close")
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Highlight Info Box
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isDark) Color(0x26FFB300) else Color(0x1AFFB300))
            .border(1.dp, VipAmberPrimary.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
            .padding(12.dp)
        ) {
          Text(
            text = "অ্যাপের তৈরি ভিডিও টিকটকে আপলোড করার সময় এই ৪টি ট্রিক ফলো করলে আর কখনো \"Not eligible\" আসবে না:",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = if (isDark) Color(0xFFF3F4F6) else Color(0xFF1F2937),
            lineHeight = 18.sp
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Rule 1: New Hook
        GoldenRuleCard(
          ruleNumber = "১",
          title = "নতুন হুক যোগ করা",
          description = "টিকটকের নিজস্ব এডিটরে ভিডিওর একদম শুরুতে ১-২ সেকেন্ড নিজের একটি ক্লিপ বা টেক্সট হুক (যেমন: \"Wait for the end 😱\") দিন।",
          icon = Icons.Default.Movie,
          badgeColor = VipAmberPrimary,
          isDark = isDark
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Rule 2: Trending Sound
        GoldenRuleCard(
          ruleNumber = "২",
          title = "ট্রেন্ডিং সাউন্ড ব্যবহার",
          description = "টিকটকের 'Add Sound' অপশন থেকে যেকোনো ট্রেন্ডিং মিউজিক সিলেক্ট করে সেটির ভলিউম কমিয়ে ১% থেকে ৩% করে দিন এবং অরিজিনাল সাউন্ড চালু রাখুন।",
          icon = Icons.Default.MusicNote,
          badgeColor = VipCoral,
          isDark = isDark
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Rule 3: Text & Auto-caption
        GoldenRuleCard(
          ruleNumber = "৩",
          title = "টিকটক টেক্সট ও ফিল্টার",
          description = "টিকটক অ্যাপের ভেতরেই ক্যাপশন বা অটো-ক্যাপশন অন করে দিন।",
          icon = Icons.Default.TextFields,
          badgeColor = VipGoldAccent,
          isDark = isDark
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Rule 4: Trim 1 second
        GoldenRuleCard(
          ruleNumber = "৪",
          title = "শেষের ১ সেকেন্ড ট্রিম",
          description = "টিকটকে ভিডিওর শেষ ১ সেকেন্ড সামান্য ট্রিম/কাট করে দিন।",
          icon = Icons.Default.Timeline,
          badgeColor = Color(0xFF38BDF8),
          isDark = isDark
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Why Not Eligible Warning Info
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isDark) Color(0x1AFF5252) else Color(0x14FF5252))
            .padding(12.dp)
        ) {
          Column {
            Text(
              text = "অ্যালগরিদম টিপস:",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = VipCoral
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "ডাউনলোড করার সময় অ্যাপের 'Anti-Copyright Shield' অন রাখলে ভিডিওর বাইনারি সিগনেচার ও মেটাডাটা স্ক্র্যাম্বল হয়ে যায়। টিকটক এডিটরে উপরের ৪টি কাজ করলে ভিডিওটি অ্যালগরিদমের চোখে সম্পূর্ণ নতুন ও আসল হিসেবে বিবেচিত হয়।",
              fontSize = 11.sp,
              color = if (isDark) Color(0xFFA5ABB8) else Color(0xFF4B5563),
              lineHeight = 15.sp
            )
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Action Buttons: Copy rules & Dismiss
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedButton(
            onClick = {
              val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
              val clip = ClipData.newPlainText("TikTok Golden Rules", tipsTextToCopy)
              clipboard.setPrimaryClip(clip)
              isCopied = true
              Toast.makeText(context, "রুলস কপি করা হয়েছে!", Toast.LENGTH_SHORT).show()
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.weight(1f)
          ) {
            Icon(
              imageVector = if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
              contentDescription = "Copy Rules",
              modifier = Modifier.size(16.dp),
              tint = if (isCopied) VipAmberPrimary else MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (isCopied) "কপি হয়েছে" else "কপি করুন",
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold
            )
          }

          Button(
            onClick = onDismiss,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = VipAmberPrimary),
            modifier = Modifier.weight(1.2f)
          ) {
            Text(
              text = "বুঝেছি (Got It)",
              color = Color.Black,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            )
          }
        }
      }
    }
  }
}

@Composable
private fun GoldenRuleCard(
  ruleNumber: String,
  title: String,
  description: String,
  icon: ImageVector,
  badgeColor: Color,
  isDark: Boolean
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .background(if (isDark) Color(0xFF1A1D2E) else Color(0xFFF3F4F6))
      .border(1.dp, if (isDark) Color(0x333A3F55) else Color(0x22000000), RoundedCornerShape(14.dp))
      .padding(12.dp),
    verticalAlignment = Alignment.Top
  ) {
    Box(
      modifier = Modifier
        .size(32.dp)
        .clip(CircleShape)
        .background(badgeColor.copy(alpha = 0.2f)),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = ruleNumber,
        fontSize = 14.sp,
        fontWeight = FontWeight.ExtraBold,
        color = badgeColor
      )
    }

    Spacer(modifier = Modifier.width(12.dp))

    Column(modifier = Modifier.weight(1f)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = icon,
          contentDescription = title,
          tint = badgeColor,
          modifier = Modifier.size(15.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = title,
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = if (isDark) Color(0xFFF9FAFB) else Color(0xFF111827)
        )
      }
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = description,
        fontSize = 12.sp,
        color = if (isDark) Color(0xFFD1D5DB) else Color(0xFF4B5563),
        lineHeight = 16.sp
      )
    }
  }
}
