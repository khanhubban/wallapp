package wallapp.crashtracking

object CrashTrackingSetEnabledActionNoOp : CrashTrackingSetEnabledAction {
    override fun invoke(enabled: Boolean) { }
}