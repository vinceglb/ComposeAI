package ui.theme

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font
import org.jetbrains.compose.resources.resource

actual suspend fun outfitFontFamily(): FontFamily = FontFamily(
    Font(
        identity = "Outfit-Thin",
        data = resource("font/outfit_thin.ttf").readBytes(),
        weight = FontWeight.Thin,
    ),
    Font(
        identity = "Outfit-ExtraLight",
        data = resource("font/outfit_extralight.ttf").readBytes(),
        weight = FontWeight.ExtraLight,
    ),
    Font(
        identity = "Outfit-Light",
        data = resource("font/outfit_light.ttf").readBytes(),
        weight = FontWeight.Light,
    ),
    Font(
        identity = "Outfit-Regular",
        data = resource("font/outfit_regular.ttf").readBytes(),
        weight = FontWeight.Normal,
    ),
    Font(
        identity = "Outfit-Medium",
        data = resource("font/outfit_medium.ttf").readBytes(),
        weight = FontWeight.Medium,
    ),
    Font(
        identity = "Outfit-SemiBold",
        data = resource("font/outfit_semibold.ttf").readBytes(),
        weight = FontWeight.SemiBold,
    ),
    Font(
        identity = "Outfit-Bold",
        data = resource("font/outfit_bold.ttf").readBytes(),
        weight = FontWeight.Bold,
    ),
    Font(
        identity = "Outfit-ExtraBold",
        data = resource("font/outfit_extrabold.ttf").readBytes(),
        weight = FontWeight.ExtraBold,
    ),
    Font(
        identity = "Outfit-Black",
        data = resource("font/outfit_black.ttf").readBytes(),
        weight = FontWeight.Black,
    ),
)