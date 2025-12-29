package wallapp.preferences

expect object PreferenceDefinitions {
    val CacheFileMediaMapFilename: String
    val UserSettingsFilename: String
    val DeviceSettingsFilename: String
    val LicenseSettingsFilename: String
    val LicenseBackupSettingsFilename: String
    val RemoteServerContentFilename: String
}