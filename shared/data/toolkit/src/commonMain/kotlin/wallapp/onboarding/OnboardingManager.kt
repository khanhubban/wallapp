package wallapp.onboarding

import kotlinx.coroutines.flow.StateFlow

interface OnboardingManager {

    val targetOnboardingState: StateFlow<OnboardingState?>

    fun setOnboardingFinished(value: Boolean)

    val homeOnboardingFinished: StateFlow<Boolean>
    fun setHomeOnboardingFinished(value: Boolean)
    val artistFollowOnboardingCount: Int
}