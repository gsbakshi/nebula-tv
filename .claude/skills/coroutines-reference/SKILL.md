---
name: coroutines-reference
description: Nebula-specific Kotlin coroutines patterns — stateIn WhileSubscribed, CancellationException rules, withTimeout for widget APIs, supervisorScope for parallel refresh, and CoroutineName for debugging. Loaded automatically when writing ViewModel or Flow code.
user-invocable: false
---

# Nebula Coroutines Reference

Distilled from official Kotlin docs and battle-tested patterns. All examples are Nebula-context (widget refresh, browser, app grid).

## 1. stateIn(WhileSubscribed) — The Standard ViewModel Pattern

**Always use this when exposing StateFlow from a ViewModel:**

```kotlin
class NasaApodViewModel(private val repo: NasaApodRepository) : ViewModel() {

    val state: StateFlow<ApodState> = repo.apodFlow()  // cold Flow from repo/DataStore
        .map { data -> ApodState.Success(data, Instant.now()) }
        .catch { e -> emit(ApodState.Error(e.message ?: "Failed")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),  // 5s grace period
            initialValue = ApodState.Loading
        )
}
```

**Why WhileSubscribed(5000)?**
- Upstream Flow stops collecting 5 seconds after the last subscriber (UI) disappears
- 5-second grace period survives Android screen rotations without restarting the upstream
- Automatically resumes when a new subscriber arrives (launcher comes back to foreground)
- Saves CPU + battery when Nebula launcher is backgrounded
- ❌ `SharingStarted.Eagerly` — keeps collecting forever, even when backgrounded
- ❌ `SharingStarted.Lazily` — never stops collecting once started

## 2. CancellationException — NEVER Catch It Silently

This is the most dangerous coroutine mistake. `CancellationException` extends `Exception`,
so `catch (e: Exception)` will eat it — breaking structured concurrency.

```kotlin
// ✅ CORRECT: explicitly rethrow CancellationException
try {
    val data = withTimeout(30.seconds) { api.fetchApod(key) }
    _state.value = ApodState.Success(data, Instant.now())
} catch (e: TimeoutCancellationException) {
    // withTimeout throws TimeoutCancellationException (a CancellationException subclass)
    // Safe to catch this specific subtype — it's OUR timeout, not parent cancellation
    _state.value = ApodState.Error("Request timed out after 30s")
} catch (e: CancellationException) {
    throw e  // ← MUST RETHROW. This coroutine's parent is cancelling — let it propagate.
} catch (e: IOException) {
    _state.value = ApodState.Error("Network error: ${e.message}")
} catch (e: Exception) {
    _state.value = ApodState.Error(e.message ?: "Unknown error")
}
```

**Rule of thumb:**
- `catch (e: CancellationException) { throw e }` — always, when catching broad exceptions
- `catch (e: TimeoutCancellationException)` — safe to handle, it's your own timeout
- `runCatching { }.getOrElse { }` — also catches CancellationException! Use only for truly throwaway operations

## 3. withTimeout — Mandatory for External API Calls

Every network call in Nebula (NASA, OpenMeteo, RSS, FxA) must have a timeout.
Without it, a slow server stalls the widget indefinitely.

```kotlin
// NASA APOD — daily fetch, generous timeout
val apod = withTimeoutOrNull(30.seconds) {
    nasaApi.fetchApod(BuildConfig.NASA_API_KEY)
} ?: return@launch  // null on timeout — handle gracefully

// OpenMeteo weather — fast API, tight timeout
val weather = withTimeout(15.seconds) {
    openMeteoApi.getCurrent(lat, lon)
}

// RSS feeds — variable latency, log slow feeds
val feed = withTimeout(20.seconds) {
    rssParser.fetch(feedUrl)
}
```

**withTimeout vs withTimeoutOrNull:**
- `withTimeout` — throws `TimeoutCancellationException` on timeout (catch it explicitly)
- `withTimeoutOrNull` — returns `null` on timeout (no exception, simpler null check)

## 4. supervisorScope — Parallel Widget Refresh (Failure Isolation)

When loading multiple widgets in parallel, one failure must NOT cancel the others.

```kotlin
// In HomeViewModel — load all widgets simultaneously
fun refreshAll() {
    viewModelScope.launch(CoroutineName("HomeRefreshAll")) {
        supervisorScope {
            // Each launch is independent — NASA failure won't kill weather refresh
            launch(CoroutineName("RefreshNasaApod")) { nasaApodRepo.refresh() }
            launch(CoroutineName("RefreshWeather"))  { weatherRepo.refresh() }
            launch(CoroutineName("RefreshRss"))      { rssRepo.refresh() }
        }
    }
}
```

**supervisorScope vs coroutineScope:**
- `supervisorScope` — child failures are isolated, siblings continue
- `coroutineScope` — one child failure cancels ALL siblings (good for "all-or-nothing" operations)

Use `supervisorScope` for widget refresh. Use `coroutineScope` for transactions.

## 5. CoroutineName — Debugging Production Crashes

Name your coroutines so stack traces and crash reports are readable.

```kotlin
// Without name: "DefaultDispatcher-worker-3" — useless in crash reports
// With name: "NasaApodRefresh" — immediately tells you what failed

viewModelScope.launch(CoroutineName("NasaApodPeriodicRefresh")) {
    while (true) {
        // ...
    }
}

// Combine with dispatcher
viewModelScope.launch(Dispatchers.IO + CoroutineName("RssFeedParse")) {
    rssParser.fetch(url)
}
```

## 6. flowOn — Move Heavy Work Off Main Thread

```kotlin
// ✅ flowOn shifts upstream operators to IO thread pool
// The downstream .stateIn() runs on the calling coroutine's context (Main)
private fun periodicFetchFlow(): Flow<ApodState> = flow {
    while (true) {
        // This block runs on Dispatchers.IO (specified by flowOn below)
        val data = api.fetchApod(key)
        emit(ApodState.Success(data, Instant.now()))
        delay(24.hours)
    }
}.flowOn(Dispatchers.IO)

val state = periodicFetchFlow().stateIn(viewModelScope, WhileSubscribed(5.seconds), ApodState.Loading)
```

## 7. Periodic Refresh Pattern — Full Example

The canonical Nebula widget refresh loop:

```kotlin
class WeatherViewModel(private val repo: WeatherRepository) : ViewModel() {

    val state: StateFlow<WeatherState> = flow {
        var lastSuccess: WeatherState.Success? = null
        while (true) {
            val newState = try {
                withTimeout(15.seconds) {
                    WeatherState.Success(repo.fetchCurrent(), Instant.now())
                        .also { lastSuccess = it }
                }
            } catch (e: TimeoutCancellationException) {
                lastSuccess?.let { WeatherState.Stale(it.data, it.updatedAt, "Timed out") }
                    ?: WeatherState.Error("Weather unavailable")
            } catch (e: CancellationException) {
                throw e  // propagate parent cancellation
            } catch (e: Exception) {
                lastSuccess?.let { WeatherState.Stale(it.data, it.updatedAt, e.message ?: "Error") }
                    ?: WeatherState.Error(e.message ?: "Failed to load weather")
            }
            emit(newState)
            delay(30.minutes)
        }
    }
    .flowOn(Dispatchers.IO)
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5.seconds),
        initialValue = WeatherState.Loading
    )
}
```

## 8. What NOT to Do

```kotlin
// ❌ GlobalScope — not tied to any lifecycle, leaks forever
GlobalScope.launch { api.fetch() }

// ❌ runBlocking on Main thread — freezes UI
runBlocking { api.fetch() }

// ❌ Catching CancellationException silently
try { /* ... */ } catch (e: Exception) { log(e) }  // eats CancellationException!

// ❌ No timeout on network calls
val data = api.fetch()  // hangs forever on slow server

// ❌ Eagerly started StateFlow
stateIn(viewModelScope, SharingStarted.Eagerly, Loading)  // collects forever

// ❌ fire-and-forget with lifecycleScope in ViewModel — use viewModelScope
lifecycleScope.launch { /* ... */ }  // ViewModels don't have lifecycleScope
```
