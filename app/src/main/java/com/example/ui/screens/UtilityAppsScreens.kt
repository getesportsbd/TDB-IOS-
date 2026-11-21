package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.LauncherUiState
import com.example.ui.theme.IosBlue
import com.example.ui.theme.IosCyan
import com.example.ui.theme.IosGroupedBackground
import com.example.ui.theme.IosLabelPrimary
import com.example.ui.theme.IosLabelSecondary
import com.example.ui.theme.IosOrange

@Composable
fun CalculatorScreen(onBack: () -> Unit) {
    BackHandler { onBack() }
    var display by remember { mutableStateOf("0") }
    var storedOperand by remember { mutableStateOf<Double?>(null) }
    var pendingOp by remember { mutableStateOf<String?>(null) }
    var resetOnNextDigit by remember { mutableStateOf(false) }

    fun formatResult(value: Double): String {
        return if (value % 1.0 == 0.0 && kotlin.math.abs(value) < 1e12) {
            value.toLong().toString()
        } else {
            String.format("%.6f", value).trimEnd('0').trimEnd('.')
        }
    }

    fun compute(a: Double, b: Double, op: String): Double = when (op) {
        "+" -> a + b
        "−" -> a - b
        "×" -> a * b
        "÷" -> if (b == 0.0) Double.NaN else a / b
        else -> b
    }

    fun handleInput(key: String) {
        when (key) {
            "C" -> {
                display = "0"
                storedOperand = null
                pendingOp = null
                resetOnNextDigit = false
            }
            "±" -> {
                val v = display.toDoubleOrNull() ?: return
                display = formatResult(-v)
            }
            "%" -> {
                val v = display.toDoubleOrNull() ?: return
                display = formatResult(v / 100.0)
            }
            "+", "−", "×", "÷" -> {
                val current = display.toDoubleOrNull() ?: 0.0
                if (storedOperand != null && pendingOp != null && !resetOnNextDigit) {
                    val res = compute(storedOperand!!, current, pendingOp!!)
                    display = if (res.isNaN()) "Error" else formatResult(res)
                    storedOperand = if (res.isNaN()) null else res
                } else {
                    storedOperand = current
                }
                pendingOp = key
                resetOnNextDigit = true
            }
            "=" -> {
                val current = display.toDoubleOrNull() ?: return
                val op = pendingOp ?: return
                val lhs = storedOperand ?: return
                val res = compute(lhs, current, op)
                display = if (res.isNaN()) "Error" else formatResult(res)
                storedOperand = null
                pendingOp = null
                resetOnNextDigit = true
            }
            "." -> {
                if (resetOnNextDigit) {
                    display = "0."
                    resetOnNextDigit = false
                } else if (!display.contains(".")) {
                    display += "."
                }
            }
            else -> {
                display = if (resetOnNextDigit || display == "0" || display == "Error") {
                    resetOnNextDigit = false
                    key
                } else {
                    (display + key).take(12)
                }
            }
        }
    }

    val rows = listOf(
        listOf("C", "±", "%", "÷"),
        listOf("7", "8", "9", "×"),
        listOf("4", "5", "6", "−"),
        listOf("1", "2", "3", "+"),
        listOf("0", ".", "=")
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF141416))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 500.dp)
                .align(Alignment.Center)
                .padding(18.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .testTag("calculator_back_button")
                        .clip(CircleShape)
                        .background(Color(0xFF2C2C2E))
                ) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.size(12.dp))
                Text("Calculator", color = Color.White, style = MaterialTheme.typography.titleLarge)
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.End
            ) {
                if (pendingOp != null) {
                    Text(
                        text = "${storedOperand?.let { formatResult(it) } ?: ""} $pendingOp",
                        color = Color.White.copy(alpha = 0.55f),
                        fontSize = 20.sp
                    )
                }
                Text(
                    text = display,
                    color = Color.White,
                    fontSize = 62.sp,
                    fontWeight = FontWeight.Light,
                    maxLines = 1,
                    modifier = Modifier.testTag("calculator_display")
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                rows.forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        row.forEach { key ->
                            val isOp = key in listOf("÷", "×", "−", "+", "=")
                            val isTop = key in listOf("C", "±", "%")
                            val bg = when {
                                isOp -> IosOrange
                                isTop -> Color(0xFFA5A5A9)
                                else -> Color(0xFF2C2C2E)
                            }
                            val fg = if (isTop) Color.Black else Color.White
                            Box(
                                modifier = Modifier
                                    .weight(if (key == "0") 2f else 1f)
                                    .height(74.dp)
                                    .clip(RoundedCornerShape(37.dp))
                                    .background(bg)
                                    .clickable { handleInput(key) }
                                    .testTag("calc_key_$key"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = key,
                                    color = fg,
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotesAndRemindersScreen(
    uiState: LauncherUiState,
    onSaveNote: (String) -> Unit,
    onAddReminder: (String) -> Unit,
    onToggleReminder: (String) -> Unit,
    onDeleteReminder: (String) -> Unit,
    onBack: () -> Unit
) {
    BackHandler { onBack() }
    var newReminder by remember { mutableStateOf("") }

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
                .padding(horizontal = 18.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.clip(CircleShape).background(Color.White)
                ) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back", tint = IosOrange)
                }
                Column {
                    Text("Notes & Reminders", style = MaterialTheme.typography.headlineMedium, color = IosLabelPrimary)
                    Text("Synced automatically with Home Widget", style = MaterialTheme.typography.labelMedium, color = IosLabelSecondary)
                }
            }

            OutlinedTextField(
                value = uiState.quickNoteText,
                onValueChange = onSaveNote,
                label = { Text("Quick Scratchpad") },
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFFFFDF4),
                    unfocusedContainerColor = Color(0xFFFFFDF4),
                    focusedBorderColor = IosOrange,
                    unfocusedBorderColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = newReminder,
                    onValueChange = { newReminder = it },
                    placeholder = { Text("New reminder...") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = IosBlue,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = {
                        onAddReminder(newReminder)
                        newReminder = ""
                    },
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(IosBlue)
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = "Add reminder", tint = Color.White)
                }
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(uiState.reminders, key = { it.id }) { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                            .clickable { onToggleReminder(item.id) }
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = if (item.isDone) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
                            contentDescription = null,
                            tint = if (item.isDone) IosBlue else IosLabelSecondary
                        )
                        Text(
                            text = item.title,
                            modifier = Modifier.weight(1f),
                            color = if (item.isDone) IosLabelSecondary else IosLabelPrimary,
                            textDecoration = if (item.isDone) TextDecoration.LineThrough else TextDecoration.None
                        )
                        IconButton(onClick = { onDeleteReminder(item.id) }) {
                            Icon(Icons.Rounded.Delete, contentDescription = "Delete reminder", tint = IosLabelSecondary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AtmosphereWeatherScreen(
    uiState: LauncherUiState,
    onSetFocusPreset: (Int) -> Unit,
    onToggleFocusTimer: () -> Unit,
    onBack: () -> Unit
) {
    BackHandler { onBack() }
    val mins = uiState.focusSecondsRemaining / 60
    val secs = uiState.focusSecondsRemaining % 60

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(IosBlue, IosCyan, Color(0xFFEAF6FF))))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 580.dp)
                .align(Alignment.Center)
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.clip(CircleShape).background(Color.White.copy(alpha = 0.28f))
                ) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.size(10.dp))
                Text("Atmosphere & Focus", color = Color.White, style = MaterialTheme.typography.titleLarge)
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color.White.copy(alpha = 0.22f))
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Rounded.WbSunny, contentDescription = "Sunny", tint = Color(0xFFFFE066), modifier = Modifier.size(54.dp))
                Text("24°C", color = Color.White, style = MaterialTheme.typography.displayLarge)
                Text("Clear Light Sky • Pleasant Day", color = Color.White.copy(alpha = 0.92f), style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "Battery ${uiState.telemetry.batteryPercent}% • ${uiState.telemetry.wifiLabel}",
                    color = Color.White.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.labelMedium
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color.White.copy(alpha = 0.9f))
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text("Dynamic Island Focus Timer", style = MaterialTheme.typography.titleLarge, color = IosLabelPrimary)
                Text(
                    text = String.format("%02d:%02d", mins, secs),
                    style = MaterialTheme.typography.displayMedium,
                    color = if (uiState.isFocusRunning) IosOrange else IosLabelPrimary
                )

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    listOf(5, 15, 25, 45).forEach { preset ->
                        val active = uiState.focusPresetMinutes == preset
                        Text(
                            text = "${preset}m",
                            fontWeight = FontWeight.Bold,
                            color = if (active) Color.White else IosBlue,
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (active) IosBlue else IosBlue.copy(alpha = 0.12f))
                                .clickable { onSetFocusPreset(preset) }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }

                Button(
                    onClick = onToggleFocusTimer,
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (uiState.isFocusRunning) IosOrange else IosBlue
                    ),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Icon(
                        imageVector = if (uiState.isFocusRunning) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                    Text(if (uiState.isFocusRunning) "Pause Focus Session" else "Start Focus Session")
                }
            }
        }
    }
}
