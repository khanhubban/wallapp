package wallapp.bottomsheet

import wallapp.pixel.screen.ScreenViewStateProvider
import wallapp.viewmodel.ViewModelExplicitLifecycle

/**
 * A [ScreenViewStateProvider] that provides a [ScreenViewState] that will be displayed on a bottom
 * sheet. Will typically be implemented by a [ViewModel].
 */
interface BottomSheetViewStateProvider :
    ScreenViewStateProvider,
    /**
     * The [ViewModel] is manually created, so require support for manual destruction.
     */
    ViewModelExplicitLifecycle
