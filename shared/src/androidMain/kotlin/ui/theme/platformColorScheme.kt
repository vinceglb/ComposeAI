package ui.theme

import android.os.Build.VERSION.SDK_INT
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun platformColorScheme(darkTheme: Boolean): ColorScheme {
    val context = LocalContext.current

    println("platformColorScheme: SDK_INT=$SDK_INT, darkTheme=$darkTheme")

    return if (SDK_INT >= 31) {
        when {
            darkTheme -> dynamicDarkColorScheme(context)
            else -> dynamicLightColorScheme(context)
        }
    } else {
        when {
            darkTheme -> darkColorScheme()
            else -> lightColorScheme()
        }
    }
}
