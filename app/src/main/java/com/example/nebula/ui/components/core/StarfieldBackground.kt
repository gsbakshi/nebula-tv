package com.example.nebula.ui.components.core

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.nebula.ui.theme.NebulaGlow
import com.example.nebula.ui.theme.NebulaPurple
import com.example.nebula.ui.theme.VoidBlack
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

private data class Star(
    val x: Float,           // 0..1 normalised screen X
    val initialY: Float,    // 0..1 normalised screen Y
    val sizeDp: Float,      // visual diameter in dp
    val speed: Float,       // drift multiplier (larger = faster)
    val baseOpacity: Float,
    val sparkle: Boolean,   // whether this star twinkles
    val phase: Float,       // twinkle phase offset (radians)
)

@Composable
fun StarfieldBackground(modifier: Modifier = Modifier) {
    val stars = remember {
        buildList {
            // ── Faint background field (tiny, slow)
            repeat(120) {
                add(Star(
                    x = Random.nextFloat(),
                    initialY = Random.nextFloat(),
                    sizeDp = Random.nextFloat() * 0.8f + 0.3f,
                    speed = Random.nextFloat() * 0.25f + 0.03f,
                    baseOpacity = Random.nextFloat() * 0.30f + 0.05f,
                    sparkle = false,
                    phase = 0f,
                ))
            }
            // ── Mid-layer stars (slightly brighter, some twinkle)
            repeat(35) {
                add(Star(
                    x = Random.nextFloat(),
                    initialY = Random.nextFloat(),
                    sizeDp = Random.nextFloat() * 1.2f + 0.8f,
                    speed = Random.nextFloat() * 0.18f + 0.04f,
                    baseOpacity = Random.nextFloat() * 0.30f + 0.25f,
                    sparkle = Random.nextFloat() > 0.4f,
                    phase = Random.nextFloat() * 2f * PI.toFloat(),
                ))
            }
            // ── Foreground hero stars (few, prominent, all twinkle)
            repeat(8) {
                add(Star(
                    x = Random.nextFloat(),
                    initialY = Random.nextFloat(),
                    sizeDp = Random.nextFloat() * 1.4f + 2.0f,
                    speed = Random.nextFloat() * 0.08f + 0.01f,
                    baseOpacity = Random.nextFloat() * 0.20f + 0.70f,
                    sparkle = true,
                    phase = Random.nextFloat() * 2f * PI.toFloat(),
                ))
            }
        }
    }

    val transition = rememberInfiniteTransition(label = "starfield")

    // Slow vertical drift — one full screen height per 90 seconds for fastest stars
    val drift by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 90_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "drift",
    )

    // Fast twinkle cycle — 0 → 2π in 5 seconds, drives sin() for opacity pulse
    val shimmer by transition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmer",
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        // ── Base void gradient ───────────────────────────────────────────────
        drawRect(
            brush = Brush.verticalGradient(
                0.0f to VoidBlack,
                0.5f to Color(0xFF090C15),
                1.0f to VoidBlack,
            )
        )

        // ── Nebula color washes (large, very faint radial gradients) ─────────
        // Cyan nebula — lower-left quadrant
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    NebulaGlow.copy(alpha = 0.07f),
                    NebulaGlow.copy(alpha = 0.02f),
                    Color.Transparent,
                ),
                center = Offset(size.width * 0.12f, size.height * 0.78f),
                radius = size.width * 0.55f,
            ),
            radius = size.width * 0.55f,
            center = Offset(size.width * 0.12f, size.height * 0.78f),
        )
        // Violet nebula — upper-right quadrant
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    NebulaPurple.copy(alpha = 0.06f),
                    NebulaPurple.copy(alpha = 0.01f),
                    Color.Transparent,
                ),
                center = Offset(size.width * 0.88f, size.height * 0.18f),
                radius = size.width * 0.45f,
            ),
            radius = size.width * 0.45f,
            center = Offset(size.width * 0.88f, size.height * 0.18f),
        )

        // ── Stars ────────────────────────────────────────────────────────────
        stars.forEach { star ->
            val effectiveY = ((star.initialY + drift * star.speed) % 1f) * size.height
            val cx = star.x * size.width
            val cy = effectiveY
            val radius = (star.sizeDp / 2f).dp.toPx()

            val opacity = if (star.sparkle) {
                (star.baseOpacity + sin(shimmer + star.phase) * 0.20f).coerceIn(0f, 1f)
            } else {
                star.baseOpacity
            }

            // Hero stars get a soft outer glow
            if (star.sizeDp >= 2f) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = opacity * 0.35f),
                            Color.Transparent,
                        ),
                        center = Offset(cx, cy),
                        radius = radius * 4f,
                    ),
                    radius = radius * 4f,
                    center = Offset(cx, cy),
                )
            }

            // Star core
            drawCircle(
                color = Color.White.copy(alpha = opacity),
                radius = radius,
                center = Offset(cx, cy),
            )
        }
    }
}
