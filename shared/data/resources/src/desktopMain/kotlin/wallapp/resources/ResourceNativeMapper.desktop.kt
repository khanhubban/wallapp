package wallapp.resources

import wallapp.resource.Resource

actual fun LocalRawAssetDefault.toResourceNative(): Resource {
    return Resource.Placeholder
}

actual fun LocalImageAssetDefault.toResourceNative(): Resource {
    return Resource.DrawableResourceCompose(this.toDrawableResource())
}

actual fun LocalFileAssetDefault.toResourceNative(): Resource {
    return Resource.FileResourceCompose(this.fileName)
}
