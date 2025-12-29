package wallapp.pixel.image.host

import androidx.compose.ui.Alignment
import wallapp.pixel.util.ParallaxAlignment
import wallapp.pixel.view.ViewAlignment

object ImageHostImgixArbitrator {

    fun appendImgixArguments(
        imageUrl: String,
        width: Int?,
        height: Int?,
        imageHostFormatCode: String?,
        crop: Boolean,
        applyDebugBlend: Boolean,
    ): String {
        val arguments = mutableListOf<String>()

        require(!(width != null && imageUrl.contains("w=")) && !(height != null && imageUrl.contains("h="))) {
            "URL must not contain width (w=) or height (h=) parameters if they are provided, imageUrl: $imageUrl, width: $width, height: $height"
        }
        width?.also { arguments.add("w=${width}") }
        height?.also { arguments.add("h=${height}") }

        if (crop) {
            arguments.add("fit=crop")
            arguments.add("crop=faces,center")
        }

        if (applyDebugBlend) {
            arguments.add("blend-color=ff0000")
        }

        imageHostFormatCode?.also { arguments.add("fm=$imageHostFormatCode") }

        if (arguments.isEmpty()) {
            return imageUrl
        }

        val firstPrefix = if (imageUrl.contains("?")) "&" else "?"
        return imageUrl + firstPrefix + arguments.joinToString(separator = "&")
    }

    fun arbitrateAlignmentSize(
        alignment: Any?,
        width: Int?,
        height: Int?,
    ): Pair<Int?, Int?> {
        return when (alignment) {
            is Alignment -> arbitrateAlignmentSize(alignment, width, height)
            is ViewAlignment -> arbitrateAlignmentSize(alignment, width, height)
            null -> width to height
            else -> throw IllegalArgumentException("Unsupported alignment type: $alignment")
        }
    }

    fun arbitrateAlignmentSize(
        alignment: Alignment?,
        width: Int?,
        height: Int?,
    ): Pair<Int?, Int?> {
        if (width == null || height == null) {
            require(width == null && height == null) { "Both width and height must be null or non-null." }
            return width to height
        }

        if (alignment != null && alignment is ParallaxAlignment) {
            return arbitrateAlignmentSizeParallax(alignment, width, height)
        }

        return width to height
    }

    fun arbitrateAlignmentSizeParallax(
        alignment: ParallaxAlignment,
        width: Int,
        height: Int,
    ): Pair<Int, Int> {
        val verticalMaxScale = alignment.verticalMaxScale
        val horizontalMaxScale = alignment.horizontalMaxScale
        require(verticalMaxScale >= 1f) {"verticalMaxScale must be >= 1f ($verticalMaxScale)" }
        require(horizontalMaxScale >= 1f) {"horizontalMaxScale must be >= 1f ($horizontalMaxScale)" }
        return (width * horizontalMaxScale).toInt() to (height * verticalMaxScale).toInt()
    }

    fun arbitrateAlignmentSize(
        alignment: ViewAlignment?,
        width: Int?,
        height: Int?,
    ): Pair<Int?, Int?> {
        if (width == null || height == null || alignment == null) {
            require(width == null && height == null) { "Both width and height must be null or non-null." }
            return width to height
        }

        return when (alignment) {
            is ViewAlignment.ParallaxVertical -> {
                arbitrateAlignmentSizeParallax(alignment, width, height)
            }

            ViewAlignment.Center -> {
                width to height
            }
        }
    }

    fun arbitrateAlignmentSizeParallax(
        alignment: ViewAlignment.ParallaxVertical,
        width: Int,
        height: Int,
    ): Pair<Int, Int> {
        val verticalMaxScale = alignment.maxScale
        val horizontalMaxScale = 1f
        require(verticalMaxScale >= 1f) {"verticalMaxScale must be >= 1f ($verticalMaxScale)" }
        return (width * horizontalMaxScale).toInt() to (height * verticalMaxScale).toInt()
    }
}