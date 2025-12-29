package wallapp.test

import org.koin.core.module.Module
import org.koin.dsl.module
import wallapp.buildconfig.BuildConfig
import wallapp.buildconfig.BuildConfigMock
import wallapp.device.DeviceCountry
import wallapp.device.DeviceCountryMock
import wallapp.device.DeviceSpec
import wallapp.device.DeviceSpecMock
import wallapp.download.FirebaseStorageDownloader
import wallapp.download.FirebaseStorageDownloaderNoOp
import wallapp.download.UrlDownloader
import wallapp.download.UrlDownloaderCompat
import wallapp.instantapp.InstantAppManager
import wallapp.instantapp.InstantAppManagerNoOp
import wallapp.language.LanguageRepository
import wallapp.language.LanguageRepositoryNoOp
import wallapp.licensing.LicenseRepository
import wallapp.licensing.LicenseRepositoryStub
import wallapp.media.network.repository.NetworkMediaMapRepository
import wallapp.media.network.repository.NetworkMediaMapRepositoryNetwork
import wallapp.network.NetworkState
import wallapp.network.NetworkStatePreset
import wallapp.remoteconfig.RemoteConfig
import wallapp.remoteconfig.RemoteConfigPreset
import wallapp.time.TimeRepository
import wallapp.time.TimeRepositoryMock

/**
 * These are dependencies that allow reliable use of DI in tests. Override as necessary.
 *
 * Note: these are automatically passed in to all [waeTest] calls.
 */
val WaeTestModule: Module = module {
    single<BuildConfig> { BuildConfigMock() }
    single<DeviceSpec> { DeviceSpecMock() }
    single<DeviceCountry> { DeviceCountryMock() }
    single<InstantAppManager> { InstantAppManagerNoOp() }
    single<LanguageRepository> { LanguageRepositoryNoOp }
    single<LicenseRepository> { LicenseRepositoryStub() }
    single<FirebaseStorageDownloader> { FirebaseStorageDownloaderNoOp }
    single<NetworkMediaMapRepository> { get<NetworkMediaMapRepositoryNetwork>() }
    single<NetworkState> { NetworkStatePreset() }
    single<RemoteConfig> { RemoteConfigPreset() }
    single<TimeRepository> { TimeRepositoryMock() }
    single<UrlDownloader> { UrlDownloaderCompat(get()) }
}
