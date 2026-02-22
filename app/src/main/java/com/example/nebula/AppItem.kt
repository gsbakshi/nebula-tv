package com.example.nebula

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.tv.material3.Card
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import androidx.compose.foundation.Image

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun AppItem(
    appInfo: AppInfo,
    modifier: Modifier = Modifier,
    onAppClick: (AppInfo) -> Unit
) {
    // Cache bitmap conversion — toBitmap() is expensive, don't run it on every recomposition.
    // Keyed by packageName so it recomputes only if the app entry changes.
    val iconBitmap = remember(appInfo.packageName) {
        appInfo.icon?.toBitmap()?.asImageBitmap()
    }

    // TV Material3 Card handles D-pad focus, scale animation, and OK key internally.
    // Do NOT add .focusable() here — Card already sets it up, and adding it twice
    // can interfere with focus traversal.
    Card(
        onClick = { onAppClick(appInfo) },
        modifier = modifier
            .width(120.dp)
            .height(150.dp)
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (iconBitmap != null) {
                Image(
                    bitmap = iconBitmap,
                    contentDescription = appInfo.label,
                    modifier = Modifier.size(64.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = appInfo.label,
                maxLines = 1,
            )
        }
    }
}
