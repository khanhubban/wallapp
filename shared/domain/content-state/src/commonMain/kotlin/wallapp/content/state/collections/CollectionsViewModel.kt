package wallapp.content.state.collections


// Note: usage will require `contentRepository.collectionGroups` to be populated.
//class CollectionsViewModel(
//    contentRepository: ContentRepository,
//    viewStateRefresher: ViewStateRefresher,
//    private val viewFactory: ViewFactory,
//    private val viewSpecFactory: ViewSpecFactory,
//    private val feedFormatter: FeedFormatter,
//) : ViewModel() {
//
//    private fun createViewState(
//        collectionGroups: List<CollectionGroup>?,
//    ): CollectionsViewState {
//        return if (collectionGroups.isNullOrEmpty()) {
//            CollectionsViewState.Loading
//        } else {
//            val rowSpacer = viewFactory.createSpacer(width = 16.dp, height = null)
//            val views = collectionGroups
//                .flatMap { viewFactory.createCollectionGroup(it) }
//                .map {
//                    val viewState = it.viewState
//                    if (viewState is HorizontalScrollRowViewState) {
//                        View(
//                            viewState = viewState.copy(
//                                views = feedFormatter.insertSpacing(
//                                    views = viewState.views,
//                                    spacer = rowSpacer,
//                                    spacerStart = rowSpacer,
//                                    spacerEnd = rowSpacer,
//                                ),
//                            ),
//                            viewSpec = it.viewSpec,
//                        )
//                    } else {
//                        it
//                    }
//                }
//
//            CollectionsViewState.Ready(
//                feedViewState = FeedViewState(
//                    feedViewSpec = viewSpecFactory.feedViewSpec,
//                    views = feedFormatter.format(
//                        views = views,
//                        spacerStart = FeedFormatter.FeedSpacerTopToolbar,
//                        spacerEnd = FeedFormatter.FeedSpacerBottomNavigationBar,
//                        canExpandItemWidth = true,
//                        canInsertAds = false,
//                    ),
//                ),
//            )
//        }
//    }
//
//    val viewState: StateFlow<CollectionsViewState> = combine(
//        contentRepository.collectionGroups,
//        viewStateRefresher.refresh,
//    ) { collectionGroups, _ ->
//        createViewState(collectionGroups)
//    }.stateIn(createViewState(null), startWhileSubscribedNetwork = true)
//
//    override fun onCleared() { }
//}