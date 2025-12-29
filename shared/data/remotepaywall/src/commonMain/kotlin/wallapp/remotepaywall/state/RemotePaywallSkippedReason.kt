package wallapp.remotepaywall.state

sealed class RemotePaywallSkippedReason : Throwable() {
    data class Holdout(val experiment: RemotePaywallExperiment) : RemotePaywallSkippedReason()
    data object NoRuleMatch : RemotePaywallSkippedReason()
    data object EventNotFound : RemotePaywallSkippedReason()
    data object UserIsSubscribed : RemotePaywallSkippedReason()

    override fun equals(other: Any?): Boolean {
        return when {
            this is Holdout && other is Holdout -> this.experiment == other.experiment
            this is NoRuleMatch && other is NoRuleMatch -> true
            this is EventNotFound && other is EventNotFound -> true
            this is UserIsSubscribed && other is UserIsSubscribed -> true
            else -> false
        }
    }

    override fun hashCode(): Int {
        return this::class.hashCode()
    }
}
