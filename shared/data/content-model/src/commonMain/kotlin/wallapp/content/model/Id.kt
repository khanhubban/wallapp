package wallapp.content.model

import androidx.compose.runtime.Immutable
import co.touchlab.skie.configuration.annotations.SealedInterop
import kotlinx.serialization.Serializable

@Immutable
@SealedInterop.Enabled
@Serializable
sealed class Id {

    abstract val name: String

    @Immutable
    @Serializable
    data class ArtistId(override val name: String): Id() {
        companion object {
            val Preset = ArtistId("a~Preset")
        }
    }

    @Immutable
    @Serializable
    data class CategoryId(override val name: String): Id() {
        val collectionId: CollectionId
            get() = CollectionId(name)
    }

    @Immutable
    @Serializable
    data class CollectionId(
        override val name: String,
    ): Id() {
        val categoryId: CategoryId
            get() = CategoryId(name)
    }

    @Immutable
    @Serializable
    data class CollectionGroupId(override val name: String): Id()

    @Immutable
    @Serializable
    data class DesignId(override val name: String): Id()

    @Immutable
    @Serializable
    data class FolderId(override val name: String): Id()

    @Immutable
    @Serializable
    data class RemixId(override val name: String): Id()

    @Immutable
    @Serializable
    data class StoryId(override val name: String): Id()

    @Immutable
    @Serializable
    data class StoryCollectionId(override val name: String): Id()

    val exportString: String
        get() = "${shortStringIdPrefix}$ShortStringSeparator$name"

    companion object {

        val Id.shortStringIdPrefix: String
            get() {
                return when (this) {
                    is ArtistId -> ShortStringIdPrefixAritst
                    is CategoryId -> ShortStringIdPrefixCategory
                    is CollectionGroupId -> ShortStringIdPrefixCollectionGroup
                    is CollectionId -> ShortStringIdPrefixCollection
                    is DesignId -> ShortStringIdPrefixDesign
                    is FolderId -> ShortStringIdPrefixFolder
                    is RemixId -> ShortStringIdPrefixRemix
                    is StoryCollectionId -> ShortStringIdPrefixStoryCollection
                    is StoryId -> ShortStringIdPrefixStory
                }
            }


        const val ShortStringSeparator = "::"

        const val ShortStringIdPrefixCommon = "id~"
        const val ShortStringIdPrefixAritst = "${ShortStringIdPrefixCommon}a"
        const val ShortStringIdPrefixCategory = "${ShortStringIdPrefixCommon}cat"
        const val ShortStringIdPrefixCollection = "${ShortStringIdPrefixCommon}col"
        const val ShortStringIdPrefixCollectionGroup = "${ShortStringIdPrefixCommon}colg"
        const val ShortStringIdPrefixDesign = "${ShortStringIdPrefixCommon}d"
        const val ShortStringIdPrefixFolder = "${ShortStringIdPrefixCommon}f"
        const val ShortStringIdPrefixRemix = "${ShortStringIdPrefixCommon}r"
        const val ShortStringIdPrefixStory = "${ShortStringIdPrefixCommon}s"
        const val ShortStringIdPrefixStoryCollection = "${ShortStringIdPrefixCommon}sc"

        fun fromExportShortString(exportString: String): Id {
            require(exportString.contains(ShortStringSeparator)) {
                "Short string must contain separator: $ShortStringSeparator, exportString: $exportString"
            }
            val parts = exportString.split(ShortStringSeparator)
            require(parts.size == 2) {
                "Short string must contain exactly one separator: $ShortStringSeparator, exportString: $exportString"
            }

            val prefix = parts[0]
            val name = parts[1]

            return when (prefix) {
                ShortStringIdPrefixAritst -> ArtistId(name)
                ShortStringIdPrefixCategory -> CategoryId(name)
                ShortStringIdPrefixCollection -> CollectionId(name)
                ShortStringIdPrefixCollectionGroup -> CollectionGroupId(name)
                ShortStringIdPrefixDesign -> DesignId(name)
                ShortStringIdPrefixFolder -> FolderId(name)
                ShortStringIdPrefixRemix -> RemixId(name)
                ShortStringIdPrefixStory -> StoryId(name)
                ShortStringIdPrefixStoryCollection -> StoryCollectionId(name)
                else -> throw IllegalArgumentException("Unknown prefix: $prefix for exportString: $exportString")
            }
        }

        fun fromExportStringChecked(exportString: String): Id? {
            return try {
                fromExportShortString(exportString)
            } catch (e: IllegalArgumentException) {
                null
            }
        }
    }
}

typealias WallpaperId = Id.RemixId