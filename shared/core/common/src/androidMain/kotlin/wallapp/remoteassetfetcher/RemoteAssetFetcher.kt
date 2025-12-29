package wallapp.remoteassetfetcher

import java.io.File

typealias UrlString = String

/**
 * Downloads a file from a given URL, saves the file to disk
 * and returns a [File].
 *
 * Subsequent requests for the same URL will return the
 * disk copy rather than calling to the network again.
 */
interface RemoteAssetFetcher {

    suspend fun fetchFileByUrl(url: UrlString): File?

    suspend fun deleteAllBackups()

    companion object {
        const val REMOTE_ASSET_FETCHER_CACHE_SUFFIX = "asset_fetcher_cache/"

        fun UrlString.asHash(): String {
            return hashCode().toString()
        }
    }
}


object RemoteAssetFetcherNoOp : RemoteAssetFetcher {
    override suspend fun fetchFileByUrl(url: UrlString): File? = null

    override suspend fun deleteAllBackups() { }
}