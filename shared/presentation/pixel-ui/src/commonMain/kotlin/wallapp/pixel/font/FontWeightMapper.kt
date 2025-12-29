package wallapp.pixel.font

import wallapp.font.FontWeight
import androidx.compose.ui.text.font.FontWeight as MaterialFontWeight

object FontWeightMapper {

    fun map(fontWeight: FontWeight): MaterialFontWeight {
        return when (fontWeight) {
            FontWeight.Thin -> MaterialFontWeight.Thin
            FontWeight.ExtraLight -> MaterialFontWeight.ExtraLight
            FontWeight.Light -> MaterialFontWeight.Light
            FontWeight.Normal -> MaterialFontWeight.Normal
            FontWeight.Medium -> MaterialFontWeight.Medium
            FontWeight.SemiBold -> MaterialFontWeight.SemiBold
            FontWeight.Bold -> MaterialFontWeight.Bold
            FontWeight.ExtraBold -> MaterialFontWeight.ExtraBold
            FontWeight.Black -> MaterialFontWeight.Black
        }
    }
}