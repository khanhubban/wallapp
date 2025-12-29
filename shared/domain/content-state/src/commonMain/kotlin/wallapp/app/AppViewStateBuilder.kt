package wallapp.app

import wallapp.content.state.index.IndexViewState
import wallapp.pixel.alert.AlertViewState
import wallapp.pixel.bottomsheet.BottomSheetViewState
import wallapp.pixel.bottomsheet.ModalBottomSheetViewState
import wallapp.pixel.globaloverlay.GlobalOverlayViewState
import wallapp.pixel.screen.ScreenViewState

object AppViewStateBuilder {

    fun build(
        screenViewStates: List<ScreenViewState>,
        bottomSheetViewState: BottomSheetViewState?,
        modalBottomSheetViewState: ModalBottomSheetViewState?,
        homeScreenViewState: ScreenViewState,
        overlayScreenViewState: ScreenViewState?,
        alertViewState: AlertViewState?,
        globalOverlayViewState: GlobalOverlayViewState,
        onBack: () -> Unit,
    ): AppViewState {
        val topmostScreen = screenViewStates.last()
        val showingOverlay = overlayScreenViewState != null
        val showingIndexView = screenViewStates.size == 1
                && screenViewStates.first() is IndexViewState
        return if (bottomSheetViewState != null) {
            AppViewState(
                screenViewState = topmostScreen,
                bottomSheetViewState = bottomSheetViewState,
                modalBottomSheetViewState = modalBottomSheetViewState,
                alertViewState = alertViewState,
                backHandlerEnabled = true,
                globalOverlayViewState = globalOverlayViewState,
                onBack = onBack,
            )
        } else if (showingOverlay) {
            AppViewState(
                screenViewState = homeScreenViewState,
                bottomSheetViewState = null,
                modalBottomSheetViewState = modalBottomSheetViewState,
                alertViewState = alertViewState,
                backHandlerEnabled = true,
                globalOverlayViewState = globalOverlayViewState,
                onBack = onBack,
            )
        } else if (showingIndexView) {
            AppViewState(
                screenViewState = homeScreenViewState,
                bottomSheetViewState = null,
                modalBottomSheetViewState = modalBottomSheetViewState,
                alertViewState = alertViewState,
                backHandlerEnabled = false,
                globalOverlayViewState = globalOverlayViewState,
                onBack = {},
            )
        } else {
            AppViewState(
                screenViewState = screenViewStates.last(),
                bottomSheetViewState = null,
                modalBottomSheetViewState = modalBottomSheetViewState,
                alertViewState = alertViewState,
                backHandlerEnabled = true,
                globalOverlayViewState = globalOverlayViewState,
                onBack = onBack,
            )
        }
    }

    fun buildDefault(
        homeScreenViewState: ScreenViewState,
    ): AppViewState = build(
        screenViewStates = listOf(homeScreenViewState),
        homeScreenViewState = homeScreenViewState,
        bottomSheetViewState = null,
        modalBottomSheetViewState = null,
        overlayScreenViewState = null,
        alertViewState = null,
        globalOverlayViewState = GlobalOverlayViewState.None,
        onBack = {},
    )
}