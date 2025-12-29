package wallapp.data.content

import wallapp.content.model.Id
import wallapp.content.model.Id.ArtistId
import wallapp.content.model.Wallpaper
import wallapp.content.model.WallpaperItem

object ContentSort {

    private fun List<WallpaperItem>.insertTopmostIds(
        topmostIds: List<Id>,
    ): List<WallpaperItem> {
        val presets = topmostIds.map { it.name }

        return sortedBy { item ->
                val index = presets.indexOf(item.id.name)
                if (index == -1) {
                    Int.MAX_VALUE
                } else {
                    index
                }
            }
    }

    private data class ArtistWeight(val artistId: ArtistId, var weight: Double)

    fun List<Wallpaper>.distributeWallpapersByArtist(): List<Wallpaper> {
        return distributeItemsByArtist()
            .filterIsInstance<Wallpaper>()
    }

    /**
     * Splits the list of wallpapers into two lists: the first containing single wallpapers, the
     * second containing collection wallpapers.
     */
    fun List<Wallpaper>.partitionByContentTypes(): Pair<List<Wallpaper>?, List<Wallpaper>?> {
        return partition { it.isSingle }
            .let {
                it.first.ifEmpty { null } to it.second.ifEmpty { null }
            }
    }

    fun List<WallpaperItem>.distributeItemsByArtist(): List<WallpaperItem> {
        val itemsByArtist = this.groupBy { it.artistId }.mapValues { it.value.toMutableList() }

        // Calculate distribution weights based on inverse proportion of items
        val totalItems = itemsByArtist.values.sumOf { it.size }
        val artistWeights = itemsByArtist.mapValues { totalItems.toDouble() / it.value.size }

        // Create a list to simulate a priority queue
        val artistList = artistWeights.keys.map { ArtistWeight(it, artistWeights[it]!!) }
        val result = mutableListOf<WallpaperItem>()
        val itemCounts = mutableMapOf<ArtistId, Int>()

        while (result.size < this.size) {
            // Sort list based on current weight (higher weight artists come first)
            artistList.sortedByDescending { it.weight }

            for (artist in artistList) {
                val queue = itemsByArtist[artist.artistId]
                if (queue != null && queue.isNotEmpty()) {
                    result.add(queue.removeAt(0))
                    itemCounts[artist.artistId] = itemCounts[artist.artistId]?.plus(1) ?: 1
                    artist.weight -= 1
                    // Break the loop if list is filled
                    if (result.size == this.size) break
                }
            }
        }

//        val logFirstCount = 120
//        if (result.size >= logFirstCount) {
//            val firstCounts = mutableMapOf<ArtistId, Int>()
//            result.subList(0, logFirstCount).forEach { item ->
//                val currentCount = firstCounts[item.artistId] ?: 0
//                firstCounts[item.artistId] = currentCount + 1
//            }
//            Log.d("[ContentSort] Distribution of the first $logFirstCount items:")
//            firstCounts.forEach { (artistId, count) ->
//                Log.d("[ContentSort] Artist $artistId: ${count.toDouble() / logFirstCount * 100}% ($count items)")
//            }
//        }

        return result
    }

}