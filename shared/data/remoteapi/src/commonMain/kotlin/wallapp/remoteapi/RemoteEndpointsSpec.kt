package wallapp.remoteapi

data class RemoteEndpointsSpec(
    val bucketRoot: String,
    val apiVersion: String,
    val base: String, // $bucketRoot/$apiVersion/
)