package wallapp.preferences

actual object PreferenceDefinitions {

    private const val Prefix = "wallapp_ios_"

    actual val CacheFileMediaMapFilename: String
        get() = "${Prefix}cache_file_media_map"
    actual val UserSettingsFilename: String
        get() = "${Prefix}user_settings"
    actual val DeviceSettingsFilename: String
        get() = "${Prefix}device_settings"
    actual val LicenseSettingsFilename: String
        get() = "${Prefix}system"
    actual val LicenseBackupSettingsFilename: String
        get() = "${Prefix}system_alt"
    actual val RemoteServerContentFilename: String
        get() = "${Prefix}rsc_02"
}
