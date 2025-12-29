package wallapp.download

import wallapp.util.CancellableWork

interface FirebaseStorageDownloadCoordinatorIos {
    /**
     * completion with url and/or error message
     */
    fun downloadFile(path: String, completion: (String?, FirebaseStorageDownloadError?) -> Unit): CancellableWork
}

