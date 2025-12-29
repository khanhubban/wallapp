package wallapp.search.filter

import wallapp.content.model.Id
import wallapp.content.model.Wallpaper
import wallapp.content.model.WallpaperCategory
import wallapp.content.model.WallpaperId
import wallapp.content.model.WallpaperItem
import wallapp.data.artist.Artist
import wallapp.data.folder.Folder
import wallapp.search.SearchArguments
import wallapp.search.SearchDataType
import wallapp.search.SearchMatchStrategies
import wallapp.search.SearchMatchStrategy
import wallapp.search.SearchQueryItemResult
import wallapp.search.SearchQueryManagerDefault.Companion.Log
import wallapp.search.SearchQueryResult
import wallapp.search.containsArtist
import wallapp.search.containsCategories
import wallapp.search.containsCollectionTitle
import wallapp.search.containsColors
import wallapp.search.containsSearchTerms
import wallapp.search.containsTags
import wallapp.search.containsTitle
import wallapp.search.fuzzymatcher.FuzzyMatcher
import wallapp.search.model.SearchArtistMetadata
import wallapp.search.model.SearchFilterEntry
import wallapp.search.model.SearchFilterMatch
import wallapp.search.model.SearchFilterResult
import wallapp.search.model.SearchFolderMetadata
import wallapp.search.model.SearchRemixMetadata
import wallapp.utils.sumByFloat
import kotlin.jvm.JvmName

object SearchFilterArbitrator {

    // Must choose a length that supports "3d"
    private const val MinLength = 1

    val SearchDebugRemixId: WallpaperId? = null

    object Weight {
        const val ExactMatch = 1f
        const val PartialMatchStartFirst = .9f
        const val PartialMatchStartNonFirst = .87f
        const val PartialMatchContains = .7f
        const val CuratorMatch = .4f
        const val FuzzyMatch = .2f
    }

    object WeightScale {
        const val Artist = 1.6f
        const val Title = 1.5f
        const val CollectionTitle = 1.4f
        const val Categories = 1.3f
        const val Color = 1f
        const val SearchTerms = 1f
        const val Tags = 1f
    }

    fun List<SearchFilterMatch>.sorted(): List<SearchFilterMatch> {
        return this.sortedByDescending { it.weight }
    }

    private fun List<SearchFilterMatch>?.asSearchFilterResult(): SearchFilterResult {
        return if (this.isNullOrEmpty()) {
            SearchFilterResult.NoResults
        } else {
            SearchFilterResult.Results(this)
        }
    }

//    fun combineExactAndFuzzy(
//        exact: List<SearchFilterResult>?,
//        fuzzy: List<SearchFilterResult>?,
//    ): List<SearchFilterResult>? {
//        val distinctFuzzy = fuzzy?.mapNotNull { f ->
//            if (exact != null && exact.any { it.id == f.id }) {
//                null
//            } else {
//                f
//            }
//        }
//
//        return ((exact ?: emptyList()) + (distinctFuzzy ?: emptyList()))
//            .distinct()
//            .sorted()
//            .ifEmpty { null }
//    }

    fun mergeSearchFilterResults(
        exact: List<SearchFilterMatch>?,
        fuzzy: List<SearchFilterMatch>?,
    ): List<SearchFilterMatch>? {
        return exact.merge(fuzzy)
    }

    private fun List<SearchFilterMatch>?.filterByQuality(
        filterByQueryQuality: Boolean,
    ): List<SearchFilterMatch>? {
        if (this.isNullOrEmpty()) return null
        val sorted = this.sorted()
        if (!filterByQueryQuality) {
            return sorted.ifEmpty { null }
        }

        val lowQualityMatchWeight = Weight.FuzzyMatch
        val highQualityMatches = sorted.filter { it.weight > lowQualityMatchWeight }
        val lowQualityMatches = sorted.filter { it.weight <= lowQualityMatchWeight }
        if (highQualityMatches.isNotEmpty()) {
            return highQualityMatches.also {
                Log.d("filterByQuality(): keeping ${highQualityMatches.size} highQualityMatches, dropping ${lowQualityMatches.size} matches")
            }
        }
        return lowQualityMatches.ifEmpty { null }
    }

    private fun merge(vararg lists: List<SearchFilterMatch>?): List<SearchFilterMatch>? {
        val filtered = lists.filterNotNull()
        if (filtered.isEmpty()) return null
        if (filtered.size == 1) return filtered.first()

        return filtered
            .reduce { acc, list -> merge(acc, list) ?: listOf() }
            .ifEmpty { null }
    }

    @JvmName("mergeExt")
    private fun List<SearchFilterMatch>?.merge(
        otherResults: List<SearchFilterMatch>?
    ): List<SearchFilterMatch>? {
        return merge(this, otherResults)
    }

    private fun merge(
        a: List<SearchFilterMatch>?,
        b: List<SearchFilterMatch>?,
    ): List<SearchFilterMatch>? {
        if (a == null && b == null) return null
        if (a == null) return b
        if (b == null) return a

        val resultMap = mutableMapOf<Id, SearchFilterMatch>()

        a.forEach { result ->
            resultMap[result.id] = result
        }

        b.forEach { result ->
            val existingResult = resultMap[result.id]
            if (existingResult != null) {
                require(existingResult is SearchFilterMatch.EntryMatch && result is SearchFilterMatch.EntryMatch)
                // Combine weights if the entry already exists
                resultMap[result.id] = SearchFilterMatch.EntryMatch(
                    entries = existingResult.entries,
                    weight = existingResult.weight + result.weight
                )
            } else {
                resultMap[result.id] = result
            }
        }

        return resultMap.values
            .toList()
            .sorted()
    }

    fun filterBy(
        query: String,
        entry: SearchFilterEntry,
        searchMatchStrategies: SearchMatchStrategies,
    ): Float? {
        searchMatchStrategies.strategies.forEach {
            val weight = filterBy(query, entry, it)
            if (weight != null) return weight
        }
        return null
    }

    fun filterBy(
        query: String,
        entry: SearchFilterEntry,
        searchMatchStrategy: SearchMatchStrategy,
    ): Float? {
        val term = entry.term
        return when (searchMatchStrategy) {
            SearchMatchStrategy.ExactMatch -> {
                if (term == query) {
                    entry.relevance * Weight.ExactMatch
                } else {
                    null
                }
            }

            SearchMatchStrategy.PartialMatchStartAny -> {
                val terms = term.split(" ")
                val first = terms.firstOrNull()
                val others = terms.drop(1)
                if (first?.startsWith(query, ignoreCase = true) == true) {
                    entry.relevance * Weight.PartialMatchStartFirst
                } else if (others.any { it.startsWith(query, ignoreCase = true) }) {
                    entry.relevance * Weight.PartialMatchStartNonFirst
                } else {
                    null
                }
            }

            SearchMatchStrategy.PartialMatchContains -> {
                if (term.contains(query, ignoreCase = true)) {
                    entry.relevance * Weight.PartialMatchContains
                } else {
                    null
                }
            }

            is SearchMatchStrategy.FuzzyMatch -> {
                filterByFuzzyMatch(query, term, threshold = searchMatchStrategy.thresholdAllowed)
                    ?.let { it * entry.relevance }
            }
        }
    }

    internal fun filterBy(
        query: String,
        weightScale: Float,
        searchMatchStrategies: SearchMatchStrategies,
        dataList: List<List<SearchFilterEntry>>,
    ): List<SearchFilterMatch>? {
        if (dataList.isEmpty()) return null
        return dataList
            .mapNotNull { filterBy(query, it, weightScale = weightScale, searchMatchStrategies) }
            .flatten()
            .sorted()
            .ifEmpty { null }
    }

    @Suppress("FoldInitializerAndIfToElvis")
    fun filterBy(
        query: String,
        data: List<SearchFilterEntry>,
        weightScale: Float,
        searchMatchStrategies: SearchMatchStrategies,
    ): List<SearchFilterMatch.EntryMatch>? {
        return data.mapNotNull { entry: SearchFilterEntry ->
            val weight = filterBy(query, entry, searchMatchStrategies)
//            if (entry.id == SearchDebugRemixId) {
//                Log.d("filterBy(): query=$query, entry: ${entry.toString(includeId = true)}, weight: $weight")
//                filterBy(query, entry, searchMatchStrategies)
//            }
            if (weight == null) {
                return@mapNotNull null
            }
            SearchFilterMatch.EntryMatch(entry, weight * weightScale)
        }
            .groupBy { it.id }
            .mapNotNull { (_, entries) ->
                val totalWeight = entries.sumByFloat { it.weight }
                val entryIds = entries.map { it.id }
                require(entryIds.distinct().size == 1) { "All entries must have the same id, ids: $entryIds" }
                entries.first().copy(weight = totalWeight)
            }
            .sortedByDescending { it.weight }
            .ifEmpty { null }
    }

//    internal fun filterByFuzzyMatch(query: String, data: List<SearchRemixMetadata>): List<SearchFilterResult>? {
//        return data.flatMap { metadata ->
//            val searchTerms = metadata.searchTerms.map { it.term }
//            searchTerms.mapNotNull { term ->
//                filterByFuzzyMatch(query, term)?.let { weight ->
//                    SearchFilterResult(SearchFilterEntry(metadata.remixId, term), weight)
//                }
//            }
//        }
//    }

    fun filterByFuzzyMatch(
        query: String,
        term: String,
        threshold: Int,
    ): Float? {
        val distance = FuzzyMatcher.levenshteinDistance(query, term)
        val passes = distance <= threshold
        return if (passes) {
            Weight.FuzzyMatch / (distance.toFloat())
        } else {
            null
        }
    }

    private fun validateQuery(query: String): String {
        return query.trim()
    }

    /**
     * Returns a validated query if it meets the minimum length requirement.
     **/
    fun validatedQuery(query: String): String? {
        val validatedQuery = validateQuery(query)
        if (validatedQuery.length < MinLength) return null
        return validatedQuery
    }


    /**
     * [wallpapers]: A list of items to filter. Not guaranteed to be exhaustive.
     */
    @JvmName("filterWallpapers")
    fun filterWallpapers(
        searchArguments: SearchArguments,
        allSearchRemixMetadata: List<SearchRemixMetadata>,
        wallpapers: List<WallpaperItem>,
        searchMatchStrategies: SearchMatchStrategies,
    ): SearchQueryResult {
        val query = searchArguments.query
        val dataTypes = searchArguments.dataTypes
        val filterByQueryQuality = searchArguments.filterByQueryQuality
        Log.d("search(): query=$query, dataTypes: $dataTypes, items to filter: ${allSearchRemixMetadata.size}")
        val searchFilterResult = SearchFilterArbitrator
            .filterRemixResults(
                data = allSearchRemixMetadata,
                query = query,
                dataTypes = dataTypes,
                searchMatchStrategies = searchMatchStrategies,
                filterByQueryQuality = filterByQueryQuality,
            )

        return when (searchFilterResult) {
            is SearchFilterResult.Results -> {
                val filteredResults = searchFilterResult.matches
                val itemResults = filteredResults.mapNotNull { result ->
                    val searchRemixMetadata = allSearchRemixMetadata.find { it.remixId == result.id }
                    requireNotNull(searchRemixMetadata)
                    val wallpaperItem = wallpapers.find { it.id == result.id }
                        ?: return@mapNotNull null
                    require(wallpaperItem is Wallpaper)
                    SearchQueryItemResult.ResultWallpaper(
                        wallpaperItem,
                        result,
                        searchMetadata = searchRemixMetadata
                    )
                }

                Log.d("search(): query=$query, results=${itemResults.size}, items filtered: ${filteredResults.size}")
                itemResults.forEach {
                    Log.v("search(): query=$query, result: ${it.searchFilterMatch.debugString}, ${it.wallpaper.previewImages.mediaHolder}")
                }

                SearchQueryResult.Results(itemResults)
            }
            is SearchFilterResult.NoResults -> {
                SearchQueryResult.NoResults
            }
            is SearchFilterResult.InvalidQuery -> {
                if (!searchArguments.requireValidQuery) {
                    SearchQueryResult.Results(wallpapers.mapPlacementSearchQueryItemResults())
                } else {
                    SearchQueryResult.Inactive
                }
            }
        }
    }

    fun filterRemixResults(
        data: List<SearchRemixMetadata>,
        query: String,
        dataTypes: List<SearchDataType>,
        searchMatchStrategies: SearchMatchStrategies,
        filterByQueryQuality: Boolean = true,
    ): SearchFilterResult {
        return data.filterRemix(
            query = query,
            dataTypes = dataTypes,
            searchMatchStrategies = searchMatchStrategies,
            filterByQueryQuality = filterByQueryQuality,
        )
    }

    @JvmName("filterExtRemix")
    private fun List<SearchRemixMetadata>.filterRemix(
        query: String,
        dataTypes: List<SearchDataType>,
        searchMatchStrategies: SearchMatchStrategies,
        filterByQueryQuality: Boolean = true,
    ): SearchFilterResult {
        val validatedQuery = validatedQuery(query)
            ?: return SearchFilterResult.InvalidQuery

        fun filterBy(condition: Boolean, weightScale: Float, dataList: List<List<SearchFilterEntry>>): List<SearchFilterMatch>? {
            return if (condition) {
                filterBy(validatedQuery, weightScale, searchMatchStrategies, dataList)
            } else {
                null
            }
        }

        val artistResults = filterBy(dataTypes.containsArtist, WeightScale.Artist, map { it.artistNames })
        val titleResults = filterBy(dataTypes.containsTitle, WeightScale.Title, map { listOf(it.title) })
        val collectionTitleResults = filterBy(dataTypes.containsCollectionTitle, WeightScale.CollectionTitle, map { listOfNotNull(it.collectionTitle) })
        val categoriesResults = filterBy(dataTypes.containsCategories, WeightScale.Categories, map { it.categories })
        val searchTermsResults = filterBy(dataTypes.containsSearchTerms, WeightScale.SearchTerms, map { it.searchTerms} )
        val tagResults = filterBy(dataTypes.containsTags, WeightScale.Tags, map { it.tags })
        val colorResults = filterBy(dataTypes.containsColors, WeightScale.Color, map { it.colors })
        return merge(
            artistResults,
            titleResults,
            collectionTitleResults,
            categoriesResults,
            searchTermsResults,
            tagResults,
            colorResults
        )
            .filterByQuality(filterByQueryQuality)
            .asSearchFilterResult()
    }

    @JvmName("mapPlacementSearchQueryItemResultsWallpaper")
    private fun List<WallpaperItem>.mapPlacementSearchQueryItemResults(): List<SearchQueryItemResult> =
        map { wallpaperItem ->
            val searchFilterMatch = SearchFilterMatch.ManualPlacement(wallpaperItem.id)
            when (wallpaperItem) {
                is Wallpaper -> {
                    SearchQueryItemResult.ResultWallpaper(
                        wallpaperItem,
                        searchFilterMatch,
                        searchMetadata = null,
                    )
                }
                is WallpaperCategory -> {
                    SearchQueryItemResult.ResultCollection(
                        category = wallpaperItem,
                        wallpaper = wallpaperItem.previewRemix,
                        searchFilterMatch,
                        searchMetadata = null,
                    )
                }
                else -> throw IllegalStateException("Unhandled WallpaperItem type: $wallpaperItem")
            }
        }

    /**
     * [artists]: A list of items to filter. Not guaranteed to be exhaustive.
     */
    suspend fun filterArtists(
        searchArguments: SearchArguments,
        allSearchArtistMetadata: List<SearchArtistMetadata>,
        artists: List<Artist>,
        searchMatchStrategies: SearchMatchStrategies,
    ): SearchQueryResult {
        val query = searchArguments.query
        val dataTypes = searchArguments.dataTypes
        val filterByQueryQuality = searchArguments.filterByQueryQuality
        Log.d("search(): query=$query, dataTypes: $dataTypes, items to filter: ${allSearchArtistMetadata.size}")
        val searchFilterResult = filterArtistResults(
                data = allSearchArtistMetadata,
                query = query,
                dataTypes = dataTypes,
                searchMatchStrategies = searchMatchStrategies,
                filterByQueryQuality = filterByQueryQuality,
            )

        return when (searchFilterResult) {
            is SearchFilterResult.Results -> {
                val filteredResults = searchFilterResult.matches
                val itemResults = filteredResults.mapNotNull { result ->
                    val metadata = allSearchArtistMetadata.find { it.artistId == result.id }
                    requireNotNull(metadata)
                    val artist = artists.find { it.id == result.id }
                        ?: return@mapNotNull null
                    SearchQueryItemResult.ResultCurator(
                        artist,
                        result,
                        searchMetadata = metadata
                    )
                }

                Log.d("search(): query=$query, results=${itemResults.size}, items filtered: ${filteredResults.size}")
                itemResults.forEach {
                    Log.v("search(): query=$query, result: ${it.searchFilterMatch.debugString}")
                }

                SearchQueryResult.Results(itemResults)
            }
            is SearchFilterResult.NoResults -> {
                SearchQueryResult.NoResults
            }
            is SearchFilterResult.InvalidQuery -> {
                if (!searchArguments.requireValidQuery) {
                    SearchQueryResult.Results(artists.mapPlacementSearchQueryItemResults())
                } else {
                    SearchQueryResult.Inactive
                }
            }
        }
    }

    fun filterArtistResults(
        data: List<SearchArtistMetadata>,
        query: String,
        dataTypes: List<SearchDataType>,
        searchMatchStrategies: SearchMatchStrategies,
        filterByQueryQuality: Boolean = true,
    ): SearchFilterResult {
        return data.filterArtist(
            query = query,
            dataTypes = dataTypes,
            searchMatchStrategies = searchMatchStrategies,
            filterByQueryQuality = filterByQueryQuality,
        )
    }

    @JvmName("filterExtArtist")
    private fun List<SearchArtistMetadata>.filterArtist(
        query: String,
        dataTypes: List<SearchDataType>,
        searchMatchStrategies: SearchMatchStrategies,
        filterByQueryQuality: Boolean = true,
    ): SearchFilterResult {
        val validatedQuery = validatedQuery(query)
            ?: return SearchFilterResult.InvalidQuery

        fun filterBy(condition: Boolean, weightScale: Float, dataList: List<List<SearchFilterEntry>>): List<SearchFilterMatch>? {
            return if (condition) {
                filterBy(validatedQuery, weightScale, searchMatchStrategies, dataList)
            } else {
                null
            }
        }

        val artistResults = filterBy(dataTypes.containsArtist, WeightScale.Artist, map { it.artistNames })
        return merge(artistResults)
            .filterByQuality(filterByQueryQuality)
            .asSearchFilterResult()
    }

    @JvmName("mapPlacementSearchQueryItemResultsArtist")
    private fun List<Artist>.mapPlacementSearchQueryItemResults(): List<SearchQueryItemResult> =
        map { artist ->
            val searchFilterMatch = SearchFilterMatch.ManualPlacement(artist.id)
            SearchQueryItemResult.ResultCurator(
                artist,
                searchFilterMatch,
                searchMetadata = null,
            )
        }

    fun filterFolderResults(
        data: List<SearchFolderMetadata>,
        query: String,
        dataTypes: List<SearchDataType>,
        searchMatchStrategies: SearchMatchStrategies,
        filterByQueryQuality: Boolean = true,
    ): SearchFilterResult {
        return data.filterFolder(
            query = query,
            dataTypes = dataTypes,
            searchMatchStrategies = searchMatchStrategies,
            filterByQueryQuality = filterByQueryQuality,
        )
    }

    @JvmName("filterExtFolder")
    private fun List<SearchFolderMetadata>.filterFolder(
        query: String,
        dataTypes: List<SearchDataType>,
        searchMatchStrategies: SearchMatchStrategies,
        filterByQueryQuality: Boolean = true,
    ): SearchFilterResult {
        val validatedQuery = validatedQuery(query)
            ?: return SearchFilterResult.InvalidQuery

        fun filterBy(condition: Boolean, weightScale: Float, dataList: List<List<SearchFilterEntry>>): List<SearchFilterMatch>? {
            return if (condition) {
                filterBy(validatedQuery, weightScale, searchMatchStrategies, dataList)
            } else {
                null
            }
        }

        val results = filterBy(dataTypes.containsTitle, WeightScale.Title, map { it.folderNames })
        return merge(results)
            .filterByQuality(filterByQueryQuality)
            .asSearchFilterResult()
    }

    /**
     * [folders]: A list of items to filter. Not guaranteed to be exhaustive.
     */
    suspend fun filterFolders(
        searchArguments: SearchArguments,
        allSearchFolderMetadata: List<SearchFolderMetadata>,
        folders: List<Folder>,
        searchMatchStrategies: SearchMatchStrategies,
    ): SearchQueryResult {
        val query = searchArguments.query
        val dataTypes = searchArguments.dataTypes
        val filterByQueryQuality = searchArguments.filterByQueryQuality
        Log.d("search(): query=$query, dataTypes: $dataTypes, items to filter: ${allSearchFolderMetadata.size}")
        val searchFilterResult = filterFolderResults(
                data = allSearchFolderMetadata,
                query = query,
                dataTypes = dataTypes,
                searchMatchStrategies = searchMatchStrategies,
                filterByQueryQuality = filterByQueryQuality,
            )

        return when (searchFilterResult) {
            is SearchFilterResult.Results -> {
                val filteredResults = searchFilterResult.matches
                val itemResults = filteredResults.mapNotNull { result ->
                    val metadata = allSearchFolderMetadata.find { it.folderId == result.id }
                    requireNotNull(metadata)
                    val folder = folders.find { it.id == result.id }
                        ?: return@mapNotNull null
                    SearchQueryItemResult.ResultCurator(
                        folder,
                        result,
                        searchMetadata = metadata
                    )
                }

                Log.d("search(): query=$query, results=${itemResults.size}, items filtered: ${filteredResults.size}")
                itemResults.forEach {
                    Log.v("search(): query=$query, result: ${it.searchFilterMatch.debugString}")
                }

                SearchQueryResult.Results(itemResults)
            }
            is SearchFilterResult.NoResults -> {
                SearchQueryResult.NoResults
            }
            is SearchFilterResult.InvalidQuery -> {
                if (!searchArguments.requireValidQuery) {
                    SearchQueryResult.Results(folders.mapPlacementSearchQueryItemResults())
                } else {
                    SearchQueryResult.Inactive
                }
            }
        }
    }

    @JvmName("mapPlacementSearchQueryItemResultsFolder")
    private fun List<Folder>.mapPlacementSearchQueryItemResults(): List<SearchQueryItemResult> =
        map { folder ->
            val searchFilterMatch = SearchFilterMatch.ManualPlacement(folder.id)
            SearchQueryItemResult.ResultCurator(
                folder,
                searchFilterMatch,
                searchMetadata = null,
            )
        }
}