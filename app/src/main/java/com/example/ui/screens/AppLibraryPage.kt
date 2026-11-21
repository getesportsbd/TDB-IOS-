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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.AppCategory
import com.example.data.InstalledApp
import com.example.ui.LauncherUiState
import com.example.ui.components.InstalledSquircleAppIcon
import com.example.ui.theme.IosBlue
import com.example.ui.theme.IosLabelPrimary
import com.example.ui.theme.IosLabelSecondary

@Composable
fun AppLibraryPage(
    uiState: LauncherUiState,
    onUpdateQuery: (String) -> Unit,
    onOpenCategory: (AppCategory?) -> Unit,
    onLaunchInstalled: (String) -> Unit,
    onOpenAppDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val grouped = remember(uiState.installedApps) {
        uiState.installedApps.groupBy { it.category }
    }
    val filteredApps = remember(uiState.installedApps, uiState.searchQuery) {
        val q = uiState.searchQuery.trim()
        if (q.isEmpty()) emptyList()
        else uiState.installedApps.filter { it.label.contains(q, ignoreCase = true) }
    }

    if (uiState.expandedLibraryCategory != null) {
        BackHandler { onOpenCategory(null) }
        val category = uiState.expandedLibraryCategory
        val categoryApps = grouped[category].orEmpty()
        Dialog(onDismissRequest = { onOpenCategory(null) }) {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = Color.White.copy(alpha = 0.96f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = category.title,
                            style = MaterialTheme.typography.titleLarge,
                            color = IosLabelPrimary
                        )
                        IconButton(onClick = { onOpenCategory(null) }) {
                            Icon(Icons.Rounded.Close, contentDescription = "Close folder")
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.height(340.dp)
                    ) {
                        items(categoryApps, key = { it.packageName }) { app ->
                            InstalledSquircleAppIcon(
                                app = app,
                                showLabel = true,
                                lightLabelText = false,
                                hapticEnabled = uiState.settings.hapticFeedback,
                                iconSize = 56.dp,
                                onClick = {
                                    onOpenCategory(null)
                                    onLaunchInstalled(app.packageName)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "App Library",
                style = MaterialTheme.typography.headlineMedium,
                color = if (uiState.settings.lightTextOverWallpaper) Color.White else IosLabelPrimary
            )
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.72f))
                    .clickable { onOpenAppDrawer() }
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(Icons.Rounded.Apps, contentDescription = "All Apps", tint = IosBlue, modifier = Modifier.size(16.dp))
                Text("A–Z List", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = IosBlue)
            }
        }

        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = onUpdateQuery,
            placeholder = { Text("App Library") },
            leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = "Search App Library") },
            trailingIcon = {
                if (uiState.searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onUpdateQuery("") }) {
                        Icon(Icons.Rounded.Close, contentDescription = "Clear search")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(18.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White.copy(alpha = 0.85f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.78f),
                focusedBorderColor = IosBlue,
                unfocusedBorderColor = Color.White.copy(alpha = 0.7f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("app_library_search_input")
        )

        if (uiState.searchQuery.isNotBlank()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredApps, key = { it.packageName }) { app ->
                    InstalledSquircleAppIcon(
                        app = app,
                        showLabel = true,
                        lightLabelText = uiState.settings.lightTextOverWallpaper,
                        hapticEnabled = uiState.settings.hapticFeedback,
                        iconSize = 56.dp,
                        onClick = { onLaunchInstalled(app.packageName) }
                    )
                }
            }
        } else {
            val nonEmptyCategories = AppCategory.entries.filter { grouped[it]?.isNotEmpty() == true }
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(nonEmptyCategories, key = { it.name }) { category ->
                    val apps = grouped[category].orEmpty()
                    AppLibraryCategoryCard(
                        category = category,
                        apps = apps,
                        onLaunchApp = onLaunchInstalled,
                        onOpenFolder = { onOpenCategory(category) }
                    )
                }
            }
        }
    }
}

@Composable
private fun AppLibraryCategoryCard(
    category: AppCategory,
    apps: List<InstalledApp>,
    onLaunchApp: (String) -> Unit,
    onOpenFolder: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .testTag("app_library_folder_${category.name.lowercase()}")
            .clickable { onOpenFolder() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(152.dp)
                .clip(RoundedCornerShape(26.dp))
                .background(Color.White.copy(alpha = 0.76f))
                .border(1.dp, Color.White.copy(alpha = 0.85f), RoundedCornerShape(26.dp))
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MiniFolderSlot(apps.getOrNull(0), onLaunchApp, Modifier.weight(1f))
                    MiniFolderSlot(apps.getOrNull(1), onLaunchApp, Modifier.weight(1f))
                }
                Row(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MiniFolderSlot(apps.getOrNull(2), onLaunchApp, Modifier.weight(1f))
                    if (apps.size > 4) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                                .clip(RoundedCornerShape(12.dp))
                                .background(category.accent.copy(alpha = 0.14f))
                                .clickable { onOpenFolder() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "+${apps.size - 3}",
                                fontWeight = FontWeight.Bold,
                                color = category.accent,
                                fontSize = 14.sp
                            )
                        }
                    } else {
                        MiniFolderSlot(apps.getOrNull(3), onLaunchApp, Modifier.weight(1f))
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = category.title,
            style = MaterialTheme.typography.labelMedium,
            color = IosLabelPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = "${apps.size} apps",
            style = MaterialTheme.typography.labelSmall,
            color = IosLabelSecondary
        )
    }
}

@Composable
private fun MiniFolderSlot(
    app: InstalledApp?,
    onLaunchApp: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (app == null) {
        Box(modifier = modifier.fillMaxSize())
        return
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .clickable { onLaunchApp(app.packageName) },
        contentAlignment = Alignment.Center
    ) {
        if (app.icon != null) {
            Image(
                bitmap = app.icon,
                contentDescription = app.label,
                modifier = Modifier.fillMaxSize().padding(3.dp).clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )
        } else {
            Text(app.label.take(2), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = IosBlue)
        }
    }
}
