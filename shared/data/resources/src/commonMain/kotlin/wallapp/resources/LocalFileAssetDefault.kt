package wallapp.resources

import wallapp.resource.LocalFileAsset
import wallapp.resource.Resource

sealed class LocalFileAssetDefault(val fileName: String): LocalFileAsset {
    override fun toResource(): Resource {
        return this.toResourceNative()
    }

    data object StringsEs: LocalFileAssetDefault("strings_es.json")
    data object StringsHi: LocalFileAssetDefault("strings_hi.json")
    data object StringsPt: LocalFileAssetDefault("strings_pt.json")
}

expect fun LocalFileAssetDefault.toResourceNative(): Resource