package wallapp.data

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.NSFileManager
import platform.Foundation.create
import platform.posix.memcpy

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
actual fun readDataBlobFromFile(filePath: String): DataBlob? {
    val fileManager = NSFileManager.defaultManager()
    if (!fileManager.fileExistsAtPath(filePath)) {
        return null
    }

    val data = NSData.create(contentsOfFile = filePath)
    if (data == null || data.length.toInt() == 0) {
        return null
    }

    // Convert NSData to ByteArray
    val bytes = ByteArray(data.length.toInt()).apply {
        usePinned {
            memcpy(it.addressOf(0), data.bytes, data.length)
        }
    }

    return DataBlob(bytes)
}
