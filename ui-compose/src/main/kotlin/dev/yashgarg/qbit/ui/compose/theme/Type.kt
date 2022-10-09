package dev.yashgarg.qbit.ui.compose.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import dev.yashgarg.qbit.ui.compose.R

val SpaceGrotesk =
    FontFamily(
        Font(R.font.spacegrotesk_regular, FontWeight.Normal),
        Font(R.font.spacegrotesk_medium, FontWeight.Medium),
        Font(R.font.spacegrotesk_semibold, FontWeight.SemiBold),
        Font(R.font.spacegrotesk_bold, FontWeight.Bold),
    )

val AppTypography =
    Typography(
        titleLarge = TextStyle(fontFamily = SpaceGrotesk, color = Color.White, fontSize = 25.sp),
        bodyMedium =
            TextStyle(
                fontFamily = SpaceGrotesk,
                color = Color.White,
                fontSize = 14.sp,
                letterSpacing = 0.3.sp
            ),
    )

val bodyMediumPrimary = AppTypography.bodyMedium.copy(color = md_theme_dark_primary)
