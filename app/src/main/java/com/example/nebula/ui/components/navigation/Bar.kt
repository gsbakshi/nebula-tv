package com.example.nebula.ui.components.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.nebula.constants.ViewScreen
import com.example.nebula.ui.theme.GlassBorder
import com.example.nebula.ui.theme.VoidBlack

private val pillShape = RoundedCornerShape(percent = 50)

@Composable
fun NavigationBar(
    currentScreen: ViewScreen,
    onScreenSelected: (ViewScreen) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Full-width row so the pill is centred within the available space
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Floating pill container
        Row(
            modifier = Modifier
                .padding(top = 14.dp, bottom = 6.dp)
                .background(
                    color = VoidBlack.copy(alpha = 0.88f),
                    shape = pillShape,
                )
                .border(
                    width = 1.dp,
                    color = GlassBorder,
                    shape = pillShape,
                )
                .padding(horizontal = 4.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ViewScreen.entries.forEach { screen ->
                NavigationItem(
                    label = screen.title,
                    isSelected = screen == currentScreen,
                    onClick = { onScreenSelected(screen) },
                )
            }
        }
    }
}
