package wallapp.userprofile

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import wallapp.auth.firebase.FirebaseAuthUser
import wallapp.result.Result

object UserProfileRepositoryNoOp : UserProfileRepository {

    override val firebaseAuthUserId: String = ""
    override val currentUserProfile: StateFlow<Result<UserProfileLocal>> = MutableStateFlow(Result.Error(Exception("User profile does not exist")))
    override val userProfileErrorFlow: Flow<UserProfileException> = MutableStateFlow(UserProfileException.UserProfileFetchException(Exception("User profile does not exist")))
    override suspend fun createUserProfile(firebaseAuthUser: FirebaseAuthUser, loginType: LoginType) { }

    override suspend fun updateUserProfile(userProfile: UserProfileLocal) { }

    override suspend fun userProfileExists(userId: String): Boolean = false

    override suspend fun getUserProfile(userId: String): UserProfileLocal? = null

    override suspend fun getUserProfileFromServer(userId: String): UserProfileLocal? = null

    override suspend fun updateUserProfileFields(vararg fieldsAndValues: Pair<String, Any?>, canPostError: Boolean): Boolean = false

    override suspend fun deletePIIFields() { }
}