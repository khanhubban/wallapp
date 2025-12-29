package wallapp.process.bus

import wallapp.process.ProcessBridgeCommand
import wallapp.process.ProcessBridgeCommandData

/**
 * Bus for sending data between processes. Sends data ultimately handled via a [ProcessReceiver].
 */
interface ProcessBus {

    fun send(command: ProcessBridgeCommand, commandData: ProcessBridgeCommandData)

    companion object {
        const val PROCESS_BUS_EXTRA_COMMAND_KEY = "command"
        const val PROCESS_BUS_EXTRA_COMMAND_DATA_KEY = "data"
    }
}