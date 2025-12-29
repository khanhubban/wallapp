package wallapp.remoteapi

const val ApiExportDataRoot = "../wallapp-api/api"

val RemoteApiExportSpecStaging = RemoteApiExportSpec(
    remoteEndpointsSpec = RemoteEndpointsSpecs.Staging,
    encryptionConfig = RemoteApiEncryptionConfigPreset(),
    keyFilename = "key1",
)

//val RemoteApiExportSpecs = listOf(
//    RemoteApiExportSpecCurrent,
//    RemoteApiExportSpecStaging,
//)

val IgnoredFilenames = listOf(
    ".DS_Store",
    ".gitignore",
)

val EncryptedFileExtension = ".data"
