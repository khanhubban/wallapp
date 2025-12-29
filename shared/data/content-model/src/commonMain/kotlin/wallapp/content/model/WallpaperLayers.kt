package wallapp.content.model

import wallapp.annotation.FloatRange
import wallapp.media.model.Media
import wallapp.media.model.MediaHolder
import wallapp.resource.ImageHash
import wallapp.type.LayerZoomBehavior

sealed class WallpaperLayer {
    abstract val imageHash: ImageHash?

    abstract val aspectRatio: Float?

    /**
     * [parallaxScale] of 0: layer doesn't move
     * [parallaxScale] of 1: layer moves 1 to 1 with device / home screen page offset.
     */
    @get:FloatRange(from = 0.0, to = 1.0)
    abstract val parallaxScale: Float

    abstract val parallaxOnlyOnTilt: Boolean

    abstract val imageSubsetLayoutParams: WallpaperLayerLayoutParams?

    abstract val animationType: AnimationType

    abstract val allowZoomScale: Boolean

    abstract val centerInAnchorPoint: Boolean

    // If > -1, apply the same position offset as is applied to the item at the specified index.
    // Allows one item to be positioned in the cutout, and other items that must be positioned
    // relative to that cutout item to have their positions adjusted also.
    abstract val inheritLayerIndexCutoutOffset: Int

    abstract val media: List<Media>?

    abstract val wallpaperLayerModifier: WallpaperLayerModifier?

    val layerZoomBehavior: LayerZoomBehavior
        get() = when {
            parallaxScale == 0f && parallaxOnlyOnTilt -> LayerZoomBehavior.TiltZoom
            parallaxScale == 0f && !parallaxOnlyOnTilt -> LayerZoomBehavior.None
            else -> LayerZoomBehavior.FullZoom
        }
}


/**
 * [imageSubsetLayoutParams]: Where to position this image relative to the scene.
 * [useImageSubset]: If true, a subset of the source image (as defined by
 *                  [layoutParams]) will be used. This is necessary to
 */
data class WallpaperImageLayer(
    val mediaHolder: MediaHolder,
    override val imageHash: ImageHash?,
    override val parallaxScale: Float = 1f,
    override val parallaxOnlyOnTilt: Boolean = false,
    override val imageSubsetLayoutParams: WallpaperLayerLayoutParams? = null,
    val useImageSubset: Boolean = false,
    val bounceAnimation: Boolean? = null,
    override val centerInAnchorPoint: Boolean = false,
    override val inheritLayerIndexCutoutOffset: Int = -1,
    override val allowZoomScale: Boolean = true,
    override val media: List<Media>? = null,
    override val wallpaperLayerModifier: WallpaperLayerModifier? = null,
    override val aspectRatio: Float? = null,
) : WallpaperLayer() {

    override val animationType: AnimationType
        get() = when {
            /*image.isColorImage ||*/ (bounceAnimation != null && bounceAnimation == false) -> AnimationType.NONE
            else -> AnimationType.BOUNCE
        }
}

abstract class WallpaperAnimatedLayer : WallpaperLayer()


data class WallpaperLayers(val layers: List<WallpaperLayer>) {

    constructor(vararg layers: WallpaperLayer) : this(layers.toList())

}

data class WallpaperVideoLayer(
    val videoUrl: String,
    val rotationInfo: RotationInfo? = null,
    val translationInfo: TranslationInfo? = null,
    override val imageHash: ImageHash? = null,
    override val parallaxScale: Float = 0f,
    override val parallaxOnlyOnTilt: Boolean = false,
    override val imageSubsetLayoutParams: WallpaperLayerLayoutParams? = null,
    override val allowZoomScale: Boolean = true,
    override val centerInAnchorPoint: Boolean = false,
    override val inheritLayerIndexCutoutOffset: Int = -1,
    override val media: List<Media>? = null,
    override val wallpaperLayerModifier: WallpaperLayerModifier? = null,
    override val aspectRatio: Float? = null,
) : WallpaperLayer() {

    override val animationType: AnimationType = when {
        translationInfo != null -> AnimationType.TRANSLATION
        rotationInfo != null -> AnimationType.ROTATION
        else -> AnimationType.BOUNCE
    }
}

data class WallpaperAudioLayer(
    val audioUrl: String,
    val rotationInfo: RotationInfo? = null,
    val translationInfo: TranslationInfo? = null,
    override val imageHash: ImageHash? = null,
    override val parallaxScale: Float = 0f,
    override val parallaxOnlyOnTilt: Boolean = false,
    override val imageSubsetLayoutParams: WallpaperLayerLayoutParams? = null,
    override val allowZoomScale: Boolean = true,
    override val centerInAnchorPoint: Boolean = false,
    override val inheritLayerIndexCutoutOffset: Int = -1,
    override val media: List<Media>? = null,
    override val wallpaperLayerModifier: WallpaperLayerModifier? = null,
    override val aspectRatio: Float? = null,
) : WallpaperLayer() {

    override val animationType: AnimationType = when {
        translationInfo != null -> AnimationType.TRANSLATION
        rotationInfo != null -> AnimationType.ROTATION
        else -> AnimationType.BOUNCE
    }
}

//@Keep
enum class AnimationType {
    TRANSLATION, ROTATION, BOUNCE, NONE
}