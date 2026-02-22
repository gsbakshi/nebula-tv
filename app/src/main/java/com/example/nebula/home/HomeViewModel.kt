package com.example.nebula.home

import android.app.Application
import android.content.Intent
import androidx.compose.runtime.Immutable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.nebula.AppInfo
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import org.json.JSONObject
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.InputStreamReader
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ── State types ──────────────────────────────────────────────────────────────

@Immutable
data class ClockState(
    val time: String = "",
    val date: String = "",
    val greeting: String = "",
)

sealed interface WeatherState {
    data object Loading : WeatherState
    data class Success(
        val temperature: String,
        val description: String,
        val city: String,
    ) : WeatherState
    data class Error(val message: String) : WeatherState
}

@Immutable
data class FeedItem(val title: String, val link: String, val pubDate: String)

sealed interface FeedState {
    data object Loading : FeedState
    data class Success(val items: List<FeedItem>) : FeedState
    data class Error(val message: String) : FeedState
}

@Immutable
data class HomeState(
    val clock: ClockState = ClockState(),
    val weather: WeatherState = WeatherState.Loading,
    val feed: FeedState = FeedState.Loading,
    val quickLaunch: List<AppInfo> = emptyList(),
)

// ── ViewModel ────────────────────────────────────────────────────────────────

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val _quickLaunch = MutableStateFlow<List<AppInfo>>(emptyList())

    private val clockFlow: Flow<ClockState> = flow {
        while (true) {
            emit(buildClockState())
            delay(1_000)
        }
    }

    private val weatherFlow: Flow<WeatherState> = flow {
        emit(WeatherState.Loading)
        while (true) {
            emit(fetchWeather())
            delay(15 * 60 * 1_000L)
        }
    }.flowOn(Dispatchers.IO)

    private val feedFlow: Flow<FeedState> = flow {
        emit(FeedState.Loading)
        while (true) {
            emit(fetchFeed())
            delay(30 * 60 * 1_000L)
        }
    }.flowOn(Dispatchers.IO)

    val state: StateFlow<HomeState> = combine(
        clockFlow, weatherFlow, feedFlow, _quickLaunch
    ) { clock: ClockState, weather: WeatherState, feed: FeedState, quickLaunch: List<AppInfo> ->
        HomeState(clock = clock, weather = weather, feed = feed, quickLaunch = quickLaunch)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeState(),
    )

    init {
        loadQuickLaunch()
    }

    private fun loadQuickLaunch() {
        viewModelScope.launch(Dispatchers.IO) {
            val pm = getApplication<Application>().packageManager
            val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            val apps = pm.queryIntentActivities(mainIntent, 0)
                .map { info ->
                    AppInfo(
                        label = info.loadLabel(pm).toString(),
                        packageName = info.activityInfo.packageName,
                        icon = info.loadIcon(pm),
                        launchIntent = pm.getLaunchIntentForPackage(info.activityInfo.packageName),
                    )
                }
                .distinctBy { it.packageName }
                .sortedBy { it.label.lowercase() }
                .take(5)
            _quickLaunch.value = apps
        }
    }

    private fun buildClockState(): ClockState {
        val now = Date()
        val time = SimpleDateFormat("h:mm a", Locale.getDefault()).format(now)
        val date = SimpleDateFormat("EEEE, MMM d", Locale.getDefault()).format(now)
        val hour = SimpleDateFormat("H", Locale.getDefault()).format(now).toInt()
        val greeting = when {
            hour < 12 -> "Good morning"
            hour < 17 -> "Good afternoon"
            else -> "Good evening"
        }
        return ClockState(time = time, date = date, greeting = greeting)
    }

    private suspend fun fetchWeather(): WeatherState {
        return try {
            withTimeout(15_000L) {
                // Step 1: IP geolocation — free, no key required
                val geoConn = URL("http://ip-api.com/json/?fields=lat,lon,city").openConnection()
                geoConn.connectTimeout = 8_000
                geoConn.readTimeout = 8_000
                val geo = JSONObject(geoConn.getInputStream().bufferedReader().readText())
                val lat = geo.getDouble("lat")
                val lon = geo.getDouble("lon")
                val city = geo.optString("city", "")

                // Step 2: Open-Meteo forecast — free, no key required
                val meteoUrl = "https://api.open-meteo.com/v1/forecast" +
                    "?latitude=$lat&longitude=$lon" +
                    "&current=temperature_2m,apparent_temperature,weathercode" +
                    "&temperature_unit=celsius&timezone=auto"
                val meteoConn = URL(meteoUrl).openConnection()
                meteoConn.connectTimeout = 8_000
                meteoConn.readTimeout = 8_000
                val current = JSONObject(meteoConn.getInputStream().bufferedReader().readText())
                    .getJSONObject("current")

                WeatherState.Success(
                    temperature = "%.0f°C".format(current.getDouble("temperature_2m")),
                    description = wmoDescription(current.getInt("weathercode")),
                    city = city,
                )
            }
        } catch (_: TimeoutCancellationException) {
            WeatherState.Error("Request timed out")
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            WeatherState.Error(e.message ?: "Weather unavailable")
        }
    }

    private fun wmoDescription(code: Int): String = when (code) {
        0          -> "Clear Sky"
        1          -> "Mainly Clear"
        2          -> "Partly Cloudy"
        3          -> "Overcast"
        45, 48     -> "Foggy"
        51, 53     -> "Drizzle"
        55         -> "Heavy Drizzle"
        61         -> "Light Rain"
        63         -> "Rain"
        65         -> "Heavy Rain"
        71         -> "Light Snow"
        73         -> "Snow"
        75         -> "Heavy Snow"
        77         -> "Snow Grains"
        80         -> "Light Showers"
        81         -> "Showers"
        82         -> "Heavy Showers"
        85, 86     -> "Snow Showers"
        95         -> "Thunderstorm"
        96, 99     -> "Thunderstorm + Hail"
        else       -> "Unknown"
    }

    private suspend fun fetchFeed(): FeedState {
        return try {
            withTimeout(15_000L) {
                val conn = URL("https://www.nasa.gov/rss/dyn/breaking_news.rss").openConnection()
                conn.connectTimeout = 10_000
                conn.readTimeout = 10_000
                val factory = XmlPullParserFactory.newInstance()
                val xpp = factory.newPullParser()
                xpp.setInput(InputStreamReader(conn.getInputStream()))

                val items = mutableListOf<FeedItem>()
                var inItem = false
                var title = ""; var link = ""; var pubDate = ""
                var currentTag = ""

                var event = xpp.eventType
                while (event != XmlPullParser.END_DOCUMENT) {
                    when (event) {
                        XmlPullParser.START_TAG -> {
                            currentTag = xpp.name
                            if (currentTag == "item") {
                                inItem = true; title = ""; link = ""; pubDate = ""
                            }
                        }
                        XmlPullParser.TEXT -> if (inItem) when (currentTag) {
                            "title" -> title += xpp.text
                            "link" -> link += xpp.text
                            "pubDate" -> pubDate += xpp.text
                        }
                        XmlPullParser.END_TAG -> {
                            if (xpp.name == "item" && inItem && title.isNotBlank()) {
                                items += FeedItem(title.trim(), link.trim(), pubDate.trim())
                                inItem = false
                            }
                            currentTag = ""
                        }
                    }
                    event = xpp.next()
                }
                if (items.isEmpty()) FeedState.Error("No articles found") else FeedState.Success(items)
            }
        } catch (_: TimeoutCancellationException) {
            FeedState.Error("Request timed out")
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            FeedState.Error(e.message ?: "Feed unavailable")
        }
    }
}
