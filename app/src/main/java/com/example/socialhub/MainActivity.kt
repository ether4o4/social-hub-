package com.example.socialhub

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.socialhub.ui.theme.SocialHubTheme

class MainActivity : ComponentActivity() {

    private val overlayResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { refreshState() }

    private val notifPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    private var overlayGrantedState by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SocialHubTheme {
                PermissionOrStatusScreen()
            }
        }
        refreshState()
        requestNotificationPermissionIfNeeded()
    }

    override fun onResume() {
        super.onResume()
        refreshState()
    }

    private fun refreshState() {
        overlayGrantedState = Settings.canDrawOverlays(this)
        if (overlayGrantedState) startOverlayService()
    }

    private fun startOverlayService() {
        val intent = Intent(this, OverlayService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(this, intent)
        } else {
            startService(intent)
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) notifPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    @Composable
    private fun PermissionOrStatusScreen() {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF0F0518), Color(0xFF1A0B2E), Color(0xFF0F0518))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(32.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFFEC4899), Color(0xFF8B5CF6), Color(0xFF3B82F6))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) { Text("✦", color = Color.White, fontSize = 28.sp) }

                Spacer(modifier = Modifier.height(20.dp))
                Text("Social Hub", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(14.dp))

                if (overlayGrantedState) {
                    Text(
                        "Edge panel is running.\nDrag the handle on the right edge of your screen to open your unified feed.",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 15.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    PrimaryButton("Stop overlay") {
                        stopService(Intent(this@MainActivity, OverlayService::class.java))
                        overlayGrantedState = false
                    }
                } else {
                    Text(
                        "Social Hub runs as an edge overlay so your screen stays visible behind it.\nGrant \"Display over other apps\" to enable the edge handle.",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 15.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    PrimaryButton("Grant overlay permission") {
                        val intent = Intent(
                            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:$packageName")
                        )
                        overlayResult.launch(intent)
                    }
                }
            }
        }
    }

    @Composable
    private fun PrimaryButton(label: String, onClick: () -> Unit) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFFEC4899), Color(0xFF8B5CF6))
                    )
                )
                .clickable(onClick = onClick)
                .padding(horizontal = 24.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(label, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}
