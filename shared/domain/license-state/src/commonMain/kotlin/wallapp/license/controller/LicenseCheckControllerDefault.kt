package wallapp.license.controller

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import wallapp.license.LicenseStateProvider
import wallapp.license.state.LicenseStateRepository
import wallapp.licensing.CheckLicenseStateResult
import wallapp.licensing.LICENSE_STATE_ALLOWED_PLUS_UNLIMITED
import wallapp.licensing.LICENSE_STATE_CHECKING
import wallapp.licensing.LICENSE_STATE_NOT_ALLOWED
import wallapp.licensing.LICENSE_STATE_NOT_APPLICABLE
import wallapp.licensing.LICENSE_STATE_RETRY
import wallapp.licensing.LICENSE_STATE_UNKNOWN
import wallapp.licensing.LicenseInfo
import wallapp.process.Process
import wallapp.time.TimeRepository
import kotlin.time.Duration.Companion.hours

/**
 * Allows a `LifecycleOwner` to check for license if required and schedules a recheck
 * after refund period.
 */
class LicenseCheckControllerDefault(
    private val licenseStateRepository: LicenseStateRepository,
    private val provider: LicenseStateProvider,
    private val timeRepository: TimeRepository,
    private val coroutineScopeIo: CoroutineScope,
    process: Process,
) : LicenseCheckController {

    companion object {
        private val REFUND_PERIOD_HOURS = 48.hours
        private const val MAX_RETRY_ATTEMPTS = 3
    }

    private var retryAttempts = 0

    private val fortyEightHoursFromInstall: Long
        get() = provider.appInstallTime + REFUND_PERIOD_HOURS.inWholeMilliseconds

    private val delayForRecheck: Long
        get() = fortyEightHoursFromInstall - timeRepository.currentTime

    private val withinRefundWindow: Boolean
        get() = timeRepository.currentTime < fortyEightHoursFromInstall

    private var skipLicenseInfo = true
    init {
        require(process.isDefaultProcess)
        coroutineScopeIo.launch {
            licenseStateRepository.licenseInfo.collect {
                onLicenseInfoUpdate(it)
            }
        }
    }

    private fun onLicenseInfoUpdate(licenseInfo: LicenseInfo) {
        // Skip the first licenseInfo received when observing to avoid triggering any
        // license checks. Calling class should explicitly call checkLicenseIfRequired()
        // as it contains conditions based on which license checks happen and are scheduled
        if (skipLicenseInfo) {
    //                Log.i("[AppBridge] licenseInfo.observeForever: skipLicenseInfo=true, early exit...")
            skipLicenseInfo = false
            return
        }

        //            Log.i("[AppBridge] licenseInfo.observeForever: licenseState: %d", it.licenseState)

        when (licenseInfo.licenseState) {
            LICENSE_STATE_UNKNOWN,
            LICENSE_STATE_RETRY,
            -> {
                if (retryAttempts < MAX_RETRY_ATTEMPTS) {
    //                        Log.d("[AppBridge] retry checkLicenseState(), retryAttempts: %d, maxRetryAttempts: %d",
    //                            retryAttempts, MAX_RETRY_ATTEMPTS)
                    licenseStateRepository.checkLicenseState(true)
                    retryAttempts += 1
                } else {
    //                        Log.d("[AppBridge] reached max retry attempts (%d), setting to unlicensed", MAX_RETRY_ATTEMPTS)
                    licenseStateRepository.setLicenseInfoState(LicenseInfo(LICENSE_STATE_NOT_ALLOWED))
                }
            }

            LICENSE_STATE_ALLOWED_PLUS_UNLIMITED -> {
                if (withinRefundWindow) {
                    scheduleRecheck()
                }
            }

            LICENSE_STATE_NOT_ALLOWED,
            LICENSE_STATE_CHECKING,
            LICENSE_STATE_NOT_APPLICABLE,
            -> { /* no-op */
            }
        }
    }

    override fun checkLicenseIfRequired() {
//        Log.i("[AppBridge] checkLicenseIfRequired()")
        retryAttempts = 0
        when {
//            licenseStateRepository.isLicensedPending.value == true ->
//                licenseStateRepository.checkLicenseState(true)
//            licenseStateRepository.requiresLicenseCheck.value == true && withinRefundWindow ->
//                licenseStateRepository.checkLicenseState(true)
            licenseStateRepository.isLicensed.value == true && withinRefundWindow ->
                scheduleRecheck()
        }
    }

    private fun scheduleRecheck() {
//        Log.i("[AppBridge] scheduleRecheck()")
        coroutineScopeIo.launch {
            delay(delayForRecheck)
            licenseStateRepository.checkLicenseState(true)
        }
    }

    override fun checkLicenseState(): CheckLicenseStateResult {
        retryAttempts = 0
        return licenseStateRepository.checkLicenseState(false)
    }

}