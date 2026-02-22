package com.example.nebula.home.widgets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.example.nebula.home.ClockState

@Composable
fun ClockWidget(clockState: ClockState, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(bottom = 4.dp)) {
        Text(
            text = clockState.time,
            style = MaterialTheme.typography.headlineLarge,
            color = Color.White,
        )
        Text(
            text = clockState.date,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White.copy(alpha = 0.7f),
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = clockState.greeting,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.5f),
        )
    }
}
