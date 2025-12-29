package wallapp.content.network.map

import wallapp.billing.sku.BillingProductId
import wallapp.content.model.ColorShade
import wallapp.content.model.Id.ArtistId
import wallapp.content.model.Id.CategoryId
import wallapp.content.model.Id.CollectionId
import wallapp.content.model.Id.FolderId
import wallapp.content.model.Id.RemixId
import wallapp.content.model.PurchasableProductIds
import wallapp.content.model.RotationInfo
import wallapp.content.model.TranslationInfo
import wallapp.content.model.WallpaperAnimatedImageLayer
import wallapp.content.model.WallpaperAudioLayer
import wallapp.content.model.WallpaperCategory
import wallapp.content.model.WallpaperCategoryType
import wallapp.content.model.WallpaperDownloadMedia
import wallapp.content.model.WallpaperId
import wallapp.content.model.WallpaperImageLayer
import wallapp.content.model.WallpaperItem
import wallapp.content.model.WallpaperLayer
import wallapp.content.model.WallpaperLayerLayoutParams
import wallapp.content.model.WallpaperLayerModifierBlend
import wallapp.content.model.WallpaperLayerModifierTint
import wallapp.content.model.WallpaperRemix
import wallapp.content.model.WallpaperRemixParallax
import wallapp.content.model.WallpaperRemixPreviewImages
import wallapp.content.model.WallpaperVideoLayer
import wallapp.content.model.asBlendMode
import wallapp.content.model.filterByCategoryTypeCollection
import wallapp.content.model.filterByCategoryTypeSingles
import wallapp.content.network.model.NetworkArtist
import wallapp.content.network.model.NetworkCategory
import wallapp.content.network.model.NetworkContent
import wallapp.content.network.model.NetworkFolder
import wallapp.content.network.model.NetworkImageSubLayoutParams
import wallapp.content.network.model.NetworkLayer
import wallapp.content.network.model.NetworkMedia
import wallapp.content.network.model.NetworkPreviews
import wallapp.content.network.model.NetworkPurchasableProductIds
import wallapp.content.network.model.NetworkSocialLinks
import wallapp.content.network.model.NetworkWallpaper
import wallapp.content.network.model.NetworkWallpaperDownloadMedia
import wallapp.data.artist.Artist
import wallapp.data.folder.Folder
import wallapp.data.social.SocialLinks
import wallapp.image.ImageSize
import wallapp.media.model.Media
import wallapp.media.model.MediaHolder
import wallapp.media.model.MediaId
import wallapp.resource.ImageHash
import wallapp.scene.SceneDefinitions.DefaultHorizontalAlignment
import wallapp.scene.SceneDefinitions.DefaultVerticalAlignment
import wallapp.string.quote
import wallapp.type.Direction
import wallapp.type.MediaType
import wallapp.type.asMediaType


class NetworkContentMapper {

    fun mapAllWallpaperItems(networkContent: NetworkContent): List<WallpaperItem> =
        mapAllCategories(networkContent) + mapAllWallpapers(networkContent)

    fun mapAllWallpapers(networkContent: NetworkContent): List<WallpaperRemix> =
        networkContent.wallpapers.map { it.toWallpaperRemix() }

    fun mapAllCategories(networkContent: NetworkContent): List<WallpaperCategory> {
        return networkContent
            .categories
            .mapNotNull { categoryNetwork ->
                networkContent.wallpapers.find { it.id == categoryNetwork.previewRemixId }
                    ?.let { categoryNetwork.toWallpaperCategory(it) }
            }
    }

    fun mapAllArtists(networkContent: NetworkContent): List<Artist> =
        networkContent.artists.map { it.mapArtist() }

    fun NetworkArtist.mapArtist(): Artist {
        val allCategoryIds = categoryIds.map { CategoryId(it) }
        val collectionIds = allCategoryIds
            .filterByCategoryTypeCollection()
            ?.map { it.collectionId }
        val singleCategoryId = allCategoryIds.filterByCategoryTypeSingles()
            ?.let { singleIds ->
                require(singleIds.size == 1 || singleIds.isEmpty()) {
                    "Data error: Artist ${id.quote()} has multiple single categories (${categoryIds.size})"
                }
                singleIds.firstOrNull()
            }

        return Artist(
            id = ArtistId(this.id),
            name = label,
            verified = true,
            profileImageMediaHolder = profileImage.mediaHolder(contentDescription = label),
            featureBannerImageMediaHolder = featureBannerImage?.mediaHolder(contentDescription = label),
            socialLinks = socialLinks.socialLinks,
            singleAndCollectionCategoryIds = allCategoryIds,
            collectionIds = collectionIds,
            singleCategoryId = singleCategoryId,
            slugs = slugs,
        )
    }

    fun mapAllFolders(networkContent: NetworkContent): List<Folder> =
        networkContent.folders.map { it.mapFolder() }

    fun NetworkFolder.mapFolder(): Folder {
        return Folder(
            id = FolderId(id),
            profileImageMediaHolder = profileImage.mediaHolder(title),
            featureBannerImageMediaHolder = featureBannerImage.mediaHolder(title),
            wallpaperIds = remixIds.map { WallpaperId(it) },
            collectionIds = collectionIds?.map { CollectionId(it) } ?: emptyList(),
            title = title,
            titleTwoLines = titleTwoLines,
        )
    }

    val NetworkSocialLinks.socialLinks: SocialLinks
        get() = SocialLinks(
            facebook = facebook,
            instagram = instagram,
            twitter = twitter,
            shop = shop,
            website = website,
            youTube = youtube,
        )

    val NetworkPurchasableProductIds.purchasableProductIds: PurchasableProductIds
        get() = PurchasableProductIds(
            appStoreProductId = BillingProductId.from(appStoreProductId, revenueCatEntitlementId),
            googlePlayProductId = BillingProductId.from(googlePlayProductId, revenueCatEntitlementId),
            revenueCatEntitlementId = revenueCatEntitlementId,
        )

    fun NetworkCategory.toWallpaperCategory(previewRemix: NetworkWallpaper): WallpaperCategory {
        val categoryId = CategoryId(id)
        val artistId = ArtistId((artistId))
        requireNotNull(artistId) { "Data error: Category ${categoryId.name.quote()} does not define 'artistId'" }
        return WallpaperCategory(
            categoryId,
            label,
            artistId = artistId,
            featureBannerImageMediaHolder = featureBannerImage?.mediaHolder(label),
            categoryType = WallpaperCategoryType.fromName(categoryType),
            previewRemix.toWallpaperRemix(),
            remixIds.map { RemixId(it) },
            slugs,
            purchasableProductIds?.purchasableProductIds,
        )
    }

    fun NetworkWallpaper.toWallpaperRemix(): WallpaperRemix {
        return WallpaperRemixParallax(
            id = RemixId(id),
            label = label,
            collectionLabel = collectionLabel,
            artistId = ArtistId(artistId),
            portraitHorizontalAlignment = DefaultHorizontalAlignment,
            portraitVerticalAlignment = DefaultVerticalAlignment,
            categoryId = CategoryId(categoryId),
            isSingle = isSingle,
            isDark = isDark,
            topColorShade = topColorShade?.let { ColorShade.fromKey(it) },
            previewImages = previews.toWallpaperRemixPreviewImages(label),
            downloadMedia = wallpaperDownloadMedia.toWallpaperDownloadMedia(),
            slugs = slugs,
            isAiEnhanced = isAiEnhanced,
            isFree = isFree,
        )
    }

    fun NetworkWallpaperDownloadMedia.toWallpaperDownloadMedia() = WallpaperDownloadMedia(
        hdImageSize = ImageSize(hdWidth, hdHeight),
        hdMediaId = MediaId(hdMediaId),
        sdMediaId = MediaId(sdMediaId),
    )

    fun NetworkMedia.mediaHolder(contentDescription: String?): MediaHolder {
        return MediaHolder(
            mediaId = MediaId(id = id),
            contentDescription = contentDescription,
            imageHash = blurHash?.let { ImageHash.from(it) },
        )
    }

    fun Media.mediaHolder(contentDescription: String?): MediaHolder {
        return MediaHolder(
            mediaId = mediaId,
            contentDescription = contentDescription,
            imageHash = imageHash,
        )
    }

    fun NetworkPreviews.toWallpaperRemixPreviewImages(
        contentDescription: String?,
    ) = WallpaperRemixPreviewImages(
        mediaHolder = standard.first().mediaHolder(contentDescription),
    )

    fun NetworkMedia.toMedia(): Media =
        Media(
            mediaId = MediaId(id = id),
            type = asMediaType(type) ?: MediaType.Image,
            width = width,
            height = height,
            imageHash = blurHash?.let { ImageHash.from(it) },
        )

    fun NetworkLayer.toWallpaperLayer(): WallpaperLayer {
        val blendMode = modifier?.blendMode
        val modifier = if (blendMode != null) {
            asBlendMode(blendMode)?.let {
                WallpaperLayerModifierBlend(it)
            }
        } else {
            val modifier = modifier
            modifier?.tintColor?.let {
                WallpaperLayerModifierTint(it, modifier.flatColored ?: false)
            }
        }
        val media = media?.map { it.toMedia() }
        requireNotNull(media) { "Must set \"media\" attribute" }

        val rotationInfo = rotation?.let {
            RotationInfo(
                timeToOneRotation = it.time?.toFloat() ?: 0f,
                clockwise = it.direction == "clockwise",
                rotateInOppositeDirection = it.direction == "anticlockwise",
            )
        }
        val translationInfo = translation?.let {
            TranslationInfo(
                timeToOneTranslation = it.time?.toFloat() ?: 0f,
                direction = when (it.direction) {
                    "north" -> Direction.NORTH
                    "south" -> Direction.SOUTH
                    "east" -> Direction.EAST
                    "west" -> Direction.WEST
                    else -> Direction.NONE
                },
                rotatedAngle = it.angle?.toFloat() ?: 0f
            )
        }

        val layerMedia = media[0]
        val mediaHolder = layerMedia.mediaHolder(contentDescription = null)

        return when (type) {
            "parallax" -> {
                when {
                    media.any { it.type == MediaType.Audio } -> {
                        WallpaperAudioLayer(
                            audioUrl = TODO("Re-implement audioUrl support"),
                            rotationInfo,
                            translationInfo,
                            imageHash = layerMedia.imageHash,
                            parallaxScale,
                            parallaxOnlyOnTilt,
                            imageSubsetLayoutParams?.toImageSubLayoutParams(),
                            allowZoomScale = allowZoomScale,
                            centerInAnchorPoint = centerInCutout,
                            inheritLayerIndexCutoutOffset = inheritLayerIndexCutoutOffset,
                            wallpaperLayerModifier = modifier,
                            media = media,
                            aspectRatio = layerMedia.aspectRatio,
                        )
                    }
                    media.any { it.type == MediaType.Video } -> {
                        WallpaperVideoLayer(
                            videoUrl = TODO("Re-implement videoUrl support"),
                            rotationInfo,
                            translationInfo,
                            imageHash = layerMedia.imageHash,
                            parallaxScale,
                            parallaxOnlyOnTilt,
                            imageSubsetLayoutParams?.toImageSubLayoutParams(),
                            allowZoomScale = allowZoomScale,
                            centerInAnchorPoint = centerInCutout,
                            inheritLayerIndexCutoutOffset = inheritLayerIndexCutoutOffset,
                            wallpaperLayerModifier = modifier,
                            media = media,
                            aspectRatio = layerMedia.aspectRatio,
                        )
                    }
                    media.any { it.type == MediaType.Image } -> {
                        WallpaperImageLayer(
                            mediaHolder = mediaHolder,
                            imageHash = layerMedia.imageHash,
                            parallaxScale,
                            parallaxOnlyOnTilt,
                            imageSubsetLayoutParams?.toImageSubLayoutParams(),
                            imageSubsetLayoutParams != null,
                            allowZoomScale = allowZoomScale,
                            centerInAnchorPoint = centerInCutout,
                            inheritLayerIndexCutoutOffset = inheritLayerIndexCutoutOffset,
                            wallpaperLayerModifier = modifier,
                            media = media,
                            aspectRatio = layerMedia.aspectRatio,
                        )
                    }
                    else -> {
                        throw IllegalArgumentException("Unhandled state")
                    }
                }
            }
            else -> {
                WallpaperAnimatedImageLayer(
                    image = TODO("Re-implement animated image support"),
//                    Image.from(imageUrl),
                    imageHash = layerMedia.imageHash,
                    aspectRatio = layerMedia.aspectRatio,
                    rotationInfo = rotationInfo,
                    translationInfo = translationInfo,
                    imageSubsetLayoutParams = imageSubsetLayoutParams?.toImageSubLayoutParams(),
                    parallaxScale = parallaxScale,
                    parallaxOnlyOnTilt = parallaxOnlyOnTilt,
                    allowZoomScale = allowZoomScale,
                    centerInAnchorPoint = centerInCutout,
                    inheritLayerIndexCutoutOffset = inheritLayerIndexCutoutOffset,
                    wallpaperLayerModifier = modifier,
                    media = media,
                )
            }
        }
    }

    fun NetworkImageSubLayoutParams.toImageSubLayoutParams(): WallpaperLayerLayoutParams =
        WallpaperLayerLayoutParams(
            xRatio = xRatio,
            yRatio = yRatio,
            sceneWidthRatio = sceneWidthRatio,
            sceneHeightRatio = sceneHeightRatio,
            // TODO: Add gravity
        )
}
