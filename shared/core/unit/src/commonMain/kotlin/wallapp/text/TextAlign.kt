package wallapp.text

import co.touchlab.skie.configuration.annotations.SealedInterop

@SealedInterop.Enabled
sealed class TextAlign {

    data object Center : TextAlign()
    data object Start : TextAlign()
    data object End : TextAlign()
    data object Justify : TextAlign()
}
