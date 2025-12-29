package wallapp.ads

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import wallapp.annotation.VisibleForTesting
import wallapp.coroutine.CoroutineContexts
import wallapp.crashtracking.CrashTrackingHolder.crashTracking
import wallapp.log.Log

/**
 * Buffers ad events in memory without persistence.
 */
class AdEventLoggerDefault(
    private val coroutineContexts: CoroutineContexts,
) : AdEventLogger {

    @VisibleForTesting internal val items: MutableList<AdEvent> = mutableListOf()

    override fun addAdEvent(adEvent: AdEvent) {
        GlobalScope.launch(coroutineContexts.io) {
            try {
                items.add(adEvent)
                Log.d("addAdEvent(): %s", adEvent)
            } catch (ex: Exception) {
                crashTracking.logNonFatalException(ex)
            }
        }
    }
}
