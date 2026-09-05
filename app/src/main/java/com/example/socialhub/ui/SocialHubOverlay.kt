package com.example.socialhub.ui
import androidx.compose.material.icons.filled.KeyboardArrowUp
import kotlin.math.roundToInt

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.socialhub.viewmodel.SocialHubViewModel

@Composable
fun SocialHubOverlay(viewModel: SocialHubViewModel = viewModel()) {
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }

    var toggleY by remember { mutableFloatStateOf(screenHeightPx / 2f) }
    var sidebarWidth by remember { mutableStateOf(with(density) { (configuration.screenWidthDp * 0.55f).dp }) }
    var isResizing by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Landing page behind
        LandingPage()

        // Backdrop
        AnimatedVisibility(
            visible = viewModel.sidebarOpen,
            enter = fadeIn(tween(300)),
            exit = fadeOut(tween(300))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = if (isResizing) 0.15f else 0.3f))
                    .clickable {
                        viewModel.sidebarOpen = false
                        viewModel.closeDropdowns()
                    }
            )
        }

        // Sidebar
        AnimatedVisibility(
            visible = viewModel.sidebarOpen,
            enter = slideInHorizontally(tween(500, easing = FastOutSlowInEasing)) { -it },
            exit = slideOutHorizontally(tween(300)) { -it }
        ) {
            Box(
                modifier = Modifier
                    .width(sidebarWidth)
                    .fillMaxHeight()
                    .background(Color(0xFF1A0B2E).copy(alpha = 0.95f))
            ) {
                SocialHubFeed(
                    viewModel = viewModel,
                    onClose = {
                        viewModel.sidebarOpen = false
                        viewModel.closeDropdowns()
                    }
                )

                // Resize handle
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 12.dp)
                        .width(24.dp)
                        .height(48.dp)
                        .pointerInput(Unit) {
                            var startX = 0f
                            var startWidth = 0.dp
                            awaitPointerEventScope {
                                while (true) {
                                    val down = awaitFirstDown()
                                    startX = down.position.x
                                    startWidth = sidebarWidth
                                    var moved = false
                                    while (true) {
                                        val event = awaitPointerEvent()
                                        val change = event.changes.first()
                                        if (event.type == PointerEventType.Move) {
                                            val dx = with(density) { (change.position.x - startX).toDp() }
                                            if (kotlin.math.abs(change.position.x - startX) > 3) moved = true
                                            if (moved) {
                                                val maxW = with(density) { (screenWidthPx * 0.92f).toDp() }
                                                sidebarWidth = (startWidth + dx).coerceIn(240.dp, maxW)
                                                isResizing = true
                                            }
                                        }
                                        if (event.type == PointerEventType.KeyboardArrowUp) {
                                            isResizing = false
                                            break
                                        }
                                    }
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(40.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color.White.copy(alpha = 0.25f))
                    )
                }
            }
        }

        // Edge toggle handle
        var moved by remember { mutableStateOf(false) }
        Box(
            modifier = Modifier
                .offset { IntOffset(0, toggleY.roundToInt()) }
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val down = awaitFirstDown()
                            val startY = down.position.y
                            val startTop = toggleY
                            moved = false
                            while (true) {
                                val event = awaitPointerEvent()
                                val change = event.changes.first()
                                if (event.type == PointerEventType.Move) {
                                    val drag = change.position.y - startY
                                    if (kotlin.math.abs(drag) > 6) moved = true
                                    if (moved) {
                                        toggleY = (startTop + drag).coerceIn(100f, screenHeightPx - 100f)
                                    }
                                }
                                if (event.type == PointerEventType.KeyboardArrowUp) {
                                    if (!moved) {
                                        viewModel.sidebarOpen = !viewModel.sidebarOpen
                                        if (!viewModel.sidebarOpen) viewModel.closeDropdowns()
                                    }
                                    break
                                }
                            }
                        }
                    }
                }
                .clip(RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp))
                .background(Color.White.copy(alpha = 0.1f))
                .padding(start = 6.dp, end = 10.dp, top = 10.dp, bottom = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFFEC4899), Color(0xFF8B5CF6), Color(0xFF3B82F6))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text("✦", color = Color.White, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color(0xFFF43F5E))
                )
            }
        }
    }
}

@Composable
private fun LandingPage() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFFEC4899), Color(0xFF8B5CF6), Color(0xFF3B82F6))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("✦", color = Color.White, fontSize = 24.sp)
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "Social Hub",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "Drag the glowing handle on the left edge to open your unified social feed — every notification and timeline, one glass panel.",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 16.sp,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }
    }
}
