package com.example.nebula.home.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Card
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.example.nebula.home.FeedItem
import com.example.nebula.home.FeedState

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun FeedWidget(
    feedState: FeedState,
    onOpenUrl: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = "NASA News",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        when (feedState) {
            FeedState.Loading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text("Loading feed…", color = Color.White.copy(alpha = 0.5f))
            }
            is FeedState.Error -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = feedState.message,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFFF6B6B).copy(alpha = 0.8f),
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
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        itemsIndexed(
            items = items,
            key = { _, item -> item.link.ifBlank { item.title } },
        ) { _, item ->
            Card(
                onClick = { if (item.link.isNotBlank()) onOpenUrl(item.link) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                )
            }
        }
    }
}
