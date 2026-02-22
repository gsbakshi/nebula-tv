package com.example.nebula.dream

import android.service.dreams.DreamService
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import androidx.compose.ui.platform.ComposeView
import androidx.tv.material3.Text
import com.example.nebula.ui.components.core.StarfieldBackground
import com.example.nebula.ui.theme.NebulaGlow
import com.example.nebula.ui.theme.NebulaTheme
import com.example.nebula.ui.theme.StarWhite
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NebulaDream : DreamService(), LifecycleOwner, ViewModelStoreOwner, SavedStateRegistryOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val _viewModelStore = ViewModelStore()
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val viewModelStore: ViewModelStore get() = _viewModelStore
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        isInteractive = false
        isFullscreen = true

        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        val view = ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@NebulaDream)
            setViewTreeViewModelStoreOwner(this@NebulaDream)
            setViewTreeSavedStateRegistryOwner(this@NebulaDream)
            setContent {
                NebulaTheme {
                    DreamScreen()
                }
            }
        }
        setContentView(view)
    }

    override fun onDreamingStopped() {
        super.onDreamingStopped()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        _viewModelStore.clear()
    }
}

@Composable
private fun DreamScreen() {
    val timeFmt = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val dateFmt = remember { SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()) }

    var time by remember { mutableStateOf(timeFmt.format(Date())) }
    var date by remember { mutableStateOf(dateFmt.format(Date())) }

    LaunchedEffect(Unit) {
        while (true) {
            val now = Date()
            time = timeFmt.format(now)
            date = dateFmt.format(now)
            delay(10_000L) // update every 10 s — sufficient for HH:mm display
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        StarfieldBackground(modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = time,
                style = TextStyle(
                    fontWeight = FontWeight.Thin,
                    fontSize = 120.sp,
                    letterSpacing = (-4).sp,
                    color = StarWhite,
                ),
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = date.uppercase(),
                style = TextStyle(
                    fontWeight = FontWeight.Light,
                    fontSize = 11.sp,
                    letterSpacing = 8.sp,
                    color = NebulaGlow.copy(alpha = 0.70f),
                ),
            )
        }

        Text(
            text = "NEBULA",
            style = TextStyle(
                fontWeight = FontWeight.Thin,
                fontSize = 9.sp,
                letterSpacing = 10.sp,
                color = StarWhite.copy(alpha = 0.12f),
            ),
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}
