package wallapp.content.model

import wallapp.type.LayerGravity
import kotlin.math.round

/**
 *
 * [sceneWidthRatio]: the fraction of the entire scene this layer consumes horizontally.
 * [sceneHeightRatio]: the fraction of the entire scene this layer consumes vertically.
 * [xRatio]: the fraction of the entire scene to determine the X render point.
 * [yRatio]: the fraction of the entire scene to determine the Y render point.
 *
 * For example, if rendering 400x400 "sun" type object that is center|top aligned on a
 * 2000x1000 scene: WallpaperLayerLayoutParams(
 *          sceneWidthRatio = .2,   // 400 / 2000
 *          sceneHeightRatio = .4,  // 400 / 1000
 *          xRatio = 0.4,  // center aligned, so (2000 / 2) - (400 / 2) which is 800, so (800 / 2000)
 *          yRatio = 0.0   // top aligned
 */
data class WallpaperLayerLayoutParams(
    val sceneWidthRatio: Double,
    val sceneHeightRatio: Double,
    val xRatio: Double,
    val yRatio: Double,
    val gravity: List<LayerGravity>? = null
) {

    init {
        require(sceneWidthRatio > 0.0)
        require(sceneHeightRatio > 0.0)
    }

    fun getXY(imageWidth: Int, imageHeight: Int): Pair<Int, Int> {
        val (fullSceneWidth, fullSceneHeight) = getFullSceneDimensions(imageWidth, imageHeight)
        val x = round(fullSceneWidth.toDouble() * xRatio).toInt()
        val y = round(fullSceneHeight.toDouble() * yRatio).toInt()
        return x to y
    }

    fun getFullSceneDimensions(imageWidth: Int, imageHeight: Int): Pair<Int, Int> {
        return round(imageWidth.toDouble() / sceneWidthRatio).toInt() to
                round(imageHeight.toDouble() / sceneHeightRatio).toInt()
    }
}