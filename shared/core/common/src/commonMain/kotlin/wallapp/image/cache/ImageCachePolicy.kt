package wallapp.image.cache

import co.touchlab.skie.configuration.annotations.EnumInterop

@EnumInterop.Enabled
enum class ImageCachePolicy(
    val readEnabled: Boolean,
    val writeEnabled: Boolean,
) {
    Enabled(true, true),
    ReadyOnly(true, false),
    WriteOnly(false, true),
    Disabled(false, false),
}
