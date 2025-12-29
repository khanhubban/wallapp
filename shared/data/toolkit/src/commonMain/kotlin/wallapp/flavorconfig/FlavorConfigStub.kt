package wallapp.flavorconfig

open class FlavorConfigStub : FlavorConfig() {
    override val applicationId: String
        get() = ""
    override val exposeUserAccount: Boolean
        get() = false
    override val requireLicense: Boolean
        get() = false
    override val cachedLicenseState: String
        get() = ""
//    override val appIconResourceId: Int
//        get() = -1
}