package wallapp.search

import wallapp.log.Log
import wallapp.search.SearchMatchStrategy.PartialMatchContains
import wallapp.search.SearchMatchStrategyDefinitions.ExactAndPartialStartAny
import wallapp.search.SearchMatchStrategyDefinitions.Fuzzy1Step
import wallapp.search.model.SearchCategory
import wallapp.search.model.SearchColor
import kotlin.jvm.JvmName

object SearchArgumentMapper {

    /**
     * Map to a string query that can be passed to [SearchQueryManager].
     */
    @JvmName("mapSearchArgumentsSearchCategory")
    fun mapSearchArguments(
        data: List<SearchCategory>?,
        searchContentTypes: SearchContentTypes,
    ): List<SearchArguments>? {
        return data
            ?.map {
                SearchArguments(
                    query = it.key,
                    searchContentTypes = searchContentTypes,
                    dataTypes = listOf(SearchDataType.Categories),
                    filterByQueryQuality = false,
                )
            }
            ?.ifEmpty { null }
    }

    @JvmName("asSearchArgumentsSearchColor")
    fun mapSearchArguments(
        data: List<SearchColor>?,
        searchContentTypes: SearchContentTypes,
    ): List<SearchArguments>? {
        return data
            ?.map {
                SearchArguments(
                    query = it.key.lowercase(),
                    searchContentTypes = searchContentTypes,
                    dataTypes = listOf(SearchDataType.Colors),
                    filterByQueryQuality = false,
                )
            }
            ?.ifEmpty { null }
    }

    fun mapSearchArguments(
        query: String?,
        searchContentTypes: SearchContentTypes,
        searchColors: List<SearchColor>?,
        searchCategories: List<SearchCategory>?,
    ): List<SearchArguments> {
        Log.d("getSearchQueryResult(): query: $query, searchContentTypes: $searchContentTypes, searchColors: $searchColors, searchCategories: $searchCategories")
        val querySearchArguments = mapQuerySearchArguments(
            query,
            searchContentTypes,
            searchColors,
            searchCategories,
            filterByQueryQuality = true,
        )
        val categoriesSearchArguments = mapSearchArguments(searchCategories, searchContentTypes)
        val colorsSearchArguments = mapSearchArguments(searchColors, searchContentTypes)
        val arguments = listOfNotNull(
            querySearchArguments?.let { listOf(it) },
            categoriesSearchArguments,
            colorsSearchArguments,
        ).flatten()

        return arguments
    }

    fun mapQuerySearchArguments(
        query: String?,
        searchContentTypes: SearchContentTypes,
        searchColors: List<SearchColor>?,
        searchCategories: List<SearchCategory>?,
        filterByQueryQuality: Boolean,
    ): SearchArguments? {
        if (query.isNullOrEmpty()) {
            require(filterByQueryQuality) { "If not passing a query, filterByQueryQuality must be true" }
        }

        return when {
            // If this is a wallpaper only search, and no colors or categories are selected,
            // use requireValidQuery = false to allow all content to display.
            searchContentTypes.isOnlyWallpapers
                    && searchColors.isNullOrEmpty()
                    && searchCategories.isNullOrEmpty() -> {
                SearchArguments.from(
                    query = query,
                    searchContentTypes = searchContentTypes,
                    filterByQueryQuality = filterByQueryQuality,
                    requireValidQuery = false,
                )!!
            }

            else -> {
                SearchArguments.from(
                    query = query,
                    searchContentTypes = searchContentTypes,
                    filterByQueryQuality = filterByQueryQuality,
                    requireValidQuery = true,
                )
            }
        }
    }

    fun mapSearchMatchStrategiesForQuery(
        query: String,
        searchContentTypes: SearchContentTypes
    ): List<SearchMatchStrategies> {
        return when {
            searchContentTypes.isOnlyWallpapers -> mapSearchMatchStrategiesForQueryWallpapers(query)
            searchContentTypes.isOnlyCurators -> mapSearchMatchStrategiesForQueryCurator(query)
            else -> throw IllegalArgumentException("Unsupported content types: $searchContentTypes")
        }
    }

    fun mapSearchMatchStrategiesForQueryWallpapers(query: String): List<SearchMatchStrategies> {
        if (query.length <= 2) {
            return listOf(ExactAndPartialStartAny)
        }

        return listOf(
            ExactAndPartialStartAny,
            SearchMatchStrategies(PartialMatchContains),
            Fuzzy1Step,
        )
    }

    fun mapSearchMatchStrategiesForQueryCurator(query: String): List<SearchMatchStrategies> {
        if (query.length <= 3) {
            return listOf(ExactAndPartialStartAny)
        }

        return listOf(
            ExactAndPartialStartAny,
            Fuzzy1Step,
        )
    }
}