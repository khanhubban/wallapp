package wallapp.pixel.resource

import wallapp.image.Image

object DefaultResourcesNoOp : DefaultResources {

    override val loading: Image
        get() = Image.Preset
}