package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import com.example.data.LauncherSettings
import com.example.data.WallpaperPreset

@Composable
fun WallpaperBackground(
    settings: LauncherSettings,
    scrimAlpha: Float = 0.08f,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        val customUri = settings.customWallpaperUri
        if (settings.wallpaperPreset == WallpaperPreset.CUSTOM && !customUri.isNullOrBlank()) {
            AsyncImage(
                model = customUri,
                contentDescription = "Custom wallpaper",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            val resId = settings.wallpaperPreset.drawableRes ?: WallpaperPreset.AURORA.drawableRes!!
            Image(
                painter = painterResource(id = resId),
                contentDescription = settings.wallpaperPreset.displayName,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        if (scrimAlpha > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = scrimAlpha * 0.6f),
                                Color.Transparent,
                                Color.Black.copy(alpha = scrimAlpha)
                            )
                        )
                    )
            )
        }
    }
}
