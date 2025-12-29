package wallapp.process.bridge

import wallapp.content.model.Id.RemixId
import wallapp.log.Log
import wallapp.process.Process
import wallapp.process.ProcessBridgeCommand
import wallapp.process.bus.ProcessBus

class ProcessBridgeAltProcess(
    override val process: Process,
    /*@ToMainProcessBus*/ override val processBus: ProcessBus,
): ProcessBridge() {

    init {
        Log.d("ProcessBridgeAltProcess.init() [processSuffix=${process.processNameSuffix}]")
    }

    fun updateCurrentWallpaper(remixId: RemixId) {
        processBus.send(ProcessBridgeCommand.UpdateFirestoreWallpaper, remixId.name)
    }
}
