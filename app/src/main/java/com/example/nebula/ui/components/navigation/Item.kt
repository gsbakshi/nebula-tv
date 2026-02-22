package com.example.nebula.ui.components.navigation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.Card
import androidx.tv.material3.CardColors
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.Surface
import com.example.nebula.ui.components.core.PanelView

@Composable
fun NavigationItem(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    Card (
        onClick = onClick,
        modifier = Modifier.width(72.dp),
        shape = CardDefaults.shape(RoundedCornerShape(4.dp)),
        colors = CardDefaults.colors(
            containerColor = if (isSelected) Color(0xFF030507) else Color.Transparent,
            focusedContainerColor = Color(3, 5, 7),
            pressedContainerColor =  Color(3, 5, 7).copy(alpha = 0.8f),
            contentColor = if (isSelected) Color(0xFF030507) else Color.Transparent,
            pressedContentColor = Color(248, 250, 252).copy(alpha = 0.8f),
            focusedContentColor = Color(248, 250, 252).copy(alpha = 0.8f)
            ),
        scale = CardDefaults.scale(focusedScale = 1.1f), // TV focus effect
        border = CardDefaults.border(
            pressedBorder = Border(
                BorderStroke(
                    2.dp,
                    Color(30, 41, 59)
                )
            ),
            focusedBorder = Border(
                BorderStroke(
                    2.dp,
                    Color(30, 41, 59)
                )
            )
        )
    ) {
        PanelView(
            backgroundColor = Color.Transparent,
            modifier = modifier // Apply the passed modifier first
                .focusable(interactionSource = interactionSource)
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = Color.White
            )
        }
    }
}
