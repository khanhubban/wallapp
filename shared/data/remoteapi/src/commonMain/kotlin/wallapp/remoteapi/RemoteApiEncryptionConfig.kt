package wallapp.remoteapi

import kotlinx.coroutines.flow.StateFlow

interface RemoteApiEncryptionConfig {

    /**
     * Whether [key] must resolve to a non-blank value before remote data can be fetched.
     *
     * False for plaintext delivery paths, where no key is provisioned and callers must not
     * wait on [key]. Callers that gate on [key] are responsible for checking this first.
     */
    val requiresEncryptionKey: Boolean

    val key: StateFlow<String>

    val initializationVector: StateFlow<ByteArray>
}