package wallapp.pixel.text

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import co.touchlab.skie.configuration.annotations.SealedInterop
import wallapp.font.FontWeight
import wallapp.font.TextStyle
import wallapp.text.TextAlign
import wallapp.theme.ColorToken
import kotlin.experimental.ExperimentalObjCName
import kotlin.native.ObjCName

@Immutable
@OptIn(ExperimentalObjCName::class)
@SealedInterop.Enabled
@ObjCName("StyledText")
sealed class Text {

    abstract val string: String
    abstract val colorToken: ColorToken?
    abstract val maxLines: Int?
    abstract val textAlign: TextAlign?
    abstract val style: TextStyle
    abstract val fontWeight: FontWeight?

    @Immutable
    data class Fixed internal constructor(
        override val string: String,
        override val style: TextStyle,
        override val fontWeight: FontWeight? = null,
        /**
         * If true, the text will adjust its size to effectively ignore any scaling applied by the
         * system per the user's font size preference. This is to be used for UI elements that
         * just won't look right if the text size is scaled.
         */
        val ignoreLargeSystemFontScaling: Boolean,
        override val colorToken: ColorToken? = null,
        override val textAlign: TextAlign? = TextAlign.Center,
        override val maxLines: Int? = null,
        val marqueeSpacing: Dp? = null,
        val animateMarquee: Boolean = true,
    ): Text() {
        val useMarquee: Boolean
            get() = marqueeSpacing != null
    }

    @Immutable
    data class AutoSize internal constructor(
        override val string: String,
        override val style: TextStyle,
        val fontSizeRange: FontSizeRange,
        override val fontWeight: FontWeight? = null,
        override val colorToken: ColorToken? = null,
        override val textAlign: TextAlign? = TextAlign.Center,
        override val maxLines: Int? = null,
    ): Text()

    companion object {

        fun createPreset(string: String): Text {
            return Fixed(
                string = string,
                style = TextStyle.Body,
                ignoreLargeSystemFontScaling = false,
            )
        }

        val String.presetText: Text
            get() = createPreset(this)

        var showDebug: Boolean = false
    }
}

