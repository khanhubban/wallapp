package wallapp.content.model

interface WallpaperGroup : WallpaperItem {
    override val id: Id
    val label: String
    val previewRemix: WallpaperRemix
}