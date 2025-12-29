package wallapp.crashtracking

import wallapp.log.Log

object CrashTrackingHolder {

    private var _crashTracking: CrashTracking? = null
    val crashTracking: CrashTracking
        get() {
            if (_crashTracking == null) {
                set(CrashTrackingLocal)
            }
            return _crashTracking!!
        }

    fun set(crashTracking: CrashTracking) {
        Log.i("CrashTrackingHolder: setting crash tracking to ${crashTracking::class.simpleName}")
        require(crashTracking !is CrashTrackingArbitrated) { "CrashTrackingHolder cannot be CrashTrackingArbitrated - circular dependency" }
        _crashTracking = crashTracking
    }
}