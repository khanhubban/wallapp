package wallapp.viewmodel

import kotlin.reflect.KClass

class ViewModelFactoryPlatformIos : ViewModelFactoryPlatform {

    override fun <T : ViewModel> create(modelClass: KClass<T>, extra: Any?): T? {
        return null
    }
}