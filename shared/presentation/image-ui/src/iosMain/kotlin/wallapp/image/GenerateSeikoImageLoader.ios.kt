package wallapp.image


import com.seiko.imageloader.ImageLoader
import com.seiko.imageloader.ImageLoaderConfigBuilder
import com.seiko.imageloader.cache.memory.maxSizePercent
import com.seiko.imageloader.component.setupDefaultComponents
import com.seiko.imageloader.defaultImageResultMemoryCache
import com.seiko.imageloader.util.LogPriority
import com.seiko.imageloader.util.Logger
import com.seiko.imageloader.util.defaultFileSystem
import io.ktor.client.HttpClient
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import okio.Path.Companion.toPath
import platform.Foundation.NSCachesDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask
import wallapp.image.cache.SeikoDiskCacheDefault
import wallapp.log.Log
import wallapp.system.SystemContext

val seikoDiskCacheDefault by lazy {
    SeikoDiskCacheDefault(
        maxSize = 512L * 1024 * 1024, // 512MB
        directory = getCacheDir().toPath().resolve("image_cache"),
        fileSystem = defaultFileSystem!!,
        cleanupDispatcher = Dispatchers.IO,
    )
}

actual fun generateSeikoImageLoader(
    systemContext: SystemContext,
    httpClient: HttpClient,
): ImageLoader {
    return ImageLoader {
        commonConfig()
        components {
            setupDefaultComponents(
                httpClient = { httpClient },
            )
        }
        interceptor {
            defaultImageResultMemoryCache()
            memoryCacheConfig {
                maxSizePercent(0.50)
            }
            if (defaultFileSystem != null) {
                Log.d("[CustomImageCache] Use Custom RealDiskCache")
                diskCache {
                    seikoDiskCacheDefault
                }
            } else {
                Log.d("[CustomImageCache] Use Default DiskCache")
                diskCacheConfig {
                    directory(getCacheDir().toPath().resolve("image_cache"))
                    maxSizeBytes(512L * 1024 * 1024) // 512MB
                }
            }
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun getCacheDir(): String {
    return NSFileManager.defaultManager.URLForDirectory(
        NSCachesDirectory,
        NSUserDomainMask,
        null,
        true,
        null,
    )!!.path.orEmpty()
}

val ImageLoaderLogger = object : Logger {
    override fun log(
        priority: LogPriority,
        tag: String,
        data: Any?,
        throwable: Throwable?,
        message: String,
    ) {
        Log.i(
            message = buildString {
                append(tag)
                if (data != null) {
                    append("[image data] ")
                    append(data.toString().take(100))
                    append('\n')
                }
                append("[message] ")
                append(message)
            },
            throwable,
        )
    }

    override fun isLoggable(priority: LogPriority) = priority >= LogPriority.DEBUG
}

fun ImageLoaderConfigBuilder.commonConfig() {
//    logger = ImageLoaderLogger
//    interceptor {
//        addInterceptor(BlurInterceptor())
//    }
}
