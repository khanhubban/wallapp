package wallapp.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.window.layout.WindowInfoTracker
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import wallapp.account.AccountManager
import wallapp.account.SignInMethod
import wallapp.app.AppStateManager
import wallapp.app.AppUiState
import wallapp.app.AppViewModel
import wallapp.app.adapter.BuildConfig
import wallapp.app.resolveAppViewModel
import wallapp.auth.google.GoogleAuthSignInResult
import wallapp.context.toast
import wallapp.coroutine.collectIn
import wallapp.deeplink.DeepLinkManager
import wallapp.image.loader.ImageLoader
import wallapp.log.Logger
import wallapp.navigation.AppNavigator
import wallapp.network.NetworkErrorBroadcaster
import wallapp.network.NetworkRefreshManager
import wallapp.network.NetworkRefreshTriggerBroadcaster
import wallapp.permission.SystemPermissionManagerAndroid
import wallapp.privacymessaging.PrivacyMessagingManager
import wallapp.result.ResultEx
import wallapp.signin.SignInNavigationEvent
import wallapp.signin.SignInProviderController
import wallapp.system.intent.wrapper
import wallapp.system.photo.picker.SystemPhotoPickerAndroid
import wallapp.system.ui.controller.UiControllerManagerAndroid
import wallapp.system.ui.controller.uiController
import wallapp.system.window.WindowFrameManagerAndroid
import wallapp.theme.ThemeManager
import wallapp.ui.app.WallAppCompositionLocalProvider
import wallapp.ui.app.WallAppContent
import wallapp.viewmodel.ViewModelProviderFactory

class MainActivity : FragmentActivity() {

    private val appStateManager: AppStateManager by inject()
    private val imageLoader: ImageLoader by inject()
    private val uiControllerManager: UiControllerManagerAndroid by inject()
    private val viewModelProviderFactory: ViewModelProviderFactory by inject()
    private val systemPhotoPicker: SystemPhotoPickerAndroid by inject()
    private val themeManager: ThemeManager by inject()
    private val systemPermissionManager: SystemPermissionManagerAndroid by inject()
    private val windowFrameManager: WindowFrameManagerAndroid by inject()

    private val googleSignInClient: GoogleSignInClient by inject()
    private val accountManager: AccountManager by inject()
    private val signInProviderController: SignInProviderController by inject()

    private val privacyMessagingManager: PrivacyMessagingManager by inject()

    private val deepLinkManager: DeepLinkManager by inject()

    private val uiControllerAndroid = this.uiController

    private val appViewModel: AppViewModel by lazy { resolveAppViewModel() }
    private val appUiState: AppUiState by lazy { AppUiState(appViewModel, themeManager) }
    private val appNavigator: AppNavigator by inject()

    private val networkRefreshTriggerBroadcaster: NetworkRefreshTriggerBroadcaster by inject()
    private val networkRefreshManager: NetworkRefreshManager by inject()
    private val networkErrorBroadcaster: NetworkErrorBroadcaster by inject()

    private var photoPickerActivityRequestCode: Int? = null

    private val pickMediaActivityResultLauncher: ActivityResultLauncher<PickVisualMediaRequest> =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            handlePhotoPickerResult(uri)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("[WallAppActivity] onCreate()")
        uiControllerManager.onCreate(uiControllerAndroid)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            WallAppCompositionLocalProvider(imageLoader) {
                WallAppContent(Modifier, appUiState)
//                WallAppContentSingleScreen()
            }
        }

        privacyMessagingManager.consentGather(uiControllerAndroid) { _ ->
        }

        signInProviderController.signInNavigationEvents.collectIn(lifecycleScope) {
            if (it == SignInNavigationEvent.NavigateToGoogleSignIn) {
                startActivityForResult(googleSignInClient.signInIntent, GOOGLE_SIGN_IN_REQUEST_CODE)
            }
        }

        systemPhotoPicker.pickerRequestCode.collectIn(lifecycleScope) {
            if (it != null) {
                photoPickerActivityRequestCode = it
                pickMediaActivityResultLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                )
            }
        }

        handleAnonymousSignIn()

        appNavigator.finishCurrentActivity.collectIn(lifecycleScope) {
            if (uiControllerAndroid.activity == this) {
                Log.w("[WallAppActivity] [Navigation] finishCurrentActivity")
                finish()
            }
        }

        configureWindowLayoutChangeListener()

        handleIntent(intent)
    }

    private fun handleAnonymousSignIn() {
        lifecycleScope.launch {
            networkRefreshTriggerBroadcaster.userSignInRefresh.collectLatest {
                Log.d("[NRW] userSignInRefresh: $it")
                when (val result = accountManager.signIn(SignInMethod.Anonymous)) {
                    is ResultEx.Error -> {
                        Log.w(result.exception, "[firebase] ${result.exception.message}")
                        Log.w("[firebase] signInAnonymously: failure")
                        networkErrorBroadcaster.reportNetworkError("signInAnonymously")
                    }
                    is ResultEx.Success -> {
                        Log.d("[firebase] signInAnonymously: success")
                    }
                }
            }
        }
        networkRefreshManager.initialize()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        intent.also { handleIntent(it) }
    }

    private fun configureWindowLayoutChangeListener() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                WindowInfoTracker.getOrCreate(this@MainActivity)
                    .windowLayoutInfo(this@MainActivity)
                    .collect { windowLayoutInfo ->
                        windowLayoutInfo.displayFeatures.forEach { platformFeature ->
                            Log.d("WindowLayoutInfo changed -> $platformFeature")
                        }
                        windowFrameManager.updateSize()
                    }
            }
        }
    }

    private fun handleIntent(intent: Intent) {
        if (handleAppShortcut(intent)) {
            return
        }
        if (handleAppLink(intent)) {
            return
        }
    }

    private fun handlePhotoPickerResult(uri: Uri?) {
        if (uri != null) {
            systemPhotoPicker.onPickerResult(
                requireNotNull(photoPickerActivityRequestCode),
                true,
                uri,
            )
        } else {
            systemPhotoPicker.onPickerResult(
                requireNotNull(photoPickerActivityRequestCode),
                false,
                null,
            )
        }
    }

    private fun handleAppLink(intent: Intent): Boolean {
        val action: String? = intent.action
        val data: Uri? = intent.data
        val wallpaperUrlExtra = intent.getStringExtra("wallpaper_url")
        Log.d("[applink]: action: $action, data: $data, wallpaperUrlExtra: $wallpaperUrlExtra")

        val url = if (action == Intent.ACTION_VIEW && data != null) {
            data.toString()
        } else {
            wallpaperUrlExtra
        }

        if (url != null) {
            deepLinkManager.handleDeepLink(url)
            return true
        }
        return false
    }

    private fun handleAppShortcut(intent: Intent): Boolean {
        val shortcutId = intent.getStringExtra("shortcut_id") ?: return false
        if (shortcutId.isNotEmpty()) {
            return deepLinkManager.handleAppShortcut(shortcutId)
        }
        return false
    }

    private fun setupFirebaseForTesting() {
        if (BuildConfig.DEBUG) {
            // 10.0.2.2 is the special IP address to connect to the 'localhost' of
            // the host computer from an Android emulator.
            val localhost = "10.0.2.2"
            Firebase.auth.useEmulator(localhost, 9099)
            Firebase.firestore.useEmulator(localhost, 8080)
            Firebase.storage.useEmulator(localhost, 9199)
            toast("Firebase emulator enabled")
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_SIGN_IN_REQUEST_CODE) {
            if (data != null) {
                signInProviderController.onGoogleSignInResult(GoogleAuthSignInResult(data.wrapper))
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("[WallAppActivity] onStart()")
        uiControllerManager.onStart(uiControllerAndroid)
    }

    override fun onStop() {
        super.onStop()
        Log.d("[WallAppActivity] onStop()")
        uiControllerManager.onStop(uiControllerAndroid)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("[WallAppActivity] onDestroy()")
        uiControllerManager.onDestroy(uiControllerAndroid)
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        Log.d("[WallAppActivity] onTrimMemory(level=$level)")
    }

    companion object {
        val Log = Logger("MainActivity")
        private const val GOOGLE_SIGN_IN_REQUEST_CODE = 101
    }
}