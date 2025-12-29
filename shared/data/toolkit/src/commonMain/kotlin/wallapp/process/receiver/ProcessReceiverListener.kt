package wallapp.process.receiver

import wallapp.process.ProcessBridgeCommand
import wallapp.process.ProcessBridgeCommandData


fun interface ProcessReceiverListener {

    fun onReceive(command: ProcessBridgeCommand, commandData: ProcessBridgeCommandData): Boolean

}