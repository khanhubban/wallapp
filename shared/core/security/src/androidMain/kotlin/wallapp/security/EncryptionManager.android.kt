package wallapp.security

import dev.whyoleg.cryptography.CryptographyProvider
import dev.whyoleg.cryptography.algorithms.symmetric.AES
import dev.whyoleg.cryptography.operations.cipher.AuthenticatedCipher
import javax.crypto.spec.SecretKeySpec

class EncryptionManagerAndroid : EncryptionManager {

    private val aesGcm: AES.GCM
        get() = CryptographyProvider.Default.get(AES.GCM)

    // Derive AES key manually from the input string and decode it to the required format
    private suspend fun deriveKeyFromString(key: String): AES.GCM.Key {
        // Ensure key is 256 bits (32 bytes)
        val keyBytes = key.toByteArray().copyOf(32)
        val secretKeySpec = SecretKeySpec(keyBytes, "AES")

        return aesGcm.keyDecoder().decodeFrom(AES.Key.Format.RAW, secretKeySpec.encoded)
    }

    override suspend fun encrypt(
        data: ByteArray,
        key: String,
        initializationVector: ByteArray,
    ): EncryptionResult {
        val aesKey = deriveKeyFromString(key)
        val cipher: AuthenticatedCipher = aesKey.cipher()

        val ciphertext = cipher.encrypt(plaintextInput = data, associatedData = initializationVector)
        return EncryptionResult(ciphertext, initializationVector)
    }

    override suspend fun decrypt(encryptionResult: EncryptionResult, key: String): ByteArray {
        val aesKey = deriveKeyFromString(key)
        val cipher = aesKey.cipher()

        return cipher.decrypt(ciphertextInput = encryptionResult.data, associatedData = encryptionResult.initializationVector)
    }
}

actual fun createEncryptionManager(): EncryptionManager {
    return EncryptionManagerAndroid()
}