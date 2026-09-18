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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.socialhub.data.platforms
import com.example.socialhub.viewmodel.SocialHubViewModel

@Composable
fun LoginSheet(
    platformId: String,
    viewModel: SocialHubViewModel,
    onClose: () -> Unit
) {
    val platform = platforms.firstOrNull { it.id == platformId } ?: platforms.first()
    val uriHandler = LocalUriHandler.current
    val existing = viewModel.accounts[platformId]
    var username by remember { mutableStateOf(existing?.username ?: "") }
    var password by remember { mutableStateOf("") }
    var showPw by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .clickable(onClick = onClose)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF1A0B2E).copy(alpha = 0.98f))
                .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
                .padding(20.dp)
                .clickable(enabled = false) {}
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(android.graphics.Color.parseColor(platform.color)).copy(alpha = 0.85f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(platform.glyph, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Log in to ${platform.name}", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .clickable(onClick = onClose),
                    contentAlignment = Alignment.Center
                ) { Text("✕", color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp) }
            }

            Spacer(modifier = Modifier.height(18.dp))
            Text("Username", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
            Spacer(modifier = Modifier.height(6.dp))
            FieldBox(
                value = username,
                onValueChange = { username = it; error = false },
                placeholder = "username or email",
                transformation = VisualTransformation.None
            )

            Spacer(modifier = Modifier.height(14.dp))
            Text("Password", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
            Spacer(modifier = Modifier.height(6.dp))
            FieldBox(
                value = password,
                onValueChange = { password = it; error = false },
                placeholder = "password",
                transformation = if (showPw) VisualTransformation.None else PasswordVisualTransformation(),
                trailing = {
                    Text(
                        if (showPw) "Hide" else "Show",
                        color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp,
                        modifier = Modifier.clickable { showPw = !showPw }.padding(6.dp)
                    )
                }
            )

            if (error) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Enter both a username and password.", color = Color(0xFFF87171), fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(18.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFFEC4899), Color(0xFF8B5CF6))
                            )
                        )
                        .clickable {
                            if (username.isBlank() || password.isBlank()) error = true
                            else viewModel.connect(platformId, username, password)
                        }
                        .padding(vertical = 13.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Connect", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White.copy(alpha = 0.06f))
                        .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(14.dp))
                        .clickable { uriHandler.openUri(platform.link) }
                        .padding(horizontal = 16.dp, vertical = 13.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Open app ↗", color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
                }
            }

            if (existing?.connected == true) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFF43F5E).copy(alpha = 0.12f))
                        .border(1.dp, Color(0xFFF43F5E).copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                        .clickable { viewModel.disconnect(platformId) }
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Disconnect account", color = Color(0xFFFFA7B8), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
private fun FieldBox(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    transformation: VisualTransformation,
    trailing: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = 0.06f))
            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            visualTransformation = transformation,
            textStyle = TextStyle(color = Color.White, fontSize = 14.sp),
            cursorBrush = SolidColor(Color(0xFFE879F9)),
            modifier = Modifier.weight(1f),
            decorationBox = { inner ->
                if (value.isEmpty()) {
                    Text(placeholder, color = Color.White.copy(alpha = 0.35f), fontSize = 14.sp)
                }
                inner()
            }
        )
        if (trailing != null) { Spacer(modifier = Modifier.width(8.dp)); trailing() }
    }
}
