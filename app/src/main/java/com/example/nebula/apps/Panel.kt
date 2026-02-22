package com.example.nebula.apps

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.nebula.AppGrid
import com.example.nebula.ui.components.core.PanelView
import com.example.nebula.ui.theme.NebulaAmber

@Composable
fun AppGridPanel(modifier: Modifier = Modifier) {
    PanelView(
        backgroundColor = NebulaAmber,
        modifier = modifier,
    ) {
        AppGrid(modifier = Modifier.fillMaxSize())
    }
}
