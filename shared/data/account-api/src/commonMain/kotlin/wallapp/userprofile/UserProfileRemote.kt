package wallapp.userprofile

import kotlinx.serialization.Serializable

/**
 * A POJO mapping of data stored in Firestore.
 *
 * Items must be `var` for parsing.
 */
@Serializable
data class UserProfileRemote(
    var userId: String? = null,
    var email: String? = null,
    var isAnonymous: Boolean = false,
    var epochCreated: Long? = null,
    var epochLastUpdated: Long? = null,
    var epochLastSeen: Long? = null,
    var dataVersion: Int = 1,
    var loginType: String? = null,
    var favoriteIds: List<String>? = null,
    var currentWallpaperIds: Map<String, String>? = null,
    var followingIds: List<String>? = null,
    var purchaseRecords: List<String>? = null,
    val deviceInfo: String? = null,
    val currency: String? = null,
    val newsletter: Boolean? = null,
    val flags: List<String>? = null,
    val wallpaperDownloadEvents: List<String>? = null,
    val receiveNotifications: Boolean? = null,
    val accountDeleted: Boolean? = null,
)

internal inline fun UserProfileRemote.isValid(): Boolean =
    userId != null
            && epochCreated != null
            && epochLastUpdated != null
            && epochLastSeen != null
            && loginType != null && loginType?.toLoginType() != null
