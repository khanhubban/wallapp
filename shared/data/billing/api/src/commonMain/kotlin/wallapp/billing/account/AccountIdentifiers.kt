package wallapp.billing.account

data class AccountIdentifiers(
    val obfuscatedAccountId: String?,
    val obfuscatedProfileId: String?,
)

val AccountIdentifiers?.isEmpty: Boolean
    get() = (this == null || (obfuscatedAccountId == null && obfuscatedProfileId == null))

val AccountIdentifiers?.isNotEmpty: Boolean
    get() = !isEmpty