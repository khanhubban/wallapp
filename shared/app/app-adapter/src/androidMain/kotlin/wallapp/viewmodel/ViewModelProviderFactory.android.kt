package wallapp.viewmodel

import androidx.lifecycle.viewmodel.CreationExtras
import wallapp.app.mapToSingleExtra
import kotlin.reflect.KClass
import androidx.lifecycle.ViewModel as AndroidXViewModel


class ViewModelProviderFactoryAndroid(
    private val viewModelFactory: ViewModelFactory,
) : ViewModelProviderFactory {

    override fun <T : AndroidXViewModel> create(modelClass: Class<T>): T =
        create(modelClass, CreationExtras.Empty)

    @Suppress("UNCHECKED_CAST")
    override fun <T : AndroidXViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val kotlinClass = modelClass.kotlin
        val internalResult = viewModelFactory.create(kotlinClass as KClass<ViewModel>,
            extras.mapToSingleExtra)
        require(internalResult is AndroidXViewModel)
        return internalResult as T
    }
}