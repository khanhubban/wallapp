package wallapp.resources.string

object StringsNoOp : Strings {
    override fun get(key: String): String {
        return key
    }
}