// NEBULA PANEL TEMPLATE
// Replace all PANEL_NAME occurrences with your actual panel name.
// This template is used by the /new-panel skill.
// ─────────────────────────────────────────────────────────────────

package com.example.nebula.PANEL_NAME_LOWERCASE

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nebula.ui.components.core.PanelView
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

// ─── UI State ────────────────────────────────────────────────────
sealed class PANEL_NAMEState {
    object Loading : PANEL_NAMEState()
    data class Success(/* TODO: add your data fields here */) : PANEL_NAMEState()
    data class Error(val message: String) : PANEL_NAMEState()
}

// ─── ViewModel ───────────────────────────────────────────────────
class PANEL_NAMEViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<PANEL_NAMEState>(PANEL_NAMEState.Loading)
    val uiState: StateFlow<PANEL_NAMEState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            try {
                // TODO: load your data here
                // _uiState.value = PANEL_NAMEState.Success(...)
            } catch (e: Exception) {
                _uiState.value = PANEL_NAMEState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun retry() = load()
}

// ─── Panel Root ──────────────────────────────────────────────────
@Composable
fun PANEL_NAMEPanel(
    modifier: Modifier = Modifier,
    viewModel: PANEL_NAMEViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    PanelView(modifier = modifier) {
        when (state) {
            is PANEL_NAMEState.Loading -> PANEL_NAMELoading()
            is PANEL_NAMEState.Error -> PANEL_NAMEError(
                message = (state as PANEL_NAMEState.Error).message,
                onRetry = viewModel::retry
            )
            is PANEL_NAMEState.Success -> PANEL_NAMEContent(
                state = state as PANEL_NAMEState.Success
            )
        }
    }
}

// ─── Content States ───────────────────────────────────────────────
// TODO: Implement these three composables.
// CRITICAL MODIFIER ORDER: focusRequester() MUST come BEFORE focusable()
//   ✅ Modifier.focusRequester(fr).focusable()
//   ❌ Modifier.focusable().focusRequester(fr)  ← silently broken
// Prefer androidx.tv.material3 components (Card, Button) — they handle focus automatically.

@Composable
private fun PANEL_NAMEContent(state: PANEL_NAMEState.Success) {
    Box(modifier = Modifier.fillMaxSize()) {
        // TODO: Implement main content
    }
}

@Composable
private fun PANEL_NAMELoading() {
    // TODO: Loading skeleton / shimmer
}

@Composable
private fun PANEL_NAMEError(message: String, onRetry: () -> Unit) {
    // TODO: Error state with retry action
    // Retry button must be D-pad focusable — use androidx.tv.material3.Button
}
