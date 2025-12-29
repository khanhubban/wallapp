package wallapp.security

import dev.whyoleg.cryptography.CryptographyProvider
import dev.whyoleg.cryptography.algorithms.symmetric.AES
import dev.whyoleg.cryptography.operations.cipher.AuthenticatedCipher
import io.ktor.utils.io.core.toByteArray

class EncryptionManagerIos : EncryptionManager {

    private val aesGcm: AES.GCM
        get() = CryptographyProvider.Default.get(AES.GCM)

    // Derive AES key from the input string and decode it to the required format
    private suspend fun deriveKeyFromString(key: String): AES.GCM.Key {
        // Convert the key string to bytes and pad/truncate it to ensure it is 32 bytes (256 bits) for AES-256
        val keyBytes = key.toByteArray().copyOf(32)
        return aesGcm.keyDecoder().decodeFrom(AES.Key.Format.RAW, keyBytes)
    }

    override suspend fun encrypt(
        data: ByteArray,
        key: String,
        initializationVector: ByteArray,
    ): EncryptionResult {
        val aesKey = deriveKeyFromString(key)
        val cipher: AuthenticatedCipher = aesKey.cipher()

        val ciphertext = cipher.encrypt(
            plaintextInput = data,
            associatedData = initializationVector,
        )
        return EncryptionResult(ciphertext, initializationVector)
    }

    override suspend fun decrypt(encryptionResult: EncryptionResult, key: String): ByteArray {
        val aesKey = deriveKeyFromString(key)
        val cipher = aesKey.cipher()

        return cipher.decrypt(
            ciphertextInput = encryptionResult.data,
            associatedData = encryptionResult.initializationVector,
        )
    }
}

actual fun createEncryptionManager(): EncryptionManager {
    return EncryptionManagerIos()
}