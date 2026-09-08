package wallapp.pipeline.build

import wallapp.search.model.*
import wallapp.pipeline.manifest.PipelineManifest

object SearchBuilder {
    private fun entries(terms: List<String>): List<NetworkSearchEntry> =
        terms.mapIndexed { i, t -> NetworkSearchEntry(term = t, relevance = (1.0f - i * 0.05f).coerceAtLeast(0.5f)) }

    fun build(m: PipelineManifest): NetworkSearchMetadata {
        val remix = m.wallpapers.map { w ->
            NetworkSearchRemixMetadata(
                remixId = w.id,
                artistNames = listOf(m.artist.label),
                title = w.label,
                styles = entries(w.styles),
                tags = entries(w.tags),
                colors = entries(w.colors),
                searchTerms = entries((w.tags + w.styles).distinct()),
                titleSuggestions = entries(listOf(w.label)),
            )
        }
        val artist = listOf(NetworkSearchArtistMetadata(m.artist.id, entries(listOf(m.artist.label))))
        val folder = listOf(NetworkSearchFolderMetadata(m.folder.id, entries(listOf(m.folder.title))))
        return NetworkSearchMetadata(remix, artist, folder)
    }
}
