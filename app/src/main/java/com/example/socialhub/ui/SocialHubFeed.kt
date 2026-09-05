package com.example.socialhub.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.socialhub.data.platforms
import com.example.socialhub.viewmodel.SocialHubViewModel

@Composable
fun SocialHubFeed(
    viewModel: SocialHubViewModel,
    onClose: () -> Unit
) {
    val uriHandler = LocalUriHandler.current
    val visibleItems = if (viewModel.selectedPlatforms.isEmpty()) {
        viewModel.items
    } else {
        viewModel.items.filter { it.platform in viewModel.selectedPlatforms }
    }
    val notifCount = visibleItems.count { it.type == "notification" }

    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp, 20.dp, 20.dp, 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                androidx.compose.ui.graphics.Brush.horizontalGradient(
                                    listOf(Color(0xFFEC4899), Color(0xFF8B5CF6), Color(0xFF3B82F6))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("✦", color = Color.White, fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            "Social Hub",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        val secsAgo = ((System.currentTimeMillis() - viewModel.lastUpdated) / 1000).toInt()
                        val label = when {
                            viewModel.refreshing -> "Updating…"
                            secsAgo < 5 -> "Updated just now"
                            else -> "Updated ${secsAgo}s ago"
                        }
                        Text(
                            "$notifCount notifications · $label",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 12.sp
                        )
                    }
                }

                Row {
                    HeaderButton(onClick = { viewModel.refresh(2) }, icon = "↻")
                    Spacer(modifier = Modifier.width(8.dp))
                    HeaderButton(
                        onClick = { viewModel.toggleSettings() },
                        icon = "⚙",
                        active = viewModel.showSettings
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    HeaderButton(onClick = onClose, icon = "✕")
                }
            }

            AnimatedVisibility(
                visible = viewModel.showSettings,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically(),
                modifier = Modifier.align(Alignment.TopEnd).padding(top = 48.dp)
            ) {
                SettingsDropdown(viewModel)
            }
        }

        // Filter + Clear All toolbar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (viewModel.showFilter || viewModel.selectedPlatforms.isNotEmpty())
                                Color.White.copy(alpha = 0.15f)
                            else
                                Color.White.copy(alpha = 0.05f)
                        )
                        .border(
                            1.dp,
                            if (viewModel.showFilter || viewModel.selectedPlatforms.isNotEmpty())
                                Color.White.copy(alpha = 0.4f)
                            else
                                Color.White.copy(alpha = 0.1f),
                            RoundedCornerShape(16.dp)
                        )
                        .clickable { viewModel.toggleFilter() }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("⧉", fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        if (viewModel.selectedPlatforms.isEmpty()) "All platforms"
                        else "${viewModel.selectedPlatforms.size} selected",
                        color = if (viewModel.showFilter || viewModel.selectedPlatforms.isNotEmpty())
                            Color.White else Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "⌄",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.rotate(if (viewModel.showFilter) 180f else 0f)
                    )
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.05f))
                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                        .clickable { viewModel.clearAll() }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🗑", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Clear All",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            AnimatedVisibility(
                visible = viewModel.showFilter,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically(),
                modifier = Modifier.padding(top = 44.dp)
            ) {
                FilterDropdown(viewModel)
            }
        }

        // Feed list
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            visibleItems.forEach { item ->
                FeedCard(
                    item = item,
                    expanded = viewModel.expandedId == item.id,
                    onClick = { viewModel.toggleExpanded(item.id) },
                    onDoubleClick = { uriHandler.openUri(item.link) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun HeaderButton(
    onClick: () -> Unit,
    icon: String,
    active: Boolean = false
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (active) Color.White.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f)
            )
            .border(
                1.dp,
                if (active) Color.White.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.1f),
                RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(icon, color = Color.White.copy(alpha = 0.8f), fontSize = 16.sp)
    }
}

@Composable
private fun SettingsDropdown(viewModel: SocialHubViewModel) {
    val modes = listOf(
        Triple("live", "Constantly", "New items stream in live"),
        Triple("on_open", "On open", "Refresh when the hub opens"),
        Triple("manual", "Pull to refresh", "Scroll up at the top to update"),
        Triple("hourly", "Every hour", "Auto-updates once per hour")
    )

    Column(
        modifier = Modifier
            .width(260.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF2A1B3E).copy(alpha = 0.95f))
            .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
            .padding(8.dp)
    ) {
        Text(
            "Refresh feed",
            color = Color.White.copy(alpha = 0.4f),
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(12.dp, 8.dp, 12.dp, 4.dp)
        )

        modes.forEach { (id, label, hint) ->
            val active = viewModel.refreshMode == id
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (active) Color.White.copy(alpha = 0.15f) else Color.Transparent)
                    .clickable { viewModel.setRefreshMode(id) }
                    .padding(12.dp, 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .border(
                            2.dp,
                            if (active) Color(0xFFE879F9) else Color.White.copy(alpha = 0.3f),
                            CircleShape
                        )
                        .background(if (active) Color(0xFFE879F9).copy(alpha = 0.2f) else Color.Transparent),
                    contentAlignment = Alignment.Center
                ) {
                    if (active) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF0ABFC))
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(label, color = Color.White, fontSize = 14.sp)
                    Text(hint, color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun FilterDropdown(viewModel: SocialHubViewModel) {
    Column(
        modifier = Modifier
            .width(260.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF2A1B3E).copy(alpha = 0.95f))
            .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
            .padding(8.dp)
    ) {
        val allSelected = viewModel.selectedPlatforms.isEmpty()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(if (allSelected) Color.White.copy(alpha = 0.15f) else Color.Transparent)
                .clickable { viewModel.selectedPlatforms.clear() }
                .padding(12.dp, 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .border(
                        2.dp,
                        if (allSelected) Color(0xFFE879F9) else Color.White.copy(alpha = 0.3f),
                        CircleShape
                    )
                    .background(if (allSelected) Color(0xFFE879F9).copy(alpha = 0.2f) else Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                if (allSelected) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF0ABFC))
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text("All platforms", color = Color.White, fontSize = 14.sp)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.White.copy(alpha = 0.1f))
        )

        platforms.filter { it.id != "all" }.forEach { platform ->
            val checked = viewModel.selectedPlatforms.contains(platform.id)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (checked) Color.White.copy(alpha = 0.15f) else Color.Transparent)
                    .clickable { viewModel.togglePlatform(platform.id) }
                    .padding(12.dp, 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .border(
                            2.dp,
                            if (checked) Color(0xFFE879F9) else Color.White.copy(alpha = 0.3f),
                            RoundedCornerShape(4.dp)
                        )
                        .background(if (checked) Color(0xFFE879F9) else Color.Transparent),
                    contentAlignment = Alignment.Center
                ) {
                    if (checked) {
                        Text("✓", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(Color(android.graphics.Color.parseColor(platform.color)))
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(platform.name, color = Color.White, fontSize = 14.sp)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.White.copy(alpha = 0.1f))
        )

        Text(
            "Done",
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable { viewModel.showFilter = false }
                .padding(12.dp)
        )
    }
}
