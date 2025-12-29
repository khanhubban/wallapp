package wallapp.remoteapi

import java.io.File


fun main() {
    val remoteApiStorageManager = RemoteApiStorageManager()
    val apiDataRootDirectory = File(ApiExportDataRoot)

    remoteApiStorageManager.update(
        apiDataRootDirectory = apiDataRootDirectory,
        processData = true,
        uploadData = true,
        remoteApiExportSpecs = listOf(RemoteApiExportSpecStaging),
//        remoteApiExportSpecs = listOf(RemoteApiExportSpecStaging),
//            .filter { it == RemoteApiExportSpecStaging },
        recursive = true,
    )
}
