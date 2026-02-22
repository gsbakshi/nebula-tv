package com.example.nebula.ui.components.core

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.nebula.ui.theme.GlassBorder

private val panelShape = RoundedCornerShape(20.dp)

/**
 * Glassmorphic panel surface. [accentColor] provides a subtle background tint — pass a
 * full-saturation theme color and the panel applies its own low opacity, so the caller
 * doesn't need to manage alpha manually.
 */
@Composable
fun PanelView(
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        backgroundColor.copy(alpha = 0.10f),
                        backgroundColor.copy(alpha = 0.04f),
                    )
                ),
                shape = panelShape,
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        GlassBorder.copy(alpha = 1.8f),   // top-left highlight
                        GlassBorder,
                        GlassBorder.copy(alpha = 0.4f),   // bottom-right fade
                    )
                ),
                shape = panelShape,
            ),
        contentAlignment = Alignment.TopStart,
        content = content,
    )
}
