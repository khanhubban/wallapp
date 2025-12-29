package wallapp.resources

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import org.jetbrains.compose.resources.ExperimentalResourceApi
import wallapp.resource.Resource

@OptIn(ExperimentalResourceApi::class)
actual suspend fun Resource.readBytes(): ByteArray? {
    return if (this is Resource.LocalFileResource) {
        Res.readBytes("files/${this.fileName}")
    } else {
        null
    }
}

@OptIn(ExperimentalResourceApi::class, ExperimentalForeignApi::class)
actual suspend fun Resource.readString(): String? {
    return readBytes()?.toKString()
}