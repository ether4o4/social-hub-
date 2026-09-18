package com.example.socialhub

import android.content.res.Resources
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.OnBackPressedCallback
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.socialhub.ui.PanelRoot
import com.example.socialhub.ui.theme.SocialHubTheme

/**
 * Floating translucent panel that slides out from the right edge.
 *
 * The window is a floating (non-fullscreen) overlay sized to ~62% of the screen width,
 * anchored to the right edge. Because it is a floating window, touches outside the panel
 * pass through to whatever app is behind it — so the screen behind stays visible AND
 * interactive, matching the Samsung edge-panel behaviour.
 *
 * The Compose content slides in from the right on open and can be dismissed by
 * dragging the panel rightward past a threshold or pressing back.
 */
class PanelActivity : ComponentActivity() {

    private val screenW: Int get() = Resources.getSystem().displayMetrics.widthPixels
    private val screenH: Int get() = Resources.getSystem().displayMetrics.heightPixels

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Floating, translucent, non-dimming window so the background app shows through
        // and remains interactive outside the panel area.
        window.apply {
            setLayout(((screenW * 0.62).toInt()).coerceAtLeast(320), WindowManager.LayoutParams.MATCH_PARENT)
            setGravity(Gravity.END or Gravity.TOP)
            clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            attributes = attributes.apply {
                dimAmount = 0f
            }
        }

        setContent {
            SocialHubTheme {
                PanelScreen(onClose = { finish() })
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() { finish() }
        })
    }

    override fun finish() {
        super.finish()
        // Remove the default closing animation so the Compose slide-out reads cleanly.
        overridePendingTransition(0, 0)
    }
}

@Composable
private fun PanelScreen(onClose: () -> Unit) {
    var dragOffset by remember { mutableStateOf(0f) }
    val density = LocalDensity.current
    val dismissThresholdPx = with(density) { 120.dp.toPx() }

    val slide by animateFloatAsState(
        targetValue = if (dragOffset > 0) dragOffset else 0f,
        animationSpec = tween(220),
        label = "panelSlide"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        if (dragOffset > dismissThresholdPx) onClose()
                        dragOffset = 0f
                    }
                ) { _, dragAmount ->
                    // Only allow dragging rightward (positive) to dismiss.
                    dragOffset = (dragOffset + dragAmount).coerceAtLeast(0f)
                }
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(360.dp)
                .let { if (slide > 0f) it.then(Modifier) else it }
        ) {
            PanelRoot(onClose = onClose)
        }
    }
}
