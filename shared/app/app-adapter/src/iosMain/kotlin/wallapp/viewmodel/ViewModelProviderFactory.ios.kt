package wallapp.viewmodel

import kotlin.reflect.KClass


class ViewModelProviderFactoryIos(
    private val viewModelFactory: ViewModelFactory,
) : ViewModelProviderFactory {

    override fun <T : ViewModel> create(modelClass: KClass<T>, extras: Any?): T {
        return viewModelFactory.create(modelClass, extras)
    }
}