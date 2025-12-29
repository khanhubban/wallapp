package wallapp.apprestarter

object AppRestarterNoOp : AppRestarter {

    override val enabled: Boolean
        get() = false

    override fun restartApp() { }
}