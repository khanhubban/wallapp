package wallapp.googlecloud

import com.google.api.gax.retrying.RetrySettings
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.http.HttpTransportOptions
import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageOptions
import com.google.common.collect.Lists
import org.threeten.bp.Duration
import wallapp.admin.AdminConfig.GoogleCloudStorageAuthPath
import java.io.FileInputStream

class GoogleStorageRepositoryLocal : GoogleStorageRepository {

    private val credentials: GoogleCredentials by lazy {
        GoogleCredentials.fromStream(FileInputStream(GoogleCloudStorageAuthPath))
            .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"))
    }

    override val storage: Storage by lazy {
        val transportOptions = HttpTransportOptions.newBuilder()
            .setConnectTimeout(60000 * 5)
            .setReadTimeout(60000 * 5)
            .build()

        val retrySettings = RetrySettings.newBuilder()
            .setMaxAttempts(2)
            .setTotalTimeout(Duration.ofMinutes(15))
            .setInitialRetryDelay(Duration.ofSeconds(10))
            .setMaxRetryDelay(Duration.ofSeconds(30))
            .setRetryDelayMultiplier(1.5)
            .setInitialRpcTimeout(Duration.ofSeconds(60))
            .setMaxRpcTimeout(Duration.ofSeconds(60))
            .setRpcTimeoutMultiplier(1.0)
            .build()

        StorageOptions
            .newBuilder()
            .setCredentials(credentials)
            .setTransportOptions(transportOptions)
            .setRetrySettings(retrySettings)
            .build()
            .service
    }
}