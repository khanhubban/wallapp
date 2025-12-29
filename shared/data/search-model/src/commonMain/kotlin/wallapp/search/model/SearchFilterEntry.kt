package wallapp.search.model

import wallapp.content.model.Id
import wallapp.string.quote

data class SearchFilterEntry(
    val id: Id,
    val term: String,
    val relevance: Float,
) {
    constructor(id: Id, entry: NetworkSearchEntry) : this(id, entry.term, entry.relevance)

    fun toString(includeId: Boolean): String {
        val term = term.quote()
        return if (includeId) {
            "${id.name}: $term"
        } else {
            term
        }.let {
            "$it, rel: $relevance"
        }
    }

    init {
        require(relevance <= MaxRelevance) {
            "Relevance ($relevance) must be less than or equal to $MaxRelevance, id: ${id.name.quote()}, term: $term"
        }
        require(relevance > 0f) {
            "Relevance ($relevance) must be greater than 0f: $relevance, id: ${id.name.quote()}, term: $term"
        }
    }

    companion object {
        const val MaxRelevance = 1f
    }
}

fun SearchFilterEntryPreset(
    id: Id,
    term: String = "",
    relevance: Float = SearchFilterEntry.MaxRelevance,
) = SearchFilterEntry(id, term, relevance)