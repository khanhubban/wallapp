package wallapp.pixel.resource

import wallapp.image.Image
import wallapp.resources.image.ImageRepository

class DefaultResourcesDefault(
    private val imageRepository: ImageRepository,
) : DefaultResources {
    override val loading: Image
        get() = imageRepository.loading
}