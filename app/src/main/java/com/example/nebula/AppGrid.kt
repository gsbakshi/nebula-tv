package com.example.nebula

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.tv.material3.Text

@Composable
fun AppGrid(modifier: Modifier = Modifier, viewModel: AppGridViewModel = viewModel()) {
    val context = LocalContext.current
    val apps by viewModel.apps.collectAsStateWithLifecycle()

    val firstItemFocus = remember { FocusRequester() }

    // Auto-focus the first app when the grid becomes visible
    LaunchedEffect(apps.isNotEmpty()) {
        if (apps.isNotEmpty()) {
            runCatching { firstItemFocus.requestFocus() }
        }
    }

    if (apps.isEmpty()) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Loading apps…")
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(apps, key = { _, app -> app.packageName }) { index, app ->
                AppItem(
                    appInfo = app,
                    modifier = if (index == 0) Modifier.focusRequester(firstItemFocus) else Modifier,
                    onAppClick = { clickedApp -> launchApp(context, clickedApp) }
                )
            }
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
