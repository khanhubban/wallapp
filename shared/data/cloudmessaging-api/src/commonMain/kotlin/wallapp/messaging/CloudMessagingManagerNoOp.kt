package wallapp.messaging

object CloudMessagingManagerNoOp : CloudMessagingManager {

    override fun initialize() { }

    override fun onNewToken(token: String) { }
}