package wallapp.resource

interface LocalAsset {
    fun toResource(): Resource

}
interface LocalImageAsset: LocalAsset

interface LocalFileAsset: LocalAsset

interface LocalRawAsset: LocalAsset
