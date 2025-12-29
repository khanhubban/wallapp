package wallapp.resources

import wallapp.resource.Resource

object LocalFileResources {

    val StringsEs get() = Resource.from(LocalFileAssetDefault.StringsEs)
    val StringsHi get() = Resource.from(LocalFileAssetDefault.StringsHi)
    val StringsPt get() = Resource.from(LocalFileAssetDefault.StringsPt)
}