package com.example.nebula.browser

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.Immutable
import androidx.lifecycle.AndroidViewModel
import com.example.nebula.NebulaApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.mozilla.geckoview.AllowOrDeny
import org.mozilla.geckoview.GeckoResult
import org.mozilla.geckoview.GeckoSession
import org.mozilla.geckoview.GeckoSession.NavigationDelegate

@Immutable
data class BrowserState(
    val displayUrl: String = "",
    val pageTitle: String = "",
    val isLoading: Boolean = false,
    val progress: Int = 0,
    val canGoBack: Boolean = false,
    val canGoForward: Boolean = false,
)

class BrowserViewModel(application: Application) : AndroidViewModel(application) {

    private val runtime = (application as NebulaApplication).geckoRuntime

    // The active GeckoSession — one per "tab". Exposed so the composable can bind GeckoView.
    val session: GeckoSession = GeckoSession()

    private val _state = MutableStateFlow(BrowserState())
    val state: StateFlow<BrowserState> = _state.asStateFlow()

    init {
        session.navigationDelegate = object : NavigationDelegate {
            override fun onCanGoBack(session: GeckoSession, canGoBack: Boolean) {
                _state.update { it.copy(canGoBack = canGoBack) }
            }

            override fun onCanGoForward(session: GeckoSession, canGoForward: Boolean) {
                _state.update { it.copy(canGoForward = canGoForward) }
            }

            override fun onLoadRequest(
                session: GeckoSession,
                request: NavigationDelegate.LoadRequest
            ): GeckoResult<AllowOrDeny> {
                return GeckoResult.fromValue(AllowOrDeny.ALLOW)
            }
        }

        session.contentDelegate = object : GeckoSession.ContentDelegate {
            override fun onTitleChange(session: GeckoSession, title: String?) {
                _state.update { it.copy(pageTitle = title ?: "") }
            }
        }

        session.progressDelegate = object : GeckoSession.ProgressDelegate {
            override fun onPageStart(session: GeckoSession, url: String) {
                // onPageStart gives us the URL at the start of each navigation —
                // sufficient for the URL bar. onLocationChange has a tricky
                // java.lang.Boolean param in GeckoView 141 that causes Kotlin interop issues.
                _state.update { it.copy(isLoading = true, progress = 0, displayUrl = url) }
            }

            override fun onPageStop(session: GeckoSession, success: Boolean) {
                _state.update { it.copy(isLoading = false, progress = 100) }
            }

            override fun onProgressChange(session: GeckoSession, progress: Int) {
                _state.update { it.copy(progress = progress) }
            }
        }

        // open() must be called before load() — opens the session against the runtime
        session.open(runtime)
        navigate("https://www.google.com")
    }

    fun navigate(input: String) {
        val url = normalizeUrl(input)
        session.load(GeckoSession.Loader().uri(url))
    }

    fun goBack() = session.goBack()
    fun goForward() = session.goForward()
    fun reload() = session.reload()

    override fun onCleared() {
        // Always close the session — not closing leaks native memory in the Gecko process
        session.close()
    }

    private fun normalizeUrl(input: String): String {
        val trimmed = input.trim()
        return when {
            trimmed.startsWith("http://") || trimmed.startsWith("https://") -> trimmed
            trimmed.contains(".") && !trimmed.contains(" ") -> "https://$trimmed"
            else -> "https://www.google.com/search?q=${Uri.encode(trimmed)}"
        }
    }
}