package com.example.kaijuinventory.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val ColoresClaros = lightColorScheme(
    primary = Verde,
    primaryContainer = VerdeClaro,
    onPrimaryContainer = VerdeOscuro,
    secondary = Musgo,
    secondaryContainer = MusgoClaro,
    tertiary = Naranjo,
    tertiaryContainer = NaranjoClaro,
    onTertiaryContainer = NaranjoOscuro
)

private val ColoresOscuros = darkColorScheme(
    primary = VerdeNoche,
    primaryContainer = Verde,
    onPrimaryContainer = VerdeClaro,
    secondary = MusgoNoche,
    secondaryContainer = MusgoNocheContenedor,
    tertiary = NaranjoNoche,
    tertiaryContainer = NaranjoNocheContenedor,
    onTertiaryContainer = NaranjoClaro
)

@Composable
fun KaijuInventoryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) ColoresOscuros else ColoresClaros,
        typography = Typography,
        content = content
    )
}
