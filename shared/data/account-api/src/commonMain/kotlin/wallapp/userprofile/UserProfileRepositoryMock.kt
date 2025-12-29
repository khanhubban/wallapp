package wallapp.userprofile

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import wallapp.account.Account
import wallapp.auth.firebase.FirebaseAuthUser
import wallapp.result.Result


class UserProfileRepositoryMock(
    var startingProfile: UserProfileLocal? = null,
    var profileToBeCreated: UserProfileLocal? = null,
    var profileToBeUpdated: UserProfileLocal? = null,
) : UserProfileRepository {

    var userProfileMock: UserProfileLocal? = startingProfile
        set(value) {
            field = value
            currentUserProfile.value = value?.let { Result.Success(it) } ?: Result.Error(Exception("User profile does not exist"))
        }

    override val firebaseAuthUserId: String = Account.Preset.userId

    override val currentUserProfile: MutableStateFlow<Result<UserProfileLocal>> = MutableStateFlow(
        startingProfile?.let { Result.Success(it) } ?: Result.Error(Exception("User profile does not exist"))
    )

    override val userProfileErrorFlow: Flow<UserProfileException> by lazy {
        MutableStateFlow(UserProfileException.UserNotFoundException()).asStateFlow()
    }

    override suspend fun createUserProfile(firebaseAuthUser: FirebaseAuthUser, loginType: LoginType) {
        userProfileMock = profileToBeCreated ?: UserProfilePreset
    }

    override suspend fun updateUserProfile(userProfile: UserProfileLocal) {
        if (userProfileMock?.userId != userProfile.userId) return
        userProfileMock = profileToBeUpdated ?: userProfileMock
    }

    override suspend fun userProfileExists(userId: String): Boolean {
        return userProfileMock?.userId == userId
    }

    override suspend fun getUserProfile(userId: String): UserProfileLocal? {
        return userProfileMock
    }

    override suspend fun getUserProfileFromServer(userId: String): UserProfileLocal? {
        return userProfileMock
    }

    companion object {
        val UserProfilePreset = UserProfileLocal(
            userId = Account.Preset.userId,
            email = Account.Preset.email,
            isAnonymous = Account.Preset.isAnonymous,
            loginType = LoginType.Google,
            epochCreated = 0,
            epochLastUpdated = 0,
            epochLastSeen = 0,
            favoriteIds = emptyList(),
            currentWallpaperIds = mapOf(),
            followingIds = emptyList(),
            purchaseRecords = emptyList(),
            deviceInfo = "",
            currency = "",
            newsletter = false,
            flags = emptyList(),
            wallpaperDownloadEvents = emptyList(),
            receiveNotifications = false,
            accountDeleted = false,
        )
    }

    override suspend fun updateUserProfileFields(vararg fieldsAndValues: Pair<String, Any?>, canPostError: Boolean): Boolean {
        return false
    }

    override suspend fun deletePIIFields() { }
}
