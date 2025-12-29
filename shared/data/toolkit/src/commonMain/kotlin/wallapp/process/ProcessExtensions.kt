package wallapp.process

import wallapp.process.bridge.ProcessBridge


fun Process.isLiveWallpaperProcess(): Boolean = processNameSuffix?.contains(":livewallpaper") == true

fun Process.isDebugPhoenixProcess(): Boolean = processNameSuffix?.contains(":phoenix") == true

fun ProcessBridge?.logPrefix(): String {
    return this?.process?.logPrefix() ?: "[process:<unknown>]"
}

fun Process.logPrefix(): String {
    return "[process:${if (isDefaultProcess) "default" else this.processNameSuffix}]"
}