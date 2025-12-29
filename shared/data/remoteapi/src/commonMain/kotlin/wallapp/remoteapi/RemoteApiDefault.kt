package wallapp.remoteapi

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import wallapp.data.DataBlob
import wallapp.data.DataHandle
import wallapp.data.DataRepository
import wallapp.download.DownloadState
import wallapp.download.FirebaseStorageDownloader
import wallapp.security.EncryptionManager
import wallapp.security.EncryptionResult

object RemoteApiNoOp : RemoteApi {
    override fun getFile(path: String): Flow<DownloadState> = flow {
        emit(DownloadState.Error(url = path, message = "NoOp"))
    }
}

class RemoteApiDefault(
    private val remoteApiEncryptionConfig: RemoteApiEncryptionConfig,
    private val firebaseStorageDownloader: FirebaseStorageDownloader,
    private val encryptionManager: EncryptionManager,
    private val dataRepository: DataRepository,
) : RemoteApi {

    private val encryptionKey: StateFlow<String>
        get() = remoteApiEncryptionConfig.key
    private val initializationVector: StateFlow<ByteArray>
        get() = remoteApiEncryptionConfig.initializationVector

    override fun getFile(path: String): Flow<DownloadState> =
        flow {
            val encryptionKey = encryptionKey.value
            if (encryptionKey.isEmpty()) {
                emit(DownloadState.Error(url = path, message = "can't start download"))
                return@flow
            }

            val initializationVector = initializationVector.value
            if (initializationVector.isEmpty()) {
                emit(DownloadState.Error(url = path, message = "can't start download"))
                return@flow
            }

            firebaseStorageDownloader.downloadFile(path).collect { downloadState ->
                emit(
                    when (downloadState) {
                        is DownloadState.Success -> {
                            processDownloadState(downloadState, encryptionKey, initializationVector)
                        }
                        else -> downloadState
                    }
                )
            }
        }

    private suspend fun processDownloadState(
        downloadState: DownloadState.Success,
        encryptionKey: String,
        initializationVector: ByteArray,
    ): DownloadState {
        val result = decryptData(
            dataHandle = downloadState.dataHandle,
            key = encryptionKey,
            initializationVector = initializationVector,
        )
        return if (result != null) {
            DownloadState.Success(
                url = downloadState.url,
                dataHandle = DataHandle.InMemory(DataBlob(result)),
            )
        } else {
            DownloadState.Error(
                url = downloadState.url,
                message = "Data error",
            )
        }
    }
    private suspend fun decryptData(
        dataHandle: DataHandle,
        key: String,
        initializationVector: ByteArray,
    ): ByteArray? {
        return dataRepository.getDataBlob(dataHandle)?.let { dataBlob ->
            val encryptionResult = EncryptionResult(
                data = dataBlob.byteArray,
                initializationVector = initializationVector,
            )
            encryptionManager.decrypt(encryptionResult, key)
        }
    }
}