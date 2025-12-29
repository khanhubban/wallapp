package wallapp.remoteendpoint

sealed interface RemoteEndpointTrack {

    data object Production : RemoteEndpointTrack

    data object Staging : RemoteEndpointTrack

    data object Development : RemoteEndpointTrack

}

val RemoteEndpointTrack.specFilename
    get() = when (this) {
        is RemoteEndpointTrack.Production -> "spec.json"
        is RemoteEndpointTrack.Staging -> "spec-s.json"
        is RemoteEndpointTrack.Development -> "spec-d.json"
    }