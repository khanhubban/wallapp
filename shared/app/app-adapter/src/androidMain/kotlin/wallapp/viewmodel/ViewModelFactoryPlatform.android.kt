package wallapp.viewmodel

import kotlin.reflect.KClass

class ViewModelFactoryPlatformAndroid : ViewModelFactoryPlatform {

    override fun <T : ViewModel> create(modelClass: KClass<T>, extra: Any?): T? {
        return null
    }
}