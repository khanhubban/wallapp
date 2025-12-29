package wallapp.system.version

object SystemVersionNoOp : SystemVersion {

    override val versionId: String
        get() = "<unknown>"
}