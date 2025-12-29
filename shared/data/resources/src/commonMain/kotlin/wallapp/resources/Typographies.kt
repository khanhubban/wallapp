package wallapp.resources

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import wallapp.resources.font.FontFamily.MontserratFontFamily


object Typographies {

    @Composable
    fun Montserrat(): Typography {
        val defaultTypography = Typography()
        val fontFamily = MontserratFontFamily()

        // Use an obviously excessive size to identify if a non-supported typography is being
        // used.
        val fontStyleInvalid = defaultTypography.displayLarge.copy(
            fontSize = 99.sp,
        )

        return Typography(
            labelSmall = fontStyleInvalid,
            labelMedium = fontStyleInvalid,
            labelLarge = fontStyleInvalid,

            /**
             * [wallapp.font.TextStyle.Caption]:
             */
            bodySmall = defaultTypography.bodySmall.copy(
                fontFamily = fontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                lineHeight = 16.0.sp,
                letterSpacing = 0.4.sp,
            ),

            /**
             * [wallapp.font.TextStyle.Body]:
             */
            bodyMedium = defaultTypography.bodyMedium.copy(
                fontFamily = fontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                letterSpacing = 0.2.sp
            ),

            bodyLarge = fontStyleInvalid,

            /**
             * [wallapp.font.TextStyle.Subheading]:
             * Note: is a copy of [bodyLarge], but with a different font weight.
             */
            titleSmall = defaultTypography.headlineSmall.copy(
                fontFamily = fontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                lineHeight = 20.sp,
                letterSpacing = 0.sp,
            ),

            /**
             * [wallapp.font.TextStyle.SubheadingActive]:
             */
            titleMedium = defaultTypography.headlineSmall.copy(
                fontFamily = fontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                lineHeight = 20.sp,
                letterSpacing = 0.sp,
            ),

            /**
             * [wallapp.font.TextStyle.Headline]:
             */
            titleLarge = defaultTypography.titleLarge.copy(
                fontFamily = fontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                lineHeight = 28.sp,
                letterSpacing = 0.sp,
            ),

            /**
             * [wallapp.font.TextStyle.CallToAction]:
             */
            headlineSmall = defaultTypography.headlineSmall.copy(
                fontFamily = fontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                lineHeight = 24.sp,
                letterSpacing = (-0.1).sp,
            ),

            headlineMedium = fontStyleInvalid,

            /**
             * [wallapp.font.TextStyle.Display]:
             */
            headlineLarge = defaultTypography.headlineLarge.copy(
                fontFamily = fontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                lineHeight = 36.sp,
                letterSpacing = (-0.2).sp,
            ),

            displaySmall = fontStyleInvalid,
            displayMedium = fontStyleInvalid,
            displayLarge = fontStyleInvalid,
        )
    }

    // Used by 3rd party libraries that do not support WallApp' custom typography.
    @Composable
    fun Montserrat3rdParty() : Typography {
        val defaultTypography = Typography()
        val fontFamily = MontserratFontFamily()

        // Use an obviously excessive size to identify if a non-supported typography is being
        // used.
        val fontStyleInvalid = defaultTypography.displayLarge.copy(
            fontSize = 99.sp,
        )

        return Typography(
            labelSmall = defaultTypography.labelSmall.copy(
                fontFamily = fontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                lineHeight = 24.sp,
                letterSpacing = (-0.1).sp,
            ),
            labelMedium = fontStyleInvalid,
            labelLarge = defaultTypography.headlineLarge.copy(
                fontFamily = fontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                lineHeight = 36.sp,
                letterSpacing = (-0.2).sp,
            ),

            /**
             * [wallapp.font.TextStyle.Caption]:
             */
            bodySmall = defaultTypography.bodySmall.copy(
                fontFamily = fontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                lineHeight = 16.0.sp,
                letterSpacing = 0.4.sp,
            ),

            /**
             * [wallapp.font.TextStyle.Body]:
             */
            bodyMedium = defaultTypography.bodyMedium.copy(
                fontFamily = fontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                letterSpacing = 0.2.sp
            ),

            bodyLarge =  defaultTypography.bodyLarge.copy(
                fontFamily = fontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                letterSpacing = 0.2.sp
            ),

            /**
             * [wallapp.font.TextStyle.Subheading]:
             * Note: is a copy of [bodyLarge], but with a different font weight.
             */
            titleSmall = defaultTypography.headlineSmall.copy(
                fontFamily = fontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                lineHeight = 20.sp,
                letterSpacing = 0.sp,
            ),

            /**
             * [wallapp.font.TextStyle.SubheadingActive]:
             */
            titleMedium = defaultTypography.headlineSmall.copy(
                fontFamily = fontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                lineHeight = 20.sp,
                letterSpacing = 0.sp,
            ),

            /**
             * [wallapp.font.TextStyle.Headline]:
             */
            titleLarge = defaultTypography.titleLarge.copy(
                fontFamily = fontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                lineHeight = 28.sp,
                letterSpacing = 0.sp,
            ),

            /**
             * [wallapp.font.TextStyle.CallToAction]:
             */
            headlineSmall = defaultTypography.headlineSmall.copy(
                fontFamily = fontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                lineHeight = 24.sp,
                letterSpacing = (-0.1).sp,
            ),

            headlineMedium = fontStyleInvalid,

            /**
             * [wallapp.font.TextStyle.Display]:
             */
            headlineLarge = defaultTypography.headlineLarge.copy(
                fontFamily = fontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                lineHeight = 36.sp,
                letterSpacing = (-0.2).sp,
            ),

            displaySmall = defaultTypography.displaySmall.copy(
                fontFamily = fontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                lineHeight = 32.sp,
                letterSpacing = (-0.2).sp,
            ),
            displayMedium = fontStyleInvalid,
            displayLarge = fontStyleInvalid,
        )
    }

}