package wallapp.bottomsheet

import wallapp.screen.ScreenArgument

object BottomSheetViewStateProviderManagerNoOp : BottomSheetViewStateProviderManager {

    override fun register(providerFactory: BottomSheetViewStateProviderFactory) {}

    override fun unregister(providerFactory: BottomSheetViewStateProviderFactory) {}

    override fun createBottomSheetScreenViewStateProvider(
        screenArgument: ScreenArgument?,
    ): BottomSheetViewStateProvider? = null
}