package wallapp.viewmodel

import kotlin.reflect.KClass

expect interface ViewModelProviderFactory

expect fun <VM : ViewModel> createViewModel(factory: ViewModelProviderFactory, viewModel: KClass<VM>): VM

inline fun <reified VM : ViewModel> ViewModelProviderFactory.createViewModel(): VM =
    createViewModel(this, VM::class)
