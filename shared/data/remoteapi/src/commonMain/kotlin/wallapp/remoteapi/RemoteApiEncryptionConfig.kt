package wallapp.remoteapi

import kotlinx.coroutines.flow.StateFlow

interface RemoteApiEncryptionConfig {

    val key: StateFlow<String>

    val initializationVector: StateFlow<ByteArray>
}