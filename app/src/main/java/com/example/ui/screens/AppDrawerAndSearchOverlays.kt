package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bluetooth
import androidx.compose.material.icons.rounded.Brightness6
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BuiltInAppType
import com.example.data.BuiltInCatalog
import com.example.data.InstalledApp
import com.example.data.SystemControlHelper
import com.example.ui.LauncherUiState
import com.example.ui.theme.IosBlue
import com.example.ui.theme.IosGroupedBackground
import com.example.ui.theme.IosLabelPrimary
import com.example.ui.theme.IosLabelSecondary
import com.example.ui.theme.IosOrange
import com.example.ui.theme.IosPurple

@Composable
fun AppDrawerOverlay(
    uiState: LauncherUiState,
    onUpdateQuery: (String) -> Unit,
    onRefreshApps: () -> Unit,
    onLaunchInstalled: (String) -> Unit,
    onOpenAppInfo: (String) -> Unit,
    onClose: () -> Unit
) {
    BackHandler { onClose() }

    val filtered = remember(uiState.installedApps, uiState.searchQuery) {
        val q = uiState.searchQuery.trim()
        if (q.isEmpty()) uiState.installedApps
        else uiState.installedApps.filter {
            it.label.contains(q, ignoreCase = true) || it.packageName.contains(q, ignoreCase = true)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xEBF2F2F7))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 620.dp)
                .align(Alignment.Center)
                .padding(horizontal = 18.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "All Installed Apps",
                        style = MaterialTheme.typography.headlineMedium,
                        color = IosLabelPrimary
                    )
                    Text(
                        text = "${uiState.installedApps.size} apps detected on device",
                        style = MaterialTheme.typography.labelMedium,
                        color = IosLabelSecondary
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onRefreshApps) {
                        Icon(Icons.Rounded.Refresh, contentDescription = "Refresh apps", tint = IosBlue)
                    }
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.testTag("btn_close_app_drawer")
                    ) {
                        Icon(Icons.Rounded.Close, contentDescription = "Close App Drawer", tint = IosLabelPrimary)
                    }
                }
            }

            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = onUpdateQuery,
                placeholder = { Text("Filter installed applications...") },
                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = "Search apps") },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onUpdateQuery("") }) {
                            Icon(Icons.Rounded.Close, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(18.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = IosBlue,
                    unfocusedBorderColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("app_drawer_search_input")
            )

            LazyColumn(
                contentPadding = PaddingValues(bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                if (filtered.isEmpty()) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = Color.White,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "No matching applications found.",
                                modifier = Modifier.padding(20.dp),
                                color = IosLabelSecondary
                            )
                        }
                    }
                } else {
                    items(filtered, key = { it.packageName }) { app ->
                        AppDrawerRow(
                            app = app,
                            onClick = { onLaunchInstalled(app.packageName) },
                            onInfoClick = { onOpenAppInfo(app.packageName) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AppDrawerRow(
    app: InstalledApp,
    onClick: () -> Unit,
    onInfoClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(IosGroupedBackground),
            contentAlignment = Alignment.Center
        ) {
            if (app.icon != null) {
                Image(
                    bitmap = app.icon,
                    contentDescription = app.label,
                    modifier = Modifier.fillMaxSize().padding(4.dp).clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(app.label.take(1).uppercase(), fontWeight = FontWeight.Bold, color = IosBlue)
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = app.label,
                style = MaterialTheme.typography.titleMedium,
                color = IosLabelPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = app.category.title,
                style = MaterialTheme.typography.labelMedium,
                color = IosLabelSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        IconButton(onClick = onInfoClick) {
            Icon(
                imageVector = Icons.Rounded.Info,
                contentDescription = "App info for ${app.label}",
                tint = IosLabelSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun SpotlightSearchOverlay(
    uiState: LauncherUiState,
    onUpdateQuery: (String) -> Unit,
    onLaunchInstalled: (String) -> Unit,
    onLaunchBuiltIn: (BuiltInAppType) -> Unit,
    onClose: () -> Unit
) {
    BackHandler { onClose() }
    val context = LocalContext.current
    val q = uiState.searchQuery.trim()

    val matchingApps = remember(uiState.installedApps, q) {
        if (q.isEmpty()) uiState.installedApps.take(6)
        else uiState.installedApps.filter { it.label.contains(q, ignoreCase = true) }.take(12)
    }
    val matchingTools = remember(q) {
        val all = BuiltInCatalog.homeShortcuts
        if (q.isEmpty()) all.take(4)
        else all.filter { it.title.contains(q, ignoreCase = true) }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xE6F2F2F7))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 620.dp)
                .align(Alignment.Center)
                .padding(horizontal = 18.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = onUpdateQuery,
                    placeholder = { Text("Search apps, tools & system settings") },
                    leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = "Spotlight search") },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onUpdateQuery("") }) {
                                Icon(Icons.Rounded.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = IosBlue,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("spotlight_search_input")
                )
                Text(
                    text = "Cancel",
                    color = IosBlue,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onClose() }
                        .padding(horizontal = 10.dp, vertical = 10.dp)
                )
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Text(
                        text = "Quick System Shortcuts",
                        style = MaterialTheme.typography.labelLarge,
                        color = IosLabelSecondary,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        SpotlightQuickShortcut(
                            icon = Icons.Rounded.Wifi,
                            label = "Wi-Fi",
                            tint = IosBlue,
                            onClick = { SystemControlHelper.openWifiSettings(context) },
                            modifier = Modifier.weight(1f)
                        )
                        SpotlightQuickShortcut(
                            icon = Icons.Rounded.Bluetooth,
                            label = "Bluetooth",
                            tint = IosBlue,
                            onClick = { SystemControlHelper.openBluetoothSettings(context) },
                            modifier = Modifier.weight(1f)
                        )
                        SpotlightQuickShortcut(
                            icon = Icons.Rounded.Brightness6,
                            label = "Display",
                            tint = IosOrange,
                            onClick = { SystemControlHelper.openDisplaySettings(context) },
                            modifier = Modifier.weight(1f)
                        )
                        SpotlightQuickShortcut(
                            icon = Icons.Rounded.VolumeUp,
                            label = "Sound",
                            tint = IosPurple,
                            onClick = { SystemControlHelper.openSoundSettings(context) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                if (matchingTools.isNotEmpty()) {
                    item {
                        Text(
                            text = "TDB Tools & Experience",
                            style = MaterialTheme.typography.labelLarge,
                            color = IosLabelSecondary,
                            modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                        )
                    }
                    items(matchingTools, key = { it.type.name }) { tool ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White)
                                .clickable { onLaunchBuiltIn(tool.type) }
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(tool.title, style = MaterialTheme.typography.titleMedium, color = IosLabelPrimary)
                            Text("Open", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = IosBlue)
                        }
                    }
                }

                item {
                    Text(
                        text = "Installed Apps",
                        style = MaterialTheme.typography.labelLarge,
                        color = IosLabelSecondary,
                        modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                    )
                }

                items(matchingApps, key = { it.packageName }) { app ->
                    AppDrawerRow(
                        app = app,
                        onClick = { onLaunchInstalled(app.packageName) },
                        onInfoClick = { SystemControlHelper.openAppInfo(context, app.packageName) }
                    )
                }

                if (q.isNotEmpty()) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(IosBlue)
                                .clickable { SystemControlHelper.openBrowser(context, q) }
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(Icons.Rounded.Public, contentDescription = "Search web", tint = Color.White)
                            Text(
                                text = "Search Web for \"$q\"",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SpotlightQuickShortcut(
    icon: ImageVector,
    label: String,
    tint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(tint.copy(alpha = 0.14f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label, tint = tint, modifier = Modifier.size(18.dp))
        }
        Text(label, fontSize = 11.5.sp, fontWeight = FontWeight.Medium, color = IosLabelPrimary)
    }
}
