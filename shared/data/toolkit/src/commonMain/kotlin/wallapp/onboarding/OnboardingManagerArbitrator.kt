package wallapp.onboarding

object OnboardingManagerArbitrator {

    fun arbitrateOnboardingState(
        firstRunOnboardingDismissed: Boolean,
        hasCurrentAccount: Boolean,
        acceptedTerms: Boolean,
        subscribedToNewsletter: Boolean,
        reportUsageStats: Boolean,
    ): OnboardingState? {
        if (firstRunOnboardingDismissed) {
            return null
        }

        if (!hasCurrentAccount) {
            return OnboardingState.SignUp
        }

        if (!acceptedTerms || !subscribedToNewsletter || !reportUsageStats) {
            return OnboardingState.DataConsent
        }

        return null
    }
}