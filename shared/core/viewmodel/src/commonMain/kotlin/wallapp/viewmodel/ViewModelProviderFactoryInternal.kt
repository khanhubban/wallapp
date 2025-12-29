package wallapp.viewmodel

import kotlin.reflect.KClass

interface ViewModelProviderFactoryInternal {

    fun <T : ViewModel> create(modelClass: KClass<T>): T = create(modelClass, null)

    fun <T : ViewModel> create(modelClass: KClass<T>, extras: Any?): T
}