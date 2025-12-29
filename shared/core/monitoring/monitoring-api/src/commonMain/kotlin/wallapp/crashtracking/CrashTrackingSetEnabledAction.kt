package wallapp.crashtracking

fun interface CrashTrackingSetEnabledAction {

    operator fun invoke(enabled: Boolean)
}

