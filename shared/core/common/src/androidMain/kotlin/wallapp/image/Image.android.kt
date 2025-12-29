package wallapp.image

import android.graphics.Bitmap
import androidx.compose.ui.graphics.vector.ImageVector
import wallapp.graphics.Color
import wallapp.resource.ImageHash
import wallapp.resource.LocalRawAsset
import wallapp.resource.Resource
import android.graphics.drawable.Drawable as DrawableSystem

fun from(
    resource: Resource,
    contentDescription: String? = null,
    imageOptions: ImageOptions? = null,
): Image {
    return when (resource) {
        is Resource.UrlImage,
        is Resource.UrlVideo,
        is Resource.Vector,
        is Resource.Drawable,
        is Resource.DrawableAndroid,
        is Resource.AnimatedImage,
        is Resource.HashedImage,
        is Resource.Color,
        is Resource.Raw,
        is Resource.FileUri,
        is Resource.SystemBitmap,
        is Resource.DrawableCompose,
        -> {
            Image.ImageResource(
                resource = resource,
                contentDescription = contentDescription,
                imageSize = imageOptions?.imageSize,
                tintColorToken = imageOptions?.tintColorToken,
            )
        }

        else -> {
            throw IllegalArgumentException("Unsupported resource type: $resource")
        }
    }
}

fun from(color: Color, contentDescription: String? = null): Image = from(
    resource = Resource.Color(color),
    contentDescription = contentDescription,
)

fun from(
    model: String,
    contentDescription: String? = null,
    imageOptions: ImageOptions? = null,
): Image {
    return if (model.startsWith("http")) {
        // Ideally remote hosted images should use ImageModel.Url, but there's legacy code and a
        // significant number of tests that don't use ImageModel.Url yet, so continue to support
        // for now.
        from(
            resource = Resource.UrlImage(model),
            contentDescription = contentDescription,
            imageOptions = imageOptions,
        )
    } else if (model.startsWith("content://") || model.startsWith("file:///data")) {
        from(
            resource = Resource.FileUri(model),
            contentDescription = contentDescription,
            imageOptions = imageOptions,
        )
    } else if (model.startsWith("{\"")) {
        Image.fromExportString(model)
    } else {
        // Assume an image hash
        from(
            resource = Resource.HashedImage(ImageHash.from(model)),
            contentDescription = contentDescription,
            imageOptions = imageOptions,
        )
//        error("Unsupported model: ${model.quote()} - only http and file uri are supported")
    }
}

actual fun imageFrom(
    model: Any,
    contentDescription: String?,
    imageOptions: ImageOptions?,
): Image {
    return when (model) {
        is ImageModel.UrlImage -> { from(Resource.UrlImage(model.url), contentDescription, imageOptions) }
        is ImageModel.UrlVideo -> { from(Resource.UrlVideo(model.url), contentDescription, imageOptions) }
        is Image -> {
            require(model is Image.ImageResource)
            model.copy(
                contentDescription = contentDescription,
                tintColorToken = imageOptions?.tintColorToken,
            )
        }
        is Resource -> from(model, contentDescription, imageOptions)
        is Color -> from(model, contentDescription)
        is String -> from(model, contentDescription, imageOptions)
        is DrawableSystem -> { from(Resource.DrawableAndroid(model), contentDescription, imageOptions) }
        is ImageHash -> { from(Resource.HashedImage(model), contentDescription, imageOptions) }
        is ImageVector -> { from(Resource.from(model), contentDescription, imageOptions) }
        is Bitmap -> { from(Resource.SystemBitmap(model), contentDescription, imageOptions) }
        is LocalRawAsset -> { from(model.toResource(), contentDescription, imageOptions) }
        else -> throw ImageModelUnsupportedException("Unsupported model: $model")
    }
}

actual fun Image.updateAnimatedSpecWith(
    startAnimation: Boolean,
    animationStarted: () -> Unit,
    animationCompleted: () -> Unit,
    animationProgressUpdates: (Float) -> Unit,
): Image {
    return if (this is Image.ImageResource) {
        this.copy(
            resource = (this.resource as? Resource.AnimatedImage)?.copy(
                spec = this.resource.spec.copy(
                    startAnimation = startAnimation,
                    animationStarted = animationStarted,
                    animationCompleted = animationCompleted,
                    animationProgressUpdates = animationProgressUpdates,
                )
            )
                ?: this.resource
        )
    } else {
        this
    }
}