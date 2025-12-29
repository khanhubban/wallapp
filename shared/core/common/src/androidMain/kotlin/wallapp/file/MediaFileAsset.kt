package wallapp.file

import android.content.res.AssetFileDescriptor
import java.io.FileDescriptor


class MediaFileAsset(private val assetFileDescriptor: AssetFileDescriptor) : MediaFile {

    override val fileDescriptor: FileDescriptor
        get() = assetFileDescriptor.fileDescriptor
    override val offset: Long
        get() = assetFileDescriptor.startOffset
    override val length: Long
        get() = assetFileDescriptor.declaredLength

    override fun close() { }
}