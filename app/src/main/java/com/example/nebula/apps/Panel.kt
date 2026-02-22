package com.example.nebula.apps

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.nebula.AppGrid
import com.example.nebula.ui.components.core.PanelView

@Composable
fun AppGridPanel(modifier: Modifier = Modifier) {
    PanelView(
        backgroundColor = Color(0xFF0D47A1).copy(alpha = 0.7f),
        modifier = modifier
    ) {
        AppGrid(modifier = Modifier.fillMaxSize())
    }
}
