package wallapp.remoteapi

data class RemoteApiExportSpec(
    val remoteEndpointsSpec: RemoteEndpointsSpec,
    val encryptionConfig: RemoteApiEncryptionConfig,
    val keyFilename: String,
) {
    val apiVersion: String
        get() = remoteEndpointsSpec.apiVersion
}
