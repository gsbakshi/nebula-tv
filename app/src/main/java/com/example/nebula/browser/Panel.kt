package com.example.nebula.browser

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tv.material3.Button
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import androidx.compose.ui.viewinterop.AndroidView
import org.mozilla.geckoview.GeckoSession
import org.mozilla.geckoview.GeckoView

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun BrowserPanel(modifier: Modifier = Modifier) {
    val viewModel: BrowserViewModel = viewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    // URL bar local input state — syncs with displayUrl when a new page loads
    var urlInput by remember(state.displayUrl) { mutableStateOf(state.displayUrl) }
    val focusManager = LocalFocusManager.current

    // Intercept Back on remote: navigate web history before exiting the browser panel
    BackHandler(enabled = state.canGoBack) {
        viewModel.goBack()
    }

    // ─── Layout ─────────────────────────────────────────────────────
    // GeckoView is a SurfaceView — it punches a hole through the compositor.
    // Toolbar MUST be in a Column row above GeckoView, never z-stacked on top of it.
    Column(modifier = modifier.fillMaxSize()) {

        // ── Toolbar ─────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1A1A2E).copy(alpha = 0.97f))
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Button(
                onClick = viewModel::goBack,
                enabled = state.canGoBack,
            ) { Text("←") }

            Button(
                onClick = viewModel::goForward,
                enabled = state.canGoForward,
            ) { Text("→") }

            // URL / search bar
            OutlinedTextField(
                value = urlInput,
                onValueChange = { urlInput = it },
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(remember { FocusRequester() }),
                singleLine = true,
                placeholder = { Text("Enter URL or search…") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Uri,
                    imeAction = ImeAction.Go
                ),
                keyboardActions = KeyboardActions(
                    onGo = {
                        viewModel.navigate(urlInput)
                        focusManager.clearFocus()  // returns D-pad control to the page
                    }
                ),
            )

            Button(onClick = viewModel::reload) {
                Text(if (state.isLoading) "✕" else "↻")
            }
        }

        // ── Loading bar ─────────────────────────────────────────────
        if (state.isLoading) {
            LinearProgressIndicator(
                progress = { state.progress / 100f },
                modifier = Modifier.fillMaxWidth()
            )
        }

        // ── GeckoView ───────────────────────────────────────────────
        // Takes all remaining space below the toolbar.
        // Compose UI cannot be drawn on top of this — SurfaceView renders below everything.
        GeckoViewComposable(
            session = viewModel.session,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        )
    }
}

@Composable
private fun GeckoViewComposable(session: GeckoSession, modifier: Modifier = Modifier) {
    AndroidView(
        factory = { ctx ->
            GeckoView(ctx).also { view ->
                view.coverUntilFirstPaint(android.graphics.Color.BLACK)  // cover until first paint to avoid white flash
                view.setSession(session)
            }
        },
        update = { view ->
            // Only swap session if it actually changed — unnecessary swaps reset the surface
            if (view.session !== session) {
                view.releaseSession()
                view.setSession(session)
            }
        },
        modifier = modifier
    )
}