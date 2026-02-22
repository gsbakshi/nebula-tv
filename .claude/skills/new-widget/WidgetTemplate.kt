// NEBULA WIDGET TEMPLATE
// Replace WIDGET_NAME with your actual widget name (e.g., NasaApod, Weather, Rss).
// Replace WIDGET_DATA with your data class name.
// ─────────────────────────────────────────────────────────────────
//
// Architecture: periodicFetchFlow() + stateIn(WhileSubscribed(5s))
//   - WhileSubscribed(5s): stops upstream collection 5s after UI detaches,
//     resumes automatically when UI returns. Survives screen rotation without
//     restarting the refresh loop. Pauses when launcher is backgrounded.
//
// ⚠️ CancellationException MUST be rethrown when catching broad exceptions.
//     Catching it silently breaks structured concurrency.
//
// ⚠️ withTimeout wraps every external API call — prevents infinite hangs.

package com.example.nebula.widgets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withTimeout
import java.time.Instant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

// ─── Data Model ──────────────────────────────────────────────────
// @Immutable tells Compose: none of the fields will change after construction.
// This lets Compose skip recomposing children that received this object when
// other state changes — a significant performance win on complex widget UIs.
@Immutable
data class WIDGET_DATA(
    val title: String,
    val content: String,
    // Add your fields here
)

// ─── Widget State ─────────────────────────────────────────────────
// Stale = we have old data but latest refresh failed. Always prefer
// showing Stale over Error when we have cached data.
sealed class WIDGET_NAMEState {
    object Loading : WIDGET_NAMEState()
    data class Success(val data: WIDGET_DATA, val updatedAt: Instant) : WIDGET_NAMEState()
    data class Stale(val data: WIDGET_DATA, val updatedAt: Instant, val error: String) : WIDGET_NAMEState()
    data class Error(val message: String) : WIDGET_NAMEState()
}

// ─── ViewModel ───────────────────────────────────────────────────
class WIDGET_NAMEViewModel : ViewModel() {

    // TODO: Set appropriate refresh interval for your widget
    //   NASA APOD: 24.hours  |  Weather: 30.minutes  |  RSS: 15.minutes  |  Calendar: 5.minutes
    private val refreshInterval: Duration = 30.minutes

    // StateFlow exposed to UI — backed by a periodic cold Flow.
    // WhileSubscribed(5s): upstream pauses 5s after last subscriber leaves,
    // then resumes when subscribers return. Rotation-safe (no restart within 5s).
    val state: StateFlow<WIDGET_NAMEState> = periodicFetchFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = WIDGET_NAMEState.Loading
        )

    private fun periodicFetchFlow(): Flow<WIDGET_NAMEState> = flow {
        var lastSuccess: WIDGET_NAMEState.Success? = null

        while (true) {
            val newState = try {
                // withTimeout prevents infinite hangs on slow/unresponsive APIs.
                // Adjust timeout based on expected response time for your data source.
                withTimeout(30.seconds) {
                    // TODO: Replace with your actual data fetch:
                    //   val data = dataSource.fetch()
                    //   WIDGET_NAMEState.Success(data, Instant.now())
                    WIDGET_NAMEState.Loading  // placeholder — replace with real fetch
                }.also { if (it is WIDGET_NAMEState.Success) lastSuccess = it }
            } catch (e: TimeoutCancellationException) {
                // withTimeout expired — our own timeout, safe to handle
                staleOrError(lastSuccess, "Request timed out")
            } catch (e: CancellationException) {
                throw e  // ← MUST RETHROW. Parent scope is cancelling this coroutine.
                         //   Catching this silently breaks structured concurrency.
            } catch (e: Exception) {
                // Network error, parse error, etc. — preserve last good data as Stale
                staleOrError(lastSuccess, e.message ?: "Failed to load")
            }

            emit(newState)
            delay(refreshInterval)
        }
    }.flowOn(Dispatchers.IO)  // fetch runs on IO thread pool; stateIn stays on Main

    private fun staleOrError(
        lastSuccess: WIDGET_NAMEState.Success?,
        error: String
    ): WIDGET_NAMEState = when (lastSuccess) {
        null -> WIDGET_NAMEState.Error(error)
        else -> WIDGET_NAMEState.Stale(lastSuccess.data, lastSuccess.updatedAt, error)
    }
}

// ─── Composable ──────────────────────────────────────────────────
@Composable
fun WIDGET_NAMEWidget(
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier,
    viewModel: WIDGET_NAMEViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    when (state) {
        is WIDGET_NAMEState.Loading -> WIDGET_NAMELoading(modifier)
        is WIDGET_NAMEState.Success -> WIDGET_NAMEContent(
            data = (state as WIDGET_NAMEState.Success).data,
            modifier = modifier
        )
        is WIDGET_NAMEState.Stale -> WIDGET_NAMEContent(
            data = (state as WIDGET_NAMEState.Stale).data,
            modifier = modifier,
            staleMessage = "Updated ${/* format timestamp */ ""}"
        )
        is WIDGET_NAMEState.Error -> WIDGET_NAMEError(
            message = (state as WIDGET_NAMEState.Error).message,
            modifier = modifier
        )
    }
}

// ─── Content Composables ──────────────────────────────────────────
// TODO: Implement all three. Interactive elements need D-pad focus.

@Composable
private fun WIDGET_NAMEContent(
    data: WIDGET_DATA,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier,
    staleMessage: String? = null
) {
    // TODO: Render widget data. Show staleMessage if non-null.
    // Use androidx.tv.material3.Card for focusable containers.
}

@Composable
private fun WIDGET_NAMELoading(modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier) {
    // TODO: Skeleton / shimmer placeholder while data loads
}

@Composable
private fun WIDGET_NAMEError(
    message: String,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier
) {
    // TODO: Error state. Retry button uses androidx.tv.material3.Button (D-pad safe).
    // Note: No manual retry needed — stateIn(WhileSubscribed) auto-resumes the flow.
    // The next fetch will happen after refreshInterval automatically.
    // If you want a manual refresh button, wire it to a MutableSharedFlow<Unit> trigger
    // that merges into periodicFetchFlow — see coroutines-reference skill for pattern.
}