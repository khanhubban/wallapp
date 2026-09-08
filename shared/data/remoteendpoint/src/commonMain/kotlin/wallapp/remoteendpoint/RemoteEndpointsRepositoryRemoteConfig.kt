package wallapp.remoteendpoint

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

/**
 * Builds [RemoteEndpoints] locally from the Remote Config catalog version instead of
 * downloading api/v0/spec.json. The RC param is the version pointer (delivery design §7);
 * its percentage conditions provide canary and rollback.
 */
class RemoteEndpointsRepositoryRemoteConfig(
    private val catalogVersion: StateFlow<String>,
    private val config: ContentDeliveryConfig,
) : RemoteEndpointsRepositoryNetwork {

    override fun getRemoteEndpoints(
        track: RemoteEndpointTrack,
        forceRefresh: Boolean,
        forceCache: Boolean,
    ): Flow<RemoteEndpoints?> =
        catalogVersion
            .filter { it.isNotBlank() }
            .map { version -> endpointsFor(version) }

    private fun endpointsFor(version: String): RemoteEndpoints {
        val root = "${config.baseUrl}/api/$version"
        return RemoteEndpoints(
            content = "$root/content-1a",
            search = "$root/content-metadata-1a",
            media = RemoteEndpointMediaMap(
                root = "$root/media-1a",
                imageHostPlatformKeys = listOf("i", "c"),
                imageBucketSpecKeys = listOf(
                    "p~s", "p~five0", "p~a~n", "p~a~xl", "p~uhd", "f~fo", "t~s", "t~m", "t~l",
                ),
            ),
        )
    }
}
