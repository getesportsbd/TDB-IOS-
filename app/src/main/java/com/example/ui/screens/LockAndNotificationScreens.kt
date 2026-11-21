package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddAlert
import androidx.compose.material.icons.rounded.BatteryChargingFull
import androidx.compose.material.icons.rounded.BatteryFull
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.FlashlightOn
import androidx.compose.material.icons.rounded.LockOpen
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.NotificationCardItem
import com.example.data.SystemControlHelper
import com.example.ui.LauncherUiState
import com.example.ui.components.WallpaperBackground
import com.example.ui.theme.IosBlue
import com.example.ui.theme.IosGreen
import com.example.ui.theme.IosLabelPrimary
import com.example.ui.theme.IosLabelSecondary
import com.example.ui.theme.IosYellow
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LockScreenOverlay(
    uiState: LauncherUiState,
    onUnlock: () -> Unit,
    onToggleTorch: () -> Unit,
    onDismissNotification: (String) -> Unit
) {
    val context = LocalContext.current
    var clockText by remember { mutableStateOf(SimpleDateFormat("h:mm", Locale.getDefault()).format(Date())) }
    var dateText by remember { mutableStateOf(SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date())) }

    LaunchedEffect(Unit) {
        while (true) {
            val now = Date()
            clockText = SimpleDateFormat("h:mm", Locale.getDefault()).format(now)
            dateText = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(now)
            delay(15_000L)
        }
    }

    val textColor = if (uiState.settings.lightTextOverWallpaper) Color.White else IosLabelPrimary

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("lock_screen_overlay")
            .pointerInput(Unit) {
                var dragY = 0f
                detectVerticalDragGestures(
                    onDragStart = { dragY = 0f },
                    onVerticalDrag = { _, dragAmount -> dragY += dragAmount },
                    onDragEnd = {
                        if (dragY < -90f) onUnlock()
                    }
                )
            }
    ) {
        WallpaperBackground(settings = uiState.settings, scrimAlpha = 0.14f)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .widthIn(max = 580.dp)
                .align(Alignment.Center)
                .padding(horizontal = 20.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = dateText,
                style = MaterialTheme.typography.titleLarge,
                color = textColor.copy(alpha = 0.9f)
            )
            Text(
                text = clockText,
                style = MaterialTheme.typography.displayLarge.copy(fontSize = 76.sp, lineHeight = 80.sp),
                color = textColor
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Lock Screen Widgets Strip
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LockWidgetChip(
                    icon = if (uiState.telemetry.isCharging) Icons.Rounded.BatteryChargingFull else Icons.Rounded.BatteryFull,
                    text = "${uiState.telemetry.batteryPercent}%",
                    tint = IosGreen
                )
                LockWidgetChip(
                    icon = Icons.Rounded.Wifi,
                    text = uiState.telemetry.wifiLabel,
                    tint = IosBlue
                )
                LockWidgetChip(
                    icon = Icons.Rounded.Notifications,
                    text = "${uiState.notifications.size} alerts",
                    tint = IosLabelPrimary
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Frosted Notification Stack
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 12.dp)
            ) {
                items(uiState.notifications.take(4), key = { it.id }) { item ->
                    FrostedNotificationCard(
                        item = item,
                        onDismiss = { onDismissNotification(item.id) }
                    )
                }
            }

            // Bottom Quick Actions + Unlock Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .testTag("lock_btn_torch")
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(if (uiState.isTorchOn) IosYellow else Color.White.copy(alpha = 0.72f))
                        .border(1.dp, Color.White, CircleShape)
                        .clickable { onToggleTorch() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.FlashlightOn,
                        contentDescription = "Flashlight",
                        tint = if (uiState.isTorchOn) Color.White else IosLabelPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Row(
                    modifier = Modifier
                        .testTag("lock_btn_unlock")
                        .clip(RoundedCornerShape(28.dp))
                        .background(Color.White.copy(alpha = 0.84f))
                        .border(1.dp, Color.White, RoundedCornerShape(28.dp))
                        .clickable { onUnlock() }
                        .padding(horizontal = 22.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.LockOpen,
                        contentDescription = "Unlock",
                        tint = IosBlue,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Tap or Swipe to Unlock",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = IosLabelPrimary
                    )
                }

                Box(
                    modifier = Modifier
                        .testTag("lock_btn_camera")
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.72f))
                        .border(1.dp, Color.White, CircleShape)
                        .clickable { SystemControlHelper.openCamera(context) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CameraAlt,
                        contentDescription = "Camera",
                        tint = IosLabelPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun LockWidgetChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    tint: Color
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White.copy(alpha = 0.78f))
            .border(0.8.dp, Color.White, RoundedCornerShape(18.dp))
            .padding(horizontal = 12.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Icon(icon, contentDescription = text, tint = tint, modifier = Modifier.size(15.dp))
        Text(text, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = IosLabelPrimary)
    }
}

@Composable
fun NotificationCenterOverlay(
    uiState: LauncherUiState,
    onDismissNotification: (String) -> Unit,
    onClearAll: () -> Unit,
    onAddSampleNotification: () -> Unit,
    onClose: () -> Unit
) {
    BackHandler { onClose() }
    val context = LocalContext.current
    val timeText = remember { SimpleDateFormat("EEEE, MMM d • h:mm a", Locale.getDefault()).format(Date()) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xE0EDF1F7))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 580.dp)
                .align(Alignment.Center)
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Notification Center",
                        style = MaterialTheme.typography.headlineMedium,
                        color = IosLabelPrimary
                    )
                    Text(
                        text = timeText,
                        style = MaterialTheme.typography.labelMedium,
                        color = IosLabelSecondary
                    )
                }
                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .testTag("btn_close_notifications")
                        .clip(CircleShape)
                        .background(Color.White)
                ) {
                    Icon(Icons.Rounded.Close, contentDescription = "Close Notification Center")
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .clickable { onAddSampleNotification() }
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Rounded.AddAlert, contentDescription = "Pin snapshot", tint = IosBlue, modifier = Modifier.size(17.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Pin Reminder", fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold, color = IosBlue)
                }

                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .clickable { SystemControlHelper.openNotificationSettings(context) }
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Rounded.Settings, contentDescription = "System notifications", tint = IosLabelPrimary, modifier = Modifier.size(17.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("System Alerts", fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold, color = IosLabelPrimary)
                }

                if (uiState.notifications.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                            .clickable { onClearAll() }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.DeleteSweep, contentDescription = "Clear all", tint = Color(0xFFFF3B30), modifier = Modifier.size(19.dp))
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                if (uiState.notifications.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(22.dp))
                                .background(Color.White.copy(alpha = 0.85f))
                                .padding(28.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No notifications — you're all caught up.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = IosLabelSecondary
                            )
                        }
                    }
                } else {
                    items(uiState.notifications, key = { it.id }) { item ->
                        FrostedNotificationCard(
                            item = item,
                            onDismiss = { onDismissNotification(item.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FrostedNotificationCard(
    item: NotificationCardItem,
    onDismiss: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(Color.White.copy(alpha = 0.88f))
            .border(0.8.dp, Color.White, RoundedCornerShape(22.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(item.accentColor.copy(alpha = 0.16f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Notifications,
                contentDescription = item.appName,
                tint = item.accentColor,
                modifier = Modifier.size(20.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.appName.uppercase(),
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = IosLabelSecondary
                )
                Text(
                    text = item.timeLabel,
                    fontSize = 11.sp,
                    color = IosLabelSecondary
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp),
                color = IosLabelPrimary
            )
            Text(
                text = item.body,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                color = IosLabelSecondary
            )
        }

        IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(28.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = "Dismiss notification",
                tint = IosLabelSecondary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
