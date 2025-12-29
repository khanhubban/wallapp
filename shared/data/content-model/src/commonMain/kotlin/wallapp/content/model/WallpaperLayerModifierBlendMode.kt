package wallapp.content.model


//@Keep
enum class WallpaperLayerModifierBlendMode(val code: String) {
    SrcAtop("src_atop")
}

fun asBlendMode(code: String?): WallpaperLayerModifierBlendMode? {
    return WallpaperLayerModifierBlendMode.entries.find { it.code == code }
}

