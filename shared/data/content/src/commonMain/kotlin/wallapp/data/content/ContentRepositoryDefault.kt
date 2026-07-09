package wallapp.data.content

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import wallapp.content.model.Id
import wallapp.content.model.Id.ArtistId
import wallapp.content.model.Id.CollectionId
import wallapp.content.model.Id.RemixId
import wallapp.content.model.Ids
import wallapp.content.model.Wallpaper
import wallapp.content.model.WallpaperCategory
import wallapp.content.model.WallpaperCategoryType
import wallapp.content.model.WallpaperId
import wallapp.content.model.WallpaperItem
import wallapp.content.model.WallpaperRemix
import wallapp.content.model.remix
import wallapp.coroutine.CoroutineScopeIo
import wallapp.coroutine.CoroutineScopeMain
import wallapp.data.artist.Artist
import wallapp.data.artist.ArtistRepository
import wallapp.data.artist.ArtistState
import wallapp.data.artist.ArtistStateRepository
import wallapp.data.artist.withUpdatedFollowStates
import wallapp.data.collection.CollectionConnectionState
import wallapp.data.collection.CollectionPurchasable
import wallapp.data.collection.CollectionRepository
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
import wallapp.data.content.media.ContentMediaRepository
import wallapp.data.entitlement.EntitlementState
import wallapp.data.favorite.FavoriteItemsRepository
import wallapp.data.folder.FolderLink
import wallapp.data.folder.FolderLinkRepository
import wallapp.data.following.FollowState
import wallapp.data.following.FollowingRepository
import wallapp.data.following.findById
import wallapp.data.highlight.Highlights
import wallapp.data.home.HomeContentType
import wallapp.data.purchase.PurchaseRecord
import wallapp.data.purchase.PurchaseRecordRepository
import wallapp.data.showcase.ShowcaseRepository
import wallapp.data.wallpaper.WallpaperRepository
import wallapp.data.wallpaper.WallpaperStateRepository
import wallapp.deeplink.DeepLinkMapping
import wallapp.di.Lazy
import wallapp.entitlement.EntitlementRepository
import wallapp.license.state.LicenseStateType
import wallapp.license.state.isPlus
import wallapp.log.Log
import wallapp.purchase.PurchasableRepository
import wallapp.random.RandomManager
import wallapp.resources.string.StringRepository
import wallapp.search.content.SearchContentRepository
import wallapp.search.model.SearchRemixMetadata
import wallapp.util.combine
import kotlinx.coroutines.flow.combine as combineFlow

@OptIn(ExperimentalCoroutinesApi::class)
class ContentRepositoryDefault(
    private val contentMediaRepository: ContentMediaRepository,
    private val wallpaperRepository: WallpaperRepository,
    private val wallpaperStateRepository: WallpaperStateRepository,
    private val collectionRepository: CollectionRepository,
    private val artistRepository: ArtistRepository,
    artistStateRepository: ArtistStateRepository,
    private val folderLinkRepository: FolderLinkRepository,
    private val searchContentRepository: SearchContentRepository,
    private val favoriteItemsRepository: FavoriteItemsRepository,
    private val entitlementRepository: EntitlementRepository,
    private val followingRepository: FollowingRepository,
    private val showcaseRepository: ShowcaseRepository,
    private val strings: StringRepository,
    private val randomManager: RandomManager,
    private val purchaseRecordRepository: PurchaseRecordRepository,
    private val purchasableRepositoryLazy: Lazy<PurchasableRepository>,
    @CoroutineScopeMain private val coroutineScopeMain: CoroutineScope,
    @CoroutineScopeIo private val coroutineScopeIo: CoroutineScope,
) : ContentRepository {
    private val purchasableRepository: PurchasableRepository by lazy { purchasableRepositoryLazy.get() }

    private val deterministicRandom
        get() = randomManager.deterministicRandom

    private val purchaseRecords: Flow<List<PurchaseRecord>?>
        get() = purchaseRecordRepository.purchaseRecords

    private val exploreHighlights: Flow<Highlights?>
        get() = showcaseRepository.exploreHighlights

    override val artistStates: Flow<List<ArtistState>?> = combine(
        artistStateRepository.artistStates,
        followingRepository.followStates,
    ) { artistStates, followStates ->
        artistStates
            .map { it.copy(followState = followStates.findById(it.id)) }
    }.stateIn(coroutineScopeMain, started = SharingStarted.WhileSubscribed(), initialValue = null)

    override fun getWallpaperRemix(remixId: RemixId): Flow<WallpaperRemix?> =
        wallpaperRepository.getRemix(remixId)

    override fun getArtistState(
        id: ArtistId,
        getCollections: Boolean,
        getFeed: Boolean,
        getFollowState: Boolean,
    ): Flow<ArtistState?> {
        return getArtistStateStream(
            id = id,
            getCollections = getCollections,
            getFeed = getFeed,
            getFollowState = getFollowState,
        )
    }

//    internal fun getArtistDetailMerged(
//        id: ArtistId,
//        getCollections: Boolean,
//        getFeed: Boolean,
//        getFollowState: Boolean,
//    ): Flow<ArtistDetail?> {
//        val detail = artistDetails.value?.find { it.id == id }
//        requireNotNull(detail) { "ArtistDetail not found for id: $id" }
//
//        val collectionIds = detail.collectionIds
//        val feedItemIds = detail.feedItemIds?.let { Ids(it) }
//
//        val collectionsFlow: Flow<List<Collection>?> = if (getCollections) {
//            if (collectionIds != null) {
//                getCollectionsSnapshot(collectionIds)
//            } else {
//                flow { emit(emptyList()) }
//            }
//        } else {
//            flow { emit(null) }
//        }
//
//        val feedFlow: Flow<List<WallpaperItem>?> = if (getFeed) {
//            if (feedItemIds != null) {
//                wallpaperRepository.getItems(feedItemIds)
//            } else {
//                flow { emit(emptyList()) }
//            }
//        } else {
//            flow { emit(null) }
//        }
//
//        val followStateFlow: Flow<FollowState?> = if (getFollowState) {
//            followingRepository.getFollowState(id)
//        } else {
//            flow { emit(null) }
//        }
//
//        return combine(
//            collectionsFlow,
//            feedFlow,
//            followStateFlow,
//        ) { collections, feedItems, followState ->
//            val remixes = feedItems?.filterIsInstance<WallpaperRemix>()
//            require(remixes?.size == feedItems?.size) {
//                "feedItems must only contain WallpaperRemixes"
//            }
//            ArtistDetail(
//                artist = detail.artist,
//                followState = followState,
//                currentWallpaper = detail.currentWallpaper,
//                collectionIds = detail.collectionIds,
//                collections = collections,
//                feedItemIds = detail.feedItemIds,
//                feedItems = remixes,
//                previewWallpapers = detail.previewWallpapers,
//            )
//        }
//    }

    internal fun getArtistStateStream(
        id: ArtistId,
        getCollections: Boolean,
        getFeed: Boolean,
        getFollowState: Boolean,
    ): Flow<ArtistState?> = channelFlow {

        launch {
            artistStates.collect { details ->
                val detail = details?.find { it.id == id } ?: return@collect

                var cachedCollectionStates: List<CollectionState>? = null
                var cachedFeedItems: List<WallpaperItem>? = null
                var cachedFollowState: FollowState? = detail.followState

                // Emit the initial detail
                send(detail)

                suspend fun emitPartial() {
                    val remixes = cachedFeedItems?.filterIsInstance<WallpaperRemix>()
                    require(remixes?.size == cachedFeedItems?.size) {
                        "feedItems must only contain WallpaperRemixes"
                    }
                    val partialDetail = ArtistState(
                        artist = detail.artist,
                        followState = cachedFollowState,
                        currentWallpaper = detail.currentWallpaper,
                        collectionIds = detail.collectionIds,
                        collectionStates = cachedCollectionStates,
                        feedItemIds = detail.feedItemIds,
                        feedItems = remixes,
                        previewWallpapers = detail.previewWallpapers,
                    )
                    send(partialDetail)
                }

                val collectionIds = detail.collectionIds
                val feedItemIds = detail.feedItemIds?.let { Ids(it) }

                if (getCollections) {
                    val collectionsFlow = if (collectionIds != null) {
                        getCollectionStatesSnapshot(collectionIds)
                    } else {
                        flow { emit(emptyList()) }
                    }
                    launch {
                        collectionsFlow.onEach { collections ->
                            cachedCollectionStates = collections
                            emitPartial()
                        }.collect()
                    }
                }

                if (getFeed) {
                    val feedFlow = if (feedItemIds != null) {
                        wallpaperRepository.getItems(feedItemIds)
                    } else {
                        flow { emit(emptyList()) }
                    }
                    launch {
                        feedFlow.onEach { feedItems ->
                            cachedFeedItems = feedItems
                            emitPartial()
                        }.collect()
                    }
                }

                if (getFollowState) {
                    val followStateFlow = followingRepository.getFollowState(id)
                    launch {
                        followStateFlow.onEach { followState ->
                            cachedFollowState = followState
                            emitPartial()
                        }.collect()
                    }
                }
            }
        }
    }

    private val allWallpaperItems: Flow<List<WallpaperItem>>
        get() = wallpaperRepository.allWallpaperItems
    private val allSingles: Flow<List<Wallpaper>>
        get() = wallpaperRepository.allSingles
    private val artists: StateFlow<List<Artist>>
        get() = artistRepository.artists

    override fun getArtists(wallpaperId: Id): Flow<List<Artist>?> =
        artistRepository.getArtists(wallpaperId)
    
    override fun getArtistConnection(artist: Artist): Flow<ArtistConnectionContentResult?> =
        followingRepository.getFollowState(artist.id).map { followState ->
            ArtistConnectionContentResult(
                artist = artist,
                artistFollowState = followState,
            )
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getWallpaperConnections(wallpaperId: Id): Flow<WallpaperConnectionContentResult?> =
        getArtists(wallpaperId).flatMapMerge { artists ->
            val artist = artists?.firstOrNull()
            if (artist != null) {
                getArtistConnection(artist)
                    .mapNotNull { artistConnection ->
                        if (artistConnection != null) {
                            WallpaperConnectionContentResult(
                                artist = artistConnection.artist,
                                artistFollowState = artistConnection.artistFollowState,
                            )
                        } else {
                            null
                        }
                    }
            } else {
                // See #919
                Log.w("getWallpaperConnections: No artist found for wallpaperId: $wallpaperId")
                flowOf(null)
            }
        }

    override fun getArtistCollectionStates(id: ArtistId): Flow<List<CollectionState>?> =
        getArtistState(id, getCollections = true, getFeed = false, getFollowState = false)
            .map { artistState ->
                artistState
                    ?.collectionStates
                    ?.ifEmpty { null }
//                    ?.also { Log.d("getArtistCollections: ${artistState.id}, results: ${it.size}") }
            }

    private val categories: Flow<List<WallpaperCategory>>
        get() = wallpaperRepository.categories

    private val singleCategories: Flow<List<WallpaperCategory>> =
        categories.map { categories ->
            categories.filter { it.categoryType == WallpaperCategoryType.Singles }
        }

    fun getSingleCategory(artistId: ArtistId): Flow<WallpaperCategory?> = singleCategories
        .map { categories ->
            categories.firstOrNull { it.artistId == artistId }
        }

    override fun getSingles(artistId: ArtistId): Flow<List<WallpaperItem>?> =
        allSingles.map { all ->
            all.filter { it.artistId == artistId }
        }

    override val collectionStates: StateFlow<List<CollectionState>> = combine(
        categories,
        entitlementRepository.licenseState
    ) { categories, _ ->
        categories
    }
        .flatMapMerge { categories ->
            flow {
                // For each category, fetch the collection and emit as a list
                emit(
                    categories.mapNotNull { category ->
                        if (category.categoryType == WallpaperCategoryType.Singles) {
                            null
                        } else {
                            getCollectionState(category.id.collectionId)
                                .firstOrNull()
                        }
                    }
                )
            }
        }.stateIn(
            scope = coroutineScopeMain,
            started = SharingStarted.Eagerly,
            initialValue = emptyList(),
        )

    override fun getCollectionState(id: CollectionId): Flow<CollectionState?> = combine(
        collectionRepository.getCollection(id),
        getWallpaperConnections(id.categoryId),
        purchasableRepository.getCollectionPurchasable(id),
        purchaseRecordRepository.getPurchaseRecord(id),
        entitlementRepository.licenseState,
    ) { collection, wallpaperConnections, purchasable, purchaseRecord, licenseState ->
        if (collection != null && wallpaperConnections != null) {
            val connectionState = mapCollectionConnectionState(
                collectionId = id,
                hasPlus = licenseState.isPlus(),
                purchaseRecord = purchaseRecord,
                isFree = collection.isFree,
            )
            val showAdFreeCollectionLockedInfo = licenseState is LicenseStateType.AdFree
                    && !connectionState.isUnlocked

            CollectionState(
                id = id,
                secondaryId = "",
                wallpapers = collection.wallpapers,
                artist = wallpaperConnections.artist,
                artistFollowState = wallpaperConnections.artistFollowState,
                label = collection.label,
                connectionState = connectionState,
                showAdFreeCollectionLockedInfo = showAdFreeCollectionLockedInfo,
                purchasable = purchasable,
            )
        } else {
            null
        }
    }

    override fun getCollectionsStatesMerged(ids: List<CollectionId>): Flow<List<CollectionState>?> {
        return flow {
            emit(ids)
        }.flatMapMerge { idList ->
            combineFlow(
                idList.map { id -> getCollectionState(id) },
            ) { collections ->
                collections.filterNotNull().toList()
            }
        }
    }

    override fun getCollectionStatesSnapshot(ids: List<CollectionId>): Flow<List<CollectionState>?> {
        return flow {
            val collections = ids.mapNotNull { id ->
                getCollectionState(id).firstOrNull()
            }
            emit(collections)
        }
    }

    // Free collections have no store product, so CollectionPurchasable.orNull drops them here.
    override val collectionPurchasables: Flow<List<CollectionPurchasable>> =
        categories.map { categories ->
            categories.mapNotNull { category ->
                if (category.categoryType != WallpaperCategoryType.Singles) {
                    CollectionPurchasable.orNull(category)
                } else {
                    null
                }
            }
        }

    override fun getCollectionPurchasable(collectionId: CollectionId): Flow<CollectionPurchasable?> =
        wallpaperRepository.getCategory(collectionId.categoryId)
            .map {
            if (it != null && it.categoryType != WallpaperCategoryType.Singles) {
                CollectionPurchasable.orNull(it)
            } else {
                null
            }
        }

    private fun mapCollectionConnectionState(
        collectionId: CollectionId,
        hasPlus: Boolean,
        purchaseRecord: PurchaseRecord,
        isFree: Boolean,
    ): CollectionConnectionState {
        return CollectionConnectionState(
            id = collectionId,
            isPurchased = purchaseRecord.isPurchased,
            isUnlockedViaSubscription = hasPlus,
            isFree = isFree,
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getCollectionScreenContent(
        id: CollectionId,
        firstWallpaperId: WallpaperId?,
    ): Flow<CollectionScreenContentResult?> {
        val collectionStateFlow: Flow<CollectionState?> = getCollectionState(id)
        val artistFlow: Flow<Pair<Artist?, FollowState?>> = collectionStateFlow.map { it?.artist to it?.artistFollowState }
        val artistCollectionsFlow: Flow<List<CollectionState>?> = artistFlow
            .map { it.first }
            .filterNotNull()
            .distinctUntilChanged()
            .flatMapLatest { artist ->
                getArtistCollectionStates(artist.id)
                    .map { collections ->
                        collections?.fold(emptyList()) { cached, item ->
                            cached + item
                        }
                    }
            }
        val collectionPurchasableFlow = purchasableRepository.getCollectionPurchasable(id)
        val purchaseRecordFlow: Flow<PurchaseRecord> = purchaseRecordRepository
            .getPurchaseRecord(id)

        return combine(
            collectionStateFlow,
            collectionPurchasableFlow,
            purchaseRecordFlow,
            entitlementRepository.licenseState,
            artistFlow,
            artistCollectionsFlow,
        ) { unmappedCollectionState, _, _, licenseStateType, artist, artistCollections ->
            val collectionState = mapCollectionState(unmappedCollectionState, firstWallpaperId)

            CollectionScreenContentResult(
                collectionState,
                artist.first,
                artistCollections
                    ?.filter { it.id != collectionState?.id }
                    ?.distinctBy { it.id },
                licenseStateType,
                artist.second,
            )
        }
    }

    private fun mapCollectionState(
        unmappedCollectionState: CollectionState?,
        firstWallpaperId: WallpaperId?,
    ): CollectionState? {
        if (unmappedCollectionState == null) return null
        if (firstWallpaperId == null) return unmappedCollectionState

        val wallpaper = unmappedCollectionState.wallpapers.find { it.id == firstWallpaperId }
        if (wallpaper == null) return unmappedCollectionState

        val updatedWallpapers = (listOf(wallpaper) + unmappedCollectionState.wallpapers).distinct()
        return unmappedCollectionState.copy(
            wallpapers = updatedWallpapers,
        )
    }

    override fun getArtistScreenContent(id: ArtistId): Flow<ArtistScreenContentResult> =
        getArtistState(
            id = id,
            getCollections = true,
            getFeed = true,
            getFollowState = true,
        ).map { ArtistScreenContentResult(it) }

    override val artistsContent: Flow<ArtistsContentResult>
        get() = artistStates.map { ArtistsContentResult(it) }

    private val purchasedCollections: Flow<List<CollectionState>?> =
        combine(
            purchaseRecords,
            collectionRepository.collections
        ) { records, collections ->
            // Proceed only if collections is not null.
            if (collections != null && records != null) {
                records.filter { it.isPurchased == true }
                    .mapNotNull { getCollectionState(it.id).firstOrNull() }
            } else {
                null // Return null if collections or records are not available.
            }
        }

    override val unlockedCollectionsContent: Flow<UnlockedCollectionsContentResult>
        get() = purchasedCollections.map { UnlockedCollectionsContentResult(it) }

    override val exploreContent: Flow<ExploreContentResult> by lazy {
        combine(
            allSingles,
//            getStories(StoriesContentArgument()),
            collectionStates,
//            artistDetails,
            exploreHighlights,
        ) { allSingles, collections, highlights ->
            ExploreContentResult(
                wallpapers = allSingles,
                collections,
                highlights = highlights?.let { listOf(it) },
                topmostIds = emptyList(),
            )
        }
    }

    override val isReady: StateFlow<Boolean>
        get() = combine(
            collectionStates,
            artistStates,
        ) { collections, artistStates ->
            collections.isNotEmpty() && artistStates != null
        }.stateIn(coroutineScopeMain, started = SharingStarted.WhileSubscribed(), initialValue = false)

    private fun getAdditionalWallpapersData(
        category: WallpaperCategory,
        remixId: RemixId,
        canFetchDesignNeighbors: Boolean,
    ): Flow<Pair<List<WallpaperRemix>?, String?>> {
        val categoryId = category.id

        return wallpaperRepository.getRemixesForCategory(
            categoryId,
        ).map { remixes ->
            val data = if (canFetchDesignNeighbors) {
                remixes
            } else {
                remixes?.find { it.id == remixId }?.let { listOf(it) }
            }
            data to category.label
        }
    }

    override fun getUnlockWallpaperContent(remixId: RemixId): Flow<UnlockWallpaperContentResult?> =
        getWallpaperContent(remixId).map { wallpaperContent ->
            val wallpaper = wallpaperContent.wallpaper
            val collection = wallpaperContent.collectionState
            val entitlementState = wallpaperContent.entitlementState

            if (wallpaper == null || entitlementState == null) {
                null
            } else {
                UnlockWallpaperContentResult(
                    wallpaper = wallpaper,
                    remixId = remixId,
                    collectionState = collection,
                    entitlementState = entitlementState,
                )
            }
        }

    override fun getWallpaperContent(remixId: RemixId): Flow<WallpaperContentResult> =
        getWallpaperContentStream(
            remixId,
            emitProgress = true,
        )

    override fun getAllWallpaperContents(
        remixId: RemixId,
        firstWallpaperId: WallpaperId?,
    ): Flow<Map<RemixId, WallpaperContentResult>> {
        return collectionStates.flatMapMerge { collections ->
            val collection = collections.find { collection ->
                collection.wallpapers.any { it.id == remixId }
            }
            collection?.wallpapers?.asFlow()?.flatMapMerge { wallpaper ->
                getWallpaperContent(wallpaper.id).map { wallpaperContentResult ->
                    wallpaperContentResult.copy(
                        collectionState = mapCollectionState(
                            wallpaperContentResult.collectionState,
                            firstWallpaperId,
                        )
                    )
                }
            }
                ?: getWallpaperContent(remixId)
        }.scan(mapOf()) { acc, result ->
            val wallpaperId = result.wallpaper?.id ?: return@scan acc
            acc + (wallpaperId to result)
        }
    }

//    fun getSpotlightContentEmit(
//        designId: DesignId,
//        canFetchDesignNeighbors: Boolean,
//    ): Flow<SpotlightContentResult> = flow {
//        var groupLabel: String? = null
//        var wallpaperItem: WallpaperItem? = null
//        var artist: Collector? = null
//        var artistFollowState: FollowState? = null
//        var additionalWallpapers: List<WallpaperItem>? = null
//        var wallpaperCategory: WallpaperCategory? = null
//        var collection: Collection? = null
//        var additionalCollections: List<Collection>? = null
//
//        suspend fun emit() {
//            emit(SpotlightContentResult(groupLabel, wallpaperItem, artist, artistFollowState,
//                wallpaperCategory, additionalWallpapers, collection, additionalCollections))
//        }
//
//        wallpaperRepository.getItem(designId).collect { wallpaper ->
//            wallpaperItem = wallpaper
//            emit()
//
//            if (wallpaper != null) {
//                getWallpaperConnections(wallpaper.id).collect { connections ->
//                    artist = connections?.artist
//                    artistFollowState = connections?.artistFollowState
//                    emit()
//                }
//            }
//        }
//
//        wallpaperRepository.getCategoriesForDesign(designId).collect { categories ->
//            val category = categories?.firstOrNull()
//            wallpaperCategory = category
//            emit()
//
//            if (category != null) {
//                getCollectionScreenContent(category.id).collect { collectionData ->
//                    collection = collectionData?.collection
//                    additionalCollections = collectionData?.additionalCollections
//                    emit()
//                }
//
//                getAdditionalWallpapersData(
//                    category = category,
//                    designId = designId,
//                    canFetchDesignNeighbors = canFetchDesignNeighbors,
//                ).collect { (wallpapers, _rootLabel) ->
//                    additionalWallpapers = wallpapers
//                    groupLabel = _rootLabel
//                    emit()
//                }
//            }
//        }
//    }.distinctUntilChanged()


//    private fun getWallpaperContentCombined(
//        designId: DesignId,
//        canFetchDesignNeighbors: Boolean,
//    ): Flow<WallpaperContentResult> = flow {
//        val wallpaperItemFlow = wallpaperRepository.getItem(designId)
//        val categoriesFlow = wallpaperRepository.getCategoriesForDesign(designId)
//        val entitlementStateFlow = entitlementRepository.getEntitlementState(designId)
//
//        combine(
//            wallpaperItemFlow,
//            categoriesFlow,
//        ) { wallpaperItem, categories ->
//            val category = categories?.firstOrNull()
//            val categoryId = category?.id
//
//            val collectionScreenContentFlow = if (categoryId != null) {
//                getCollectionScreenContent(categoryId.collectionId)
//            } else {
//                emptyFlow()
//            }
//
//            val additionalWallpapersDataFlow = if (category != null) {
//                getAdditionalWallpapersData(category, designId, canFetchDesignNeighbors)
//            } else {
//                emptyFlow()
//            }
//
//            val connectionsFlow = wallpaperItem?.id?.let { wallpaperId ->
//                getWallpaperConnections(wallpaperId)
//            } ?: emptyFlow()
//
//            combine(
//                connectionsFlow,
//                additionalWallpapersDataFlow,
//                collectionScreenContentFlow,
//                entitlementStateFlow,
//            ) { connections, additionalWallpapersData, collectionScreenContent, entitlementState ->
//                val wallpaper = wallpaperItem?.remix
//                val additionalCollections = collectionScreenContent?.additionalCollections
//                val collection = collectionScreenContent?.collection
//                val additionalWallpapers = additionalWallpapersData.first
//                val groupLabel = additionalWallpapersData.second
//
//                WallpaperContentResult(
//                    groupLabel,
//                    wallpaper,
//                    additionalWallpapers,
//                    connections?.artist,
//                    connections?.artistFollowState,
//                    entitlementState,
//                    collection,
//                    additionalCollections
//                )
//            }
//                .distinctUntilChanged()
//                .collect { result ->
//                    addCachedWallpaperResult(designId, result)
//                    emit(result)
//                }
//        }.collect()
//    }

    private fun getWallpaperContentStream(
        remixId: RemixId,
        emitProgress: Boolean,
    ): Flow<WallpaperContentResult> = channelFlow {

        var cachedWallpaper: WallpaperRemix? = null
        var cachedArtistState: ArtistState? = null
        var cachedEntitlementState: EntitlementState? = null
        var cachedSearchRemixMetadata: SearchRemixMetadata? = null
        var cachedFolderLinks: List<FolderLink>? = null

        suspend fun emit(forceEmit: Boolean = false): WallpaperContentResult? {
            if (!emitProgress && !forceEmit) return null

            val wallpaper = cachedWallpaper
            val artistState = cachedArtistState
            val entitlementState = cachedEntitlementState
            val searchRemixMetadata = cachedSearchRemixMetadata
            val folderLinks = cachedFolderLinks

            val result = wallpaper
                ?.mapWallpaperContentResult(artistState, entitlementState, folderLinks, searchRemixMetadata)
            if (result != null) {
                require(wallpaper.isSingle || (wallpaper.isInCollection && result.collectionState != null)) {
                    "Wallpaper is in a collection, but collection is not found, remixId: $remixId, ${wallpaper.isInCollection}"
                }
                send(result)
            }
            return result
        }

        wallpaperRepository.getItem(remixId).map { wallpaperItem ->
            val wallpaper = wallpaperItem?.remix ?: return@map null
            cachedWallpaper = wallpaper

            val artistId = wallpaper.artistId ?: return@map null
            val artistStateFlow = getArtistStateStream(
                artistId,
                getCollections = true,
                getFeed = false,
                getFollowState = true,
            )

            launch {
                artistStateFlow
                    .onEach { artistState ->
                        cachedArtistState = artistState
                        emit()
                    }
                    .collect()
            }

            launch {
                entitlementRepository.getEntitlementState(remixId)
                    .onEach { entitlementState ->
                        cachedEntitlementState = entitlementState
                        emit()
                    }
                    .collect()
            }

            launch {
                searchContentRepository.getSearchRemixMetadata(remixId)
                    .onEach { searchRemixMetadata ->
                        cachedSearchRemixMetadata = searchRemixMetadata
                        emit()
                    }
                    .collect()
            }

            launch {
                folderLinkRepository.getFolderLinks(remixId)
                    .onEach { folderLinks ->
                        cachedFolderLinks = folderLinks
                        emit()
                    }
                    .collect()
            }
        }.collect()
    }

    override val connectionsContent: Flow<ConnectionsContentResult>
        get() = flow {
            combine(
                favoriteItemsRepository.favoriteWallpapers,
                artistStates,
                unlockedCollectionsContent,
            ) { favorites, artistStates, unlockedCollections ->
                val artists = artistStates
                    ?.filter { it.followState?.isFollowing == true }
                val wallpapers = unlockedCollections.collectionStates
                    ?.flatMap { it.wallpapers }

                ConnectionsContentResult(
                    favorites = favorites,
                    artists = artists,
                    wallpapers = wallpapers,
                    collectionStates = unlockedCollections.collectionStates,
                )
            }.distinctUntilChanged()
                .collect { result ->
                    emit(result)
                }
        }

    override val connectionsSummaryContent: Flow<ConnectionsSummaryContentResult>
        get() = flow {
            connectionsContent
                .distinctUntilChanged()
                .collect { result ->
                    emit(
                        ConnectionsSummaryContentResult(
                            result.favorites?.size,
                            result.artists?.size,
                            result.wallpapers?.size,
                        )
                    )
                }
        }

    override fun getHomeContent(homeContentType: HomeContentType): Flow<HomeContentResult> {
        return when (homeContentType) {
            HomeContentType.Suggested -> { homeContentFeedDefault }
            HomeContentType.Liked -> { homeContentFeedLiked }
            HomeContentType.Purchased -> { homeContentPurchasedFeed }
        }
    }

    override val homePagedContent: Flow<HomePagedContentResult?> by lazy {
        combine(
            homeContentFeedDefault,
            homeContentFeedLiked,
            homeContentPurchasedFeed,
        ) { default, liked, purchased ->
            if (default.isEmpty && liked.isEmpty && purchased.isEmpty) {
                null
            } else {
                HomePagedContentResult(
                    suggestedHomeContent = default,
                    likedHomeContent = liked,
                    purchasedHomeContent = purchased,
                )
            }
        }
    }

    override val suggestedWallpapers: Flow<List<WallpaperRemix>> by lazy {
        wallpaperRepository.allWallpapers.map { remixes ->
            remixes
                .shuffled(deterministicRandom)
                .take(60)
        }
    }

    override val suggestedCollections: Flow<List<CollectionState>> by lazy {
        collectionStates.map { collections ->
            collections
                .shuffled(deterministicRandom)
                .take(60)
        }
    }

    private fun getHomeFeedWallpaperItems(
        artists: List<ArtistState>?,
        allCollectionStates: List<CollectionState>?,
    ): HomeContentResult? {
        if (artists == null) return null

//        val collections = artists.flatMap { it.collections ?: emptyList() }
        // Manually filter for now. Change to the above when #28 is fixed.
        val collections = allCollectionStates
            ?.filter { collection -> collection.artist?.id in artists.map { it.id } }
        val wallpapers = artists.flatMap { it.feedItems ?: emptyList() }

        return HomeContentResult(
            wallpapers = wallpapers,
            collectionStates = collections,
            artists = artists,
            fallbackWallpapers = null,
            fallbackCollectionStates = null,
        )
    }

    /**
     * The content to display based on a user's followers. For each artist followed, all
     * collections and wallpapers for that artist are displayed.
     */
    private val followingHomeContentResult: Flow<HomeContentResult?>
        get() = flow {
            val recentArtists = MutableStateFlow<List<ArtistState>>(emptyList())
            /**
             * The artists to display. This is the [updated] items, plus [recentCollectors].
             * This means if a user unfollows an artist, the item doesn't immediately disappear from view.
             */
            fun getDisplayArtists(
                updated: List<ArtistState>?,
                followStates: List<FollowState>?,
            ): List<ArtistState>? {
                if (updated == null) return null

                return mutableListOf<ArtistState>()
                    .apply {
                        addAll(recentArtists.value.withUpdatedFollowStates(followStates))
                        addAll(updated)
                    }
                    .distinctBy { it.id }
                    .also {
                        recentArtists.value = it
                    }
            }

            combine(
                connectionsContent,
                followingRepository.followStates,
                collectionStates,
//                allWallpaperItems,
            ) { connectionsContent, followStates, collections ->
                val artists = getDisplayArtists(connectionsContent.artists, followStates)
                getHomeFeedWallpaperItems(artists, collections)
            }.distinctUntilChanged()
                .collect { result ->
                    emit(result)
                }
        }

    private val homeContentFeedDefault: Flow<HomeContentResult>
        get() = flow {
            val recentFavoriteWallpaperItems =
                MutableStateFlow<List<Wallpaper>>(emptyList())
            /**
             * The favorites to display. This is the [updatedFavorites], plus [recentWallpaperItems].
             * This means if a user toggles a favorite, the item doesn't immediately disappear from view.
             */
            fun getWallpapers(
                updatedFavorites: List<Wallpaper>?,
                unlockedWallpapers: List<Wallpaper>?,
                followingItems: List<Wallpaper>?,
            ): List<Wallpaper>? {
                val favorites = mutableListOf<Wallpaper>()
                    .apply {
                        addAll(recentFavoriteWallpaperItems.value)
                        if (updatedFavorites != null) {
                            addAll(updatedFavorites)
                        }
                    }
                    .distinctBy { it.id }
                    .also {
                        recentFavoriteWallpaperItems.value = it
                    }
                    .ifEmpty { null }

                return mutableListOf<Wallpaper>()
                    .apply {
                        if (favorites != null) {
                            addAll(favorites)
                        }
                        if (unlockedWallpapers != null) {
                            addAll(unlockedWallpapers)
                        }
                        if (followingItems != null) {
                            addAll(followingItems)
                        }
                    }
                    .distinctBy { it.id }
                    .sortedBy { it.id.name }    // Ensure deterministic order. See #6
                    .ifEmpty { null }
            }

            fun getCollections(
                connectionCollectionStates: List<CollectionState>?,
                followingCollectionStates: List<CollectionState>?,
            ): List<CollectionState>? {
                return mutableListOf<CollectionState>()
                    .apply {
                        if (connectionCollectionStates != null) {
                            addAll(connectionCollectionStates)
                        }
                        if (followingCollectionStates != null) {
                            addAll(followingCollectionStates)
                        }
                    }
                    .distinctBy { it.id }
                    .ifEmpty { null }
            }

            combine(
                connectionsContent,
                followingHomeContentResult,
                suggestedWallpapers,
                suggestedCollections,
                recentFavoriteWallpaperItems,
            ) { connectionsContent, followingHomeContentResult, fallbackWallpapers, fallbackCollections, _ ->
                val wallpapers = getWallpapers(
                    updatedFavorites = connectionsContent.favorites,
                    unlockedWallpapers = connectionsContent.wallpapers,
                    followingItems = followingHomeContentResult?.wallpapers,
                )
                val collections = getCollections(
                    connectionCollectionStates = connectionsContent.collectionStates,
                    followingCollectionStates = followingHomeContentResult?.collectionStates,
                )

                HomeContentResult(
                    wallpapers = wallpapers,
                    collectionStates = collections,
                    artists = null,//getDisplayArtists(connectionsContent.artists, followStates),
                    fallbackWallpapers = fallbackWallpapers,
                    fallbackCollectionStates = fallbackCollections,
                )
            }.distinctUntilChanged()
                .collect { result ->
                    emit(result)
                }
        }

    private val homeContentFeedLiked: Flow<HomeContentResult>
        get() = combine(
            connectionsContent,
            suggestedWallpapers,
        ) { connectionsContent, fallbackWallpapers ->
            HomeContentResult(
                wallpapers = connectionsContent.favorites,
                collectionStates = null,
                artists = null,
                fallbackWallpapers = fallbackWallpapers,
                fallbackCollectionStates = null,
            )
        }

    private val homeContentPurchasedFeed: Flow<HomeContentResult>
        get() = combine(
            unlockedCollectionsContent,
            suggestedCollections,
        ) { unlockedCollectionsContent, fallbackCollections ->
            HomeContentResult(
                wallpapers = null,
                collectionStates = unlockedCollectionsContent.collectionStates,
                artists = null,
                fallbackWallpapers = null,
                fallbackCollectionStates = fallbackCollections,
            )
        }

    override val signUpContentResult: Flow<SignUpContentResult>
        get() = showcaseRepository.signUpWallpapers
            .map { wallpapers ->
                SignUpContentResult(wallpapers)
            }

    override val deepLinkMappings: Flow<List<DeepLinkMapping>> = combine(
        artists,
        allWallpaperItems,
    ) { artists, allWallpaperItems ->
        artists.mapToDeepLinkMappings() + allWallpaperItems.mapToDeepLinkMappings()
    }

    init {
        Log.d("ContentRepositoryDefault: init")
    }
}

/**
 * Intended for use in tests.
 */
suspend fun ContentRepositoryDefault.suspendUntilNonEmpty() {
    artistStates.first { it?.isNotEmpty() == true }
    collectionStates.first { it.isNotEmpty() }
}