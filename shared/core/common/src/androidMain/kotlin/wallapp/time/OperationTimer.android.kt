package wallapp.time

import wallapp.common.BuildConfig

fun createOperationTimerForDebug(): OperationTimer {
    return OperationTimer.create(BuildConfig.DEBUG)
}