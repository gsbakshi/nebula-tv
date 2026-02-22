package com.example.nebula.home.widgets

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Border
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import com.example.nebula.home.FeedItem
import com.example.nebula.home.FeedState
import com.example.nebula.ui.theme.NebulaGlow
import com.example.nebula.ui.theme.NebulaPurple
import com.example.nebula.ui.theme.StarWhite

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun FeedWidget(
    feedState: FeedState,
    onOpenUrl: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        // ── Section header ───────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .width(16.dp)
                    .height(1.dp)
                    .background(NebulaGlow.copy(alpha = 0.5f))
            )
            Text(
                text = "NASA NEWS",
                style = TextStyle(
                    fontSize = 9.sp,
                    letterSpacing = 5.sp,
                    fontWeight = FontWeight.Medium,
                    color = NebulaGlow.copy(alpha = 0.6f),
                ),
                modifier = Modifier.padding(horizontal = 10.dp),
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(1.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                NebulaGlow.copy(alpha = 0.3f),
                                Color.Transparent,
                            )
                        )
                    )
            )
        }

        when (feedState) {
            FeedState.Loading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "LOADING FEED…",
                    style = TextStyle(
                        fontSize = 9.sp,
                        letterSpacing = 5.sp,
                        color = StarWhite.copy(alpha = 0.25f),
                    ),
                )
            }
            is FeedState.Error -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = feedState.message,
                    style = TextStyle(
                        fontSize = 10.sp,
                        letterSpacing = 1.sp,
                        color = NebulaPurple.copy(alpha = 0.7f),
                    ),
                )
            }
            is FeedState.Success -> FeedList(items = feedState.items, onOpenUrl = onOpenUrl)
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun FeedList(items: List<FeedItem>, onOpenUrl: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        itemsIndexed(
            items = items,
            key = { _, item -> item.link.ifBlank { item.title } },
        ) { _, item ->
            FeedCard(item = item, onOpenUrl = onOpenUrl)
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun FeedCard(item: FeedItem, onOpenUrl: (String) -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    // Left-border colour responds to focus — the editorial accent
    val borderColor = if (isFocused) NebulaGlow else NebulaPurple.copy(alpha = 0.45f)

    Card(
        onClick = { if (item.link.isNotBlank()) onOpenUrl(item.link) },
        interactionSource = interactionSource,
        shape = CardDefaults.shape(RoundedCornerShape(6.dp)),
        colors = CardDefaults.colors(
            containerColor = Color.Transparent,
            focusedContainerColor = NebulaGlow.copy(alpha = 0.05f),
            pressedContainerColor = NebulaGlow.copy(alpha = 0.03f),
        ),
        scale = CardDefaults.scale(focusedScale = 1.0f),
        border = CardDefaults.border(
            border = Border(BorderStroke(0.dp, Color.Transparent)),
            focusedBorder = Border(BorderStroke(0.dp, Color.Transparent)),
        ),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                // Draw the editorial left-accent line using drawBehind for exact sizing
                .drawBehind {
                    drawLine(
                        color = borderColor,
                        start = Offset(0f, 6.dp.toPx()),
                        end = Offset(0f, size.height - 6.dp.toPx()),
                        strokeWidth = 2.5.dp.toPx(),
                        cap = StrokeCap.Round,
                    )
                }
                .padding(start = 14.dp, top = 10.dp, bottom = 10.dp, end = 12.dp),
        ) {
            Text(
                text = item.title,
                style = TextStyle(
                    fontSize = 13.sp,
                    fontWeight = if (isFocused) FontWeight.Normal else FontWeight.Light,
                    letterSpacing = 0.15.sp,
                    lineHeight = 19.sp,
                    color = if (isFocused) StarWhite else StarWhite.copy(alpha = 0.75f),
                ),
                maxLines = 2,
            )
        }
    }
}
