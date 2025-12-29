package wallapp.data.content

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import wallapp.content.model.Id
import wallapp.content.model.Id.ArtistId
import wallapp.content.model.Id.CollectionId
import wallapp.content.model.Id.RemixId
import wallapp.content.model.WallpaperId
import wallapp.content.model.WallpaperItem
import wallapp.content.model.WallpaperRemix
import wallapp.data.artist.Artist
import wallapp.data.artist.ArtistState
import wallapp.data.collection.CollectionPurchasable
import wallapp.data.collection.CollectionState
import wallapp.data.content.ContentResult.ArtistConnectionContentResult
import wallapp.data.content.ContentResult.ArtistScreenContentResult
import wallapp.data.content.ContentResult.ArtistsContentResult
import wallapp.data.content.ContentResult.CollectionScreenContentResult
import wallapp.data.content.ContentResult.ConnectionsContentResult
import wallapp.data.content.ContentResult.ConnectionsSummaryContentResult
import wallapp.data.content.ContentResult.ExploreContentResult
import wallapp.data.content.ContentResult.HomeContentResult
import wallapp.data.content.ContentResult.HomePagedContentResult
import wallapp.data.content.ContentResult.SignUpContentResult
import wallapp.data.content.ContentResult.UnlockWallpaperContentResult
import wallapp.data.content.ContentResult.UnlockedCollectionsContentResult
import wallapp.data.content.ContentResult.WallpaperConnectionContentResult
import wallapp.data.content.ContentResult.WallpaperContentResult
import wallapp.data.home.HomeContentType
import wallapp.deeplink.DeepLinkMapping

interface ContentRepository {

    val isReady: StateFlow<Boolean>

    fun getWallpaperRemix(remixId: RemixId): Flow<WallpaperRemix?>

    val artistStates: Flow<List<ArtistState>?>
    fun getArtistState(
        id: ArtistId,
        getCollections: Boolean = false,
        getFeed: Boolean = false,
        getFollowState: Boolean = false,
    ): Flow<ArtistState?>

    fun getArtists(wallpaperId: Id): Flow<List<Artist>?>

    fun getArtistCollectionStates(id: ArtistId): Flow<List<CollectionState>?>

    fun getArtistConnection(artist: Artist): Flow<ArtistConnectionContentResult?>

    fun getSingles(artistId: ArtistId): Flow<List<WallpaperItem>?>

    /**
     * Returns all Collections. Collections containing only Singles are NOT included.
     */
    val collectionStates: Flow<List<CollectionState>>

    fun getCollectionState(id: CollectionId): Flow<CollectionState?>
    // Emits a new list every time any of the underlying getCollection(id) Flows emit
    fun getCollectionsStatesMerged(ids: List<CollectionId>): Flow<List<CollectionState>?>
    // Collects all at once and doesn't provide real-time updates
    fun getCollectionStatesSnapshot(ids: List<CollectionId>): Flow<List<CollectionState>?>

    val collectionPurchasables: Flow<List<CollectionPurchasable>>
    fun getCollectionPurchasable(collectionId: CollectionId): Flow<CollectionPurchasable?>

    fun getCollectionScreenContent(
        id: CollectionId,
        firstWallpaperId: WallpaperId?,
    ): Flow<CollectionScreenContentResult?>

    fun getArtistScreenContent(id: ArtistId): Flow<ArtistScreenContentResult>

    val artistsContent: Flow<ArtistsContentResult>

    fun getWallpaperConnections(wallpaperId: Id): Flow<WallpaperConnectionContentResult?>

    val unlockedCollectionsContent: Flow<UnlockedCollectionsContentResult>

    val exploreContent: Flow<ExploreContentResult>

    fun getWallpaperContent(remixId: RemixId): Flow<WallpaperContentResult>

    fun getAllWallpaperContents(
        remixId: RemixId,
        firstWallpaperId: WallpaperId?,
    ): Flow<Map<RemixId, WallpaperContentResult>>

    fun getUnlockWallpaperContent(remixId: RemixId): Flow<UnlockWallpaperContentResult?>

    val connectionsContent: Flow<ConnectionsContentResult>

    val connectionsSummaryContent: Flow<ConnectionsSummaryContentResult>

    fun getHomeContent(homeContentType: HomeContentType): Flow<HomeContentResult>
    val homePagedContent: Flow<HomePagedContentResult?>

    val signUpContentResult: Flow<SignUpContentResult>

    val suggestedWallpapers: Flow<List<WallpaperRemix>>
    val suggestedCollections: Flow<List<CollectionState>>

    val deepLinkMappings: Flow<List<DeepLinkMapping>>
}