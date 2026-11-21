package com.example

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.AppScreen
import com.example.data.OverlayType
import com.example.ui.LauncherViewModel
import com.example.ui.components.WallpaperBackground
import com.example.ui.screens.AppDrawerOverlay
import com.example.ui.screens.AtmosphereWeatherScreen
import com.example.ui.screens.CalculatorScreen
import com.example.ui.screens.ControlCenterOverlay
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LockScreenOverlay
import com.example.ui.screens.NotesAndRemindersScreen
import com.example.ui.screens.NotificationCenterOverlay
import com.example.ui.screens.SettingsDashboardScreen
import com.example.ui.screens.SpotlightSearchOverlay
import com.example.ui.screens.WallpaperStudioScreen
import com.example.ui.theme.TdbIosTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TdbIosTheme {
                TdbExperienceRoot()
            }
        }
    }
}

@Composable
fun TdbExperienceRoot(
    viewModel: LauncherViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Listen for live app install / uninstall events
    DisposableEffect(context) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                viewModel.refreshInstalledApps()
            }
        }
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addAction(Intent.ACTION_PACKAGE_CHANGED)
            addDataScheme("package")
        }
        context.registerReceiver(receiver, filter)
        onDispose {
            runCatching { context.unregisterReceiver(receiver) }
        }
    }

    LaunchedEffect(uiState.statusMessage) {
        if (uiState.statusMessage != null) {
            delay(2600L)
            viewModel.clearStatusMessage()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Active Screen Layer
        AnimatedContent(
            targetState = uiState.activeScreen,
            transitionSpec = {
                (fadeIn(spring(stiffness = Spring.StiffnessMedium)) togetherWith
                    fadeOut(spring(stiffness = Spring.StiffnessMedium)))
            },
            label = "active_screen_transition"
        ) { screen ->
            when (screen) {
                AppScreen.HOME -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        WallpaperBackground(settings = uiState.settings, scrimAlpha = 0.06f)
                        HomeScreen(
                            uiState = uiState,
                            onToggleIsland = viewModel::toggleDynamicIsland,
                            onToggleFocusTimer = viewModel::toggleFocusTimer,
                            onToggleTorch = viewModel::toggleTorch,
                            onOpenOverlay = viewModel::openOverlay,
                            onNavigateScreen = viewModel::navigateToScreen,
                            onLaunchBuiltIn = viewModel::launchBuiltInApp,
                            onLaunchInstalled = viewModel::launchInstalledApp,
                            onToggleReminder = viewModel::toggleReminder,
                            onLockScreen = viewModel::lockScreen,
                            onOpenLibraryCategory = viewModel::openLibraryCategory,
                            onUpdateSearchQuery = viewModel::updateSearchQuery
                        )
                    }
                }
                AppScreen.SETTINGS -> {
                    SettingsDashboardScreen(
                        uiState = uiState,
                        onBack = viewModel::navigateHome,
                        onOpenWallpaperStudio = { viewModel.navigateToScreen(AppScreen.WALLPAPER_STUDIO) },
                        onOpenLockScreen = viewModel::lockScreen,
                        onOpenAppDrawer = {
                            viewModel.navigateHome()
                            viewModel.openOverlay(OverlayType.APP_DRAWER)
                        },
                        onGridColumnsChange = viewModel::setGridColumns,
                        onShowIconLabelsChange = viewModel::setShowIconLabels,
                        onStartOnLockScreenChange = viewModel::setStartOnLockScreen,
                        onLightTextChange = viewModel::setLightTextOverWallpaper,
                        onHapticChange = viewModel::setHapticFeedback,
                        onRefreshApps = {
                            viewModel.refreshInstalledApps()
                            viewModel.showMessage("Installed app list refreshed.")
                        }
                    )
                }
                AppScreen.WALLPAPER_STUDIO -> {
                    WallpaperStudioScreen(
                        uiState = uiState,
                        onSelectPreset = viewModel::selectWallpaperPreset,
                        onCustomPhotoSelected = viewModel::setCustomWallpaperUri,
                        onLightTextToggle = viewModel::setLightTextOverWallpaper,
                        onBack = viewModel::navigateHome
                    )
                }
                AppScreen.CALCULATOR -> {
                    CalculatorScreen(onBack = viewModel::navigateHome)
                }
                AppScreen.NOTES -> {
                    NotesAndRemindersScreen(
                        uiState = uiState,
                        onSaveNote = viewModel::saveQuickNote,
                        onAddReminder = viewModel::addReminder,
                        onToggleReminder = viewModel::toggleReminder,
                        onDeleteReminder = viewModel::deleteReminder,
                        onBack = viewModel::navigateHome
                    )
                }
                AppScreen.WEATHER -> {
                    AtmosphereWeatherScreen(
                        uiState = uiState,
                        onSetFocusPreset = viewModel::setFocusPreset,
                        onToggleFocusTimer = viewModel::toggleFocusTimer,
                        onBack = viewModel::navigateHome
                    )
                }
            }
        }

        // Control Center Overlay
        AnimatedVisibility(
            visible = uiState.activeOverlay == OverlayType.CONTROL_CENTER && !uiState.isLocked,
            enter = fadeIn() + slideInVertically { -it / 3 },
            exit = fadeOut() + slideOutVertically { -it / 3 }
        ) {
            ControlCenterOverlay(
                uiState = uiState,
                onBrightnessChange = viewModel::updateBrightness,
                onVolumeChange = viewModel::updateVolume,
                onToggleTorch = viewModel::toggleTorch,
                onToggleFocusTimer = viewModel::toggleFocusTimer,
                onNavigateScreen = viewModel::navigateToScreen,
                onLockScreen = viewModel::lockScreen,
                onClose = viewModel::closeOverlay
            )
        }

        // Notification Center Overlay
        AnimatedVisibility(
            visible = uiState.activeOverlay == OverlayType.NOTIFICATION_CENTER && !uiState.isLocked,
            enter = fadeIn() + slideInVertically { -it / 3 },
            exit = fadeOut() + slideOutVertically { -it / 3 }
        ) {
            NotificationCenterOverlay(
                uiState = uiState,
                onDismissNotification = viewModel::dismissNotification,
                onClearAll = viewModel::clearAllNotifications,
                onAddSampleNotification = viewModel::addSampleReminderNotification,
                onClose = viewModel::closeOverlay
            )
        }

        // App Drawer Overlay
        AnimatedVisibility(
            visible = uiState.activeOverlay == OverlayType.APP_DRAWER && !uiState.isLocked,
            enter = fadeIn() + slideInVertically { it / 2 },
            exit = fadeOut() + slideOutVertically { it / 2 }
        ) {
            AppDrawerOverlay(
                uiState = uiState,
                onUpdateQuery = viewModel::updateSearchQuery,
                onRefreshApps = viewModel::refreshInstalledApps,
                onLaunchInstalled = viewModel::launchInstalledApp,
                onOpenAppInfo = viewModel::openInstalledAppInfo,
                onClose = viewModel::closeOverlay
            )
        }

        // Spotlight Search Overlay
        AnimatedVisibility(
            visible = uiState.activeOverlay == OverlayType.SPOTLIGHT_SEARCH && !uiState.isLocked,
            enter = fadeIn() + slideInVertically { -it / 4 },
            exit = fadeOut() + slideOutVertically { -it / 4 }
        ) {
            SpotlightSearchOverlay(
                uiState = uiState,
                onUpdateQuery = viewModel::updateSearchQuery,
                onLaunchInstalled = viewModel::launchInstalledApp,
                onLaunchBuiltIn = { type -> viewModel.launchBuiltInApp(type) },
                onClose = viewModel::closeOverlay
            )
        }

        // Lock Screen Overlay
        AnimatedVisibility(
            visible = uiState.isLocked,
            enter = fadeIn(spring(stiffness = Spring.StiffnessMediumLow)),
            exit = fadeOut() + slideOutVertically { -it }
        ) {
            LockScreenOverlay(
                uiState = uiState,
                onUnlock = viewModel::unlockScreen,
                onToggleTorch = viewModel::toggleTorch,
                onDismissNotification = viewModel::dismissNotification
            )
        }

        // Status Feedback Pill
        AnimatedVisibility(
            visible = uiState.statusMessage != null,
            enter = fadeIn() + slideInVertically { -it },
            exit = fadeOut() + slideOutVertically { -it },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 56.dp, start = 24.dp, end = 24.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color(0xE61C1C1E))
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(
                    text = uiState.statusMessage.orEmpty(),
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
