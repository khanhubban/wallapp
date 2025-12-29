package wallapp.app

import wallapp.pixel.alert.AlertViewState
import wallapp.pixel.bottomsheet.BottomSheetViewState
import wallapp.pixel.bottomsheet.ModalBottomSheetViewState
import wallapp.pixel.globaloverlay.GlobalOverlayViewState
import wallapp.pixel.screen.ScreenViewState


data class AppViewState(
    val screenViewState: ScreenViewState,
    val bottomSheetViewState: BottomSheetViewState?,
    val modalBottomSheetViewState: ModalBottomSheetViewState?,
    /**
     * The current alert dialog to show to the user. Will only be valid when Compose is used to
     * render the alert dialog.
     */
    val alertViewState: AlertViewState?,
    val globalOverlayViewState: GlobalOverlayViewState,
    val backHandlerEnabled: Boolean,
    val onBack: () -> Unit,
) {
    /**
     * true if either the bottom sheet or modal bottom sheet can be used.
     */
    val configureForBottomSheets: Boolean
        get() = bottomSheetViewState != null || modalBottomSheetViewState != null
}
