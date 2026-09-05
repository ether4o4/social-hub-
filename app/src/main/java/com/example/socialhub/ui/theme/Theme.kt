package com.example.socialhub.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFEC4899),
    secondary = Color(0xFF8B5CF6),
    background = HubBackground,
    surface = HubSurface,
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun SocialHubTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
