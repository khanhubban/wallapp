package wallapp.remoteconfig

import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours


/**
 * Defines global variables that can be configured and changed remotely.
 */
interface RemoteConfig {

    val dataRefreshed: Flow<Unit>

    suspend fun update()

    companion object {
        val ConfigCacheExpiration: Duration = 24.hours
    }
}


