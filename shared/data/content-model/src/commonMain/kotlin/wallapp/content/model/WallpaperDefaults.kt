package wallapp.content.model

class WallpaperDefaults(
    val defaultRemixId: Id.RemixId,
    val defaultDesignId: Id.DesignId,
) {
    companion object {
        val Preset = WallpaperDefaults(
            defaultRemixId = Id.RemixId("preset~wallpaper"),
            defaultDesignId = Id.DesignId("preset~design"),
        )
    }
}