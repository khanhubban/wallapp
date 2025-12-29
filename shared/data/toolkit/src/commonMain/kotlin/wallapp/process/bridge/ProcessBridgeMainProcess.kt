package wallapp.process.bridge

import wallapp.log.Log
import wallapp.process.Process
import wallapp.process.bus.ProcessBus

class ProcessBridgeMainProcess(
    override val process: Process,
    /*@ToAltProcessBus*/ override val processBus: ProcessBus,
) : ProcessBridge() {

    init {
        Log.d("ProcessBridgeMainProcess.init() [processSuffix=${process.processNameSuffix}]")
    }

}
