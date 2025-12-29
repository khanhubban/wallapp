package wallapp.remoteendpoint

sealed interface RemoteEndpointMode {

    data object Bundled : RemoteEndpointMode

    data object FirebaseAdmin : RemoteEndpointMode

    data object Firebase : RemoteEndpointMode
}