package wallapp.image

import com.seiko.imageloader.ImageLoader
import com.seiko.imageloader.cache.memory.maxSizePercent
import com.seiko.imageloader.component.setupDefaultComponents
import com.seiko.imageloader.intercept.bitmapMemoryCacheConfig
import com.seiko.imageloader.intercept.imageMemoryCacheConfig
import com.seiko.imageloader.intercept.painterMemoryCacheConfig
import com.seiko.imageloader.option.androidContext
import io.ktor.client.HttpClient
import okio.Path.Companion.toOkioPath
import wallapp.system.SystemContext
import wallapp.system.SystemContextAndroid

actual fun generateSeikoImageLoader(
    systemContext: SystemContext,
    httpClient: HttpClient,
): ImageLoader {
    val context = (systemContext as SystemContextAndroid).applicationContext

    return ImageLoader {
//        logger = object : Logger {
//            override fun isLoggable(priority: LogPriority): Boolean {
//                return true
//            }
//
//            override fun log(
//                priority: LogPriority,
//                tag: String,
//                data: Any?,
//                throwable: Throwable?,
//                message: String,
//            ) {
//                Log.d("[SeikoImageLoader] $message")
//            }
//        }

        options {
            androidContext(context)
        }
        components {
            setupDefaultComponents(
                httpClient = { httpClient },
            )
        }
        interceptor {
            bitmapMemoryCacheConfig {
                maxSizePercent(context, 0.25)
            }
            imageMemoryCacheConfig {
                maxSize(50)
            }
            painterMemoryCacheConfig {
                maxSize(50)
            }
            diskCacheConfig {
                directory(context.cacheDir.resolve("image_loader_cache").toOkioPath())
                maxSizeBytes(512L * 1024 * 1024) // 512MB
            }
        }
    }
}