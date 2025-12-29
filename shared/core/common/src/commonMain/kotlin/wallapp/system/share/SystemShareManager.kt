package wallapp.system.share

interface SystemShareManager {

    val isAvailable: Boolean

    fun share(text: String)

}