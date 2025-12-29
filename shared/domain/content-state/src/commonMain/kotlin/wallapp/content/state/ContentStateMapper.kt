package wallapp.content.state

import wallapp.data.content.ContentResult.ExploreContentResult

interface ContentStateMapper {

    fun mapExplore(explore: ExploreContentResult): ContentStateFeed?

    fun mapExploreForChunks(explore: ExploreContentResult): ContentStateFeed?
}