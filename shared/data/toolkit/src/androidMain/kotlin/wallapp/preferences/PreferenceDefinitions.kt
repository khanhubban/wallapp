package wallapp.preferences

actual object PreferenceDefinitions {

    actual val CacheFileMediaMapFilename: String
        get() = "cache_file_media_map"
    actual val UserSettingsFilename: String
        get() = "user_settings"
    actual val DeviceSettingsFilename: String
        get() = "device_settings"
    actual val LicenseSettingsFilename: String
        get() = "system"
    actual val LicenseBackupSettingsFilename: String
        get() = "system_alt"
    actual val RemoteServerContentFilename: String
        get() = "rsc_01"
}
