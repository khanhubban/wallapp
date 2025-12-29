package wallapp.screen

import co.touchlab.skie.configuration.annotations.SealedInterop
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import wallapp.content.model.Id.ArtistId
import wallapp.content.model.Id.CollectionId
import wallapp.content.model.Id.DesignId
import wallapp.content.model.Id.FolderId
import wallapp.content.model.Id.RemixId
import wallapp.content.model.WallpaperId
import wallapp.content.state.connections.ConnectionType
import wallapp.content.state.error.ErrorScreen
import wallapp.content.state.upgrade.SubscriptionPlan
import wallapp.pixel.navigation.NavigationArgument
import wallapp.preference.ObservableValue
import wallapp.remotepaywall.RemotePaywallEvent
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
@SealedInterop.Enabled
@Serializable
sealed class ScreenArgument : NavigationArgument {

    @Serializable
    data object AccountScreenArgument : ScreenArgument()

    @Serializable
    data object ArtistsScreenArgument : ScreenArgument()

    @Serializable
    data class ArtistIdScreenArgument(val artistId: ArtistId) : ScreenArgument()

    @Serializable
    data class CollectionIdScreenArgument(
        val collectionId: CollectionId,
        val firstWallpaperId: WallpaperId?,
    ) : ScreenArgument()

    @Serializable
    data class CollectionActionScreenArgument(
        val collectionId: CollectionId,
        val singleWallpaperId: WallpaperId?,
        val modalSheetHeight: Float, // Needed for iOS sheet to present as we don't have the state/spec info while presenting
    ) : ScreenArgument()

    @Serializable
    data class ConnectionsScreenArgument(
        val initialConnectionType: ConnectionType,
    ) : ScreenArgument()

    @Serializable
    data object DataConsentScreenArgument : ScreenArgument()

    @Serializable
    data object DebugSettingsScreenArgument : ScreenArgument()

    @Serializable
    data class DesignIdScreenArgument(val designId: DesignId) : ScreenArgument()

    @Serializable
    data class ErrorScreenArgument(val errorScreen: ErrorScreen) : ScreenArgument()

    @Serializable
    data object ExploreScreenArgument : ScreenArgument()

    @Serializable
    data class FolderScreenArgument(val folderId: FolderId) : ScreenArgument()

    @Serializable
    data object FirstRunScreenArgument : ScreenArgument()

    @Serializable
    data object HomeOnboardingScreenArgument : ScreenArgument()

    @Serializable
    data object HomeScreenArgument : ScreenArgument()

    @Serializable
    data object IndexScreenArgument : ScreenArgument()

    @Serializable
    data object ManageSubscriptionScreenArgument : ScreenArgument()

    @SealedInterop.Enabled
    @Serializable
    sealed class PaywallScreenArgument : ScreenArgument() {
        abstract val autoTriggerPurchase: Boolean
        abstract val subscriptionExpired: Boolean
        abstract val subscriptionPlan: SubscriptionPlan?

        @Serializable
        data class Arbitrated(
            override val autoTriggerPurchase: Boolean,
            override val subscriptionExpired: Boolean,
            override val subscriptionPlan: SubscriptionPlan?,
        ) : PaywallScreenArgument()

        @Serializable
        data class Internal(
            override val autoTriggerPurchase: Boolean,
            override val subscriptionExpired: Boolean,
            override val subscriptionPlan: SubscriptionPlan?,
        ) : PaywallScreenArgument()

        @Serializable
        data class Remote(
            val paywallEvent: RemotePaywallEvent,
            override val autoTriggerPurchase: Boolean,
            override val subscriptionExpired: Boolean,
            override val subscriptionPlan: SubscriptionPlan?,
        ) : PaywallScreenArgument()
    }

    @Serializable
    data object ProfileScreenArgument : ScreenArgument()

    @Serializable
    data class RemixIdScreenArgument(val remixId: RemixId) : ScreenArgument()

    @Serializable
    data class SettingsArgument(
        val title: String,
        val settingKeys: List<String>,
    ) : ScreenArgument() {

        constructor(title: String, vararg settingKeys: String)
                : this(title, settingKeys.toList())

        constructor(title: String, settingKey: String)
                : this(title, listOf(settingKey))

        constructor(title: String, vararg observableValues: ObservableValue<Any>)
                : this(title, observableValues.map { it.key() })

        constructor(title: String, observableValue: ObservableValue<Any>)
                : this(title, listOf(observableValue.key()))

        init {
            require(settingKeys.isNotEmpty()) { "Setting keys must not be empty" }
        }
    }

    @Serializable
    data object SearchScreenArgument : ScreenArgument()

    @Serializable
    data object SearchResultsScreenArgument : ScreenArgument()

    @Serializable
    data object ShowcaseAdsScreenArgument : ScreenArgument()

    @Serializable
    data object ShowcaseIosNativeFeedScreenArgument : ScreenArgument()

    @Serializable
    data object ShowcaseTypefaceScreenArgument : ScreenArgument()

    @Serializable
    data object ShowcaseTypefaceIosScreenArgument : ScreenArgument()

    @Serializable
    data object SignUpScreenArgument : ScreenArgument()

    @Serializable
    data object OssLicensesScreenArgument : ScreenArgument()

    @Serializable
    data object RewardAdInternalScreenArgument : ScreenArgument()

    @Serializable
    data class WallpaperShowcaseScreenArgument(
        val remixId: RemixId,
        val firstWallpaperId: WallpaperId? = null,
    ) : ScreenArgument()

    @Serializable
    data class WallpaperSingleActionScreenArgument(
        val wallpaperId: WallpaperId,
        val modalSheetHeight: Float, // Needed for iOS sheet to present as we don't have the state/spec info while presenting
    ) : ScreenArgument()

    val jsonString: String
        get() = Base64.encode(
            Json.encodeToString(kotlinx.serialization.serializer(), this).encodeToByteArray()
        )

    companion object {
        fun fromJsonString(jsonString: String): ScreenArgument? {
            return try {
                Json.decodeFromString(
                    kotlinx.serialization.serializer(),
                    Base64.decode(jsonString).decodeToString()
                )
            } catch (e: SerializationException) {
                null
            } catch (e: IllegalArgumentException) {
                null
            }
        }
    }
}

fun ScreenArgument.id(): String = this.screen.routeBuilder(this.jsonString)
