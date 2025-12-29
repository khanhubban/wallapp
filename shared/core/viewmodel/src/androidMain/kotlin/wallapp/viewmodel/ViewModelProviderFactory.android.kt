package wallapp.viewmodel

import androidx.lifecycle.ViewModelProvider
import kotlin.reflect.KClass


actual interface ViewModelProviderFactory : ViewModelProvider.Factory
//interface ViewModelProviderFactory : ViewModelProviderFactoryInternal

actual fun <VM : ViewModel> createViewModel(
    factory: ViewModelProviderFactory,
    viewModel: KClass<VM>,
): VM {
    val modelClass: Class<VM> = viewModel.java
    return factory.create(modelClass)
}