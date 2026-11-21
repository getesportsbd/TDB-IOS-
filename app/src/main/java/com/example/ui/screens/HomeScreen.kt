package com.example.ui.screens

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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppCategory
import com.example.data.AppScreen
import com.example.data.BuiltInAppType
import com.example.data.BuiltInCatalog
import com.example.data.OverlayType
import com.example.ui.LauncherUiState
import com.example.ui.components.BuiltInSquircleAppIcon
import com.example.ui.components.DynamicIslandPill
import com.example.ui.components.HomeWidgetsSection
import com.example.ui.components.InstalledSquircleAppIcon
import com.example.ui.theme.IosBlue
import com.example.ui.theme.IosLabelPrimary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.ceil

@Composable
fun HomeScreen(
    uiState: LauncherUiState,
    onToggleIsland: () -> Unit,
    onToggleFocusTimer: () -> Unit,
    onToggleTorch: () -> Unit,
    onOpenOverlay: (OverlayType) -> Unit,
    onNavigateScreen: (AppScreen) -> Unit,
    onLaunchBuiltIn: (BuiltInAppType, () -> Unit) -> Unit,
    onLaunchInstalled: (String) -> Unit,
    onToggleReminder: (String) -> Unit,
    onLockScreen: () -> Unit,
    onOpenLibraryCategory: (AppCategory?) -> Unit,
    onUpdateSearchQuery: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val gridCols = uiState.settings.gridColumns
    val appsPerPage = gridCols * 4
    val installedChunks = remember(uiState.installedApps, appsPerPage) {
        if (uiState.installedApps.isEmpty()) listOf(emptyList())
        else uiState.installedApps.chunked(appsPerPage)
    }
    // Page 0 = Widgets + Core Shortcuts; Pages 1..N = Installed Apps; Final Page = App Library
    val totalPages = 1 + installedChunks.size + 1
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { totalPages })
    val coroutineScope = rememberCoroutineScope()

    var currentTime by remember { mutableStateOf(SimpleDateFormat("h:mm", Locale.getDefault()).format(Date())) }
    LaunchedEffect(Unit) {
        while (true) {
            currentTime = SimpleDateFormat("h:mm", Locale.getDefault()).format(Date())
            delay(15_000L)
        }
    }

    val textColor = if (uiState.settings.lightTextOverWallpaper) Color.White else IosLabelPrimary

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                var dragAccum = 0f
                detectVerticalDragGestures(
                    onDragStart = { dragAccum = 0f },
                    onVerticalDrag = { _, dragAmount ->
                        dragAccum += dragAmount
                    },
                    onDragEnd = {
                        if (dragAccum > 110f) {
                            onOpenOverlay(OverlayType.SPOTLIGHT_SEARCH)
                        } else if (dragAccum < -110f) {
                            onOpenOverlay(OverlayType.APP_DRAWER)
                        }
                    }
                )
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .widthIn(max = 620.dp)
                .align(Alignment.Center)
        ) {
            // Top Status + Dynamic Island Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                // Left: Time & Notification Center trigger
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .testTag("btn_notification_center")
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color.White.copy(alpha = 0.52f))
                        .border(0.7.dp, Color.White.copy(alpha = 0.7f), RoundedCornerShape(18.dp))
                        .clickable { onOpenOverlay(OverlayType.NOTIFICATION_CENTER) }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = currentTime,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = IosLabelPrimary
                    )
                    if (uiState.notifications.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Rounded.Notifications,
                            contentDescription = "Notification Center",
                            tint = IosBlue,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                // Right: Control Center trigger
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .testTag("btn_control_center")
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color.White.copy(alpha = 0.52f))
                        .border(0.7.dp, Color.White.copy(alpha = 0.7f), RoundedCornerShape(18.dp))
                        .clickable { onOpenOverlay(OverlayType.CONTROL_CENTER) }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Tune,
                        contentDescription = "Control Center",
                        tint = IosLabelPrimary,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = "Controls",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = IosLabelPrimary
                    )
                }

                // Center: Dynamic Island Pill
                DynamicIslandPill(
                    uiState = uiState,
                    onToggleExpand = onToggleIsland,
                    onToggleTimer = onToggleFocusTimer,
                    onToggleTorch = onToggleTorch,
                    onOpenControlCenter = { onOpenOverlay(OverlayType.CONTROL_CENTER) },
                    onLockScreen = onLockScreen,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }

            // Main Pager (Home -> Installed Apps Pages -> App Library)
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { page ->
                when {
                    page == 0 -> {
                        HomePrimaryPage(
                            uiState = uiState,
                            gridCols = gridCols,
                            onToggleReminder = onToggleReminder,
                            onToggleFocusTimer = onToggleFocusTimer,
                            onOpenWeather = { onNavigateScreen(AppScreen.WEATHER) },
                            onOpenSettings = { onNavigateScreen(AppScreen.SETTINGS) },
                            onLaunchBuiltIn = { type ->
                                onLaunchBuiltIn(type) {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(totalPages - 1)
                                    }
                                }
                            }
                        )
                    }
                    page in 1..installedChunks.size -> {
                        val apps = installedChunks[page - 1]
                        InstalledAppsPage(
                            apps = apps,
                            gridCols = gridCols,
                            showLabels = uiState.settings.showIconLabels,
                            lightText = uiState.settings.lightTextOverWallpaper,
                            hapticEnabled = uiState.settings.hapticFeedback,
                            pageIndex = page,
                            onLaunchInstalled = onLaunchInstalled
                        )
                    }
                    else -> {
                        AppLibraryPage(
                            uiState = uiState,
                            onUpdateQuery = onUpdateSearchQuery,
                            onOpenCategory = onOpenLibraryCategory,
                            onLaunchInstalled = onLaunchInstalled,
                            onOpenAppDrawer = { onOpenOverlay(OverlayType.APP_DRAWER) }
                        )
                    }
                }
            }

            // Page dots + Search Pill (hidden on App Library page or kept compact)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(totalPages) { idx ->
                        val active = pagerState.currentPage == idx
                        Box(
                            modifier = Modifier
                                .size(if (active) 8.dp else 6.dp)
                                .clip(CircleShape)
                                .background(
                                    if (active) textColor else textColor.copy(alpha = 0.35f)
                                )
                                .clickable {
                                    coroutineScope.launch { pagerState.animateScrollToPage(idx) }
                                }
                        )
                    }
                }

                // Spotlight Search Pill
                Row(
                    modifier = Modifier
                        .testTag("home_search_pill")
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.62f))
                        .border(0.7.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(20.dp))
                        .clickable { onOpenOverlay(OverlayType.SPOTLIGHT_SEARCH) }
                        .padding(horizontal = 16.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = "Search",
                        tint = IosLabelPrimary.copy(alpha = 0.8f),
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = "Search",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = IosLabelPrimary.copy(alpha = 0.85f)
                    )
                }
            }

            // Frosted iOS-Inspired Dock
            Row(
                modifier = Modifier
                    .testTag("ios_dock")
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 12.dp)
                    .clip(RoundedCornerShape(30.dp))
                    .background(Color.White.copy(alpha = 0.68f))
                    .border(1.dp, Color.White.copy(alpha = 0.85f), RoundedCornerShape(30.dp))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BuiltInCatalog.dockShortcuts.forEach { dockItem ->
                    BuiltInSquircleAppIcon(
                        item = dockItem,
                        showLabel = false,
                        lightLabelText = false,
                        hapticEnabled = uiState.settings.hapticFeedback,
                        iconSize = 56.dp,
                        onClick = {
                            onLaunchBuiltIn(dockItem.type) {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(totalPages - 1)
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun HomePrimaryPage(
    uiState: LauncherUiState,
    gridCols: Int,
    onToggleReminder: (String) -> Unit,
    onToggleFocusTimer: () -> Unit,
    onOpenWeather: () -> Unit,
    onOpenSettings: () -> Unit,
    onLaunchBuiltIn: (BuiltInAppType) -> Unit
) {
    val scrollState = rememberScrollState()
    val rows = remember(BuiltInCatalog.homeShortcuts, gridCols) {
        BuiltInCatalog.homeShortcuts.chunked(gridCols)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 18.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        HomeWidgetsSection(
            telemetry = uiState.telemetry,
            reminders = uiState.reminders,
            focusSecondsRemaining = uiState.focusSecondsRemaining,
            isFocusRunning = uiState.isFocusRunning,
            onToggleReminder = onToggleReminder,
            onToggleFocusTimer = onToggleFocusTimer,
            onOpenWeather = onOpenWeather,
            onOpenSettings = onOpenSettings
        )

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            rows.forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    rowItems.forEach { item ->
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            BuiltInSquircleAppIcon(
                                item = item,
                                showLabel = uiState.settings.showIconLabels,
                                lightLabelText = uiState.settings.lightTextOverWallpaper,
                                hapticEnabled = uiState.settings.hapticFeedback,
                                iconSize = if (gridCols == 5) 52.dp else 58.dp,
                                onClick = { onLaunchBuiltIn(item.type) }
                            )
                        }
                    }
                    repeat(gridCols - rowItems.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun InstalledAppsPage(
    apps: List<com.example.data.InstalledApp>,
    gridCols: Int,
    showLabels: Boolean,
    lightText: Boolean,
    hapticEnabled: Boolean,
    pageIndex: Int,
    onLaunchInstalled: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp, vertical = 12.dp)
    ) {
        Text(
            text = if (pageIndex == 1) "Installed Android Apps (${apps.size})" else "More Apps",
            style = MaterialTheme.typography.labelLarge,
            color = if (lightText) Color.White.copy(alpha = 0.85f) else IosLabelPrimary.copy(alpha = 0.72f),
            modifier = Modifier.padding(bottom = 10.dp, start = 4.dp)
        )

        if (apps.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White.copy(alpha = 0.76f))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No additional launchable apps detected yet. Swipe right for App Library or tap All Apps in the Dock.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = IosLabelPrimary
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(gridCols),
                contentPadding = PaddingValues(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(apps, key = { it.packageName }) { app ->
                    InstalledSquircleAppIcon(
                        app = app,
                        showLabel = showLabels,
                        lightLabelText = lightText,
                        hapticEnabled = hapticEnabled,
                        iconSize = if (gridCols == 5) 52.dp else 58.dp,
                        onClick = { onLaunchInstalled(app.packageName) }
                    )
                }
            }
        }
    }
}
