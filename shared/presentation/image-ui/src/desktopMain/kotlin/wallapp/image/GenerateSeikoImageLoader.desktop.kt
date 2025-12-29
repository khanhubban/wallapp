package wallapp.image

import com.seiko.imageloader.ImageLoader
import com.seiko.imageloader.component.setupDefaultComponents
import com.seiko.imageloader.defaultImageResultMemoryCache
import com.seiko.imageloader.intercept.bitmapMemoryCacheConfig
import io.ktor.client.HttpClient
import okio.Path.Companion.toOkioPath
import wallapp.system.SystemContext
import java.io.File

actual fun generateSeikoImageLoader(
    systemContext: SystemContext,
    httpClient: HttpClient,
): ImageLoader {
    return ImageLoader {
        components {
            setupDefaultComponents(
                httpClient = { httpClient },
            )
        }
        interceptor {
            // cache 100 success image result, without bitmap
            defaultImageResultMemoryCache()
            bitmapMemoryCacheConfig {
//                maxSizeBytes(32 * 1024 * 1024) // 32MB
            }
            diskCacheConfig {
                directory(getCacheDir().toOkioPath().resolve("image_cache"))
                maxSizeBytes(512L * 1024 * 1024) // 512MB
            }
        }
    }
}

private fun getCacheDir(): File {
    return File(System.getProperty("user.home"), "Library/Caches/WallApp")
}

//private fun getCacheDir() = when (currentOperatingSystem) {
//    OperatingSystem.Windows -> File(System.getenv("AppData"), "$ApplicationName/cache")
//    OperatingSystem.Linux -> File(System.getProperty("user.home"), ".cache/$ApplicationName")
//    OperatingSystem.MacOS -> File(System.getProperty("user.home"), "Library/Caches/$ApplicationName")
//    else -> throw IllegalStateException("Unsupported operating system")
//}