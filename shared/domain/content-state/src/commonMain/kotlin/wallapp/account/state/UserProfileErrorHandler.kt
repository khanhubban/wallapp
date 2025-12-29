package wallapp.account.state

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import wallapp.app.AppStateManager
import wallapp.content.state.error.ErrorScreen
import wallapp.log.Log
import wallapp.userprofile.UserProfileException
import wallapp.userprofile.UserProfileRepository

class UserProfileErrorHandler(
    appStateManager: AppStateManager,
    userProfileRepository: UserProfileRepository,
    coroutineScopeMain: CoroutineScope,
) {

    init {
        userProfileRepository.userProfileErrorFlow.onEach { userProfileError ->
            Log.d("[firestore] UserProfileErrorHandler, userProfileError: $userProfileError")
            if (userProfileError is UserProfileException.UserProfileUpdateException) {
                appStateManager.navigateToError(
                    ErrorScreen.UserProfile(
                        errorMessage = userProfileError.message ?: "User update failed"
                    )
                )
            }
        }.launchIn(coroutineScopeMain)
    }
}