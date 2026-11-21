package com.example.ui.components

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BatteryChargingFull
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DeviceTelemetry
import com.example.data.ReminderItem
import com.example.ui.theme.IosBlue
import com.example.ui.theme.IosGreen
import com.example.ui.theme.IosLabelPrimary
import com.example.ui.theme.IosLabelSecondary
import com.example.ui.theme.IosOrange
import com.example.ui.theme.IosRed
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeWidgetsSection(
    telemetry: DeviceTelemetry,
    reminders: List<ReminderItem>,
    focusSecondsRemaining: Int,
    isFocusRunning: Boolean,
    onToggleReminder: (String) -> Unit,
    onToggleFocusTimer: () -> Unit,
    onOpenWeather: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BatteryTelemetryWidget(
                telemetry = telemetry,
                onClick = onOpenSettings,
                modifier = Modifier.weight(1f)
            )
            CalendarRemindersWidget(
                reminders = reminders,
                onToggleReminder = onToggleReminder,
                modifier = Modifier.weight(1f)
            )
        }

        FocusAndAtmosphereWidget(
            focusSecondsRemaining = focusSecondsRemaining,
            isFocusRunning = isFocusRunning,
            onToggleFocus = onToggleFocusTimer,
            onOpenWeather = onOpenWeather
        )
    }
}

@Composable
fun BatteryTelemetryWidget(
    telemetry: DeviceTelemetry,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .testTag("widget_battery_health")
            .height(148.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White.copy(alpha = 0.80f))
            .border(1.dp, Color.White.copy(alpha = 0.72f), RoundedCornerShape(24.dp))
            .clickable { onClick() }
            .padding(14.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(52.dp),
                contentAlignment = Alignment.Center
            ) {
                val sweep = (telemetry.batteryPercent / 100f) * 360f
                val ringColor = if (telemetry.batteryPercent > 20) IosGreen else IosRed
                Canvas(modifier = Modifier.size(50.dp)) {
                    drawArc(
                        color = ringColor.copy(alpha = 0.18f),
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                    )
                    drawArc(
                        color = ringColor,
                        startAngle = -90f,
                        sweepAngle = sweep,
                        useCenter = false,
                        style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
                if (telemetry.isCharging) {
                    Icon(
                        imageVector = Icons.Rounded.BatteryChargingFull,
                        contentDescription = "Charging",
                        tint = IosGreen,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        text = "${telemetry.batteryPercent}%",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = IosLabelPrimary
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = telemetry.wifiLabel,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (telemetry.wifiConnected) IosBlue else IosLabelSecondary
                )
                Text(
                    text = "${String.format("%.0f", telemetry.freeStorageGb)} GB free",
                    fontSize = 10.5.sp,
                    color = IosLabelSecondary
                )
            }
        }

        Column {
            Text(
                text = "Device Health",
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                color = IosLabelPrimary
            )
            Text(
                text = if (telemetry.isCharging) "Charging • ${telemetry.batteryPercent}%" else "Android ${telemetry.androidVersion}",
                style = MaterialTheme.typography.labelMedium,
                color = IosLabelSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun CalendarRemindersWidget(
    reminders: List<ReminderItem>,
    onToggleReminder: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val weekday = remember { SimpleDateFormat("EEEE", Locale.getDefault()).format(Date()).uppercase() }
    val dayNumber = remember { SimpleDateFormat("d", Locale.getDefault()).format(Date()) }

    Column(
        modifier = modifier
            .testTag("widget_calendar_reminders")
            .height(148.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White.copy(alpha = 0.80f))
            .border(1.dp, Color.White.copy(alpha = 0.72f), RoundedCornerShape(24.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = weekday,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = IosRed
                )
                Text(
                    text = dayNumber,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = IosLabelPrimary,
                    lineHeight = 26.sp
                )
            }
            Text(
                text = "${reminders.count { !it.isDone }} due",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = IosBlue
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
            reminders.take(2).forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onToggleReminder(item.id) }
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = if (item.isDone) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
                        contentDescription = if (item.isDone) "Completed" else "Pending",
                        tint = if (item.isDone) IosBlue else IosLabelSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = item.title,
                        fontSize = 12.sp,
                        color = if (item.isDone) IosLabelSecondary else IosLabelPrimary,
                        textDecoration = if (item.isDone) TextDecoration.LineThrough else TextDecoration.None,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun FocusAndAtmosphereWidget(
    focusSecondsRemaining: Int,
    isFocusRunning: Boolean,
    onToggleFocus: () -> Unit,
    onOpenWeather: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentTime by remember { mutableStateOf(SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())) }
    LaunchedEffect(Unit) {
        while (true) {
            currentTime = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
            delay(15_000L)
        }
    }

    val mins = focusSecondsRemaining / 60
    val secs = focusSecondsRemaining % 60
    val timerString = String.format("%02d:%02d", mins, secs)

    Row(
        modifier = modifier
            .testTag("widget_focus_atmosphere")
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xD9EBF5FF),
                        Color(0xD9FFFFFF)
                    )
                )
            )
            .border(1.dp, Color.White.copy(alpha = 0.78f), RoundedCornerShape(24.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .clickable { onOpenWeather() },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(Color(0xFFFFD60A), IosOrange))),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.WbSunny,
                    contentDescription = "Atmosphere",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            Column {
                Text(
                    text = currentTime,
                    style = MaterialTheme.typography.titleLarge,
                    color = IosLabelPrimary
                )
                Text(
                    text = "Clear Sky • 24°C • Atmosphere",
                    style = MaterialTheme.typography.labelMedium,
                    color = IosLabelSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(18.dp))
                .background(if (isFocusRunning) IosOrange else IosBlue)
                .clickable { onToggleFocus() }
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = if (isFocusRunning) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                contentDescription = if (isFocusRunning) "Pause focus" else "Start focus",
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = timerString,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
