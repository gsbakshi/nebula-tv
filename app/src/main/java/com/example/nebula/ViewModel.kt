package com.example.nebula

import android.app.Application
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AppInfo(
    val label: String,
    val packageName: String,
    val icon: Drawable?,
    val launchIntent: Intent?
)

// AndroidViewModel provides Application context safely — no need to pass Context from composables.
class AppGridViewModel(application: Application) : AndroidViewModel(application) {

    private val _apps = MutableStateFlow<List<AppInfo>>(emptyList())
    val apps: StateFlow<List<AppInfo>> = _apps.asStateFlow()

    init {
        loadApps()
    }

    private fun loadApps() {
        viewModelScope.launch(Dispatchers.IO) {
            val pm = getApplication<Application>().packageManager
            val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            val appList = pm.queryIntentActivities(mainIntent, 0)
                .map { info ->
                    AppInfo(
                        label = info.loadLabel(pm).toString(),
                        packageName = info.activityInfo.packageName,
                        icon = info.loadIcon(pm),
                        launchIntent = pm.getLaunchIntentForPackage(info.activityInfo.packageName)
                    )
                }
                .distinctBy { it.packageName }  // packages with multiple launcher activities appear once
                .sortedBy { it.label.lowercase() }
            _apps.value = appList
        }
    }
}