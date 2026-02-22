package com.example.nebula.ui.components.navigation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Border
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import com.example.nebula.ui.theme.NebulaGlow
import com.example.nebula.ui.theme.StarWhite

private val itemShape = RoundedCornerShape(percent = 50)

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun NavigationItem(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = CardDefaults.shape(itemShape),
        colors = CardDefaults.colors(
            containerColor = if (isSelected) NebulaGlow.copy(alpha = 0.14f) else androidx.compose.ui.graphics.Color.Transparent,
            focusedContainerColor = StarWhite.copy(alpha = 0.12f),
            pressedContainerColor = StarWhite.copy(alpha = 0.08f),
        ),
        scale = CardDefaults.scale(focusedScale = 1.05f),
        border = CardDefaults.border(
            border = Border(BorderStroke(0.dp, androidx.compose.ui.graphics.Color.Transparent)),
            focusedBorder = Border(BorderStroke(0.dp, androidx.compose.ui.graphics.Color.Transparent)),
        ),
    ) {
        Box(modifier = Modifier.padding(horizontal = 22.dp, vertical = 9.dp)) {
            Text(
                text = label.uppercase(),
                style = TextStyle(
                    fontSize = 10.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    letterSpacing = 3.5.sp,
                    color = if (isSelected) NebulaGlow else StarWhite.copy(alpha = 0.38f),
                ),
            )
        }
    }
}
