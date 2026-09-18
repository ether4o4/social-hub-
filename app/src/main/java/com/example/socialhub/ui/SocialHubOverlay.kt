package com.example.socialhub.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.socialhub.viewmodel.SocialHubViewModel

/**
 * Root of the pull-out panel. Hosted inside the overlay service's ComposeView.
 * Background is a translucent glass gradient so the screen behind stays visible.
 */
@Composable
fun PanelRoot(
    onClose: () -> Unit,
    viewModel: SocialHubViewModel = viewModel()
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFF1A0B2E), Color(0xFF140A24))
                )
            )
    ) {
        SocialHubFeed(viewModel = viewModel, onClose = onClose)

        AnimatedVisibility(
            visible = viewModel.showAccounts,
            enter = fadeIn(tween(220)),
            exit = fadeOut(tween(180))
        ) {
            AccountsSheet(viewModel = viewModel)
        }

        AnimatedVisibility(
            visible = viewModel.loginPlatformId != null,
            enter = fadeIn(tween(220)),
            exit = fadeOut(tween(180))
        ) {
            viewModel.loginPlatformId?.let { id ->
                LoginSheet(
                    platformId = id,
                    viewModel = viewModel,
                    onClose = { viewModel.closeLogin() }
                )
            }
        }
    }
}
