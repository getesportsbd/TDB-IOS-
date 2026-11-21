package com.example.ui

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppCategory
import com.example.data.AppScreen
import com.example.data.BuiltInAppType
import com.example.data.DeviceTelemetry
import com.example.data.InstalledApp
import com.example.data.LauncherSettings
import com.example.data.NotificationCardItem
import com.example.data.OverlayType
import com.example.data.PreferencesRepository
import com.example.data.ReminderItem
import com.example.data.SystemControlHelper
import com.example.data.WallpaperPreset
import com.example.ui.theme.IosBlue
import com.example.ui.theme.IosGreen
import com.example.ui.theme.IosIndigo
import com.example.ui.theme.IosOrange
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LauncherUiState(
    val installedApps: List<InstalledApp> = emptyList(),
    val isLoadingApps: Boolean = true,
    val settings: LauncherSettings = LauncherSettings(),
    val reminders: List<ReminderItem> = emptyList(),
    val quickNoteText: String = "",
    val telemetry: DeviceTelemetry = DeviceTelemetry(),
    val isLocked: Boolean = false,
    val activeOverlay: OverlayType = OverlayType.NONE,
    val activeScreen: AppScreen = AppScreen.HOME,
    val expandedLibraryCategory: AppCategory? = null,
    val isIslandExpanded: Boolean = false,
    val isTorchOn: Boolean = false,
    val focusPresetMinutes: Int = 25,
    val focusSecondsRemaining: Int = 25 * 60,
    val isFocusRunning: Boolean = false,
    val searchQuery: String = "",
    val notifications: List<NotificationCardItem> = emptyList(),
    val statusMessage: String? = null
)

class LauncherViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = PreferencesRepository(application.applicationContext)
    private val _uiState = MutableStateFlow(LauncherUiState())
    val uiState: StateFlow<LauncherUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var currentWindowBrightness: Float? = null

    init {
        observePreferences()
        refreshInstalledApps()
        refreshTelemetry()
        seedNotifications()
    }

    private fun observePreferences() {
        viewModelScope.launch {
            val initial = repo.settingsFlow.first()
            _uiState.update {
                it.copy(
                    settings = initial,
                    isLocked = initial.startOnLockScreen
                )
            }
            repo.settingsFlow.collect { latest ->
                _uiState.update { it.copy(settings = latest) }
            }
        }
        viewModelScope.launch {
            repo.remindersFlow.collect { list ->
                _uiState.update { it.copy(reminders = list) }
            }
        }
        viewModelScope.launch {
            repo.quickNoteFlow.collect { note ->
                _uiState.update { it.copy(quickNoteText = note) }
            }
        }
    }

    private fun seedNotifications() {
        val ctx = getApplication<Application>()
        val t = SystemControlHelper.readDeviceTelemetry(ctx, currentWindowBrightness)
        _uiState.update {
            it.copy(
                notifications = listOf(
                    NotificationCardItem(
                        id = "n_welcome",
                        appName = "TDB Experience",
                        title = "Welcome to TDB iOS Experience 2.0",
                        body = "Swipe horizontally for App Library, tap Dynamic Island for live controls, or pull down Control Center.",
                        timeLabel = "Now",
                        accentColor = IosBlue
                    ),
                    NotificationCardItem(
                        id = "n_system",
                        appName = "Device Health",
                        title = "Battery ${t.batteryPercent}% • ${t.wifiLabel}",
                        body = "${String.format("%.1f", t.freeStorageGb)} GB free on ${t.deviceModel} (Android ${t.androidVersion}).",
                        timeLabel = "2m ago",
                        accentColor = IosGreen
                    ),
                    NotificationCardItem(
                        id = "n_tips",
                        appName = "Wallpaper Studio",
                        title = "4 Original Light Wallpapers Ready",
                        body = "Open Wallpaper or Settings to switch themes or pick your own photo.",
                        timeLabel = "15m ago",
                        accentColor = IosIndigo
                    )
                )
            )
        }
    }

    fun refreshInstalledApps() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingApps = true) }
            val apps = SystemControlHelper.loadInstalledApps(getApplication())
            _uiState.update {
                it.copy(
                    installedApps = apps,
                    isLoadingApps = false
                )
            }
        }
    }

    fun refreshTelemetry() {
        val telemetry = SystemControlHelper.readDeviceTelemetry(getApplication(), currentWindowBrightness)
        _uiState.update { it.copy(telemetry = telemetry) }
    }

    fun openOverlay(overlay: OverlayType) {
        refreshTelemetry()
        _uiState.update {
            it.copy(
                activeOverlay = overlay,
                isIslandExpanded = false,
                searchQuery = if (overlay == OverlayType.SPOTLIGHT_SEARCH || overlay == OverlayType.APP_DRAWER) "" else it.searchQuery
            )
        }
    }

    fun closeOverlay() {
        _uiState.update { it.copy(activeOverlay = OverlayType.NONE, searchQuery = "") }
    }

    fun navigateToScreen(screen: AppScreen) {
        refreshTelemetry()
        _uiState.update {
            it.copy(
                activeScreen = screen,
                activeOverlay = OverlayType.NONE,
                isIslandExpanded = false
            )
        }
    }

    fun navigateHome() {
        _uiState.update {
            it.copy(
                activeScreen = AppScreen.HOME,
                activeOverlay = OverlayType.NONE,
                expandedLibraryCategory = null
            )
        }
    }

    fun lockScreen() {
        refreshTelemetry()
        _uiState.update {
            it.copy(
                isLocked = true,
                activeOverlay = OverlayType.NONE,
                activeScreen = AppScreen.HOME,
                isIslandExpanded = false
            )
        }
    }

    fun unlockScreen() {
        _uiState.update { it.copy(isLocked = false) }
    }

    fun toggleDynamicIsland() {
        refreshTelemetry()
        _uiState.update { it.copy(isIslandExpanded = !it.isIslandExpanded) }
    }

    fun collapseDynamicIsland() {
        _uiState.update { it.copy(isIslandExpanded = false) }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun openLibraryCategory(category: AppCategory?) {
        _uiState.update { it.copy(expandedLibraryCategory = category) }
    }

    fun launchBuiltInApp(type: BuiltInAppType, onRequestAppLibraryPage: () -> Unit = {}) {
        val ctx = getApplication<Application>()
        when (type) {
            BuiltInAppType.PHONE -> {
                if (!SystemControlHelper.openDialer(ctx)) showMessage("Phone dialer not available.")
            }
            BuiltInAppType.MESSAGES -> {
                if (!SystemControlHelper.openMessages(ctx)) showMessage("Messaging app not available.")
            }
            BuiltInAppType.SAFARI -> {
                if (!SystemControlHelper.openBrowser(ctx)) showMessage("Web browser not available.")
            }
            BuiltInAppType.CAMERA -> {
                if (!SystemControlHelper.openCamera(ctx)) showMessage("Camera app not available.")
            }
            BuiltInAppType.PHOTOS -> {
                if (!SystemControlHelper.openGallery(ctx)) showMessage("Gallery app not available.")
            }
            BuiltInAppType.CLOCK -> {
                if (!SystemControlHelper.openClockAlarms(ctx)) showMessage("Clock app not available.")
            }
            BuiltInAppType.CALENDAR -> {
                if (!SystemControlHelper.openCalendar(ctx)) showMessage("Calendar app not available.")
            }
            BuiltInAppType.WEATHER -> navigateToScreen(AppScreen.WEATHER)
            BuiltInAppType.NOTES -> navigateToScreen(AppScreen.NOTES)
            BuiltInAppType.CALCULATOR -> navigateToScreen(AppScreen.CALCULATOR)
            BuiltInAppType.SETTINGS -> navigateToScreen(AppScreen.SETTINGS)
            BuiltInAppType.WALLPAPER -> navigateToScreen(AppScreen.WALLPAPER_STUDIO)
            BuiltInAppType.LOCK_SCREEN -> lockScreen()
            BuiltInAppType.APP_DRAWER -> openOverlay(OverlayType.APP_DRAWER)
            BuiltInAppType.APP_LIBRARY -> {
                _uiState.update { it.copy(activeOverlay = OverlayType.NONE, activeScreen = AppScreen.HOME) }
                onRequestAppLibraryPage()
            }
        }
    }

    fun launchInstalledApp(packageName: String) {
        val ok = SystemControlHelper.launchInstalledApp(getApplication(), packageName)
        if (!ok) {
            showMessage("Unable to launch this application.")
        } else {
            closeOverlay()
        }
    }

    fun openInstalledAppInfo(packageName: String) {
        SystemControlHelper.openAppInfo(getApplication(), packageName)
    }

    fun toggleTorch() {
        val next = !_uiState.value.isTorchOn
        val success = SystemControlHelper.setTorchEnabled(getApplication(), next)
        if (success) {
            _uiState.update { it.copy(isTorchOn = next) }
        } else {
            _uiState.update { it.copy(isTorchOn = false) }
            showMessage("Flashlight hardware unavailable on this device.")
        }
    }

    fun updateBrightness(activity: Activity?, fraction: Float) {
        val clamped = fraction.coerceIn(0.05f, 1f)
        currentWindowBrightness = clamped
        SystemControlHelper.applyBrightness(activity, getApplication(), clamped)
        _uiState.update { it.copy(telemetry = it.telemetry.copy(brightnessFraction = clamped)) }
    }

    fun updateVolume(fraction: Float) {
        val clamped = fraction.coerceIn(0f, 1f)
        SystemControlHelper.setMusicVolume(getApplication(), clamped)
        _uiState.update { it.copy(telemetry = it.telemetry.copy(volumeFraction = clamped)) }
    }

    fun toggleFocusTimer() {
        val running = _uiState.value.isFocusRunning
        if (running) {
            timerJob?.cancel()
            _uiState.update { it.copy(isFocusRunning = false) }
        } else {
            if (_uiState.value.focusSecondsRemaining <= 0) {
                val reset = _uiState.value.focusPresetMinutes * 60
                _uiState.update { it.copy(focusSecondsRemaining = reset) }
            }
            _uiState.update { it.copy(isFocusRunning = true) }
            timerJob?.cancel()
            timerJob = viewModelScope.launch {
                while (_uiState.value.isFocusRunning && _uiState.value.focusSecondsRemaining > 0) {
                    delay(1000L)
                    _uiState.update { state ->
                        val next = (state.focusSecondsRemaining - 1).coerceAtLeast(0)
                        if (next == 0) {
                            state.copy(
                                focusSecondsRemaining = 0,
                                isFocusRunning = false,
                                notifications = listOf(
                                    NotificationCardItem(
                                        id = "n_focus_${System.currentTimeMillis()}",
                                        appName = "Focus Timer",
                                        title = "Focus Session Complete",
                                        body = "${state.focusPresetMinutes}-minute focus timer finished.",
                                        timeLabel = "Just now",
                                        accentColor = IosOrange
                                    )
                                ) + state.notifications
                            )
                        } else {
                            state.copy(focusSecondsRemaining = next)
                        }
                    }
                }
            }
        }
    }

    fun setFocusPreset(minutes: Int) {
        timerJob?.cancel()
        _uiState.update {
            it.copy(
                focusPresetMinutes = minutes,
                focusSecondsRemaining = minutes * 60,
                isFocusRunning = false
            )
        }
    }

    fun toggleReminder(id: String) {
        val updated = _uiState.value.reminders.map {
            if (it.id == id) it.copy(isDone = !it.isDone) else it
        }
        viewModelScope.launch { repo.saveReminders(updated) }
    }

    fun addReminder(title: String) {
        val clean = title.trim()
        if (clean.isEmpty()) return
        val updated = listOf(ReminderItem("r_${System.currentTimeMillis()}", clean, false)) +
            _uiState.value.reminders
        viewModelScope.launch { repo.saveReminders(updated) }
    }

    fun deleteReminder(id: String) {
        val updated = _uiState.value.reminders.filterNot { it.id == id }
        viewModelScope.launch { repo.saveReminders(updated) }
    }

    fun saveQuickNote(text: String) {
        _uiState.update { it.copy(quickNoteText = text) }
        viewModelScope.launch { repo.saveQuickNote(text) }
    }

    fun dismissNotification(id: String) {
        _uiState.update { state ->
            state.copy(notifications = state.notifications.filterNot { it.id == id })
        }
    }

    fun clearAllNotifications() {
        _uiState.update { it.copy(notifications = emptyList()) }
    }

    fun addSampleReminderNotification() {
        _uiState.update { state ->
            val card = NotificationCardItem(
                id = "n_custom_${System.currentTimeMillis()}",
                appName = "TDB Reminders",
                title = "Snapshot Saved",
                body = "Active reminders: ${state.reminders.count { !it.isDone }} pending.",
                timeLabel = "Just now",
                accentColor = IosBlue
            )
            state.copy(notifications = listOf(card) + state.notifications)
        }
    }

    fun selectWallpaperPreset(preset: WallpaperPreset) {
        viewModelScope.launch { repo.selectWallpaperPreset(preset) }
    }

    fun setCustomWallpaperUri(uriString: String) {
        viewModelScope.launch { repo.setCustomWallpaperUri(uriString) }
    }

    fun setGridColumns(columns: Int) {
        viewModelScope.launch { repo.setGridColumns(columns) }
    }

    fun setShowIconLabels(show: Boolean) {
        viewModelScope.launch { repo.setShowIconLabels(show) }
    }

    fun setStartOnLockScreen(enabled: Boolean) {
        viewModelScope.launch { repo.setStartOnLockScreen(enabled) }
    }

    fun setLightTextOverWallpaper(enabled: Boolean) {
        viewModelScope.launch { repo.setLightTextOverWallpaper(enabled) }
    }

    fun setHapticFeedback(enabled: Boolean) {
        viewModelScope.launch { repo.setHapticFeedback(enabled) }
    }

    fun showMessage(message: String) {
        _uiState.update { it.copy(statusMessage = message) }
    }

    fun clearStatusMessage() {
        _uiState.update { it.copy(statusMessage = null) }
    }
}
