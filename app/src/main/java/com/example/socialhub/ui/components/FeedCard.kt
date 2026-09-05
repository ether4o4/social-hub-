package com.example.socialhub.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.socialhub.data.FeedItem
import com.example.socialhub.data.platforms

@Composable
fun FeedCard(
    item: FeedItem,
    expanded: Boolean,
    onClick: () -> Unit,
    onDoubleClick: () -> Unit
) {
    val scale by animateFloatAsState(if (expanded) 1.01f else 1f, label = "scale")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(24.dp))
            .background(
                if (expanded) Color.White.copy(alpha = 0.15f)
                else Color.White.copy(alpha = 0.08f)
            )
            .border(
                1.dp,
                if (expanded) Color.White.copy(alpha = 0.4f)
                else Color.White.copy(alpha = 0.15f),
                RoundedCornerShape(24.dp)
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onClick() },
                    onDoubleTap = { onDoubleClick() }
                )
            }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row {
                Box {
                    val platform = platforms.find { it.id == item.platform } ?: platforms.first()
                    val isLight = platform.id == "snapchat" || platform.id == "tiktok"
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                Color(android.graphics.Color.parseColor(platform.color)).copy(alpha = 0.8f)
                            )
                            .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            platform.glyph,
                            color = if (isLight) Color.Black else Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    if (item.type == "notification") {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF43F5E))
                                .border(2.dp, Color(0xFF1A0B2E).copy(alpha = 0.8f), CircleShape)
                                .align(Alignment.TopEnd)
                                .offset(x = 2.dp, y = (-2).dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f, fill = false),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                item.author,
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                item.handle,
                                color = Color.White.copy(alpha = 0.4f),
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            item.time,
                            color = Color.White.copy(alpha = 0.4f),
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        item.content,
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 14.sp,
                        maxLines = if (expanded) Int.MAX_VALUE else 3,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (item.image != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        AsyncImage(
                            model = item.image,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(if (expanded) 180.dp else 128.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            item.stats.likes?.let { Stat("♥", it) }
                            item.stats.comments?.let { Stat("💬", it) }
                            item.stats.shares?.let { Stat("↗", it) }
                        }

                        val isNotif = item.type == "notification"
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isNotif) Color(0xFFF43F5E).copy(alpha = 0.15f)
                                    else Color.White.copy(alpha = 0.1f)
                                )
                                .border(
                                    1.dp,
                                    if (isNotif) Color(0xFFF43F5E).copy(alpha = 0.3f)
                                    else Color.White.copy(alpha = 0.2f),
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                if (isNotif) "Notification" else "Post",
                                color = if (isNotif) Color(0xFFFFA7B8) else Color.White.copy(alpha = 0.6f),
                                fontSize = 10.sp
                            )
                        }
                    }

                    if (expanded) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Double-tap to open app ↗",
                            color = Color.White.copy(alpha = 0.4f),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Stat(icon: String, value: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(icon, fontSize = 12.sp)
        Spacer(modifier = Modifier.width(2.dp))
        val text = when {
            value >= 1_000_000 -> String.format("%.1fM", value / 1_000_000f)
            value >= 1000 -> String.format("%.1fk", value / 1000f)
            else -> "$value"
        }
        Text(text, color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
    }
}
