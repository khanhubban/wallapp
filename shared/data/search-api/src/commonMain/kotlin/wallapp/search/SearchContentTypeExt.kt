package wallapp.search

val SearchContentType.isCurator: Boolean
    get() = this == SearchContentType.Artists || this == SearchContentType.Folders

val SearchContentTypes.isOnlyWallpapers: Boolean
    get() = !types.contains(SearchContentType.Artists)
            && !types.contains(SearchContentType.Folders)
            && (types.contains(SearchContentType.Singles) || types.contains(SearchContentType.Tracks))

val SearchContentTypes.isOnlyCurators: Boolean
    get() = types.contains(SearchContentType.Artists) || types.contains(SearchContentType.Folders)