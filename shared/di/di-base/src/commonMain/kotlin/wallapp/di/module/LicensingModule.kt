package wallapp.di.module

import org.koin.dsl.module
import wallapp.di.Factory
import wallapp.di.Lazy
import wallapp.di.NamedScope
import wallapp.di.getLazy
import wallapp.license.LicenseStateProvider
import wallapp.license.cache.LicenseCacheBackup
import wallapp.license.cache.LicenseCacheBackupNoOp
import wallapp.license.controller.LicenseCheckController
import wallapp.license.controller.LicenseCheckControllerDefault
import wallapp.license.controller.LicenseCheckControllerNoOp
import wallapp.license.state.LicenseState
import wallapp.license.state.LicenseStateInAppPurchase
import wallapp.license.state.LicenseStateRepository
import wallapp.license.state.LicenseStateRepositoryDefault
import wallapp.license.state.LicenseStateSessionManager
import wallapp.licensing.LicenseRepository
import wallapp.licensing.LicenseRepositoryBilling
import wallapp.licensing.LicenseStateProviderDefault

@Suppress("RemoveExplicitTypeArguments")
val LicensingModule = module {
    single<Lazy<LicenseCheckControllerDefault>>(NamedScope.LazyLicenseCheckControllerDefault) { getLazy() }
    single<Lazy<LicenseCheckControllerNoOp>>(NamedScope.LazyLicenseCheckControllerNoOp) { getLazy() }
    single<Lazy<LicenseRepository>>(NamedScope.LazyLicenseRepository) { getLazy() }
    single<Lazy<LicenseState>>(NamedScope.LazyLicenseState) { getLazy() }
    single<Lazy<LicenseStateRepository>>(NamedScope.LazyLicenseStateRepository) { getLazy() }
    single<LicenseCacheBackup> { LicenseCacheBackupNoOp }
    single<LicenseCheckController> { Factory.licenseCheckController(this) }
    single<LicenseCheckControllerDefault> { LicenseCheckControllerDefault(get(), get(), get(), get(NamedScope.CoroutineScopeIo), get()) }
    single<LicenseCheckControllerNoOp> { LicenseCheckControllerNoOp() }
    single<LicenseRepository> { LicenseRepositoryBilling(get(), get(), get(NamedScope.CoroutineScopeMain)) }
    single<LicenseState> { get<LicenseStateInAppPurchase>() }
    single<LicenseStateInAppPurchase> { LicenseStateInAppPurchase(get(), get(), get(), get(), get(), get(), get(NamedScope.CoroutineScopeMain)) }
    single<LicenseStateProvider> { LicenseStateProviderDefault(get()) }
    single<LicenseStateRepository> { Factory.licenseStateRepository(this) }
    single<LicenseStateRepositoryDefault> { LicenseStateRepositoryDefault(get(NamedScope.LazyLicenseRepository), get(NamedScope.LazyLicenseCacheMainProcess), get(), get(), get(NamedScope.CoroutineScopeMain)) }
    single<LicenseStateSessionManager> { Factory.licenseStateSessionManager(this) }
//RestoreMe    single<LicenseCacheBackupDefault> { LicenseCacheBackupDefault({ get() }, { get() }, get()) }
//RestoreMe    single<LicenseCheckControllerDefault> { LicenseCheckControllerDefault(get(), get(), get(), get(), get()) }
//RestoreMe    single<LicenseStateRepositoryAltProcess> { LicenseStateRepositoryAltProcess({ get() }, get(), get(), get()) }
//RestoreMe    single<LicenseStateRepositoryDefault> { LicenseStateRepositoryDefault(get(), { get() }, get(), get(), get()) }
//RestoreMe    single<LicenseStateRepositoryPreset> { LicenseStateRepositoryPreset(get()) }
}
