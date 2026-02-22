package com.example.nebula.home.widgets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Text
import com.example.nebula.home.ClockState
import com.example.nebula.ui.theme.NebulaGlow
import com.example.nebula.ui.theme.StarWhite

@Composable
fun ClockWidget(clockState: ClockState, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.drawBehind {
            // Soft cyan aura radiating from behind the clock — the pulsar glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        NebulaGlow.copy(alpha = 0.09f),
                        NebulaGlow.copy(alpha = 0.02f),
                        Color.Transparent,
                    ),
                    center = Offset(size.width * 0.2f, size.height * 0.35f),
                    radius = size.height * 1.8f,
                ),
                radius = size.height * 1.8f,
                center = Offset(size.width * 0.2f, size.height * 0.35f),
            )
        }
    ) {
        // ── Time — the hero element ──────────────────────────────────────────
        Text(
            text = clockState.time,
            style = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Thin,
                fontSize = 96.sp,
                letterSpacing = (-3).sp,
                color = StarWhite,
            ),
        )

        Spacer(Modifier.height(2.dp))

        // ── Date — tracked-out, accent colour ───────────────────────────────
        Text(
            text = clockState.date.uppercase(),
            style = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Normal,
                fontSize = 10.sp,
                letterSpacing = 7.sp,
                color = NebulaGlow.copy(alpha = 0.75f),
            ),
        )

        Spacer(Modifier.height(8.dp))

        // ── Greeting — whisper quiet ─────────────────────────────────────────
        Text(
            text = clockState.greeting,
            style = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Light,
                fontSize = 13.sp,
                letterSpacing = 1.sp,
                color = StarWhite.copy(alpha = 0.35f),
            ),
        )
    }
}
