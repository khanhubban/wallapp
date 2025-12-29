package wallapp.bottomsheet

import wallapp.collection.MutableStack
import wallapp.log.Logger
import wallapp.screen.ScreenArgument

class BottomSheetViewStateProviderManagerDefault : BottomSheetViewStateProviderManager {

    companion object {
        val Log = Logger("BottomSheetViewStateProviderManager")
    }

    private val factories = MutableStack<BottomSheetViewStateProviderFactory>()

    override fun register(providerFactory: BottomSheetViewStateProviderFactory) {
        Log.d("Registering BottomSheetViewStateProviderFactory: $providerFactory")
        require(!factories.contains(providerFactory)) {
            "BottomSheetViewStateProviderFactory already registered: $providerFactory"
        }
        factories.push(providerFactory)
        Log.d("Registered BottomSheetViewStateProviderFactory: $providerFactory")
    }

    override fun unregister(providerFactory: BottomSheetViewStateProviderFactory) {
        Log.d("Unregistering BottomSheetViewStateProviderFactory: $providerFactory")
        if (!factories.contains(providerFactory)) {
            Log.w("BottomSheetViewStateProviderFactory not registered: $providerFactory")
            return
        }
        val last = factories.lastOrNull()
        factories.remove(providerFactory)
        // ViewModel destroy order is not guaranteed, so this is not necessarily an error.
        if (last != providerFactory) {
            Log.w("Unregistering *non-last* BottomSheetViewStateProviderFactory: $providerFactory")
        } else {
            Log.d("Unregistered BottomSheetViewStateProviderFactory: $providerFactory")
        }
    }

    override fun createBottomSheetScreenViewStateProvider(
        screenArgument: ScreenArgument?,
    ): BottomSheetViewStateProvider? {
        Log.d("Creating BottomSheetViewStateProvider for $screenArgument, last factory: ${factories.lastOrNull()}")
        return factories
            .lastOrNull()
            ?.createBottomSheetScreenViewStateProvider(screenArgument)
            ?.also {
                Log.d("Created BottomSheetViewStateProvider: $it")
            }
    }
}