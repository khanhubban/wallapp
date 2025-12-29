package wallapp.viewmodel

import kotlin.reflect.KClass


actual interface ViewModelProviderFactory : ViewModelProviderFactoryInternal

actual fun <VM : ViewModel> createViewModel(
    factory: ViewModelProviderFactory,
    viewModel: KClass<VM>,
): VM {
    return factory.create(viewModel)
}
