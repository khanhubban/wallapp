package wallapp.resources

import wallapp.resource.Resource

actual fun LocalFileAssetBundled.toResourceNative(): Resource {
    return Resource.FileResourceCompose(this.fileName)
}
