package wallapp.image.loader

import coil.ImageLoader as CoilImageLoader

val ImageLoader.imageLoaderCoil: CoilImageLoader
    get() = (this as ImageLoaderCoil).imageLoaderCoil