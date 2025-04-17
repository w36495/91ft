package com.w36495.senty.view.screen.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import com.w36495.senty.view.ui.theme.LocalSentyTypography
import com.w36495.senty.view.ui.theme.Pink80
import com.w36495.senty.view.ui.theme.Purple80
import com.w36495.senty.view.ui.theme.PurpleGrey80
import com.w36495.senty.view.ui.theme.SentyBlack
import com.w36495.senty.view.ui.theme.SentyGray30
import com.w36495.senty.view.ui.theme.SentyGray60
import com.w36495.senty.view.ui.theme.SentyGreen60
import com.w36495.senty.view.ui.theme.SentyTypography
import com.w36495.senty.view.ui.theme.SentyWhite
import com.w36495.senty.view.ui.theme.SentyYellow60
import com.w36495.senty.view.ui.theme.Typography

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = SentyGreen60,
    onPrimary = SentyWhite,

    secondary = SentyGray60,
    onSecondary = SentyWhite,

    tertiary = SentyYellow60, // 핑크 계열 포인트 컬러 예시
    onTertiary = SentyBlack,

    background = SentyWhite,
    onBackground = SentyBlack,

    surface = SentyWhite,
    onSurface = SentyBlack,

    outline = SentyGray30,
)

@Composable
fun SentyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.navigationBarColor = SentyWhite.toArgb()
        }
    }

    CompositionLocalProvider(LocalSentyTypography provides Typography) {
        MaterialTheme(
            colorScheme = LightColorScheme,
            content = content
        )
    }
}

object SentyTheme {
    val typography: SentyTypography
        @Composable get() = LocalSentyTypography.current
}