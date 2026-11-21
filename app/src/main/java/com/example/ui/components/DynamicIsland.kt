package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BatteryChargingFull
import androidx.compose.material.icons.rounded.BatteryFull
import androidx.compose.material.icons.rounded.Bluetooth
import androidx.compose.material.icons.rounded.FlashlightOn
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.LauncherUiState
import com.example.ui.theme.IosBlue
import com.example.ui.theme.IosDynamicIslandBg
import com.example.ui.theme.IosGreen
import com.example.ui.theme.IosOrange
import com.example.ui.theme.IosYellow

@Composable
fun DynamicIslandPill(
    uiState: LauncherUiState,
    onToggleExpand: () -> Unit,
    onToggleTimer: () -> Unit,
    onToggleTorch: () -> Unit,
    onOpenControlCenter: () -> Unit,
    onLockScreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    val expanded = uiState.isIslandExpanded
    val hasLiveActivity = uiState.isFocusRunning || uiState.isTorchOn

    val targetWidth by animateDpAsState(
        targetValue = when {
            expanded -> 336.dp
            hasLiveActivity -> 196.dp
            else -> 132.dp
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "island_width"
    )

    val targetRadius by animateDpAsState(
        targetValue = if (expanded) 30.dp else 22.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "island_radius"
    )

    Box(
        modifier = modifier
            .testTag("dynamic_island_pill")
            .width(targetWidth)
            .shadow(14.dp, RoundedCornerShape(targetRadius))
            .clip(RoundedCornerShape(targetRadius))
            .background(IosDynamicIslandBg)
            .border(
                width = 0.8.dp,
                color = Color.White.copy(alpha = 0.14f),
                shape = RoundedCornerShape(targetRadius)
            )
            .clickable { onToggleExpand() }
            .padding(
                horizontal = if (expanded) 16.dp else 12.dp,
                vertical = if (expanded) 14.dp else 8.dp
            )
    ) {
        AnimatedContent(targetState = expanded, label = "island_content") { isExpanded ->
            if (!isExpanded) {
                CompactIslandContent(uiState = uiState)
            } else {
                ExpandedIslandContent(
                    uiState = uiState,
                    onToggleTimer = onToggleTimer,
                    onToggleTorch = onToggleTorch,
                    onOpenControlCenter = onOpenControlCenter,
                    onLockScreen = onLockScreen
                )
            }
        }
    }
}

@Composable
private fun CompactIslandContent(uiState: LauncherUiState) {
    val mins = uiState.focusSecondsRemaining / 60
    val secs = uiState.focusSecondsRemaining % 60
    val timerText = String.format("%02d:%02d", mins, secs)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            uiState.isFocusRunning -> IosOrange
                            uiState.isTorchOn -> IosYellow
                            uiState.telemetry.isCharging -> IosGreen
                            else -> IosBlue
                        }
                    )
            )
            if (uiState.isFocusRunning) {
                Text(
                    text = timerText,
                    color = IosOrange,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            } else if (uiState.isTorchOn) {
                Text(
                    text = "Torch On",
                    color = IosYellow,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.SemiBold
                )
            } else {
                Text(
                    text = "TDB",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = if (uiState.telemetry.isCharging) Icons.Rounded.BatteryChargingFull else Icons.Rounded.BatteryFull,
                contentDescription = "Battery level",
                tint = if (uiState.telemetry.batteryPercent > 20) IosGreen else Color(0xFFFF453A),
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = "${uiState.telemetry.batteryPercent}%",
                color = Color.White,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun ExpandedIslandContent(
    uiState: LauncherUiState,
    onToggleTimer: () -> Unit,
    onToggleTorch: () -> Unit,
    onOpenControlCenter: () -> Unit,
    onLockScreen: () -> Unit
) {
    val mins = uiState.focusSecondsRemaining / 60
    val secs = uiState.focusSecondsRemaining % 60
    val timerText = String.format("%02d:%02d", mins, secs)

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(IosOrange.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Timer,
                        contentDescription = "Focus timer",
                        tint = IosOrange,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column {
                    Text(
                        text = if (uiState.isFocusRunning) "Focus Active" else "Focus Standby",
                        color = Color.White.copy(alpha = 0.68f),
                        fontSize = 11.sp
                    )
                    Text(
                        text = timerText,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(IosOrange)
                        .clickable { onToggleTimer() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (uiState.isFocusRunning) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        contentDescription = if (uiState.isFocusRunning) "Pause timer" else "Start timer",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IslandChip(
                icon = Icons.Rounded.Wifi,
                label = uiState.telemetry.wifiLabel,
                active = uiState.telemetry.wifiConnected,
                activeColor = IosBlue,
                onClick = onOpenControlCenter
            )
            IslandChip(
                icon = Icons.Rounded.Bluetooth,
                label = if (uiState.telemetry.bluetoothEnabled) "BT On" else "BT Off",
                active = uiState.telemetry.bluetoothEnabled,
                activeColor = IosBlue,
                onClick = onOpenControlCenter
            )
            IslandChip(
                icon = Icons.Rounded.FlashlightOn,
                label = if (uiState.isTorchOn) "Torch" else "Light",
                active = uiState.isTorchOn,
                activeColor = IosYellow,
                onClick = onToggleTorch
            )
            IslandChip(
                icon = Icons.Rounded.Tune,
                label = "Controls",
                active = false,
                activeColor = IosBlue,
                onClick = onOpenControlCenter
            )
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.14f))
                    .clickable { onLockScreen() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Lock,
                    contentDescription = "Lock screen",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun IslandChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    active: Boolean,
    activeColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (active) activeColor.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.11f)
            )
            .clickable { onClick() }
            .padding(horizontal = 9.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (active) activeColor else Color.White.copy(alpha = 0.85f),
            modifier = Modifier.size(13.dp)
        )
        Text(
            text = label,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
