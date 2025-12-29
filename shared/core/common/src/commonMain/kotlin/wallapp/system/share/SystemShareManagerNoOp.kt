package wallapp.system.share

object SystemShareManagerNoOp : SystemShareManager {

    override val isAvailable: Boolean
        get() = false

    override fun share(text: String) { }
}