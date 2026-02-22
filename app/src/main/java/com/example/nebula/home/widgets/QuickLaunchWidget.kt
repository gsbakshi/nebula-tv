package com.example.nebula.home.widgets

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.tv.material3.Card
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.example.nebula.AppInfo

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun QuickLaunchWidget(apps: List<AppInfo>, modifier: Modifier = Modifier) {
    if (apps.isEmpty()) return

    val context = LocalContext.current
    val firstItemFocus = remember { FocusRequester() }

    LaunchedEffect(apps.isNotEmpty()) {
        if (apps.isNotEmpty()) runCatching { firstItemFocus.requestFocus() }
    }

    Column(modifier = modifier) {
        Text(
            text = "Quick Launch",
            style = MaterialTheme.typography.labelMedium,
            color = Color.White.copy(alpha = 0.5f),
            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp),
        )
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            itemsIndexed(apps, key = { _, app -> app.packageName }) { index, app ->
                QuickLaunchItem(
                    app = app,
                    modifier = if (index == 0) Modifier.focusRequester(firstItemFocus) else Modifier,
                    onLaunch = { launchApp(context, app) },
                )
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun QuickLaunchItem(app: AppInfo, modifier: Modifier = Modifier, onLaunch: () -> Unit) {
    val iconBitmap = remember(app.packageName) {
        app.icon?.toBitmap()?.asImageBitmap()
    }

    // TV Card handles D-pad focus, scale, and OK-key click — no extra .focusable() needed
    Card(
        onClick = onLaunch,
        modifier = modifier.width(80.dp).height(90.dp),
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            if (iconBitmap != null) {
                Image(
                    bitmap = iconBitmap,
                    contentDescription = app.label,
                    modifier = Modifier.size(44.dp),
                )
            }
            Text(
                text = app.label,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
            )
        }
    }
}

private fun launchApp(context: Context, app: AppInfo) {
    app.launchIntent?.let {
        try {
            context.startActivity(it)
        } catch (e: Exception) {
            Toast.makeText(context, "Could not launch ${app.label}", Toast.LENGTH_SHORT).show()
        }
    }
}
