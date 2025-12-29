package wallapp.remoteapi

import com.google.cloud.storage.Bucket
import com.google.firebase.cloud.StorageClient
import kotlinx.coroutines.runBlocking
import wallapp.firebase.firestore.initializeFirebase
import wallapp.security.createEncryptionManager
import wallapp.service.Constant.AppStorageBucketName
import java.io.File

class RemoteApiStorageManager {

    private val encryptionManager = createEncryptionManager()
    private val remoteApiSecretManager = RemoteApiSecretManagerAll()

    fun update(
        apiDataRootDirectory: File,
        processData: Boolean,
        uploadData: Boolean,
        remoteApiExportSpecs: List<RemoteApiExportSpec>,
        recursive: Boolean,
    ) {
        if (!apiDataRootDirectory.isDirectory) {
            throw IllegalArgumentException("The provided path is not a directory")
        }

        val data = remoteApiExportSpecs.map { remoteApiExportSpec ->
            val directory = File(apiDataRootDirectory, remoteApiExportSpec.apiVersion)
            require(directory.isDirectory) {
                "The provided path is not a directory: ${directory.absolutePath}"
            }
            remoteApiExportSpec to directory
        }

        if (processData) {
            println("Processing data...")
            data.forEach { (remoteApiExportSpec, directory) ->
                processApiData(remoteApiExportSpec, directory, recursive)
            }
        }
        if (uploadData) {
            println("Uploading data...")
            data.forEach { (remoteApiExportSpec, directory) ->
                uploadApiData(remoteApiExportSpec, directory, recursive)
            }
        }

        if (processData && uploadData) {
            println("Update complete")
        } else if (!processData) {
            println("*** Data uploaded but NOT processed!")
        } else if (!uploadData) {
            println("*** Data processed but NOT uploaded!")
        }
    }

    private fun processApiData(
        remoteApiExportSpec: RemoteApiExportSpec,
        directory: File,
        recursive: Boolean,
    ) {
        createStorageHostedEncryptionKey(remoteApiExportSpec, directory)
        encryptApiData(remoteApiExportSpec, directory, recursive)
    }

    private fun createStorageHostedEncryptionKey(
        remoteApiExportSpec: RemoteApiExportSpec,
        directory: File,
    ) {
        val encryptionConfig = remoteApiExportSpec.encryptionConfig

        val key = encryptionConfig.key.value
        val keyFile = File(directory, remoteApiExportSpec.keyFilename)
        keyFile.writeText(key)
        println("** Created key file in ${directory.absolutePath}")
    }

    /**
     * Encrypts all files in the provided directory and its subdirectories. Each encrypted file is
     * also decrypted to ensure the encryption is correct.
     *
     * Note: the results are NOT deterministic.
     */
    private fun encryptApiData(
        remoteApiExportSpec: RemoteApiExportSpec,
        directory: File,
        recursive: Boolean,
    ) {
        processDirectory(directory, directory, recursive) { file, baseDirectory ->
            if (file.isFileEncryptionEligible(remoteApiExportSpec)) {
                if (file.name.endsWith(EncryptedFileExtension)) {
                    println("-- Already encrypted, ignoring ${file.absolutePath}")
                    return@processDirectory
                }
                encryptFile(file, remoteApiExportSpec.encryptionConfig)
            } else {
                println("-- Ignoring ${file.absolutePath}")
            }
        }
    }

    private fun encryptFile(file: File, encryptionConfig: RemoteApiEncryptionConfig) {
        val fileData = file.readBytes()
        val encryptionResult = runBlocking {
            encryptionManager.encrypt(
                fileData,
                encryptionConfig.key.value,
                encryptionConfig.initializationVector.value,
            )
        }

        // save encryptionResult.data to a filename with ".enc" extension
        val encryptedFile = File(file.absolutePath + ".data")
        encryptedFile.writeBytes(encryptionResult.data)
        println("Encrypted ${file.absolutePath}")

        // Now decrypt the file and compare against the original data
        val decryptedData = runBlocking {
            encryptionManager.decrypt(encryptionResult, encryptionConfig.key.value)
        }
        require(fileData.contentEquals(decryptedData)) {
            "Decrypted data does not match the original data"
        }
    }

    private fun uploadApiData(remoteApiExportSpec: RemoteApiExportSpec, directory: File, recursive: Boolean) {
        initializeFirebase()
        val bucket = StorageClient.getInstance().bucket(AppStorageBucketName)
        uploadDirectoryContentsToFirebaseStorage(bucket, remoteApiExportSpec, directory, recursive)
    }

    private fun Bucket.uploadFileToStorage(file: File, relativePath: String) {
        // Upload the file to Firebase Storage, preserving its directory structure
        val blob = create("$relativePath${file.name}", file.readBytes())
        println("Uploaded ${file.absolutePath} to ${blob.name}")
    }

    private fun File.isFileEncryptionEligible(remoteApiExportSpec: RemoteApiExportSpec): Boolean {
        return isFile
                && !IgnoredFilenames.contains(name)
                && name != remoteApiExportSpec.keyFilename
    }

    private fun File.isFileUploadEligible(remoteApiExportSpec: RemoteApiExportSpec): Boolean {
        return isFile
                && !IgnoredFilenames.contains(name)
                && (name.endsWith(EncryptedFileExtension) || name.equals(remoteApiExportSpec.keyFilename))
    }

    private fun processDirectory(
        dir: File,
        baseDirectory: File,
        recursive: Boolean,
        processFile: (File, File) -> Unit,
    ) {
        val files = dir.listFiles() ?: return
        for (file in files) {
            if (file.isDirectory && recursive) {
                processDirectory(file, baseDirectory, recursive, processFile)
            } else if (file.isFile) {
                processFile(file, baseDirectory)
            }
        }
    }

    private fun uploadDirectoryContentsToFirebaseStorage(
        bucket: Bucket,
        remoteApiExportSpec: RemoteApiExportSpec,
        directory: File,
        recursive: Boolean,
    ) {
        processDirectory(directory, directory, recursive) { file, other ->
            val name = file.name
            println("Processing $name..., $other")
            if (file.isFileUploadEligible(remoteApiExportSpec)) {
                val relativePath = remoteApiExportSpec.remoteEndpointsSpec.base
                bucket.uploadFileToStorage(file, relativePath)
            } else {
                if (file.isFileEncryptionEligible(remoteApiExportSpec)) {
                    println("-- Skipping ${file.absolutePath} (not encrypted)")
                } else {
                    println("-- Skipping ${file.absolutePath}")
                }
            }
        }
    }
}
