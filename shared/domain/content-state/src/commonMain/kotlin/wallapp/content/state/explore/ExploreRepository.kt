package wallapp.content.state.explore

import kotlinx.coroutines.flow.Flow
import wallapp.content.state.ContentStateFeed
import wallapp.content.state.paging.ContentStatePagingController

interface ExploreRepository {

    val contentStateFeed: Flow<ContentStateFeed?>

    val contentStatePagingController: ContentStatePagingController
}