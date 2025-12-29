package wallapp.remoteapi

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RemoteApiEncryptionConfigPreset : RemoteApiEncryptionConfig {

    // Keep this aligned with RemoteApiEncryptionConfigDefault + the key stored in Firebase Storage.
    override val key: StateFlow<String> = MutableStateFlow("bd446249-1c66-4a67-b49b-c605f922b5cb")

    override val initializationVector: StateFlow<ByteArray> = MutableStateFlow(
        byteArrayOf(47, -93, 98, 49, 107, 77, -74, 68, -17, -105, 89, 86)
    )
}
