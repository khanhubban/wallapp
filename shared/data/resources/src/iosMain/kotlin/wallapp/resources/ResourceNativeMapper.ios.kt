package wallapp.resources

import wallapp.resource.Resource

actual fun LocalRawAssetDefault.toResourceNative(): Resource {
    return Resource.AnimatedAssetFile(fileName = this.fileName)
}

actual fun LocalImageAssetDefault.toResourceNative(): Resource {
    return Resource.LocalImageAssetFile(
        fileName = this.fileName,
        drawableResource = this.toDrawableResource()
    )
}

actual fun LocalFileAssetDefault.toResourceNative(): Resource {
    return Resource.LocalFileResource(fileName = this.fileName)
}
