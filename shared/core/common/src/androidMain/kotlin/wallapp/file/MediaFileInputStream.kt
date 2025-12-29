package wallapp.file

import java.io.File
import java.io.FileDescriptor
import java.io.FileInputStream


class MediaFileInputStream(val file: File) : MediaFile {

    var initialized = false
    val fileInputStream = FileInputStream(file).also {
        initialized = true
    }

    override val fileDescriptor: FileDescriptor
        get() {
            require(initialized)
            return fileInputStream.fd
        }

    override val offset: Long
        get() = 0

    override val length: Long
        get() = 0

    override fun close() {
        if (initialized) {
            fileInputStream.close()
            initialized = false
        }
    }

    override fun toString(): String {
        return file.name
    }
}