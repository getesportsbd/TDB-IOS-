package com.example.ui.screens

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.WallpaperPreset
import com.example.ui.LauncherUiState
import com.example.ui.components.WallpaperBackground
import com.example.ui.theme.IosBlue
import com.example.ui.theme.IosGreen
import com.example.ui.theme.IosGroupedBackground
import com.example.ui.theme.IosLabelPrimary
import com.example.ui.theme.IosLabelSecondary

@Composable
fun WallpaperStudioScreen(
    uiState: LauncherUiState,
    onSelectPreset: (WallpaperPreset) -> Unit,
    onCustomPhotoSelected: (String) -> Unit,
    onLightTextToggle: (Boolean) -> Unit,
    onBack: () -> Unit
) {
    BackHandler { onBack() }
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            runCatching {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            onCustomPhotoSelected(uri.toString())
        }
    }

    val presets = listOf(
        WallpaperPreset.AURORA,
        WallpaperPreset.OCEANIC,
        WallpaperPreset.BLOSSOM,
        WallpaperPreset.TITANIUM
    )

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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .testTag("wallpaper_back_button")
                        .clip(CircleShape)
                        .background(Color.White)
                ) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back", tint = IosBlue)
                }
                Column {
                    Text("Wallpaper & Style", style = MaterialTheme.typography.headlineMedium, color = IosLabelPrimary)
                    Text("Original light collection & Photo Picker", style = MaterialTheme.typography.labelMedium, color = IosLabelSecondary)
                }
            }

            // Live Mini Preview Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .border(2.dp, Color.White, RoundedCornerShape(28.dp))
            ) {
                WallpaperBackground(settings = uiState.settings, scrimAlpha = 0.08f)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "9:41",
                        style = MaterialTheme.typography.displayMedium,
                        color = if (uiState.settings.lightTextOverWallpaper) Color.White else IosLabelPrimary
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White.copy(alpha = 0.72f))
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = uiState.settings.wallpaperPreset.displayName,
                            fontWeight = FontWeight.SemiBold,
                            color = IosLabelPrimary
                        )
                        Text(
                            text = "Active",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = IosBlue
                        )
                    }
                }
            }

            // Bundled Original Wallpapers
            Text(
                text = "CURATED LIGHT WALLPAPERS",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = IosLabelSecondary
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(presets, key = { it.id }) { preset ->
                    val selected = uiState.settings.wallpaperPreset == preset
                    Column(
                        modifier = Modifier
                            .testTag("wallpaper_preset_${preset.id}")
                            .clickable { onSelectPreset(preset) },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(width = 112.dp, height = 182.dp)
                                .clip(RoundedCornerShape(22.dp))
                                .border(
                                    width = if (selected) 3.dp else 1.dp,
                                    color = if (selected) IosBlue else Color.White,
                                    shape = RoundedCornerShape(22.dp)
                                )
                        ) {
                            preset.drawableRes?.let { res ->
                                Image(
                                    painter = painterResource(id = res),
                                    contentDescription = preset.displayName,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            if (selected) {
                                Icon(
                                    imageVector = Icons.Rounded.CheckCircle,
                                    contentDescription = "Selected",
                                    tint = IosBlue,
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(8.dp)
                                        .size(22.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                )
                            }
                        }
                        Text(
                            text = preset.displayName,
                            style = MaterialTheme.typography.labelLarge,
                            color = if (selected) IosBlue else IosLabelPrimary
                        )
                    }
                }
            }

            // Custom Photo Picker Button (Zero Storage Permission)
            Button(
                onClick = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = IosBlue),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("btn_pick_custom_wallpaper")
            ) {
                Icon(Icons.Rounded.AddPhotoAlternate, contentDescription = null)
                Spacer(modifier = Modifier.size(8.dp))
                Text("Choose Photo from Library", fontWeight = FontWeight.SemiBold)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("White Icon & Clock Labels", style = MaterialTheme.typography.titleMedium, color = IosLabelPrimary)
                    Text("Recommended for darker custom photos", style = MaterialTheme.typography.labelMedium, color = IosLabelSecondary)
                }
                Switch(
                    checked = uiState.settings.lightTextOverWallpaper,
                    onCheckedChange = onLightTextToggle,
                    colors = SwitchDefaults.colors(checkedTrackColor = IosGreen)
                )
            }
        }
    }
}
