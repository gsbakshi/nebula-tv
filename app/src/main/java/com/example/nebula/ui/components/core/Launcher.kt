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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.nebula.apps.AppGridPanel
import com.example.nebula.browser.BrowserPanel
import com.example.nebula.browser.BrowserViewModel
import com.example.nebula.constants.ViewScreen
import com.example.nebula.home.WidgetAreaPanel
import com.example.nebula.ui.components.navigation.NavigationBar

@Composable
fun LauncherScreen(navController: NavHostController = rememberNavController()) {
    var currentView by remember { mutableStateOf(ViewScreen.WIDGETS) }

    // Hoist BrowserViewModel here so the home panel can navigate to a URL and switch panels.
    // BrowserPanel's internal viewModel() call returns the same instance (same ViewModelStoreOwner).
    val browserViewModel: BrowserViewModel = viewModel()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            NavigationBar(
                currentScreen = currentView,
                onScreenSelected = { screen -> currentView = screen },
                modifier = Modifier.fillMaxWidth(),
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp),
            ) {
                when (currentView) {
                    ViewScreen.WIDGETS -> WidgetAreaPanel(
                        onOpenUrl = { url ->
                            browserViewModel.navigate(url)
                            currentView = ViewScreen.BROWSER
                        },
                        modifier = Modifier.fillMaxSize(),
                    )
                    ViewScreen.APP_GRID -> AppGridPanel(Modifier.fillMaxSize())
                    ViewScreen.BROWSER -> BrowserPanel(Modifier.fillMaxSize())
                }
            }
        }
    }
}
