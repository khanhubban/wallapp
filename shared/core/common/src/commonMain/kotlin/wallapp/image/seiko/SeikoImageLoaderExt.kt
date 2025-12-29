package wallapp.image.seiko

import com.seiko.imageloader.cache.CachePolicy
import com.seiko.imageloader.option.SizeResolver
import wallapp.image.cache.ImageCachePolicy
import wallapp.unit.Size
import wallapp.unit.pxOrElse
import kotlin.math.max
import androidx.compose.ui.geometry.Size as SizeCompose

/**
 * Workaround for ImageLoader bug (#156).
 *
 * Use the view's smaller of the two dimensions to calculate the maxSize of the decoded image.
 * This is not ideal for images that are not close to square. (#156)
 */
val Size.seikoImageLoaderSizeResolverWorkaround: SizeResolver?
    get() {
        val width = width.pxOrElse { null }?.toFloat()
        val height = height.pxOrElse { null }?.toFloat()
        if (width == null || height == null) return null
        val largerDimension = max(width, height)
        return SizeResolver(SizeCompose(largerDimension, largerDimension))
    }


val ImageCachePolicy.cachePolicy: CachePolicy
    get() = when (this) {
        ImageCachePolicy.Enabled -> CachePolicy.ENABLED
        ImageCachePolicy.Disabled -> CachePolicy.DISABLED
        ImageCachePolicy.WriteOnly -> CachePolicy.WRITE_ONLY
        ImageCachePolicy.ReadyOnly -> CachePolicy.READ_ONLY
    }