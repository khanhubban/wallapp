package wallapp.userprofile

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import wallapp.auth.firebase.FirebaseAuthUser
import wallapp.result.Result


interface UserProfileRepository {

    val firebaseAuthUserId: String?
    val currentUserProfile: StateFlow<Result<UserProfileLocal>>
    val userProfileErrorFlow: Flow<UserProfileException>
    suspend fun createUserProfile(firebaseAuthUser: FirebaseAuthUser, loginType: LoginType)
    suspend fun updateUserProfile(userProfile: UserProfileLocal)
    suspend fun userProfileExists(userId: String): Boolean
    suspend fun getUserProfile(userId: String): UserProfileLocal?
    suspend fun getUserProfileFromServer(userId: String): UserProfileLocal?
    suspend fun updateUserProfileFields(vararg fieldsAndValues: Pair<String, Any?>, canPostError: Boolean = true): Boolean

    @Throws(Exception::class)
    suspend fun deletePIIFields()
}
