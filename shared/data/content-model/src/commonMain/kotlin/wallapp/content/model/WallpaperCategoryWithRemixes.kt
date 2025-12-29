package wallapp.content.model

data class WallpaperCategoryWithRemixes(
    val category: WallpaperCategory,
    val remixes: List<WallpaperRemix>,
)
