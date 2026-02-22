package com.example.nebula

import android.app.Application
import org.mozilla.geckoview.GeckoRuntime
import org.mozilla.geckoview.GeckoRuntimeSettings

class NebulaApplication : Application() {

    // GeckoRuntime is a singleton — creating it more than once per process crashes.
    // lazy{} ensures it's initialized once, on first access.
    // Initialized in Application (not Activity) so it survives configuration changes.
    val geckoRuntime: GeckoRuntime by lazy {
        GeckoRuntime.create(
            this,
            GeckoRuntimeSettings.Builder()
                .consoleOutput(false)
                .build()
        )
    }
}
