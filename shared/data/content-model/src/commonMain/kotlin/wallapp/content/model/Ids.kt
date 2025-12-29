package wallapp.content.model

import wallapp.content.model.Id.ArtistId
import wallapp.content.model.Id.CategoryId
import wallapp.content.model.Id.CollectionId
import wallapp.content.model.Id.DesignId
import wallapp.content.model.Id.RemixId

data class Ids(val ids: List<Id>) {

    val remixIds: List<RemixId>? by lazy {
        ids.filterIsInstance<RemixId>().ifEmpty { null }
    }

    val designIds: List<DesignId>? by lazy {
        ids.filterIsInstance<DesignId>().ifEmpty { null }
    }

    val categoryIds: List<CategoryId>? by lazy {
        ids.filterIsInstance<CategoryId>().ifEmpty { null }
    }

    val artistIds: List<ArtistId>? by lazy {
        ids.filterIsInstance<ArtistId>().ifEmpty { null }
    }

    val collectionIds: List<CollectionId>? by lazy {
        ids.filterIsInstance<CollectionId>().ifEmpty { null }
    }

    val exportString: String
        get() = ids.joinToString(separator = Separator) { it.exportString }

    companion object {
        private const val Separator = "<,>"

        fun fromExportString(exportString: String): Ids? {
            if (exportString.isEmpty()) {
                return null
            }
            if (exportString.contains(Separator).not()) {
                return Ids(listOf(Id.fromExportShortString(exportString)))
            }
            return Ids(exportString.split(Separator).map { Id.fromExportShortString(it) })
        }
    }
}