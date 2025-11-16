package com.example.nebula.ui.components.navigation

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.copy
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Surface
import androidx.tv.material3.SurfaceDefaults
import com.example.nebula.constants.ViewScreen
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.tv.material3.Border

@Composable
fun NavigationBar(
    currentScreen: ViewScreen,
    onScreenSelected: (ViewScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedTabIndex = remember(currentScreen) {
        ViewScreen.entries.indexOf(currentScreen)
    }

    // For Glass morphism, the background of the bar itself needs to be semi-transparent
    // and potentially have a blur effect if we can apply it.
    // androidx.compose.ui.draw.blur is not directly available on Android TV for background blur
    // We might need to simulate it or use other techniques for a glass-like feel.
    Box(
        modifier = modifier
            .padding(8.dp)
            .background(
                // For Glass morphism, panels might also have semi-transparent backgrounds
                // and potentially a border to lift them off the main background.
                brush = Brush.linearGradient( // Example subtle gradient
                    colors = listOf(
                        Color(30, 41, 59).copy(alpha = 0.6f),
                        Color(30, 41, 59).copy(alpha = 0.4f)
                    )
                ),
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                1.dp, Color.White.copy(alpha = 0.1f),
                RoundedCornerShape(8.dp)
            )
            .height(48.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround // Or SpaceBetween
        ) {
            ViewScreen.entries.forEach { screen ->
                NavigationItem(
                    label = screen.title,
                    isSelected = screen == currentScreen,
                    onClick = { onScreenSelected(screen) }
                )

            }
        }


        // Spacer to push status items to the right (if using SpaceAround, this might not be needed)
        // Spacer(Modifier.weight(1f))

        // Status/Contextual Items (Placeholders for now)
        // Example: Media Controls (very simplified)
        // IconButton(onClick = { /* Play/Pause */ }) {
        //     Icon(Icons.Filled.PlayArrow, contentDescription = "Play/Pause")
        // }
        // IconButton(onClick = { /* Settings */ }) {
        //     Icon(Icons.Filled.Settings, contentDescription = "Settings")
        // }
    }
}


/**
 * A custom pill-shaped indicator for a TabRow.
 *
 * @param currentTabPosition The [TabPosition] of the currently selected tab.
 * @param activeColor The color of the pill indicator.
 * @param modifier Modifier for the indicator.
 * @param pillHeight The height of the pill.
 * @param pillHorizontalPadding The horizontal padding for the pill, making it slightly narrower than the tab.
 */
@Composable
fun CustomPillIndicator(
    currentTabPosition: TabPosition,
    activeColor: Color,
    modifier: Modifier = Modifier,
    pillHeight: Dp = Dp.Unspecified, // Default to full tab height or specify
    pillHorizontalPadding: Dp = 0.dp
) {
    // Animate the indicator's position and width
    val currentTabWidth by animateDpAsState(
        targetValue = currentTabPosition.width - (pillHorizontalPadding * 2), // Adjust width for padding
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMedium),
        label = "PillIndicatorWidth"
    )
    val indicatorOffset by animateDpAsState(
        targetValue = currentTabPosition.left + pillHorizontalPadding, // Adjust offset for padding
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMedium),
        label = "PillIndicatorOffset"
    )

    Box(
        modifier
            .fillMaxHeight(if (pillHeight == Dp.Unspecified) 1f else 0f) // If no specific height, fill.
            .height(if (pillHeight != Dp.Unspecified) pillHeight else 0.dp) // Apply specific height if given.
            .width(currentTabWidth)
            .offset(x = indicatorOffset)
            .background(
                color = activeColor,
                shape = RoundedCornerShape(percent = 50) // Pill shape
            )
    )
}