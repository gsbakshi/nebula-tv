package com.example.nebula.ui.theme

import androidx.compose.runtime.Composable
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.darkColorScheme

private val NebulaColorScheme = darkColorScheme(
    primary         = NebulaGlow,
    secondary       = NebulaPurple,
    tertiary        = NebulaAmber,
    background      = VoidBlack,
    surface         = AbyssBlue,
    onPrimary       = VoidBlack,
    onSecondary     = VoidBlack,
    onBackground    = StarWhite,
    onSurface       = StarWhite,
)

@Composable
fun NebulaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = NebulaColorScheme,
        typography = Typography,
        content = content,
    )
}
