package wallapp.content.state.wallpaper

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import co.touchlab.skie.configuration.annotations.SealedInterop
import wallapp.image.sized.SizedImage
import wallapp.pixel.image.ImageViewSpec
import wallapp.pixel.shape.ShapeSpec
import wallapp.pixel.view.ViewSpec
import wallapp.unit.Padding

@Immutable
@SealedInterop.Enabled
sealed class WallpaperPreviewViewSpec : ViewSpec {

    abstract val imageViewSpec: ImageViewSpec
    val width: Dp
        get() = imageViewSpec.width
    val height: Dp
        get() = imageViewSpec.height
    abstract val shapeSpec: ShapeSpec?
    abstract val sizedImage: SizedImage

    @Immutable
    data class Default(
        override val imageViewSpec: ImageViewSpec,
        override val sizedImage: SizedImage,
        override val shapeSpec: ShapeSpec? = imageViewSpec.shapeSpec,
    ) : WallpaperPreviewViewSpec()

    @Immutable
    data class WithFooter(
        override val imageViewSpec: ImageViewSpec,
        override val sizedImage: SizedImage,
        /**
         * This [ShapeSpec] is applied to the container of the element containing the image
         * and footer. For this type, [imageViewSpec]'s shapeSpec must be null.
         */
        override val shapeSpec: ShapeSpec,
        val footerHeight: Dp,
        val footerContentPadding: Padding = Padding(8.dp, 8.dp, end = 0.dp, 8.dp),
        val likeButtonSize: Dp = 36.dp,
    ) : WallpaperPreviewViewSpec()

    companion object {
        val Preset by lazy {
            Default(
                imageViewSpec = ImageViewSpec.Preset,
                sizedImage = SizedImage.Preset,
            )
        }
    }
}