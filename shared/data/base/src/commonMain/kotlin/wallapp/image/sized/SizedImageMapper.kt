package wallapp.image.sized

import wallapp.pixel.image.ImageViewSpec

interface SizedImageMapper {

    fun mapImageViewSpec(sizedImage: SizedImage): ImageViewSpec
}