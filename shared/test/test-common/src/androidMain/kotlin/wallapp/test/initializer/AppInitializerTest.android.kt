package wallapp.test.initializer

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.dsl.KoinAppDeclaration
import wallapp.interop.InteropBridgeNoOp
import wallapp.log.Log
import wallapp.runmode.RunMode


@Suppress("unused")
class AppInitializerTestAndroid : AppInitializerRunMode() {
    // Note: may be null during unit tests.
    private val context: Context? by lazy {
        try {
        InstrumentationRegistry.getInstrumentation().targetContext
        } catch (e: IllegalStateException) {
            Log.e("No instrumentation registered")
            try {
                ApplicationProvider.getApplicationContext()
            } catch (fallbackException: IllegalStateException) {
                Log.e("No application context available")
                null
            }
        }
    }

    override val application: Any?
        get() = context
    override val interopBridge: Any
        get() = InteropBridgeNoOp
    override val runMode: RunMode
        get() = RunMode.Test
    override val onStartKoin: KoinAppDeclaration
        get() = {
            context?.also {
                androidLogger()
                androidContext(it)
            }
        }

    init {
        Log.d("Create AppInitializerTestAndroid")
    }
}
