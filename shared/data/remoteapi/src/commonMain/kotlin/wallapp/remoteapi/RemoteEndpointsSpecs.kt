package wallapp.remoteapi


object RemoteEndpointsSpecs {

    /**
     * Note: Any changes should be verified in [RemoteEndpointsSpecsTest].
     */
    val Staging = RemoteEndpointsSpec(
        bucketRoot = "api",
        apiVersion = "v0",
        base = "api/v0/",
    )
}

