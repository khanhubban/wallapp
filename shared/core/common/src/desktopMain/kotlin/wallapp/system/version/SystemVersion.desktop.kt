package wallapp.system.version

object SystemVersionDesktop : SystemVersion {

    override val versionId: String by lazy {
        val osName = System.getProperty("os.name")
        val osVersion = System.getProperty("os.version")
        "${osName}_${osVersion}"
    }
}