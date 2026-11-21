package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val TdbLightColorScheme = lightColorScheme(
    primary = IosBlue,
    onPrimary = Color.White,
    primaryContainer = IosBlueSoft,
    onPrimaryContainer = IosBlue,
    secondary = IosIndigo,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEEEEFC),
    onSecondaryContainer = IosIndigo,
    tertiary = IosTeal,
    onTertiary = Color.White,
    background = IosGroupedBackground,
    onBackground = IosLabelPrimary,
    surface = IosSurfaceCard,
    onSurface = IosLabelPrimary,
    surfaceVariant = IosSecondarySurface,
    onSurfaceVariant = IosLabelSecondary,
    outline = IosSeparator,
    error = IosRed,
    onError = Color.White
)

private val TdbShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun TdbIosTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = TdbLightColorScheme,
        typography = TdbTypography,
        shapes = TdbShapes,
        content = content
    )
}
