package wallapp.messaging

interface CloudMessagingManager {

    fun initialize()

    fun onNewToken(token: String)

}