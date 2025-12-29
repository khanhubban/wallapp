package wallapp.content.model

import wallapp.image.Image
import wallapp.media.model.Media
import wallapp.resource.ImageHash
import wallapp.type.Direction

data class WallpaperAnimatedImageLayer(
    val image: Image,
    override val imageHash: ImageHash?,
    val rotationInfo: RotationInfo? = null,
    val translationInfo: TranslationInfo? = null,
    override val parallaxScale: Float = 1f,
    override val parallaxOnlyOnTilt: Boolean = false,
    override val imageSubsetLayoutParams: WallpaperLayerLayoutParams? = null,
    override val centerInAnchorPoint: Boolean = false,
    override val inheritLayerIndexCutoutOffset: Int = -1,
    override val allowZoomScale: Boolean = true,
    override val media: List<Media>? = null,
    override val wallpaperLayerModifier: WallpaperLayerModifier? = null,
    override val aspectRatio: Float? = null,
) : WallpaperAnimatedLayer() {

    override val animationType: AnimationType = when {
        translationInfo != null -> AnimationType.TRANSLATION
        rotationInfo != null -> AnimationType.ROTATION
        else -> AnimationType.NONE
    }
}


data class TranslationInfo(
    /** Time it takes to complete one full translation across the screen in seconds */
    val timeToOneTranslation: Float = 10f,
    var direction: Direction = Direction.NORTH,
    val rotatedAngle: Degree = 0f // positive value for clockwise and vice versa
)

data class RotationInfo(
    /** The time it takes to complete one 360 degree rotation in seconds */
    val timeToOneRotation: Float = 180f,
    var clockwise: Boolean = true,
    val rotateInOppositeDirection: Boolean = false,
    val inputRotationScaler: Float = .75f
)

typealias Degree = Float