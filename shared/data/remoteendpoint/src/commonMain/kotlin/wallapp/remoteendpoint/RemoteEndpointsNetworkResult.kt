package wallapp.remoteendpoint

sealed interface RemoteEndpointsNetworkResult {

    data class Success(val remoteEndpoints: RemoteEndpoints) : RemoteEndpointsNetworkResult

    data class Error(val errorMessage: String) : RemoteEndpointsNetworkResult
}
