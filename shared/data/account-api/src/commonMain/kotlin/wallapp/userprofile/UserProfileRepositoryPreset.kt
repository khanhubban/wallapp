package wallapp.userprofile

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import wallapp.auth.firebase.FirebaseAuthUser
import wallapp.result.Result
import wallapp.userprofile.UserProfileRepositoryMock.Companion.UserProfilePreset

class UserProfileRepositoryPreset : UserProfileRepository {
    override val firebaseAuthUserId: String?
        get() = "123456"

    private val _currentUserProfile = MutableStateFlow<Result<UserProfileLocal>>(Result.Success(UserProfilePreset))
    override val currentUserProfile: StateFlow<Result<UserProfileLocal>>
        get() = _currentUserProfile

    private val _userProfileErrorFlow = MutableSharedFlow<UserProfileException>(extraBufferCapacity = 1)
    override val userProfileErrorFlow: Flow<UserProfileException>
        get() = _userProfileErrorFlow.asSharedFlow()

    override suspend fun createUserProfile(
        firebaseAuthUser: FirebaseAuthUser,
        loginType: LoginType,
    ) {
        error("Not implemented")
//        require(loginType == LoginType.Anonymous) { "Only Anonymous loginType is supported in Preset" }
//
//        updateUserProfile()
    }

    override suspend fun updateUserProfile(userProfile: UserProfileLocal) {
        _currentUserProfile.value = Result.Success(userProfile)
    }

    override suspend fun userProfileExists(userId: String): Boolean {
        return true
    }

    override suspend fun getUserProfile(userId: String): UserProfileLocal? {
        val profile = _currentUserProfile.value as? Result.Success<UserProfileLocal>
        return profile?.data
    }

    override suspend fun getUserProfileFromServer(userId: String): UserProfileLocal? {
        return getUserProfile(userId)
    }

    override suspend fun updateUserProfileFields(
        vararg fieldsAndValues: Pair<String, Any?>,
        canPostError: Boolean,
    ): Boolean {
        return true
    }

    override suspend fun deletePIIFields() {

    }
}