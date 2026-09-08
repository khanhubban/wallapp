package wallapp.remoteapi

import app.cash.turbine.test
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import wallapp.remoteconfig.data.RemoteConfigDataDefaultsProviderDefault
import wallapp.remoteconfig.data.RemoteConfigDataMock
import wallapp.remoteconfig.data.RemoteConfigEntry
import wallapp.remoteendpoint.ContentDeliveryConfig
import wallapp.remoteendpoint.RemoteEndpointTrack
import wallapp.remoteendpoint.RemoteEndpointsRepositoryRemoteConfig
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CatalogVersionConfigTest {

    @Test
    fun catalogVersionEntry_hasStableKeyAndDefault() {
        assertEquals("catalog_version", RemoteConfigEntry.CatalogVersion.key)
        assertEquals("20260709-06", RemoteConfigEntry.CatalogVersion.default)
    }

    @Test
    fun catalogVersionStagingEntry_hasStableKeyAndDefault() {
        assertEquals("catalog_version_staging", RemoteConfigEntry.CatalogVersionStaging.key)
        assertEquals("20260709-06", RemoteConfigEntry.CatalogVersionStaging.default)
    }

    @Test
    fun mockData_exposesBothCatalogVersionDefaults() {
        val data = RemoteConfigDataMock(RemoteConfigDataDefaultsProviderDefault)
        assertEquals("20260709-06", data.catalogVersion.value)
        assertEquals("20260709-06", data.catalogVersionStaging.value)
    }

    @Test
    fun defaultsArray_registersBothCatalogKeys() {
        val keys = RemoteConfigEntry.asDefaultsArray().map { it.first }
        assertTrue("catalog_version" in keys)
        assertTrue("catalog_version_staging" in keys)
    }
}

class RemoteEndpointsRemoteConfigTest {

    private val config = ContentDeliveryConfig(baseUrl = "https://media-staging.example.com")

    @Test
    fun buildsAbsoluteEndpointsFromVersion() = runTest {
        val version = MutableStateFlow("99999999")
        val repo = RemoteEndpointsRepositoryRemoteConfig(version, config)
        repo.getRemoteEndpoints(RemoteEndpointTrack.Production).test {
            val endpoints = awaitItem()!!
            assertEquals("https://media-staging.example.com/api/99999999/content-1a", endpoints.content)
            assertEquals("https://media-staging.example.com/api/99999999/content-metadata-1a", endpoints.search)
            assertEquals(
                "https://media-staging.example.com/api/99999999/media-1a-c-p~s",
                endpoints.media.getEndpoint("c", "p~s"),
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun blankVersionEmitsNothing() = runTest {
        val version = MutableStateFlow("")
        val repo = RemoteEndpointsRepositoryRemoteConfig(version, config)
        repo.getRemoteEndpoints(RemoteEndpointTrack.Production).test {
            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }
}
