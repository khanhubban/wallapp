package wallapp.firebase.firestore

import com.google.cloud.firestore.DocumentSnapshot
import com.google.cloud.firestore.QueryDocumentSnapshot

data class FirestoreUserSnapshot(
    val userId: String,
    val email: String?,
    val loginType: String?,
    val isAnonymous: Boolean?,
    val accountDeleted: Boolean?,
    val newsletter: Boolean?,
    val receiveNotifications: Boolean?,
    val languages: List<String>?,
    val flags: Any?,
    val epochCreated: Long?,
    val epochLastSeen: Long?,
    val epochLastUpdated: Long?,
    val deviceInfo: String?,
    val timezones: List<String>?,
    val wallpaperDownloadEvents: List<String>?,
    val favoriteIds: List<String>?,
    val purchaseRecords: List<String>?,
    val followingIds: List<String>?,
    val currency: String?,
    val locations: List<String>?
)

fun FirestoreUserSnapshot(document: QueryDocumentSnapshot): FirestoreUserSnapshot {
    return FirestoreUserSnapshot(
        userId = document.getString("userId") ?: document.id,
        email = document.getString("email"),
        loginType = document.getString("loginType"),
        isAnonymous = document.getBoolean("isAnonymous"),
        accountDeleted = document.getBoolean("accountDeleted"),
        newsletter = document.getBoolean("newsletter"),
        receiveNotifications = document.getBoolean("receiveNotifications"),
        languages = document.get("languages") as? List<String>,
        flags = document.get("flags"),
        epochCreated = document.getLong("epochCreated"),
        epochLastSeen = document.getLong("epochLastSeen"),
        epochLastUpdated = document.getLong("epochLastUpdated"),
        deviceInfo = document.getString("deviceInfo"),
        timezones = document.get("timezones") as? List<String>,
        wallpaperDownloadEvents = document.get("wallpaperDownloadEvents") as? List<String>,
        favoriteIds = document.get("favoriteIds") as? List<String>,
        purchaseRecords = document.get("purchaseRecords") as? List<String>,
        followingIds = document.get("followingIds") as? List<String>,
        currency = document.getString("currency"),
        locations = document.get("locations") as? List<String>
    )
}

fun FirestoreUserSnapshot(document: DocumentSnapshot): FirestoreUserSnapshot {
    return FirestoreUserSnapshot(
        userId = document.id,
        email = document.getString("email"),
        loginType = document.getString("loginType"),
        isAnonymous = document.getBoolean("isAnonymous"),
        accountDeleted = document.getBoolean("accountDeleted"),
        newsletter = document.getBoolean("newsletter"),
        receiveNotifications = document.getBoolean("receiveNotifications"),
        languages = document.get("languages") as? List<String>,
        flags = document.get("flags"),
        epochCreated = document.getLong("epochCreated"),
        epochLastSeen = document.getLong("epochLastSeen"),
        epochLastUpdated = document.getLong("epochLastUpdated"),
        deviceInfo = document.getString("deviceInfo"),
        timezones = document.get("timezones") as? List<String>,
        wallpaperDownloadEvents = document.get("wallpaperDownloadEvents") as? List<String>,
        favoriteIds = document.get("favoriteIds") as? List<String>,
        purchaseRecords = document.get("purchaseRecords") as? List<String>,
        followingIds = document.get("followingIds") as? List<String>,
        currency = document.getString("currency"),
        locations = document.get("locations") as? List<String>
    )
}