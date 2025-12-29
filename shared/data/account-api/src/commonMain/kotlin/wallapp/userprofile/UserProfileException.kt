package wallapp.userprofile

sealed class UserProfileException : Exception() {
    data class UserNotSignedInException(override val message: String = "User not signed in") : UserProfileException()
    data class UserNotFoundException(override val message: String = "User not found") : UserProfileException()
    data class UserProfileInvalidException(override val message: String = "User profile invalid") : UserProfileException()
    data class UserProfileFetchException(val throwable: Throwable) : UserProfileException()
    data class UserProfileUpdateException(val throwable: Throwable, override val message: String?) : UserProfileException()
    data class UserProfileCreateException(val throwable: Throwable, override val message: String?) : UserProfileException()
    data class UserProfileDeleteException(val throwable: Throwable, override val message: String?) : UserProfileException()
}