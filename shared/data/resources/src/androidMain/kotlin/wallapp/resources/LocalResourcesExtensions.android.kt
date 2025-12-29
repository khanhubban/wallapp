package wallapp.resources

import org.jetbrains.compose.resources.ExperimentalResourceApi
import wallapp.resource.Resource
import java.io.File
import java.nio.charset.Charset

private fun findRepoRoot(start: File): File? {
    var dir: File? = start
    repeat(12) {
        val current = dir ?: return null
        if (File(current, "gradlew").exists() || File(current, ".git").exists()) {
            return current
        }
        dir = current.parentFile
    }
    return null
}

private fun Resource.readBytesFromFileSystem(): ByteArray? {
    if (this !is Resource.FileResourceCompose) return null
    val currentDirectory = System.getProperty("user.dir") ?: return null
    val resourceSuffix = "shared/data/resources/src/commonMain/composeResources/files/${this.fileName}"
    val repoRoot = findRepoRoot(File(currentDirectory))
    val repoCandidate = repoRoot?.let { File(it, resourceSuffix) }
    if (repoCandidate?.exists() == true) return repoCandidate.readBytes()

    var dir: File? = File(currentDirectory)
    repeat(12) {
        val current = dir ?: return null
        val candidate = File(current, resourceSuffix)
        if (candidate.exists()) return candidate.readBytes()
        dir = current.parentFile
    }
    return null
}

@OptIn(ExperimentalResourceApi::class)
actual suspend fun Resource.readBytes(): ByteArray? {
    return if (this is Resource.FileResourceCompose) {
        try {
            Res.readBytes("files/${this.fileName}")
        } catch (e: IllegalStateException) {
            readBytesFromFileSystem()
        }
    } else {
        null
    }
}

actual suspend fun Resource.readString(): String? {
    return readBytes()?.toString(Charset.defaultCharset())
}
