package wallapp.search.model

import wallapp.content.model.Id
import wallapp.search.model.SearchFilterEntry.Companion.MaxRelevance
import wallapp.utils.sumByFloat

sealed class SearchFilterMatch {

    abstract val id: Id
    abstract val weight: Float
    abstract val debugString: String

    data class EntryMatch(
        val entries: List<SearchFilterEntry>,
        override val weight: Float,
    ): SearchFilterMatch() {
        constructor(entry: SearchFilterEntry, weight: Float) : this(listOf(entry), weight)

        override val id: Id by lazy { entries.first().id }
        val terms: List<String> by lazy { entries.map { it.term } }
        val relevance: Float by lazy { entries.sumByFloat { it.relevance } }

        override val debugString: String
            get() = "EntryMatch(id=${id}, weight=${weight}, terms=${terms}/relevance:${relevance})"

        init {
            val ids = entries.map { it.id }
            require(ids.distinct().size == 1) { "All entries must have the same id, ids: $ids" }
        }
    }

    /**
     * Represents a manual placement in the search results. Example: when filtering by Collections
     * with no query, insert all Collections.
     */
    data class ManualPlacement(
        override val id: Id,
        override val weight: Float = MaxRelevance,
    ): SearchFilterMatch() {
        override val debugString: String
            get() = "ManualPlacement(id=${id}, weight=${weight})"
    }
}