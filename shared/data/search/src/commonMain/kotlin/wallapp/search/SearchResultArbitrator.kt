package wallapp.search

import wallapp.content.model.ContentCategory
import wallapp.search.model.SearchCategory
import wallapp.search.model.SearchColor

object SearchResultArbitrator {

//    fun validateSearchArguments(
//        searchArguments: List<SearchArguments>,
//        contentTypes: List<SearchContentType>,
//    ): List<SearchArguments> {
//        return searchArguments
//            .map { it.copy(contentTypes = contentTypes) }
//    }

    fun arbitrateSearchContentTypes(
        contentCategories: List<Pair<ContentCategory, Boolean>>,
        colors: List<SearchColor>,
        categories: List<SearchCategory>,
    ): SearchContentTypes {
        return arbitrateSearchContentTypes(contentCategories)
    }

    fun arbitrateSearchContentTypes(
        contentCategoriesPairs: List<Pair<ContentCategory, Boolean>>,
    ): SearchContentTypes {
        val selectedContentTypes = contentCategoriesPairs
            .filter { it.second }
            .map { it.first }

        return when {
            selectedContentTypes.contains(ContentCategory.Singles) &&
                    selectedContentTypes.contains(ContentCategory.Collection)-> {
                SearchContentType.AllWallpapers
            }
            selectedContentTypes.isEmpty() -> {
                SearchContentType.AllWallpapers
            }
            selectedContentTypes == listOf(ContentCategory.Singles) -> {
                SearchContentTypes(SearchContentType.Singles)
            }
            selectedContentTypes == listOf(ContentCategory.Collection) -> {
                SearchContentTypes(SearchContentType.Tracks)
            }
            else -> {
                throw IllegalStateException("Invalid content categories: $selectedContentTypes")
            }
        }
    }
}