package wallapp.viewmodel

import kotlin.reflect.KClass

interface ViewModelFactory {
    fun <T : ViewModel> create(
        modelClass: KClass<T>,
        extra: Any? = null,
        onClearedCallback: (() -> Unit)? = null
    ): T
}