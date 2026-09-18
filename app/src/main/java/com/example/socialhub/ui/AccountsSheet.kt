package com.example.socialhub.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.socialhub.data.platforms
import com.example.socialhub.viewmodel.SocialHubViewModel

@Composable
fun AccountsSheet(viewModel: SocialHubViewModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.55f))
            .clickable { viewModel.toggleAccounts() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxSize()
                .padding(16.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF1A0B2E).copy(alpha = 0.97f))
                .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
                .padding(20.dp)
                .clickable(enabled = false) {}
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Connected accounts", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                        .clickable { viewModel.toggleAccounts() },
                    contentAlignment = Alignment.Center
                ) { Text("✕", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp) }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                "Log in to each app so Social Hub remembers your accounts and marks them connected.",
                color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(16.dp))

            platforms.filter { it.id != "all" }.forEach { platform ->
                val account = viewModel.accounts[platform.id]
                val connected = account?.connected == true
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (connected) Color.White.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.03f))
                        .border(
                            1.dp,
                            if (connected) Color(0xFF34D399).copy(alpha = 0.4f) else Color.White.copy(alpha = 0.1f),
                            RoundedCornerShape(16.dp)
                        )
                        .clickable { viewModel.openLogin(platform.id) }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(android.graphics.Color.parseColor(platform.color)).copy(alpha = 0.85f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(platform.glyph, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(platform.name, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                        Text(
                            if (connected) "Connected · ${account?.username}" else "Not connected — tap to log in",
                            color = if (connected) Color(0xFF6EE7B7) else Color.White.copy(alpha = 0.45f),
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (connected) Color(0xFF34D399) else Color.White.copy(alpha = 0.2f))
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                "Note: Social Hub stores your saved credentials encrypted on-device. Real OAuth per platform requires each platform's developer keys; this stores your account so the hub remembers it.",
                color = Color.White.copy(alpha = 0.35f), fontSize = 11.sp
            )
        }
    }
}
