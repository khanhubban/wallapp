package wallapp.process.bus

import wallapp.process.ProcessBridgeCommand
import wallapp.process.ProcessBridgeCommandData
import wallapp.process.receiver.ProcessReceiver

class ProcessBusMock(private val receivers: List<ProcessReceiver>) : ProcessBus {

    var sendCount: Int = 0

    constructor(receiver: ProcessReceiver) : this (listOf(receiver))

    override fun send(command: ProcessBridgeCommand, commandData: ProcessBridgeCommandData) {
        receivers.forEach {
            it.onReceive(command, commandData)
        }
        sendCount += 1
    }
}