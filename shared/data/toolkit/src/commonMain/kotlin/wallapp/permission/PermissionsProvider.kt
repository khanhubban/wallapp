package wallapp.permission


interface PermissionsProvider {
    fun requiresSystemAlertWindowPermission(): Boolean
    fun requiresAccessibilityEnabled(): Boolean
    fun hasWallpaperStoragePermission(): Boolean
    fun hasQueryAllPackagesPermission(): Boolean
    val hasStorageReadPermission: Boolean
    val hasStorageWritePermission: Boolean
    val hasStoragePermissions: Boolean
    val hasRequestInstallPackagePermission: Boolean
}

class PermissionsProviderStub : PermissionsProvider {
    var requiresSystemAlertWindowPermission = false
    var requiresAccessibilityEnabled = false
    var hasWallpaperStoragePermission = false
    var hasQueryAllPackagesPermission = true
    override var hasRequestInstallPackagePermission = false
    override var hasStoragePermissions: Boolean = false
    override var hasStorageReadPermission: Boolean = false
    override var hasStorageWritePermission: Boolean = false

    override fun requiresSystemAlertWindowPermission(): Boolean = requiresSystemAlertWindowPermission
    override fun requiresAccessibilityEnabled(): Boolean = requiresAccessibilityEnabled
    override fun hasWallpaperStoragePermission(): Boolean = hasWallpaperStoragePermission
    override fun hasQueryAllPackagesPermission(): Boolean = hasQueryAllPackagesPermission
}
