package wallapp.di.module

import org.koin.dsl.module
import wallapp.licensing.LicenseRepositoryDebug

val LicensingPlatformModule = module {
    single<LicenseRepositoryDebug> { LicenseRepositoryDebug(get(), get()) }
}