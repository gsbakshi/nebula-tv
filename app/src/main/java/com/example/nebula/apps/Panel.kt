package com.example.nebula.apps

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.example.nebula.ui.components.core.PanelView

@Composable
fun AppGridPanel(modifier: Modifier = Modifier) {
    PanelView(
        backgroundColor = Color(0xFF0D47A1).copy(alpha = 0.7f),
        modifier = modifier.padding(16.dp).border(
            1.dp, Color.White.copy(alpha = 0.1f),
            RoundedCornerShape(12.dp)
        )
    ) {
        Text(
            text = "App Grid Panel",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            color = Color.White
        )
    }
}
