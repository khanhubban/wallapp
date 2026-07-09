package wallapp.di.module

import kotlinx.coroutines.flow.StateFlow
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import wallapp.di.NamedScope
import wallapp.remoteconfig.data.RemoteConfigData
import wallapp.remoteconfig.data.RemoteConfigDataDefaultsProvider
import wallapp.remoteconfig.data.RemoteConfigDataMock
import wallapp.remoteendpoint.ContentDeliveryConfig
import kotlin.test.Test
import kotlin.test.assertEquals

/** Distinct values so the test can tell which Remote Config key was actually read. */
private object DistinguishableProvider : RemoteConfigDataDefaultsProvider() {
    override val catalogVersion: String get() = "prod-catalog"
    override val catalogVersionStaging: String get() = "staging-catalog"
}

class BuildConfigModuleDebugTest {

    private val koin = koinApplication {
        modules(
            module { single<RemoteConfigData> { RemoteConfigDataMock(DistinguishableProvider) } },
            BuildConfigModule,
        )
    }.koin

    @Test
    fun debugBuildsPointAtTheStagingCdn() {
        assertEquals("https://media-staging.stillscenes.app", koin.get<ContentDeliveryConfig>().baseUrl)
    }

    @Test
    fun debugBuildsReadTheStagingCatalogVersionKey() {
        val version: StateFlow<String> = koin.get(NamedScope.CatalogVersion)
        assertEquals("staging-catalog", version.value)
    }
}
