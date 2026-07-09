package wallapp.image.sized

/**
 * Which [SizedImage] keys each kind of media-map entry must carry.
 *
 * The media map is a `mediaId -> { SizedImage.key -> url }` wire structure with no schema. Before
 * this enum the required keys were written out in three places — the pipeline's MediaMapBuilder,
 * CatalogValidator, and implicitly in whatever the renderers requested — and they drifted twice.
 * State the fact once, here.
 *
 * Declaration order within each set is the wire key order, and the pipeline's JSON preserves it.
 * Reordering a set changes published bytes.
 */
enum class MediaEntityKind(val requiredKeys: Set<SizedImage>) {

    WallpaperDownload(
        setOf(
            SizedImage.DownloadableWallpaperHd,
            SizedImage.DownloadableWallpaperSd,
        ),
    ),

    /** A collection card stacks three preview layers, each looked up by its own key. */
    WallpaperPreview(
        setOf(
            SizedImage.Showcase,
            SizedImage.WallpaperFeedSingle,
            SizedImage.WallpaperFeedTrack,
            SizedImage.FullScreen,
            SizedImage.WallpaperCollectionSmallLayer0,
            SizedImage.WallpaperCollectionSmallLayer1,
            SizedImage.WallpaperCollectionSmallLayer2,
            SizedImage.WallpaperCollectionLargeLayer0,
            SizedImage.WallpaperCollectionLargeLayer1,
            SizedImage.WallpaperCollectionLargeLayer2,
        ),
    ),

    ArtistProfile(
        setOf(
            SizedImage.ArtistMedium,
            SizedImage.ArtistSmall,
            SizedImage.Exhibit,
        ),
    ),

    FolderProfile(setOf(SizedImage.WallpaperFeedSingle)),

    /** The carousel highlight reads Exhibit. A feed key here leaves the card with no background. */
    FolderBanner(setOf(SizedImage.Exhibit)),

    ;

    val requiredKeyStrings: Set<String> get() = requiredKeys.mapTo(LinkedHashSet()) { it.key }
}
