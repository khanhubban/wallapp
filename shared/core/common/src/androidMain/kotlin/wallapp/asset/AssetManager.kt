package wallapp.asset

import wallapp.log.Log
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream

abstract class AssetManager {

    @Throws(IOException::class)
    abstract fun open(fileName: String): InputStream

    open fun loadStringFromAsset(filename: String): String? {
        return try {
            with(open(filename)) {
                val buffer = ByteArray(available())
                read(buffer)
                close()
                String(buffer)
            }
        } catch (ex: IOException) {
            Log.w(ex, ex.localizedMessage)
            null
        }
    }
}

class AssetManagerMock() : AssetManager() {
    var mockAssetString: String? = null

    override fun loadStringFromAsset(filename: String): String? {
        return mockAssetString ?: super.loadStringFromAsset(filename)
    }

    override fun open(fileName: String): InputStream {
        return FileInputStream(File(fileName))
    }
}