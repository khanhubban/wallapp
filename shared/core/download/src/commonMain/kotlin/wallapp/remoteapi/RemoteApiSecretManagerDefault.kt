package wallapp.remoteapi

open class RemoteApiSecretManagerDefault : RemoteApiSecretManager {

    override fun getObfuscatedKey(key: String): String = key

    override fun getDeobfuscatedKey(obfuscatedKey: String): String = obfuscatedKey
}
