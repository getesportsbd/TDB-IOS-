package com.example.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Chat
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BuiltInAppItem
import com.example.data.BuiltInAppType
import com.example.data.InstalledApp
import com.example.ui.theme.IosBlue
import com.example.ui.theme.IosGreen
import com.example.ui.theme.IosIndigo
import com.example.ui.theme.IosLabelPrimary
import com.example.ui.theme.IosOrange
import com.example.ui.theme.IosPink
import com.example.ui.theme.IosPurple
import com.example.ui.theme.IosRed
import com.example.ui.theme.IosTeal
import com.example.ui.theme.IosYellow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BuiltInSquircleAppIcon(
    item: BuiltInAppItem,
    showLabel: Boolean,
    lightLabelText: Boolean,
    hapticEnabled: Boolean,
    iconSize: Dp = 58.dp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.90f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "icon_press_scale"
    )
    val haptic = LocalHapticFeedback.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .testTag("builtin_app_${item.type.name.lowercase()}")
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                if (hapticEnabled) {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                }
                onClick()
            }
            .padding(vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(iconSize)
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(16.dp),
                    ambientColor = Color.Black.copy(alpha = 0.25f),
                    spotColor = Color.Black.copy(alpha = 0.25f)
                )
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(item.primaryColor, item.secondaryColor)
                    )
                )
                .border(
                    width = 0.7.dp,
                    color = Color.White.copy(alpha = 0.45f),
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            BuiltInGlyphArtwork(item.type)
        }

        if (showLabel) {
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = item.title,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = if (lightLabelText) Color.White else IosLabelPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun BuiltInGlyphArtwork(type: BuiltInAppType) {
    when (type) {
        BuiltInAppType.CALENDAR -> {
            val dayName = remember { SimpleDateFormat("EEE", Locale.getDefault()).format(Date()).uppercase() }
            val dayNum = remember { SimpleDateFormat("d", Locale.getDefault()).format(Date()) }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize().padding(4.dp)
            ) {
                Text(
                    text = dayName,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = IosRed,
                    lineHeight = 11.sp
                )
                Text(
                    text = dayNum,
                    fontSize = 23.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = IosLabelPrimary,
                    lineHeight = 25.sp
                )
            }
        }
        BuiltInAppType.PHOTOS -> {
            Canvas(modifier = Modifier.size(38.dp)) {
                val colors = listOf(
                    IosOrange, IosYellow, IosGreen, IosTeal,
                    IosBlue, IosIndigo, IosPurple, IosPink
                )
                val radius = size.minDimension * 0.23f
                val orbit = size.minDimension * 0.25f
                val center = Offset(size.width / 2f, size.height / 2f)
                colors.forEachIndexed { idx, color ->
                    val angle = Math.toRadians(idx * 45.0)
                    val cx = center.x + (kotlin.math.cos(angle) * orbit).toFloat()
                    val cy = center.y + (kotlin.math.sin(angle) * orbit).toFloat()
                    drawCircle(color = color.copy(alpha = 0.85f), radius = radius, center = Offset(cx, cy))
                }
            }
        }
        BuiltInAppType.NOTES -> {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                        .background(IosOrange)
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFFFFDF5)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Description,
                        contentDescription = "Notes",
                        tint = Color(0xFF8E8E93),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
        BuiltInAppType.CALCULATOR -> {
            Column(
                modifier = Modifier.fillMaxSize().padding(8.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF3A3A3C))
                )
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(modifier = Modifier.size(9.dp).clip(CircleShape).background(Color(0xFF636366)))
                    Box(modifier = Modifier.size(9.dp).clip(CircleShape).background(Color(0xFF636366)))
                    Box(modifier = Modifier.size(9.dp).clip(CircleShape).background(IosOrange))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(modifier = Modifier.size(9.dp).clip(CircleShape).background(Color(0xFF636366)))
                    Box(modifier = Modifier.size(9.dp).clip(CircleShape).background(Color(0xFF636366)))
                    Box(modifier = Modifier.size(9.dp).clip(CircleShape).background(IosOrange))
                }
            }
        }
        BuiltInAppType.SAFARI -> {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(Color(0xFF32ADE6), IosBlue))),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Explore,
                    contentDescription = "Browser",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
        BuiltInAppType.PHONE -> Icon(
            imageVector = Icons.Rounded.Phone,
            contentDescription = "Phone",
            tint = Color.White,
            modifier = Modifier.size(30.dp)
        )
        BuiltInAppType.MESSAGES -> Icon(
            imageVector = Icons.AutoMirrored.Rounded.Chat,
            contentDescription = "Messages",
            tint = Color.White,
            modifier = Modifier.size(29.dp)
        )
        BuiltInAppType.CAMERA -> Icon(
            imageVector = Icons.Rounded.CameraAlt,
            contentDescription = "Camera",
            tint = Color.White,
            modifier = Modifier.size(29.dp)
        )
        BuiltInAppType.WEATHER -> Icon(
            imageVector = Icons.Rounded.WbSunny,
            contentDescription = "Weather",
            tint = Color(0xFFFFE066),
            modifier = Modifier.size(31.dp)
        )
        BuiltInAppType.CLOCK -> Icon(
            imageVector = Icons.Rounded.Schedule,
            contentDescription = "Clock",
            tint = Color.White,
            modifier = Modifier.size(31.dp)
        )
        BuiltInAppType.SETTINGS -> Icon(
            imageVector = Icons.Rounded.Settings,
            contentDescription = "Settings",
            tint = Color.White,
            modifier = Modifier.size(30.dp)
        )
        BuiltInAppType.WALLPAPER -> Icon(
            imageVector = Icons.Rounded.Palette,
            contentDescription = "Wallpaper",
            tint = Color.White,
            modifier = Modifier.size(29.dp)
        )
        BuiltInAppType.LOCK_SCREEN -> Icon(
            imageVector = Icons.Rounded.Lock,
            contentDescription = "Lock Screen",
            tint = Color.White,
            modifier = Modifier.size(28.dp)
        )
        BuiltInAppType.APP_DRAWER -> Icon(
            imageVector = Icons.Rounded.Apps,
            contentDescription = "All Apps",
            tint = Color.White,
            modifier = Modifier.size(30.dp)
        )
        BuiltInAppType.APP_LIBRARY -> Icon(
            imageVector = Icons.Rounded.GridView,
            contentDescription = "App Library",
            tint = Color.White,
            modifier = Modifier.size(29.dp)
        )
    }
}

@Composable
fun InstalledSquircleAppIcon(
    app: InstalledApp,
    showLabel: Boolean,
    lightLabelText: Boolean,
    hapticEnabled: Boolean,
    iconSize: Dp = 58.dp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.90f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "installed_icon_scale"
    )
    val haptic = LocalHapticFeedback.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .testTag("installed_app_${app.packageName}")
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                if (hapticEnabled) {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                }
                onClick()
            }
            .padding(vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(iconSize)
                .shadow(
                    elevation = 7.dp,
                    shape = RoundedCornerShape(16.dp),
                    ambientColor = Color.Black.copy(alpha = 0.22f),
                    spotColor = Color.Black.copy(alpha = 0.22f)
                )
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White.copy(alpha = 0.92f))
                .border(
                    width = 0.6.dp,
                    color = Color.White.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (app.icon != null) {
                Image(
                    bitmap = app.icon,
                    contentDescription = app.label,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(6.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Rounded.Calculate,
                    contentDescription = app.label,
                    tint = IosBlue,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        if (showLabel) {
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = app.label,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = if (lightLabelText) Color.White else IosLabelPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}
