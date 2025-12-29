package wallapp.bottomsheet

import wallapp.screen.ScreenArgument

/**
 *
 */
interface BottomSheetViewStateProviderManager {

    /**
     *
     */
    fun register(providerFactory: BottomSheetViewStateProviderFactory)
    fun unregister(providerFactory: BottomSheetViewStateProviderFactory)

    fun createBottomSheetScreenViewStateProvider(
        screenArgument: ScreenArgument?,
    ): BottomSheetViewStateProvider?
}