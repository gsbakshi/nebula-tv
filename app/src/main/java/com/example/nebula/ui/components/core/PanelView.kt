package com.example.nebula.ui.components.core

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PanelView(
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    content: @Composable() (BoxScope.() -> Unit)
) {
    Box(
        modifier = modifier
            .background(
                // For Glass morphism, panels might also have semi-transparent backgrounds
                // and potentially a border to lift them off the main background.
                brush = Brush.linearGradient( // Example subtle gradient
                    colors = listOf(
                        backgroundColor,
                        backgroundColor
                    )
                ),
                shape = RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.Center,
        content = content
    )
}
