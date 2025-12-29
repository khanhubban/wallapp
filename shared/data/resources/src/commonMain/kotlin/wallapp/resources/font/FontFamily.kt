package wallapp.resources.font

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight

object FontFamily {

    @Composable
    fun MontserratFontFamily(): FontFamily =
        FontFamily(
//                font(
//                    fontName = "Montserrat",
//                    resourceId = "montserrat_italic",
//                    weight = FontWeight.Normal,
//                    style = FontStyle.Italic,
//                ),

            // Note use of SemiBold for regular
            font(
                fontName = "Montserrat",
                resourceId = "montserrat_medium",
                weight = FontWeight.Normal,
                style = FontStyle.Normal,
            ),

            font(
                fontName = "Montserrat",
                resourceId = "montserrat_bold",
                weight = FontWeight.Bold,
                style = FontStyle.Normal,
            ),
        )
}