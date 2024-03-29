package ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import composeai.shared.generated.resources.Res
import composeai.shared.generated.resources.outfit_black
import composeai.shared.generated.resources.outfit_bold
import composeai.shared.generated.resources.outfit_extrabold
import composeai.shared.generated.resources.outfit_extralight
import composeai.shared.generated.resources.outfit_light
import composeai.shared.generated.resources.outfit_medium
import composeai.shared.generated.resources.outfit_regular
import composeai.shared.generated.resources.outfit_semibold
import composeai.shared.generated.resources.outfit_thin
import org.jetbrains.compose.resources.Font

@Composable
private fun outfitFontFamily(): FontFamily = FontFamily(
    Font(
        resource = Res.font.outfit_thin,
        weight = FontWeight.Thin,
    ),
    Font(
        resource = Res.font.outfit_extralight,
        weight = FontWeight.ExtraLight,
    ),
    Font(
        resource = Res.font.outfit_light,
        weight = FontWeight.Light,
    ),
    Font(
        resource = Res.font.outfit_regular,
        weight = FontWeight.Normal,
    ),
    Font(
        resource = Res.font.outfit_medium,
        weight = FontWeight.Medium,
    ),
    Font(
        resource = Res.font.outfit_semibold,
        weight = FontWeight.SemiBold,
    ),
    Font(
        resource = Res.font.outfit_bold,
        weight = FontWeight.Bold,
    ),
    Font(
        resource = Res.font.outfit_extrabold,
        weight = FontWeight.ExtraBold,
    ),
    Font(
        resource = Res.font.outfit_black,
        weight = FontWeight.Black,
    ),
)

@Composable
fun appTypography(defaultTypography: Typography): Typography {
    val outfitFamily = outfitFontFamily()

    return Typography(
        bodyLarge = defaultTypography.bodyLarge.copy(
            fontFamily = outfitFamily,
            fontWeight = FontWeight.Light,
            letterSpacing = (-0.03).sp
        ),
        bodyMedium = defaultTypography.bodyMedium.copy(fontFamily = outfitFamily),
        bodySmall = defaultTypography.bodySmall.copy(fontFamily = outfitFamily),
        labelLarge = defaultTypography.labelLarge.copy(fontFamily = outfitFamily),
        labelMedium = defaultTypography.labelMedium.copy(fontFamily = outfitFamily),
        labelSmall = defaultTypography.labelSmall.copy(fontFamily = outfitFamily),
        displayLarge = defaultTypography.displayLarge.copy(fontFamily = outfitFamily),
        displayMedium = defaultTypography.displayMedium.copy(fontFamily = outfitFamily),
        displaySmall = defaultTypography.displaySmall.copy(fontFamily = outfitFamily),
        headlineLarge = defaultTypography.headlineLarge.copy(
            fontFamily = outfitFamily,
            fontWeight = FontWeight.Medium,
            letterSpacing = (-1).sp
        ),
        headlineMedium = defaultTypography.headlineMedium.copy(
            fontFamily = outfitFamily,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = (-1).sp
        ),
        headlineSmall = defaultTypography.headlineSmall.copy(
            fontFamily = outfitFamily,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = (-1).sp
        ),
        titleLarge = defaultTypography.titleLarge.copy(
            fontFamily = outfitFamily,
            fontWeight = FontWeight.SemiBold
        ),
        titleMedium = defaultTypography.titleMedium.copy(fontFamily = outfitFamily),
        titleSmall = defaultTypography.titleSmall.copy(fontFamily = outfitFamily),
    )
}
