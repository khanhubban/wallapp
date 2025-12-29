package wallapp.app

import wallapp.di.resolveDependency
import wallapp.viewmodel.ViewModelProviderFactory

actual fun resolveAppViewModel(): AppViewModel {
    val viewModelProviderFactory: ViewModelProviderFactory = resolveDependency()
    return viewModelProviderFactory.create(AppViewModel::class)
}