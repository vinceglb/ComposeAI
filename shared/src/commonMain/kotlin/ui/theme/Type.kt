package ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

expect suspend fun outfitFontFamily(): FontFamily

suspend fun appTypography(defaultTypography: Typography): Typography {
    val outfitFamily = outfitFontFamily()

    return Typography(
        bodyLarge = defaultTypography.bodyLarge.copy(fontFamily = outfitFamily, fontWeight = FontWeight.Light, letterSpacing = (-0.1).sp),
        bodyMedium = defaultTypography.bodyMedium.copy(fontFamily = outfitFamily),
        bodySmall = defaultTypography.bodySmall.copy(fontFamily = outfitFamily),
        labelLarge = defaultTypography.labelLarge.copy(fontFamily = outfitFamily),
        labelMedium = defaultTypography.labelMedium.copy(fontFamily = outfitFamily),
        labelSmall = defaultTypography.labelSmall.copy(fontFamily = outfitFamily),
        displayLarge = defaultTypography.displayLarge.copy(fontFamily = outfitFamily),
        displayMedium = defaultTypography.displayMedium.copy(fontFamily = outfitFamily),
        displaySmall = defaultTypography.displaySmall.copy(fontFamily = outfitFamily),
        headlineLarge = defaultTypography.headlineLarge.copy(fontFamily = outfitFamily, fontWeight = FontWeight.Medium, letterSpacing = (-1).sp),
        headlineMedium = defaultTypography.headlineMedium.copy(fontFamily = outfitFamily, fontWeight = FontWeight.SemiBold, letterSpacing = (-1).sp),
        headlineSmall = defaultTypography.headlineSmall.copy(fontFamily = outfitFamily, fontWeight = FontWeight.SemiBold, letterSpacing = (-1).sp),
        titleLarge = defaultTypography.titleLarge.copy(fontFamily = outfitFamily, fontWeight = FontWeight.SemiBold),
        titleMedium = defaultTypography.titleMedium.copy(fontFamily = outfitFamily),
        titleSmall = defaultTypography.titleSmall.copy(fontFamily = outfitFamily),
    )
}