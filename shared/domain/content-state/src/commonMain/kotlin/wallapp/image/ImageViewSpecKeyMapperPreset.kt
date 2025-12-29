package wallapp.image

import wallapp.image.sized.SizedImageMapper
import wallapp.image.sized.SizedImageMapperDefault

fun ImageViewSpecKeyMapperPreset(
    imageViewSpecFactory: ImageViewSpecFactory = ImageViewSpecFactoryPreset()
): SizedImageMapper = SizedImageMapperDefault(
    imageViewSpecFactory = imageViewSpecFactory,
)