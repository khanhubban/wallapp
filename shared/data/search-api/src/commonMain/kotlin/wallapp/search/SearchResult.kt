package wallapp.search

import androidx.compose.runtime.Immutable
import co.touchlab.skie.configuration.annotations.SealedInterop
import wallapp.content.model.WallpaperRemix
import wallapp.data.collection.CollectionState
import wallapp.data.curator.Curator

@SealedInterop.Enabled
sealed class SearchResult {

    @Immutable
    data object Inactive : SearchResult()

    @Immutable
    data object Loading : SearchResult()

    @Immutable
    data object NoResults : SearchResult()

    @Immutable
    data class Results(
        val wallpapers: List<WallpaperRemix>?,
        val collectionStates: List<CollectionState>?,
        val curators: List<Curator>?,
    ) : SearchResult() {
        
        val isEmpty: Boolean by lazy {
            wallpapers.isNullOrEmpty()
                    && collectionStates.isNullOrEmpty()
                    && curators.isNullOrEmpty()
        }
    }
}

val SearchResult.debugString: String
    get() = when (this) {
        SearchResult.Inactive -> "Inactive"
        SearchResult.Loading -> "Loading"
        SearchResult.NoResults -> "NoResults"
        is SearchResult.Results -> "Results(wallpapers=${wallpapers?.size}, collections=${collectionStates?.size}, curators=${curators?.size})"
    }
