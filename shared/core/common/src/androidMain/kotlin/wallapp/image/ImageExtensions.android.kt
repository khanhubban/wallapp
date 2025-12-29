package wallapp.image

import wallapp.graphics.Color
import wallapp.image.Image.ImageResource
import wallapp.image.Image.ImageStates
import wallapp.resource.Resource


actual val Image.isUrlImage: Boolean
    get() = this is ImageResource && this.resource is Resource.UrlImage

private val Image.resourceUrlImage: Resource.UrlImage?
    get() {
        return when (this) {
            is ImageResource -> {
                if (this.resource is Resource.UrlImage) {
                    this.resource
                } else {
                    null
                }
            }
            is ImageStates -> {
                if (this.success is ImageResource) {
                    if (this.success.resource is Resource.UrlImage) {
                        this.success.resource
                    } else {
                        null
                    }
                } else {
                    null
                }
            }
            else -> {
                null
            }
        }
    }

actual val Image.imageUrl: String?
    get() = resourceUrlImage?.url

actual val Image.isDrawableImage: Boolean
    get() = this is ImageResource && this.resource is Resource.Drawable

actual val Image.isVectorImage: Boolean
    get() = this is ImageResource && this.resource is Resource.Vector

actual val Image.isAnimatedImage: Boolean
    get() = this is ImageResource && this.resource is Resource.AnimatedImage

actual val Image.isColorImage: Boolean
    get() = this is ImageResource && this.resource is Resource.Color
actual val Image.imageColor: Color?
    get() = (this as? ImageResource)?.resource?.let { resource ->
        if (resource is Resource.Color) {
            resource.color
        } else {
            null
        }
    }

actual val Image.isRawImage: Boolean
    get() = this is ImageResource && this.resource is Resource.Raw

actual val Image.isFileUriImage: Boolean
    get() = this is ImageResource && this.resource is Resource.FileUri