package wallapp.onboarding

import kotlinx.coroutines.flow.MutableStateFlow

class OnboardingManagerNoOp : OnboardingManager {
    override val targetOnboardingState: MutableStateFlow<OnboardingState?> = MutableStateFlow(null)

    override fun setOnboardingFinished(value: Boolean) { }

    override val homeOnboardingFinished: MutableStateFlow<Boolean> = MutableStateFlow(false)

    override fun setHomeOnboardingFinished(value: Boolean) {
        homeOnboardingFinished.value = value
    }

    override val artistFollowOnboardingCount: Int
        get() = 5
}