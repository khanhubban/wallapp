package wallapp.process.bus

import android.content.Context
import android.content.Intent
import wallapp.log.Log
import wallapp.process.ProcessBridgeCommand
import wallapp.process.ProcessBridgeCommandData
import wallapp.process.bus.ProcessBus.Companion.PROCESS_BUS_EXTRA_COMMAND_DATA_KEY
import wallapp.process.bus.ProcessBus.Companion.PROCESS_BUS_EXTRA_COMMAND_KEY

/**
 * Send data to another process (as specified by [intentAction]) via a broadcast.
 */
class ProcessBusIntraBroadcast(private val context: Context,
                                                   private val intentAction: String)
    : ProcessBus {

    override fun send(command: ProcessBridgeCommand, commandData: ProcessBridgeCommandData) {
        Log.d("send(): command: ${command}, commandData: $commandData")
        context.sendBroadcast(Intent(intentAction)
            .putExtra(PROCESS_BUS_EXTRA_COMMAND_KEY, command.command)
            .putExtra(PROCESS_BUS_EXTRA_COMMAND_DATA_KEY, commandData)
            .setPackage(context.packageName))
    }

}