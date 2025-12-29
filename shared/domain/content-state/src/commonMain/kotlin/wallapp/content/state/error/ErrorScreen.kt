package wallapp.content.state.error

import co.touchlab.skie.configuration.annotations.SealedInterop
import kotlinx.serialization.Serializable
import wallapp.content.model.WallpaperId

@SealedInterop.Enabled
@Serializable
sealed class ErrorScreen {

    @Serializable
    data object AppUpdateRequired : ErrorScreen()

    @Serializable
    data class DownloadFailed(val errorMessage: String) : ErrorScreen()

    @Serializable
    data class Network(val showCloseButton: Boolean = true) : ErrorScreen()

    @Serializable
    data object PermissionSystemMediaDeniedAndroid : ErrorScreen()

    @Serializable
    data object PermissionSystemMediaDeniedIos : ErrorScreen()

    @Serializable
    data class Purchase(val errorMessage: String) : ErrorScreen()

    @Serializable
    data object RemoteDataFetch : ErrorScreen()

    @Serializable
    data class RewardAd(val wallpaperId: WallpaperId, val errorMessage: String?) : ErrorScreen()

    @Serializable
    data class SignIn(val errorMessage: String?) : ErrorScreen()

    @Serializable
    data object SubscriptionExpired : ErrorScreen()

    @Serializable
    data object Template : ErrorScreen()

    @Serializable
    data class UserProfile(val errorMessage: String) : ErrorScreen()
}

expect fun permissionSystemMediaDenied(): ErrorScreen