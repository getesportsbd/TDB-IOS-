package com.example.ui.screens

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.rounded.AirplanemodeActive
import androidx.compose.material.icons.rounded.Bluetooth
import androidx.compose.material.icons.rounded.BrightnessHigh
import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.FlashlightOn
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.SignalCellularAlt
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppScreen
import com.example.data.SystemControlHelper
import com.example.ui.LauncherUiState
import com.example.ui.theme.IosBlue
import com.example.ui.theme.IosGreen
import com.example.ui.theme.IosIndigo
import com.example.ui.theme.IosLabelPrimary
import com.example.ui.theme.IosLabelSecondary
import com.example.ui.theme.IosOrange
import com.example.ui.theme.IosYellow

@Composable
fun ControlCenterOverlay(
    uiState: LauncherUiState,
    onBrightnessChange: (Activity?, Float) -> Unit,
    onVolumeChange: (Float) -> Unit,
    onToggleTorch: () -> Unit,
    onToggleFocusTimer: () -> Unit,
    onNavigateScreen: (AppScreen) -> Unit,
    onLockScreen: () -> Unit,
    onClose: () -> Unit
) {
    BackHandler { onClose() }
    val context = LocalContext.current
    val activity = context as? Activity
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xD9E9EDF5))
            .clickable(enabled = true, onClick = {})
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 580.dp)
                .align(Alignment.Center)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Control Center",
                        style = MaterialTheme.typography.headlineMedium,
                        color = IosLabelPrimary
                    )
                    Text(
                        text = "Battery ${uiState.telemetry.batteryPercent}% • ${uiState.telemetry.ringerModeLabel}",
                        style = MaterialTheme.typography.labelMedium,
                        color = IosLabelSecondary
                    )
                }
                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .testTag("btn_close_control_center")
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.8f))
                ) {
                    Icon(Icons.Rounded.Close, contentDescription = "Close Control Center", tint = IosLabelPrimary)
                }
            }

            // Top Row: 2x2 Connectivity Module + Focus/Audio Module
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Connectivity 2x2 Card
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .height(166.dp)
                        .clip(RoundedCornerShape(26.dp))
                        .background(Color.White.copy(alpha = 0.82f))
                        .border(1.dp, Color.White, RoundedCornerShape(26.dp))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        ControlCircleButton(
                            icon = Icons.Rounded.AirplanemodeActive,
                            label = "Airplane",
                            active = false,
                            activeColor = IosOrange,
                            testTag = "cc_airplane",
                            onClick = { SystemControlHelper.openAirplaneSettings(context) }
                        )
                        ControlCircleButton(
                            icon = Icons.Rounded.SignalCellularAlt,
                            label = "Network",
                            active = true,
                            activeColor = IosGreen,
                            testTag = "cc_network",
                            onClick = { SystemControlHelper.openWifiSettings(context) }
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        ControlCircleButton(
                            icon = Icons.Rounded.Wifi,
                            label = "Wi-Fi",
                            active = uiState.telemetry.wifiConnected,
                            activeColor = IosBlue,
                            testTag = "cc_wifi",
                            onClick = { SystemControlHelper.openWifiSettings(context) }
                        )
                        ControlCircleButton(
                            icon = Icons.Rounded.Bluetooth,
                            label = "Bluetooth",
                            active = uiState.telemetry.bluetoothEnabled,
                            activeColor = IosBlue,
                            testTag = "cc_bluetooth",
                            onClick = { SystemControlHelper.openBluetoothSettings(context) }
                        )
                    }
                }

                // Focus & Audio Module
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .height(166.dp)
                        .clip(RoundedCornerShape(26.dp))
                        .background(Color.White.copy(alpha = 0.82f))
                        .border(1.dp, Color.White, RoundedCornerShape(26.dp))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.MusicNote,
                            contentDescription = "Audio settings",
                            tint = IosIndigo,
                            modifier = Modifier
                                .size(22.dp)
                                .clickable { SystemControlHelper.openSoundSettings(context) }
                        )
                        Text(
                            text = "Sound >",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = IosBlue,
                            modifier = Modifier.clickable { SystemControlHelper.openSoundSettings(context) }
                        )
                    }

                    val mins = uiState.focusSecondsRemaining / 60
                    val secs = uiState.focusSecondsRemaining % 60
                    Column {
                        Text(
                            text = if (uiState.isFocusRunning) "Focus Running" else "Focus Session",
                            fontSize = 12.sp,
                            color = IosLabelSecondary
                        )
                        Text(
                            text = String.format("%02d:%02d", mins, secs),
                            style = MaterialTheme.typography.headlineMedium,
                            color = IosLabelPrimary
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (uiState.isFocusRunning) IosOrange else IosBlue)
                                .clickable { onToggleFocusTimer() }
                                .padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = if (uiState.isFocusRunning) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                contentDescription = "Toggle focus",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (uiState.isFocusRunning) "Pause" else "Start",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Icon(
                            imageVector = Icons.Rounded.Timer,
                            contentDescription = "Alarms",
                            tint = IosLabelSecondary,
                            modifier = Modifier
                                .size(24.dp)
                                .clickable { SystemControlHelper.openClockAlarms(context) }
                        )
                    }
                }
            }

            // Middle Row: Real Vertical Brightness & Volume Sliders + Shortcut Tiles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                VerticalControlPillSlider(
                    value = uiState.telemetry.brightnessFraction,
                    onValueChange = { onBrightnessChange(activity, it) },
                    icon = Icons.Rounded.BrightnessHigh,
                    label = "Brightness",
                    accentColor = IosOrange,
                    testTag = "cc_brightness_slider",
                    onSettingsShortcut = { SystemControlHelper.openDisplaySettings(context) },
                    modifier = Modifier.weight(1f)
                )
                VerticalControlPillSlider(
                    value = uiState.telemetry.volumeFraction,
                    onValueChange = onVolumeChange,
                    icon = Icons.Rounded.VolumeUp,
                    label = "Volume",
                    accentColor = IosBlue,
                    testTag = "cc_volume_slider",
                    onSettingsShortcut = { SystemControlHelper.openSoundSettings(context) },
                    modifier = Modifier.weight(1f)
                )
            }

            // Bottom Action Tiles Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ControlSquareTile(
                    icon = Icons.Rounded.FlashlightOn,
                    label = if (uiState.isTorchOn) "Torch On" else "Flashlight",
                    active = uiState.isTorchOn,
                    activeColor = IosYellow,
                    testTag = "cc_flashlight",
                    onClick = onToggleTorch,
                    modifier = Modifier.weight(1f)
                )
                ControlSquareTile(
                    icon = Icons.Rounded.Calculate,
                    label = "Calculator",
                    active = false,
                    activeColor = IosBlue,
                    testTag = "cc_calculator",
                    onClick = { onNavigateScreen(AppScreen.CALCULATOR) },
                    modifier = Modifier.weight(1f)
                )
                ControlSquareTile(
                    icon = Icons.Rounded.CameraAlt,
                    label = "Camera",
                    active = false,
                    activeColor = IosBlue,
                    testTag = "cc_camera",
                    onClick = { SystemControlHelper.openCamera(context) },
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ControlSquareTile(
                    icon = Icons.Rounded.Schedule,
                    label = "Alarms",
                    active = false,
                    activeColor = IosIndigo,
                    testTag = "cc_alarms",
                    onClick = { SystemControlHelper.openClockAlarms(context) },
                    modifier = Modifier.weight(1f)
                )
                ControlSquareTile(
                    icon = Icons.Rounded.Lock,
                    label = "Lock Screen",
                    active = false,
                    activeColor = IosIndigo,
                    testTag = "cc_lock_screen",
                    onClick = onLockScreen,
                    modifier = Modifier.weight(1f)
                )
                ControlSquareTile(
                    icon = Icons.Rounded.Settings,
                    label = "Settings",
                    active = false,
                    activeColor = IosBlue,
                    testTag = "cc_settings",
                    onClick = { onNavigateScreen(AppScreen.SETTINGS) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ControlCircleButton(
    icon: ImageVector,
    label: String,
    active: Boolean,
    activeColor: Color,
    testTag: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .testTag(testTag)
                .size(50.dp)
                .clip(CircleShape)
                .background(if (active) activeColor else Color(0xFFE5E5EA))
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (active) Color.White else IosLabelPrimary,
                modifier = Modifier.size(23.dp)
            )
        }
        Text(
            text = label,
            fontSize = 10.5.sp,
            color = IosLabelSecondary,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun VerticalControlPillSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    icon: ImageVector,
    label: String,
    accentColor: Color,
    testTag: String,
    onSettingsShortcut: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .testTag(testTag)
                .fillMaxWidth()
                .height(172.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(Color.White.copy(alpha = 0.68f))
                .border(1.dp, Color.White, RoundedCornerShape(28.dp))
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val fraction = 1f - (offset.y / size.height.toFloat()).coerceIn(0f, 1f)
                        onValueChange(fraction)
                    }
                }
                .pointerInput(Unit) {
                    detectVerticalDragGestures { change, _ ->
                        change.consume()
                        val fraction = 1f - (change.position.y / size.height.toFloat()).coerceIn(0f, 1f)
                        onValueChange(fraction)
                    }
                },
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(value.coerceIn(0.06f, 1f))
                    .background(Color.White)
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = accentColor,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${(value * 100).toInt()}%",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = IosLabelPrimary
                )
            }
        }

        Text(
            text = "$label Settings >",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = IosBlue,
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .clickable { onSettingsShortcut() }
                .padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun ControlSquareTile(
    icon: ImageVector,
    label: String,
    active: Boolean,
    activeColor: Color,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .testTag(testTag)
            .height(82.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(if (active) activeColor else Color.White.copy(alpha = 0.85f))
            .border(1.dp, Color.White, RoundedCornerShape(22.dp))
            .clickable { onClick() }
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (active) Color.White else IosLabelPrimary,
            modifier = Modifier.size(25.dp)
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = label,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (active) Color.White else IosLabelPrimary
        )
    }
}
