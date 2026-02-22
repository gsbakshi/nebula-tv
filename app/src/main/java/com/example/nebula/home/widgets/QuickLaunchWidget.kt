package com.example.nebula.home.widgets

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.tv.material3.Border
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import com.example.nebula.AppInfo
import com.example.nebula.ui.theme.GlassDark
import com.example.nebula.ui.theme.NebulaGlow
import com.example.nebula.ui.theme.StarWhite

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
            text = "QUICK LAUNCH",
            style = TextStyle(
                fontSize = 9.sp,
                letterSpacing = 4.sp,
                color = StarWhite.copy(alpha = 0.30f),
            ),
            modifier = Modifier.padding(start = 4.dp, bottom = 10.dp),
        )
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            itemsIndexed(apps, key = { _, app -> app.packageName }) { index, app ->
                AppOrb(
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
private fun AppOrb(app: AppInfo, modifier: Modifier = Modifier, onLaunch: () -> Unit) {
    val iconBitmap = remember(app.packageName) {
        app.icon?.toBitmap()?.asImageBitmap()
    }

    Column(
        modifier = modifier.width(72.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Circular icon — glows cyan on focus
        Card(
            onClick = onLaunch,
            shape = CardDefaults.shape(CircleShape),
            modifier = Modifier.size(68.dp),
            colors = CardDefaults.colors(
                containerColor = GlassDark,
                focusedContainerColor = NebulaGlow.copy(alpha = 0.14f),
                pressedContainerColor = NebulaGlow.copy(alpha = 0.08f),
            ),
            scale = CardDefaults.scale(focusedScale = 1.12f),
            border = CardDefaults.border(
                border = Border(BorderStroke(1.dp, StarWhite.copy(alpha = 0.10f)), shape = CircleShape),
                focusedBorder = Border(BorderStroke(1.5.dp, NebulaGlow.copy(alpha = 0.65f)), shape = CircleShape),
                pressedBorder = Border(BorderStroke(1.5.dp, NebulaGlow.copy(alpha = 0.40f)), shape = CircleShape),
            ),
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                if (iconBitmap != null) {
                    Image(
                        bitmap = iconBitmap,
                        contentDescription = app.label,
                        modifier = Modifier.size(42.dp),
                    )
                }
            }
        }

        Spacer(Modifier.height(6.dp))

        Text(
            text = app.label,
            style = TextStyle(
                fontSize = 9.sp,
                letterSpacing = 0.5.sp,
                color = StarWhite.copy(alpha = 0.45f),
                textAlign = TextAlign.Center,
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
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
