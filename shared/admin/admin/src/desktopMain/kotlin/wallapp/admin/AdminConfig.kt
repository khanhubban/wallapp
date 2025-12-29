package wallapp.admin

import wallapp.string.quote

object AdminConfig {

    private val BackerGcpPathKey = "BACKER_GCP_PATH"
    val GoogleCloudStorageAuthPath: String = requireNotNull(System.getenv(BackerGcpPathKey)) {
        "Environment variable ${BackerGcpPathKey.quote()} not set"
    }
}