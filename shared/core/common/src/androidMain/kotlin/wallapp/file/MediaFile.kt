package wallapp.file

import java.io.FileDescriptor


interface MediaFile {

    val fileDescriptor: FileDescriptor

    val offset: Long

    val length: Long

    fun close()
}