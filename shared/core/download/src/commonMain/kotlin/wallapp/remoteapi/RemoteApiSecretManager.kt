package wallapp.remoteapi

interface RemoteApiSecretManager {

    // Exposed for testing only - not implemented in production
    fun getObfuscatedKey(key: String): String

    fun getDeobfuscatedKey(obfuscatedKey: String): String
}

fun getInitializationVector(): ByteArray {
    return byteArrayOf(47, -93, 98, 49, 107, 77, -74, 68, -17, -105, 89, 86)
}