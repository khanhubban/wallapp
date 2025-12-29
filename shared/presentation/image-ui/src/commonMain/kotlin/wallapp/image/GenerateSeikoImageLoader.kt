package wallapp.image

import io.ktor.client.HttpClient
import wallapp.system.SystemContext
import com.seiko.imageloader.ImageLoader as SeikoImageLoader


expect fun generateSeikoImageLoader(
    systemContext: SystemContext,
    httpClient: HttpClient,
): SeikoImageLoader
