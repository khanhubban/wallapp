package wallapp.process.receiver

import wallapp.log.Log
import wallapp.process.ProcessBridgeCommand
import wallapp.process.ProcessBridgeCommandData

class ProcessReceiverMock : ProcessReceiver() {
    val valuesMap = mutableMapOf<ProcessBridgeCommand, ProcessBridgeCommandData>()

    var lastCommand: ProcessBridgeCommand? = null
    var lastCommandData: ProcessBridgeCommandData? = null

    override fun onReceive(command: ProcessBridgeCommand, commandData: ProcessBridgeCommandData) {
        Log.d("onReceiver(): command: $command, $commandData: $commandData")
        valuesMap[command] = commandData
        lastCommand = command
        lastCommandData = commandData
    }

}