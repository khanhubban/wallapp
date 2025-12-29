package wallapp.remoteendpoint

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import wallapp.string.quote

@Serializable
data class RemoteEndpointMediaMap(
    val root: String,
    @SerialName("p") val imageHostPlatformKeys: List<String>,
    @SerialName("b") val imageBucketSpecKeys: List<String>,
) {
    fun getEndpoint(imageHostPlatformKey: String, imageBucketSpecKey: String): String {
        require(imageHostPlatformKeys.contains(imageHostPlatformKey)) {
            "Platform key (${imageHostPlatformKey.quote()}) should be in $imageHostPlatformKeys" }
        require(imageBucketSpecKeys.contains(imageBucketSpecKey)) {
            "Image bucket spec key (${imageBucketSpecKey.quote()}) should be in $imageBucketSpecKeys" }
        return "$root-$imageHostPlatformKey-$imageBucketSpecKey"
    }

    init {
        require(root.contains("-")) {
            "Root (${root.quote()}) should contain ${"-".quote()} for the version" }
    }
}


fun RemoteEndpointMediaMap.getEndpointPreset(): String {
    return getEndpoint(imageHostPlatformKeys.first(), imageBucketSpecKeys.first())
}