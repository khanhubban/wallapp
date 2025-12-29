package wallapp.remoteapi

class RemoteEndpointsSpecRepositoryStaging : RemoteEndpointsSpecRepository {

    override val remoteEndpointsSpec: RemoteEndpointsSpec
        get() = RemoteEndpointsSpecs.Staging
}