package wallapp.content.model

sealed class WallpaperLayerModifier

data class WallpaperLayerModifierBlend(
    val blendMode: WallpaperLayerModifierBlendMode,
) : WallpaperLayerModifier()

data class WallpaperLayerModifierTint(
    val tintColor: Int,

    // If true, use GlTintWhiteImage
    val flatColored: Boolean,
) : WallpaperLayerModifier()