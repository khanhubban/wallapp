package wallapp.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.window.layout.WindowInfoTracker
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import wallapp.activity.SingleScreenActivity.Companion.start
import wallapp.image.loader.ImageLoader
import wallapp.log.Logger
import wallapp.pixel.alert.AlertManager
import wallapp.pixel.alert.AlertViewState
import wallapp.screen.ScreenArgument
import wallapp.system.ui.controller.UiControllerManagerAndroid
import wallapp.system.ui.controller.uiController
import wallapp.system.window.WindowFrameManagerAndroid
import wallapp.ui.app.WallAppCompositionLocalProvider
import wallapp.ui.app.WallAppContentSingleScreen

/**
 * Displays a single screen on a dedicated Activity.
 *
 * Call [start] to display the screen and pass in the desired [ScreenArgument].
 */
class SingleScreenActivity : FragmentActivity() {

    companion object {
        val Log = Logger("SingleScreenActivity")

        private const val KeyScreenArgument = "arg"

        private fun getIntent(context: Context, screenArgument: ScreenArgument): Intent {
            return Intent(context, SingleScreenActivity::class.java)
                .putExtras(
                    Bundle().apply {
                        putString(KeyScreenArgument, screenArgument.jsonString)
                    }
                )
        }

        fun start(context: Context, screenArgument: ScreenArgument) {
            context.startActivity(getIntent(context, screenArgument))
        }
    }

    private val imageLoader: ImageLoader by inject()
    private val uiControllerAndroid = this.uiController
    private val uiControllerManager: UiControllerManagerAndroid by inject()
    private val windowFrameManager: WindowFrameManagerAndroid by inject()
    private val alertManager: AlertManager by inject()
    private val currentDialogState: StateFlow<AlertViewState?>
        get() = alertManager.currentDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val screenArgument = intent?.extras?.getString(KeyScreenArgument)
            ?.let { ScreenArgument.fromJsonString(it) }
        if (screenArgument == null) {
            Log.e("Screen argument is null, finishing activity")
            finish()
            return
        }

        MainActivity.Log.d("[WallAppActivity] onCreate()")
        uiControllerManager.onCreate(uiControllerAndroid)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            WallAppCompositionLocalProvider(imageLoader) {
                WallAppContentSingleScreen(
                    screenArgument,
                    alertViewState = currentDialogState,
                )
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

    private fun configureWindowLayoutChangeListener() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                WindowInfoTracker.getOrCreate(this@SingleScreenActivity)
                    .windowLayoutInfo(this@SingleScreenActivity)
                    .collect { windowLayoutInfo ->
                        windowLayoutInfo.displayFeatures.forEach { platformFeature ->
                            MainActivity.Log.d("WindowLayoutInfo changed -> $platformFeature")
                        }
                        windowFrameManager.updateSize()
                    }
            }
        }
    }

}