package wallapp.viewmodel

import kotlin.reflect.KClass

interface ViewModelFactoryPlatform {
    fun <T : ViewModel> create(modelClass: KClass<T>, extra: Any? = null): T?
}