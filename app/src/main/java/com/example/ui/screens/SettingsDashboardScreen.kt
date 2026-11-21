package com.example.ui.screens

import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.Bluetooth
import androidx.compose.material.icons.rounded.Brightness6
import androidx.compose.material.icons.rounded.Contrast
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Tag
import androidx.compose.material.icons.rounded.Vibration
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppScreen
import com.example.data.OverlayType
import com.example.data.SystemControlHelper
import com.example.ui.LauncherUiState
import com.example.ui.theme.IosBlue
import com.example.ui.theme.IosGreen
import com.example.ui.theme.IosGroupedBackground
import com.example.ui.theme.IosIndigo
import com.example.ui.theme.IosLabelPrimary
import com.example.ui.theme.IosLabelSecondary
import com.example.ui.theme.IosOrange
import com.example.ui.theme.IosPink
import com.example.ui.theme.IosPurple
import com.example.ui.theme.IosSeparator
import com.example.ui.theme.IosTeal

@Composable
fun SettingsDashboardScreen(
    uiState: LauncherUiState,
    onBack: () -> Unit,
    onOpenWallpaperStudio: () -> Unit,
    onOpenLockScreen: () -> Unit,
    onOpenAppDrawer: () -> Unit,
    onGridColumnsChange: (Int) -> Unit,
    onShowIconLabelsChange: (Boolean) -> Unit,
    onStartOnLockScreenChange: (Boolean) -> Unit,
    onLightTextChange: (Boolean) -> Unit,
    onHapticChange: (Boolean) -> Unit,
    onRefreshApps: () -> Unit
) {
    BackHandler { onBack() }
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val canWriteSystemSettings = Settings.System.canWrite(context)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(IosGroupedBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 620.dp)
                .align(Alignment.Center)
                .verticalScroll(scrollState)
                .padding(horizontal = 18.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .testTag("settings_back_button")
                        .clip(CircleShape)
                        .background(Color.White)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back to Home",
                        tint = IosBlue
                    )
                }
                Column {
                    Text(
                        text = "TDB Settings",
                        style = MaterialTheme.typography.headlineLarge,
                        color = IosLabelPrimary
                    )
                    Text(
                        text = "Version 2.0 • Native Android Experience",
                        style = MaterialTheme.typography.labelMedium,
                        color = IosLabelSecondary
                    )
                }
            }

            // Device Profile Card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color.White)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(IosBlue, IosTeal))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.PhoneAndroid,
                        contentDescription = "Device model",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = uiState.telemetry.deviceModel,
                        style = MaterialTheme.typography.titleLarge,
                        color = IosLabelPrimary
                    )
                    Text(
                        text = "Android ${uiState.telemetry.androidVersion} (API ${uiState.telemetry.sdkInt})",
                        style = MaterialTheme.typography.bodyMedium,
                        color = IosLabelSecondary
                    )
                    Text(
                        text = "${String.format("%.1f", uiState.telemetry.freeStorageGb)} GB free of ${String.format("%.1f", uiState.telemetry.totalStorageGb)} GB • ${uiState.installedApps.size} apps",
                        style = MaterialTheme.typography.labelMedium,
                        color = IosBlue
                    )
                }
            }

            // Section: Personalization
            SettingsSectionTitle("Personalization & Home Screen")
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
            ) {
                SettingsNavRow(
                    icon = Icons.Rounded.Palette,
                    iconBg = IosPurple,
                    title = "Wallpaper & Style",
                    subtitle = uiState.settings.wallpaperPreset.displayName,
                    testTag = "settings_row_wallpaper",
                    onClick = onOpenWallpaperStudio
                )
                SettingsDivider()
                SettingsNavRow(
                    icon = Icons.Rounded.Lock,
                    iconBg = IosIndigo,
                    title = "Preview Lock Screen",
                    subtitle = "Open iOS-inspired lock interface",
                    testTag = "settings_row_lock_preview",
                    onClick = onOpenLockScreen
                )
                SettingsDivider()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SettingsSquircleBadge(Icons.Rounded.GridView, IosBlue)
                        Column {
                            Text("Home Grid Columns", style = MaterialTheme.typography.titleMedium, color = IosLabelPrimary)
                            Text("Choose 4 or 5 icons per row", style = MaterialTheme.typography.labelMedium, color = IosLabelSecondary)
                        }
                    }
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(IosGroupedBackground)
                            .padding(3.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf(4, 5).forEach { cols ->
                            val selected = uiState.settings.gridColumns == cols
                            Text(
                                text = "$cols",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selected) IosBlue else IosLabelSecondary,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(9.dp))
                                    .background(if (selected) Color.White else Color.Transparent)
                                    .clickable { onGridColumnsChange(cols) }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
                SettingsDivider()
                SettingsSwitchRow(
                    icon = Icons.Rounded.Tag,
                    iconBg = IosTeal,
                    title = "Show App Labels",
                    checked = uiState.settings.showIconLabels,
                    onCheckedChange = onShowIconLabelsChange
                )
                SettingsDivider()
                SettingsSwitchRow(
                    icon = Icons.Rounded.Lock,
                    iconBg = IosOrange,
                    title = "Start on Lock Screen",
                    checked = uiState.settings.startOnLockScreen,
                    onCheckedChange = onStartOnLockScreenChange
                )
                SettingsDivider()
                SettingsSwitchRow(
                    icon = Icons.Rounded.Contrast,
                    iconBg = Color(0xFF48484A),
                    title = "Light Labels Over Wallpaper",
                    checked = uiState.settings.lightTextOverWallpaper,
                    onCheckedChange = onLightTextChange
                )
                SettingsDivider()
                SettingsSwitchRow(
                    icon = Icons.Rounded.Vibration,
                    iconBg = IosPink,
                    title = "Haptic Icon Feedback",
                    checked = uiState.settings.hapticFeedback,
                    onCheckedChange = onHapticChange
                )
            }

            // Section: Android System Shortcuts
            SettingsSectionTitle("Connectivity & System Shortcuts")
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
            ) {
                SettingsNavRow(
                    icon = Icons.Rounded.Wifi,
                    iconBg = IosBlue,
                    title = "Wi-Fi Settings",
                    subtitle = uiState.telemetry.wifiLabel,
                    testTag = "settings_row_wifi",
                    onClick = { SystemControlHelper.openWifiSettings(context) }
                )
                SettingsDivider()
                SettingsNavRow(
                    icon = Icons.Rounded.Bluetooth,
                    iconBg = IosBlue,
                    title = "Bluetooth Settings",
                    subtitle = if (uiState.telemetry.bluetoothEnabled) "Enabled" else "Open system panel",
                    testTag = "settings_row_bluetooth",
                    onClick = { SystemControlHelper.openBluetoothSettings(context) }
                )
                SettingsDivider()
                SettingsNavRow(
                    icon = Icons.Rounded.Brightness6,
                    iconBg = IosOrange,
                    title = "Display & Brightness",
                    subtitle = if (canWriteSystemSettings) "System write enabled" else "Window brightness active • Tap for Display",
                    testTag = "settings_row_display",
                    onClick = { SystemControlHelper.openDisplaySettings(context) }
                )
                if (!canWriteSystemSettings) {
                    SettingsDivider()
                    SettingsNavRow(
                        icon = Icons.Rounded.Brightness6,
                        iconBg = IosGreen,
                        title = "Enable Global Brightness Control",
                        subtitle = "Optional permission for Control Center slider",
                        testTag = "settings_row_write_settings",
                        onClick = { SystemControlHelper.openWriteSettingsPermission(context) }
                    )
                }
                SettingsDivider()
                SettingsNavRow(
                    icon = Icons.Rounded.VolumeUp,
                    iconBg = IosPink,
                    title = "Sound & Haptics",
                    subtitle = "Volume ${(uiState.telemetry.volumeFraction * 100).toInt()}% • ${uiState.telemetry.ringerModeLabel}",
                    testTag = "settings_row_sound",
                    onClick = { SystemControlHelper.openSoundSettings(context) }
                )
                SettingsDivider()
                SettingsNavRow(
                    icon = Icons.Rounded.Home,
                    iconBg = IosIndigo,
                    title = "Default Home Launcher",
                    subtitle = "Set TDB iOS Experience as default Home",
                    testTag = "settings_row_default_home",
                    onClick = { SystemControlHelper.openHomeLauncherSettings(context) }
                )
                SettingsDivider()
                SettingsNavRow(
                    icon = Icons.Rounded.Settings,
                    iconBg = Color(0xFF636366),
                    title = "Android System Settings",
                    subtitle = "Open full device settings",
                    testTag = "settings_row_system_settings",
                    onClick = { SystemControlHelper.openSystemSettings(context) }
                )
            }

            // Section: Apps & Transparency
            SettingsSectionTitle("Installed Applications")
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
            ) {
                SettingsNavRow(
                    icon = Icons.Rounded.Apps,
                    iconBg = IosGreen,
                    title = "App Drawer (${uiState.installedApps.size} apps)",
                    subtitle = "Browse, search & launch installed Android apps",
                    testTag = "settings_row_app_drawer",
                    onClick = onOpenAppDrawer
                )
                SettingsDivider()
                SettingsNavRow(
                    icon = Icons.Rounded.Info,
                    iconBg = IosTeal,
                    title = "Refresh App Catalog",
                    subtitle = "Re-scan PackageManager for newly installed apps",
                    testTag = "settings_row_refresh_apps",
                    onClick = onRefreshApps
                )
            }

            // Platform Honesty Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "About TDB iOS Experience on Android",
                    style = MaterialTheme.typography.titleMedium,
                    color = IosLabelPrimary
                )
                Text(
                    text = "This is a native Android application recreating an iPhone-inspired interface using official Android APIs. Hardware toggles like Wi-Fi, Bluetooth, and system-wide Lock Screen security are managed by Android OS and open native Android panels where required.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = IosLabelSecondary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SettingsSectionTitle(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
        color = IosLabelSecondary,
        modifier = Modifier.padding(start = 6.dp)
    )
}

@Composable
private fun SettingsSquircleBadge(icon: ImageVector, bg: Color) {
    Box(
        modifier = Modifier
            .size(34.dp)
            .clip(RoundedCornerShape(9.dp))
            .background(bg),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun SettingsNavRow(
    icon: ImageVector,
    iconBg: Color,
    title: String,
    subtitle: String,
    testTag: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SettingsSquircleBadge(icon, iconBg)
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = IosLabelPrimary)
            Text(subtitle, style = MaterialTheme.typography.labelMedium, color = IosLabelSecondary)
        }
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
            contentDescription = null,
            tint = IosLabelSecondary
        )
    }
}

@Composable
private fun SettingsSwitchRow(
    icon: ImageVector,
    iconBg: Color,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SettingsSquircleBadge(icon, iconBg)
            Text(title, style = MaterialTheme.typography.titleMedium, color = IosLabelPrimary)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = IosGreen
            )
        )
    }
}

@Composable
private fun SettingsDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(start = 60.dp),
        thickness = 0.6.dp,
        color = IosSeparator.copy(alpha = 0.65f)
    )
}
