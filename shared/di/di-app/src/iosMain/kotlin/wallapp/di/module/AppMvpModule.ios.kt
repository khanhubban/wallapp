package wallapp.di.module

import org.koin.core.module.Module
import org.koin.dsl.module
import wallapp.application.Application
import wallapp.application.ApplicationIos

@Suppress("RemoveExplicitTypeArguments")
actual val AppMvpModule: Module = module {
    single<Application> { get<ApplicationIos>() }
    single<ApplicationIos> { ApplicationIos() }
}