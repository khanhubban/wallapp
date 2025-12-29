package wallapp.bottomsheet

import wallapp.screen.ScreenArgument

/**
 * A factory that creates a [BottomSheetViewStateProvider] for a given [ScreenArgument]. An example
 * might be a `MyScreenViewModel` that generates a `MyBottomSheetViewModel`.
 */
interface BottomSheetViewStateProviderFactory {

    fun createBottomSheetScreenViewStateProvider(screenArgument: ScreenArgument?): BottomSheetViewStateProvider
}