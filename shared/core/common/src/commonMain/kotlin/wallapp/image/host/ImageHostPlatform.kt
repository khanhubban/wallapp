package wallapp.image.host

import kotlinx.serialization.Serializable

@Serializable
enum class ImageHostPlatform(val key: String) {

    Apple("i"), // Use of "a" can be ambiguous with "Android" - use "i" for "iOS"
    Compose("c"),
    ;
}

expect fun getSystemImageHostPlatform(): ImageHostPlatform