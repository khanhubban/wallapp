package wallapp.ads

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.initialization.AdapterStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import wallapp.account.data.AccountDataRepository
import wallapp.ads.AdSourceInitializer.State
import wallapp.buildconfig.BuildConfig
import wallapp.log.Logger
import wallapp.process.Process
import wallapp.time.createOperationTimerForDebug
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class AdSourceInitializerAdMob(
    private val context: Context,
    process: Process,
    private val testDeviceIds: List<String>,
    private val buildConfig: BuildConfig,
    private val accountDataRepository: AccountDataRepository,
    private val coroutineScopeIo: CoroutineScope,
) : AdSourceInitializer {

    companion object {
        val Log = Logger("AdMobInitializer")
    }

    init {
        Log.d("init")
        require(process.isDefaultProcess) {
            "Can only initialize in default process. Do you need to inject AdSourceInitializerNoOp instead?"
        }
    }

    private val timer = createOperationTimerForDebug()
    private var _state: State = State.Uninitialized
    override val state: State
        get() = _state

    // This method is *slow*. Should be called lazily and in the background.
    override fun init() {
        if (state == State.Initialized || state == State.Initializing) return

        Log.i("Initializing AdMob")
        _state = State.Initializing

        coroutineScopeIo.launch {
            val timestamp = timer.operationStart()

            val setTestAdIdInBuild = withTimeoutOrNull(1000L) {
                accountDataRepository.hasSpecialCaseIsDeveloper.first { it != null } ?: false
            } ?: false

            val finalTestDeviceIds = if (setTestAdIdInBuild && !buildConfig.debug) {
                val deviceId = GoogleAdvertisingId.getAdvertisingId(context)
                    ?: getDeviceIdForAdMobTestAds(context)
                    ?: ""
                Log.d("[Ads] AdMob test device ID: $deviceId")
                testDeviceIds + deviceId
            } else {
                testDeviceIds
            }

            val configuration = RequestConfiguration.Builder()
                .setTestDeviceIds(finalTestDeviceIds).build()
            MobileAds.setRequestConfiguration(configuration)

            MobileAds.initialize(context.applicationContext) { initializationStatus ->
                Log.d("AdMob init finished")
                initializationStatus.adapterStatusMap.forEach { (key, status) ->
                    Log.d("Init status: $%s: %s", key, status.asString())
                }

                _state = State.Initialized
                timer.logOperationTime(
                    timestamp,
                    "AdMobManager.init()",
                    Thread.currentThread().name
                )
            }

        }
    }

    fun AdapterStatus.asString(): String {
        return "state: $initializationState, latency: $latency, description: $description"
    }

    @SuppressLint("HardwareIds")
    private fun getDeviceIdForAdMobTestAds(context: Context): String? {
        val md5 = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        try {
            val md = MessageDigest.getInstance("MD5")
            val array = md.digest(md5.toByteArray())
            val sb = StringBuilder()
            for (i in array.indices)
                sb.append(Integer.toHexString(array[i].toInt() and 0xFF or 0x100).substring(1, 3))
            return sb.toString().uppercase()
        } catch (_: NoSuchAlgorithmException) {
        }
        return null
    }
}
