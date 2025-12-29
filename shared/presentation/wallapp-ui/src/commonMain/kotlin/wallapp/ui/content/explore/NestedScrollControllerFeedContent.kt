package wallapp.ui.content.explore

import wallapp.pixel.input.NestedScrollController

// The offset for feed content depends on the toolbar offset. This controller
// sets the content offset based on the toolbar offset so that the content is
// moved down when the toolbar is shown. Currently this is being used in the specific case
// of the Explore feed.
class NestedScrollControllerFeedContent(
    private val maxToolbarHeightPx: Float,
    private val actualToolbarHeightPx: Float,
    private val toolbarNestedScrollController: NestedScrollController
) : NestedScrollController {

    private var isOffsetEnabled = false

    override val currentYOffsetPx: Float
        get() = if (isOffsetEnabled) {
            ((maxToolbarHeightPx + toolbarNestedScrollController.currentYOffsetPx) / maxToolbarHeightPx) * actualToolbarHeightPx
        } else {
            0f
        }

    override fun hide() {}

    override fun show() {}

    fun enableOffset(enable: Boolean) {
        isOffsetEnabled = enable
    }
}