package com.example.nebula.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nebula.home.widgets.ClockWidget
import com.example.nebula.home.widgets.FeedWidget
import com.example.nebula.home.widgets.QuickLaunchWidget
import com.example.nebula.home.widgets.WeatherWidget
import com.example.nebula.ui.components.core.PanelView
import com.example.nebula.ui.theme.NebulaGlow
import com.example.nebula.ui.theme.NebulaPurple

@Composable
fun WidgetAreaPanel(
    onOpenUrl: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Row(modifier = modifier) {
        // ── Left: clock + weather + quick launch (cyan tinted glass) ────────
        PanelView(
            backgroundColor = NebulaGlow,
            modifier = Modifier
                .weight(0.55f)
                .fillMaxHeight()
                .padding(end = 8.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    ClockWidget(clockState = state.clock)
                    Spacer(Modifier.height(16.dp))
                    WeatherWidget(weatherState = state.weather)
                }
                QuickLaunchWidget(apps = state.quickLaunch)
            }
        }

        // ── Right: RSS news feed (violet tinted glass) ───────────────────────
        PanelView(
            backgroundColor = NebulaPurple,
            modifier = Modifier
                .weight(0.45f)
                .fillMaxHeight(),
        ) {
            FeedWidget(
                feedState = state.feed,
                onOpenUrl = onOpenUrl,
                modifier = Modifier.fillMaxSize().padding(16.dp),
            )
        }
    }
}
