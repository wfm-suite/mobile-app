package org.worklog.app.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import worklog.composeapp.generated.resources.Res
import worklog.composeapp.generated.resources.poppins_bold
import worklog.composeapp.generated.resources.poppins_medium
import worklog.composeapp.generated.resources.poppins_regular
import worklog.composeapp.generated.resources.poppins_semibold

@Composable
fun poppinsFontFamily() = FontFamily(
    Font(Res.font.poppins_regular, weight = FontWeight.Normal),
    Font(Res.font.poppins_medium, weight = FontWeight.Medium),
    Font(Res.font.poppins_semibold, weight = FontWeight.SemiBold),
    Font(Res.font.poppins_bold, weight = FontWeight.Bold)
)

@Composable
fun appTypography(): Typography {
    val fontFamily = poppinsFontFamily()
    return Typography().applyFontFamily(fontFamily)
}

private fun Typography.applyFontFamily(fontFamily: FontFamily) = copy(
    displayLarge = displayLarge.copy(fontFamily = fontFamily),
    displayMedium = displayMedium.copy(fontFamily = fontFamily),
    displaySmall = displaySmall.copy(fontFamily = fontFamily),
    headlineLarge = headlineLarge.copy(fontFamily = fontFamily),
    headlineMedium = headlineMedium.copy(
        fontFamily = fontFamily,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        fontWeight = FontWeight.W700
    ),
    headlineSmall = headlineSmall.copy(
        fontFamily = fontFamily,
        fontSize = 20.sp,
        fontWeight = FontWeight.W700
    ),
    titleLarge = titleLarge.copy(
        fontFamily = fontFamily,
        fontWeight = FontWeight.W700,
        fontSize = 20.sp,
        lineHeight = 28.sp
    ),
    titleMedium = titleMedium.copy(
        fontFamily = fontFamily,
        fontWeight = FontWeight.W700,
        fontSize = 16.sp
    ),
    titleSmall = titleSmall.copy(fontFamily = fontFamily),
    bodyLarge = bodyLarge.copy(
        fontFamily = fontFamily,
        fontWeight = FontWeight.W400,
    ),
    bodyMedium = bodyMedium.copy(fontFamily = fontFamily),
    bodySmall = bodySmall.copy(fontFamily = fontFamily),
    labelLarge = labelLarge.copy(fontFamily = fontFamily),
    labelMedium = labelMedium.copy(fontFamily = fontFamily),
    labelSmall = labelSmall.copy(
        fontFamily = fontFamily,
        fontWeight = FontWeight.W400
    )
)