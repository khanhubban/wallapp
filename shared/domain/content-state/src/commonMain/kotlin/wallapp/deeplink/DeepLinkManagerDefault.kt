package wallapp.deeplink

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import wallapp.app.AppStateManager
import wallapp.content.model.Id
import wallapp.content.model.Id.Companion.ShortStringIdPrefixCommon
import wallapp.coroutine.collectIn
import wallapp.data.folder.FolderDefinitions.FolderIdJustAdded
import wallapp.license.state.LicenseStateSessionManager
import wallapp.log.Log
import wallapp.screen.ScreenArgument
import wallapp.screen.ScreenArgument.FolderScreenArgument

class DeepLinkManagerDefault(
    private val appStateManager: AppStateManager,
    private val deepLinkMapper: DeepLinkMapper,
    private val licenseStateSessionManager: LicenseStateSessionManager,
    private val coroutineScopeIo: CoroutineScope,
) : DeepLinkManager {

    private val _navigationArgument = MutableSharedFlow<ScreenArgument?>(extraBufferCapacity = 1)
    override val navigationArgument: Flow<ScreenArgument?>
        get() = _navigationArgument

    override fun handleDeepLink(url: String) {
        licenseStateSessionManager.onDeepLinkUrl(url)

        Log.d("[deeplink] DeepLinkManager.handleDeepLink($url)")
        deepLinkMapper.map(url).collectIn(coroutineScopeIo) { screenArgument ->
            val logMessage = if (screenArgument == null) {
                "[deeplink] DeepLinkManager no mapping for url: $url"
            } else {
                "[deeplink] DeepLinkManager result: $screenArgument, url: $url"
            }
            Log.d(logMessage)
            emitWhenUiReady(screenArgument)
        }
    }

    override fun handleAppShortcut(shortcutId: String): Boolean {
        Log.d("[deeplink] DeepLinkManager.handleAppShortcut($shortcutId)")
        return when (shortcutId) {
            "shortcut_folder_just_added" -> {
                emitWhenUiReady(FolderScreenArgument(FolderIdJustAdded))
                true
            }

            else -> {
                getIdFromShortcutId(shortcutId)?.let { id ->
                    emitWhenUiReady(deepLinkMapper.map(id))
                    true
                } ?: false
            }
        }
    }

    private fun getIdFromShortcutId(shortcutId: String): Id? {
        return if (shortcutId.startsWith(ShortStringIdPrefixCommon)) {
            Id.fromExportStringChecked(shortcutId)
        } else {
            null
        }
    }

    private fun emitWhenUiReady(screenArgument: ScreenArgument?) {
        coroutineScopeIo.launch {
            appStateManager.isUiReady.collect { isUiReady ->
                if (isUiReady) {
                    Log.i("[deeplink] DeepLinkManager.emitWhenUiReady($screenArgument) emit")
                    _navigationArgument.emit(screenArgument)
                    cancel()
                }
            }
        }
    }
}