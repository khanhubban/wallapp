package wallapp.content.model

val WallpaperItem.remixId: Id.RemixId?
    get() = when (this) {
        is WallpaperRemix -> id
        is WallpaperRemixParallax -> id
        else -> null
    }

val WallpaperItem.remix: WallpaperRemix
    get() = when (this) {
        is WallpaperRemix -> this
        is WallpaperRemixParallax -> this
        is WallpaperCategory -> throw IllegalStateException("WallpaperCategory does not have a remix ($this)")
        else -> throw IllegalStateException("Unknown WallpaperItem type: $this")
    }

val WallpaperRemix.designIdCompat: Id.DesignId
    get() = id.designIdCompat

val List<WallpaperItem>.wallpapers: List<Wallpaper>?
    get() = filterIsInstance<Wallpaper>()
        .ifEmpty { null }

val List<WallpaperItem>.singles: List<Wallpaper>?
    get() = wallpapers
        ?.filter { it.isSingle }
        ?.ifEmpty { null }

val List<WallpaperItem>.tracks: List<Wallpaper>?
    get() = wallpapers
        ?.filter { it.isTrack }
        ?.ifEmpty { null }

val List<WallpaperItem>.collectionWallpapers: List<Wallpaper>?
    get() = tracks

val List<WallpaperItem>.collectionCategories: List<WallpaperCategory>?
    get() = filterIsInstance<WallpaperCategory>()
        .filter { it.categoryType == WallpaperCategoryType.Collection }
        .ifEmpty { null }