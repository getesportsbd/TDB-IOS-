package com.example.data

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import com.example.R
import com.example.ui.theme.IosBlue
import com.example.ui.theme.IosCyan
import com.example.ui.theme.IosGreen
import com.example.ui.theme.IosIndigo
import com.example.ui.theme.IosOrange
import com.example.ui.theme.IosPink
import com.example.ui.theme.IosPurple
import com.example.ui.theme.IosTeal

enum class AppCategory(
    val title: String,
    val accent: Color
) {
    PRODUCTIVITY("Productivity & Work", IosBlue),
    SOCIAL("Social & Messaging", IosGreen),
    MEDIA("Media & Entertainment", IosPink),
    CREATIVITY("Photos & Creativity", IosOrange),
    UTILITIES("Utilities & Tools", IosIndigo),
    SYSTEM("System & Settings", IosTeal),
    OTHER("Other Apps", IosPurple)
}

data class InstalledApp(
    val packageName: String,
    val label: String,
    val category: AppCategory,
    val icon: ImageBitmap?,
    val isSystemApp: Boolean
)

enum class BuiltInAppType {
    PHONE,
    MESSAGES,
    SAFARI,
    CAMERA,
    PHOTOS,
    CLOCK,
    CALENDAR,
    WEATHER,
    NOTES,
    CALCULATOR,
    APP_DRAWER,
    APP_LIBRARY,
    SETTINGS,
    WALLPAPER,
    LOCK_SCREEN
}

data class BuiltInAppItem(
    val type: BuiltInAppType,
    val title: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val badgeCount: Int = 0
)

enum class WallpaperPreset(
    val id: String,
    val displayName: String,
    val subtitle: String,
    @DrawableRes val drawableRes: Int?,
    val preferDarkTextOnHome: Boolean
) {
    AURORA("aurora", "Aurora Mist", "Sky & Pearl Ribbons", R.drawable.img_wallpaper_aurora, true),
    OCEANIC("oceanic", "Oceanic Glass", "Cerulean & Mint Wave", R.drawable.img_wallpaper_oceanic, true),
    BLOSSOM("blossom", "Rose Quartz", "Warm Coral & Pearl", R.drawable.img_wallpaper_blossom, true),
    TITANIUM("titanium", "Titanium Studio", "Silver & Champagne Arc", R.drawable.img_wallpaper_titanium, true),
    CUSTOM("custom", "Custom Photo", "Selected from Photo Picker", null, false)
}

data class ReminderItem(
    val id: String,
    val title: String,
    val isDone: Boolean
)

data class NotificationCardItem(
    val id: String,
    val appName: String,
    val title: String,
    val body: String,
    val timeLabel: String,
    val accentColor: Color
)

data class DeviceTelemetry(
    val batteryPercent: Int = 85,
    val isCharging: Boolean = false,
    val wifiConnected: Boolean = true,
    val wifiLabel: String = "Wi-Fi",
    val bluetoothEnabled: Boolean = false,
    val ringerModeLabel: String = "Normal",
    val brightnessFraction: Float = 0.72f,
    val volumeFraction: Float = 0.6f,
    val maxVolume: Int = 15,
    val deviceModel: String = "Android Device",
    val androidVersion: String = "14",
    val sdkInt: Int = 34,
    val freeStorageGb: Double = 32.0,
    val totalStorageGb: Double = 64.0
)

data class LauncherSettings(
    val wallpaperPreset: WallpaperPreset = WallpaperPreset.AURORA,
    val customWallpaperUri: String? = null,
    val gridColumns: Int = 4,
    val showIconLabels: Boolean = true,
    val startOnLockScreen: Boolean = false,
    val lightTextOverWallpaper: Boolean = false,
    val hapticFeedback: Boolean = true
)

enum class OverlayType {
    NONE,
    CONTROL_CENTER,
    NOTIFICATION_CENTER,
    APP_DRAWER,
    SPOTLIGHT_SEARCH
}

enum class AppScreen {
    HOME,
    SETTINGS,
    WALLPAPER_STUDIO,
    CALCULATOR,
    NOTES,
    WEATHER
}

object BuiltInCatalog {
    val homeShortcuts: List<BuiltInAppItem> = listOf(
        BuiltInAppItem(BuiltInAppType.CALENDAR, "Calendar", Color(0xFFFFFFFF), Color(0xFFF2F2F7)),
        BuiltInAppItem(BuiltInAppType.PHOTOS, "Photos", Color(0xFFFFFFFF), Color(0xFFF7F7FA)),
        BuiltInAppItem(BuiltInAppType.CAMERA, "Camera", Color(0xFF48484A), Color(0xFF2C2C2E)),
        BuiltInAppItem(BuiltInAppType.WEATHER, "Weather", IosCyan, IosBlue),
        BuiltInAppItem(BuiltInAppType.CLOCK, "Clock", Color(0xFF1C1C1E), Color(0xFF2C2C2E)),
        BuiltInAppItem(BuiltInAppType.NOTES, "Notes", Color(0xFFFFD60A), Color(0xFFFF9F0A)),
        BuiltInAppItem(BuiltInAppType.CALCULATOR, "Calculator", Color(0xFF2C2C2E), Color(0xFF1C1C1E)),
        BuiltInAppItem(BuiltInAppType.WALLPAPER, "Wallpaper", IosPurple, IosBlue),
        BuiltInAppItem(BuiltInAppType.APP_LIBRARY, "App Library", IosIndigo, IosCyan),
        BuiltInAppItem(BuiltInAppType.LOCK_SCREEN, "Lock Screen", Color(0xFF3A3A3C), Color(0xFF1C1C1E)),
        BuiltInAppItem(BuiltInAppType.SETTINGS, "Settings", Color(0xFF8E8E93), Color(0xFF636366)),
        BuiltInAppItem(BuiltInAppType.APP_DRAWER, "All Apps", IosBlue, IosTeal)
    )

    val dockShortcuts: List<BuiltInAppItem> = listOf(
        BuiltInAppItem(BuiltInAppType.PHONE, "Phone", IosGreen, Color(0xFF248A3D)),
        BuiltInAppItem(BuiltInAppType.SAFARI, "Safari", Color(0xFFFFFFFF), Color(0xFFE5F1FF)),
        BuiltInAppItem(BuiltInAppType.MESSAGES, "Messages", IosGreen, Color(0xFF30D158)),
        BuiltInAppItem(BuiltInAppType.APP_DRAWER, "Apps", IosBlue, IosIndigo)
    )
}
