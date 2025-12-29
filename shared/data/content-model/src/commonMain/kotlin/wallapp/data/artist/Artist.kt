package wallapp.data.artist

import wallapp.content.model.Id.ArtistId
import wallapp.content.model.Id.CategoryId
import wallapp.content.model.Id.CollectionId
import wallapp.data.curator.Curator
import wallapp.data.social.SocialLinks
import wallapp.media.model.MediaHolder
import wallapp.string.splitIntoLinesConditional

data class Artist(
    override val id: ArtistId,
    val name: String,
    val verified: Boolean,
    override val profileImageMediaHolder: MediaHolder,
    val featureBannerImageMediaHolder: MediaHolder?,
    val singleAndCollectionCategoryIds: List<CategoryId>,
    val collectionIds: List<CollectionId>?,
    val singleCategoryId: CategoryId?,
    val socialLinks: SocialLinks?,
    val slugs: List<String>,
) : Curator {

    override val title: String get() = name
    override val titleTwoLines: String by lazy {
        name.splitIntoLinesConditional(numLines = 2)
    }
}


fun ArtistPreset(
    id: ArtistId = ArtistId.Preset,
    name: String = "Artist Name",
    verified: Boolean = true,
    profileImageMediaHolder: MediaHolder = MediaHolder.Preset,
    featureBannerImageMediaHolder: MediaHolder? = null,
    singleAndCollectionCategoryIds: List<CategoryId> = emptyList(),
    collectionIds: List<CollectionId>? = null,
    singleCategoryId: CategoryId? = null,
    socialLinks: SocialLinks? = null,
    slugs: List<String> = emptyList(),
) = Artist(
    id = id,
    name = name,
    verified = verified,
    profileImageMediaHolder = profileImageMediaHolder,
    featureBannerImageMediaHolder = featureBannerImageMediaHolder,
    singleAndCollectionCategoryIds = singleAndCollectionCategoryIds,
    collectionIds = collectionIds,
    singleCategoryId = singleCategoryId,
    socialLinks = socialLinks,
    slugs = slugs,
)