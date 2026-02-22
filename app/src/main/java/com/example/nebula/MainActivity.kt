package com.example.nebula

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.tv.material3.Surface
import com.example.nebula.browser.BrowserViewModel
import com.example.nebula.ui.theme.NebulaTheme
import com.example.nebula.ui.components.core.LauncherScreen

class MainActivity : ComponentActivity() {

    private val browserViewModel: BrowserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent?.extractUrl()?.let { browserViewModel.openExternal(it) }
        setContent {
            NebulaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RectangleShape,
                ) {
                    LauncherScreen()
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent.extractUrl()?.let { browserViewModel.openExternal(it) }
    }

    private fun Intent.extractUrl(): String? =
        data?.toString()?.takeIf { action == Intent.ACTION_VIEW }
}

@Preview(device = "id:tv_1080p") // Use a TV device for preview
@Composable
fun DefaultPreviewOfTVLauncherScreen() {
    // For older tv-material:
    NebulaTheme () {
        Surface(modifier = Modifier.fillMaxSize(), shape = RectangleShape) {
            LauncherScreen()
        }
    }
}