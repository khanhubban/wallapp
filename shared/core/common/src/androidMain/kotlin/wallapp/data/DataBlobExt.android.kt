package wallapp.data

import wallapp.log.Log
import java.io.File

actual fun readDataBlobFromFile(filePath: String): DataBlob? {
    return try {
        val file = File(filePath)
        if (!file.exists() || !file.isFile) {
            return null
        }
        val bytes = file.readBytes()
        if (bytes.isEmpty()) {
            return null
        }
        DataBlob(bytes)
    } catch (e: Exception) {
        Log.e(e)
        null
    }
}