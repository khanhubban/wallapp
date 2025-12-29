package wallapp.permission

enum class PhotoPermissionTypeIos {
    AddToPhotos,
    FullPhotosLibrary,
    ;

    companion object {
        val Default = FullPhotosLibrary
    }
}