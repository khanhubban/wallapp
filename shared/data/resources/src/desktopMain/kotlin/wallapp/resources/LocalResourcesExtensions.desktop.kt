package wallapp.resources

import org.jetbrains.compose.resources.ExperimentalResourceApi
import wallapp.resource.Resource
import java.io.File
import java.nio.charset.Charset

fun Resource.readBytesFromFileSystem(): ByteArray? {
    return if (this is Resource.FileResourceCompose) {
        try {
            val currentDirectory = System.getProperty("user.dir")
            println("Current directory: $currentDirectory")

            val filePath =
                "$currentDirectory/shared/data/resources/src/commonMain/composeResources/files/${this.fileName}"
            println("Reading file from: $filePath")

            val file = File(filePath)
            if (file.exists()) {
                file.readBytes().also {
                    println("Read ${it.size} bytes from ${this.fileName}")
                }
            } else {
                println("File not found: $filePath")
                null
            }
        } catch (e: Exception) {
            println("Error reading file ${this.fileName}: ${e.message}")
            null
        }
    } else {
        null
    }
}

@OptIn(ExperimentalResourceApi::class)
actual suspend fun Resource.readBytes(): ByteArray? {
    return if (this is Resource.FileResourceCompose) {
        Res.readBytes("files/${this.fileName}")
    } else {
        null
    }
}

actual suspend fun Resource.readString(): String? {
    return readBytes()?.toString(Charset.defaultCharset())
}