package wallapp.pixel.view

import androidx.compose.runtime.Immutable
import kotlin.experimental.ExperimentalObjCName
import kotlin.native.ObjCName

@Immutable
@OptIn(ExperimentalObjCName::class)
@ObjCName("CommonView")
data class View(
    val viewState: ViewState,
    val viewSpec: ViewSpec?,
    /**
     * This flag is no longer used by Compose, but remains in use by iOS.
     */
    @Deprecated("Use `render.viewAlignmentMapper.map(view)` instead")
    val applyScrollParallax: Boolean = false,
) {
    constructor(viewState: ViewState) : this(viewState, null)

}
