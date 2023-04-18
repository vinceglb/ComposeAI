package ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.myapplication.common.R

actual suspend fun outfitFontFamily(): FontFamily {
    return FontFamily(
        Font(
            resId = R.font.outfit_thin,
            weight = FontWeight.Thin,
        ),
        Font(
            resId = R.font.outfit_extralight,
            weight = FontWeight.ExtraLight,
        ),
        Font(
            resId = R.font.outfit_light,
            weight = FontWeight.Light,
        ),
        Font(
            resId = R.font.outfit_regular,
            weight = FontWeight.Normal,
        ),
        Font(
            resId = R.font.outfit_medium,
            weight = FontWeight.Medium,
        ),
        Font(
            resId = R.font.outfit_semibold,
            weight = FontWeight.SemiBold,
        ),
        Font(
            resId = R.font.outfit_bold,
            weight = FontWeight.Bold,
        ),
        Font(
            resId = R.font.outfit_extrabold,
            weight = FontWeight.ExtraBold,
        ),
        Font(
            resId = R.font.outfit_black,
            weight = FontWeight.Black,
        ),
    )
}