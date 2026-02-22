package com.example.nebula

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.tv.material3.Surface
import com.example.nebula.ui.theme.NebulaTheme
import com.example.nebula.ui.components.core.LauncherScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Apply your TV-specific Material 3 Compose theme here
            NebulaTheme () { // This is from androidx.tv.material3
                Surface(
                    // Use androidx.tv.material3.Surface
                    modifier = Modifier.fillMaxSize(),
                    shape = RectangleShape, // TV apps often use RectangleShape
                ) {
                    LauncherScreen()
                }
            }
        }
    }
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