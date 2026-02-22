package com.example.nebula.ui.components.core

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.nebula.apps.AppGridPanel
import com.example.nebula.browser.BrowserPanel
import com.example.nebula.constants.ViewScreen
import com.example.nebula.home.WidgetAreaPanel
import com.example.nebula.ui.components.navigation.NavigationBar

@Composable
fun LauncherScreen(navController: NavHostController = rememberNavController()) {
    // State to manage which view is currently shown in the Main Content Area
    var currentView by remember { mutableStateOf(ViewScreen.WIDGETS) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background Image
//        Image(
//            painter = painterResource(id = R.drawable.hud_3), // REPLACE with your image
//            contentDescription = "Space background", // Decorative image, null contentDescription is also okay
//            modifier = Modifier.fillMaxSize(),
//            contentScale = ContentScale.Crop // Or ContentScale.FillBounds, ContentScale.Fit, etc.
//            // Crop usually works well for backgrounds to fill space
//            // without distortion, by cropping parts of the image
//            // if aspect ratios don't match.
//        )

        // This is where main TV UI will be built.
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            // Navigation & Status Bar
            NavigationBar(
                currentScreen = currentView,
                onScreenSelected = { screen -> currentView = screen },
                modifier = Modifier.fillMaxWidth()
            )

            // Top Area: Perhaps for a greeting or global status (e.g., time/weather preview)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // Takes up all space not used by the bottom bar
                    .fillMaxWidth()
                    .padding(16.dp) // Padding for the content area
            ) {
                // This is where your different views will be swapped
                // We'll use a simple when for now, but Navigation Component is better for complex cases
                when (currentView) {
                    ViewScreen.WIDGETS -> WidgetAreaPanel(Modifier.fillMaxSize())
                    ViewScreen.APP_GRID -> AppGridPanel(Modifier.fillMaxSize())
                    ViewScreen.BROWSER -> BrowserPanel(Modifier.fillMaxSize())
                }
            }
        }
    }
}
