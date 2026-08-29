package org.pangwali.preservation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = TextPureWhite,
    onPrimary = InkBlack,
    background = InkBlack,
    surface = CharcoalSurface,
    surfaceVariant = CharcoalBorder,
    onBackground = TextPureWhite,
    onSurface = TextPureWhite,
    onSurfaceVariant = TextMutedGray
)

private val LightColorScheme = lightColorScheme(
    primary = TextInk,
    onPrimary = PureWhite,
    background = PaperWhite,
    surface = PureWhite,
    surfaceVariant = HairlineBorder,
    onBackground = TextInk,
    onSurface = TextInk,
    onSurfaceVariant = TextSubtle
)

@Composable
fun PangwaliMinimalTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
