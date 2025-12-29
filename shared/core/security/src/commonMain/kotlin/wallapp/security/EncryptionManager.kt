package wallapp.security

interface EncryptionManager {

    suspend fun encrypt(data: ByteArray, key: String, initializationVector: ByteArray): EncryptionResult

    suspend fun decrypt(encryptionResult: EncryptionResult, key: String): ByteArray
}

expect fun createEncryptionManager(): EncryptionManager