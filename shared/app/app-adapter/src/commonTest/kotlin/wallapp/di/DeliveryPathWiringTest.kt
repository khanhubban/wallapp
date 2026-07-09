package wallapp.di

import app.cash.turbine.test
import wallapp.remoteendpoint.ContentDeliveryConfig
import wallapp.remoteendpoint.RemoteEndpointTrack
import wallapp.remoteendpoint.RemoteEndpointsRepositoryNetwork
import wallapp.remoteendpoint.RemoteEndpointsRepositoryRemoteConfig
import wallapp.test.WaeTest
import wallapp.test.waeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class DeliveryPathWiringTest : WaeTest {

    // Desktop links di-buildconfig-debug (see di-base.gradle.kts). This asserts the graph resolves
    // to staging rather than prod; the prod binding is asserted in di-buildconfig-release's own test,
    // which is the only place it is reachable.
    @Test fun debugGraphResolvesToTheStagingCdn() = waeTest {
        val config: ContentDeliveryConfig = resolveDependency()
        assertEquals("https://media-staging.stillscenes.app", config.baseUrl)
    }

    @Test fun remoteEndpointsAreBuiltFromRemoteConfigCatalogVersion() = waeTest {
        val repository: RemoteEndpointsRepositoryNetwork = resolveDependency()
        assertIs<RemoteEndpointsRepositoryRemoteConfig>(repository)
        repository.getRemoteEndpoints(RemoteEndpointTrack.Production).test {
            val endpoints = awaitItem()!!
            assertEquals("https://media-staging.stillscenes.app/api/20260709-06/content-1a", endpoints.content)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
