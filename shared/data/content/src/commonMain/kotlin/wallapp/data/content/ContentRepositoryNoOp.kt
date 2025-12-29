package wallapp.data.content

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
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

class ContentRepositoryNoOp : ContentRepository {

    override val isReady: StateFlow<Boolean> = MutableStateFlow(true)

    override fun getWallpaperRemix(remixId: RemixId): Flow<WallpaperRemix?> = MutableStateFlow(null)

    override val artistStates: Flow<List<ArtistState>?> = MutableStateFlow(null)

    override fun getArtistState(
        id: ArtistId,
        getCollections: Boolean,
        getFeed: Boolean,
        getFollowState: Boolean,
    ): Flow<ArtistState?> = MutableStateFlow(null)

    override fun getArtists(wallpaperId: Id): Flow<List<Artist>?> = MutableStateFlow(null)

    override fun getArtistCollectionStates(id: ArtistId): Flow<List<CollectionState>?> = MutableStateFlow(null)

    override fun getArtistConnection(artist: Artist): Flow<ArtistConnectionContentResult?> = MutableStateFlow(null)

    override fun getSingles(artistId: ArtistId): Flow<List<WallpaperItem>?> = flowOf(null)

    override val collectionStates: Flow<List<CollectionState>> = flowOf(emptyList())

    override fun getCollectionState(id: CollectionId): Flow<CollectionState?> = MutableStateFlow(null)

    override fun getCollectionsStatesMerged(ids: List<CollectionId>): Flow<List<CollectionState>?> = MutableStateFlow(null)

    override fun getCollectionStatesSnapshot(ids: List<CollectionId>): Flow<List<CollectionState>?> = MutableStateFlow(null)

    override val collectionPurchasables: Flow<List<CollectionPurchasable>> = emptyFlow()

    override fun getCollectionPurchasable(collectionId: CollectionId): Flow<CollectionPurchasable?> =
        flowOf(null)

    override fun getCollectionScreenContent(
        id: CollectionId,
        firstWallpaperId: WallpaperId?,
    ): Flow<CollectionScreenContentResult?> = MutableStateFlow(null)

    override fun getArtistScreenContent(id: ArtistId): Flow<ArtistScreenContentResult> = MutableStateFlow(
        ArtistScreenContentResult(artistState = null)
    )

    override val artistsContent: Flow<ArtistsContentResult> = MutableStateFlow(ArtistsContentResult(emptyList()))

    override fun getWallpaperConnections(wallpaperId: Id): Flow<WallpaperConnectionContentResult?> = MutableStateFlow(null)

    override val unlockedCollectionsContent: Flow<UnlockedCollectionsContentResult> =
        MutableStateFlow(UnlockedCollectionsContentResult(emptyList()))

    override val exploreContent: Flow<ExploreContentResult> = flowOf(
        ExploreContentResult(
            emptyList(),
            emptyList(),
            emptyList(),
            emptyList(),
        )
    )

    override fun getWallpaperContent(remixId: RemixId): Flow<WallpaperContentResult> = flowOf(WallpaperContentResult.Empty)

    override fun getAllWallpaperContents(
        remixId: RemixId,
        firstWallpaperId: WallpaperId?
    ): Flow<Map<RemixId, WallpaperContentResult>> = emptyFlow()

    override fun getUnlockWallpaperContent(remixId: RemixId): Flow<UnlockWallpaperContentResult?> =
        MutableStateFlow(null)

    override val connectionsContent: Flow<ConnectionsContentResult> = flowOf(ConnectionsContentResult.Empty)
    override val connectionsSummaryContent: Flow<ConnectionsSummaryContentResult> = flowOf(ConnectionsSummaryContentResult.Empty)

    override fun getHomeContent(homeContentType: HomeContentType): Flow<HomeContentResult> = MutableStateFlow(
        HomeContentResult(
            wallpapers = null,
            collectionStates = null,
            artists = null,
            fallbackWallpapers = null,
            fallbackCollectionStates = null,
        )
    )

    override val homePagedContent: Flow<HomePagedContentResult?> = MutableStateFlow(null)

    override val signUpContentResult: Flow<SignUpContentResult> = MutableStateFlow(SignUpContentResult(null))

    override val suggestedWallpapers: Flow<List<WallpaperRemix>> = emptyFlow()
    override val suggestedCollections: Flow<List<CollectionState>> = emptyFlow()

    override val deepLinkMappings: Flow<List<DeepLinkMapping>> = MutableStateFlow(emptyList())
}