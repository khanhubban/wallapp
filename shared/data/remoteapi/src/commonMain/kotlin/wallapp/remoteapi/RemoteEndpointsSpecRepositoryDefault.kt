package wallapp.remoteapi

class RemoteEndpointsSpecRepositoryDefault : RemoteEndpointsSpecRepository {

    override val remoteEndpointsSpec: RemoteEndpointsSpec
        get() = RemoteEndpointsSpecs.Staging
}