package wallapp.pixel.button

import kotlin.experimental.ExperimentalObjCName
import kotlin.native.ObjCName

@OptIn(ExperimentalObjCName::class)
@ObjCName("CustomButtonStyle")
sealed class ButtonStyle {
    data object Default : ButtonStyle()

    data object Outline : ButtonStyle()
}
