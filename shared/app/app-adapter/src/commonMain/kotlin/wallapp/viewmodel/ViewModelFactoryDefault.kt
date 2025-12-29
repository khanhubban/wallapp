package wallapp.viewmodel

import kotlin.reflect.KClass

class ViewModelFactoryDefault(
    private val factoryCommon: ViewModelFactoryCommon,
    private val factoryPlatform: ViewModelFactoryPlatform,
) : ViewModelFactory {

    override fun <T : ViewModel> create(
        modelClass: KClass<T>,
        extra: Any?,
        onClearedCallback: (() -> Unit)?
    ): T {
        return factoryPlatform.create(modelClass, extra)
            ?: factoryCommon.create(modelClass, extra, onClearedCallback)
    }
}