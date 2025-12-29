package wallapp.time


import wallapp.log.Log
import wallapp.time.OperationTimer.Companion.NULL_OPERATION_TIMESTAMP
import wallapp.time.OperationTimer.Companion.TAG

data class OperationTimestamp(val millis: Long)


interface OperationTimer {

    fun operationStart(): OperationTimestamp

    fun logOperationTime(
        startTime: OperationTimestamp,
        operation: String?,
        vararg arguments: Any?
    )

    companion object {
        val NULL_OPERATION_TIMESTAMP = OperationTimestamp(0)
        const val TAG = "Timer"

        fun create(enabled: Boolean): OperationTimer {
            return if (enabled) {
                OperationTimerSystem()
            } else {
                OperationTimerNoOp
            }
        }
    }

}

private class OperationTimerSystem : OperationTimer {
    override fun operationStart(): OperationTimestamp {
        return OperationTimestamp(getCurrentTimeMillis())
    }

    override fun logOperationTime(
        startTime: OperationTimestamp,
        operation: String?,
        vararg arguments: Any?
    ) {
        val millisPassed = getCurrentTimeMillis() - startTime.millis
        Log.d("%s, %s(%s): %d", TAG, operation, arguments.joinToString { it.toString() }, millisPassed)
    }
}

object OperationTimerNoOp : OperationTimer {

    override fun operationStart(): OperationTimestamp {
        return NULL_OPERATION_TIMESTAMP
    }

    override fun logOperationTime(
        startTime: OperationTimestamp,
        operation: String?,
        vararg arguments: Any?
    ) {
        /* NOP */
    }
}
