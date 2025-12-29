package wallapp.remoteendpoint

import wallapp.remoteapi.RemoteEndpointsSpec

fun RemoteEndpointsSpec.getUrlForTrack(track: RemoteEndpointTrack): String {
    return "${base}${track.specFilename}"
}