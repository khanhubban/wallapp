package wallapp.process.receiver

import wallapp.process.ProcessBridgeCommand
import wallapp.process.ProcessBridgeCommandData

/**
 * Receives data send from another process. Used in conjunction with [ProcessBridge].
 */
abstract class ProcessReceiver {

    abstract fun onReceive(command: ProcessBridgeCommand, commandData: ProcessBridgeCommandData)

    protected fun processOnReceiveForListeners(
        command: ProcessBridgeCommand,
        commandData: ProcessBridgeCommandData,
    ): Boolean {
        var handled = false
        listeners.forEach {
            if (it.onReceive(command, commandData)) {
                handled = true
            }
        }
        return handled
    }

    protected val listeners = mutableListOf<ProcessReceiverListener>()

    fun addListener(listener: ProcessReceiverListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: ProcessReceiverListener) {
        listeners.remove(listener)
    }
}