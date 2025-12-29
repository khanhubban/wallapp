package wallapp.remoteendpoint

class RemoteEndpointsRepositoryPresetDefault(
    private val jsonString: String = "",
) : RemoteEndpointsRepositoryPreset {

    companion object {
        val Log = RemoteEndpointLog
    }

    private fun loadRemoteEndpoints(): RemoteEndpoints {
        return RemoteEndpoints.from(jsonString)
    }

    override val remoteEndpointsProduction: RemoteEndpoints by lazy {
        loadRemoteEndpoints()
    }
}
