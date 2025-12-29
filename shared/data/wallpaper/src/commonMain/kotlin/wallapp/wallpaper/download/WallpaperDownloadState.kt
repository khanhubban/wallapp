package wallapp.wallpaper.download

import wallapp.content.model.Id
import wallapp.data.DataHandle
import wallapp.data.wallpaper.StaticWallpaperSize

sealed interface WallpaperDownloadState {

    val id: Id
    val staticWallpaperSize: StaticWallpaperSize?

    data class None(
        override val id: Id,
        override val staticWallpaperSize: StaticWallpaperSize? = null,
    ) : WallpaperDownloadState

    data class Queued(
        override val id: Id,
        override val staticWallpaperSize: StaticWallpaperSize,
//        val url: String,
    ) : WallpaperDownloadState

    data class DownloadStarting(
        override val id: Id,
        override val staticWallpaperSize: StaticWallpaperSize,
//        val url: String,
    ) : WallpaperDownloadState

    data class Downloading(
        override val id: Id,
        override val staticWallpaperSize: StaticWallpaperSize,
//        val url: String,
        /**
         * Progress as a percentage, or null if unknown
         */
        val progress: Float?,
    ) : WallpaperDownloadState

    data class Success(
        override val id: Id,
        val dataHandle: DataHandle,
        override val staticWallpaperSize: StaticWallpaperSize,
        /**
         * Optional logging data. Typically the URL that was downloaded, if it is available.
         * This data may not be available in the event say a [Success] instance is constructed
         * in a later session after verifying the data exists on the system.
         */
        val logData: String? = null,
    ) : WallpaperDownloadState {
        override fun toString(): String {
            return "Success(id=$id, dataHandle=${dataHandle}, logData: $logData)"
        }
    }

    data class Error(
        override val id: Id,
        override val staticWallpaperSize: StaticWallpaperSize,
        // Will be null in the event of the wallpaper URL being unavailable
        val url: String?,
        val message: String,
    ) : WallpaperDownloadState

    data class Cancelled(
        override val id: Id,
        override val staticWallpaperSize: StaticWallpaperSize,
        val url: String,
    ) : WallpaperDownloadState
}

//fun WallpaperDownloadState.isTerminalState(): Boolean {
//    return this is WallpaperDownloadState.Success ||
//            this is WallpaperDownloadState.Error ||
//            this is WallpaperDownloadState.Cancelled
//}

fun WallpaperDownloadState.isInProgress(): Boolean {
    return this is WallpaperDownloadState.Queued ||
            this is WallpaperDownloadState.DownloadStarting ||
            this is WallpaperDownloadState.Downloading
}

//val WallpaperDownloadState.buttonText: String
//    get() = when (this) {
//        is WallpaperDownloadState.None -> "d/l - None"
//        is WallpaperDownloadState.Queued -> "1%"
//        is WallpaperDownloadState.DownloadStarting -> "10%"
//        is WallpaperDownloadState.Downloading -> {
//            val normalizedProgress = progress
//                ?.let { (12 + ((it) * (100 - 12))).toInt() } ?: 12
//            "$normalizedProgress%"
//        }
//
//        is WallpaperDownloadState.Success -> "100%"
//        is WallpaperDownloadState.Error -> "d/l - error"
//        is WallpaperDownloadState.Cancelled -> "d/l - cancelled"
//    }


val WallpaperDownloadState.normalizedProgress: Float
    get() = when (this) {
        is WallpaperDownloadState.None -> 0f
        is WallpaperDownloadState.Queued -> 0.01f
        is WallpaperDownloadState.DownloadStarting -> 0.1f
        is WallpaperDownloadState.Downloading -> progress?.let { 0.12f + (it * (1f - 0.12f)) } ?: 0.12f
        is WallpaperDownloadState.Success -> 1f
        is WallpaperDownloadState.Error -> 0f
        is WallpaperDownloadState.Cancelled -> 0f
    }