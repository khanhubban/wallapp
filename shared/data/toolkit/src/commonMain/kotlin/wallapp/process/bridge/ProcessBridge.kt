package wallapp.process.bridge

import wallapp.appicon.AppIcon
import wallapp.log.Log
import wallapp.process.Process
import wallapp.process.ProcessBridgeCommand
import wallapp.process.bus.ProcessBus
import wallapp.process.logPrefix
import wallapp.theme.ThemeType

/**
 * Help class for communicating between app processes. Communication occurs via the [processBus].
 */
abstract class ProcessBridge {

    abstract val process: Process

    abstract val processBus: ProcessBus

    fun <T : Any> updatePreferenceValue(key: String, value: T) {
        Log.d("${process.logPrefix()} updatePreferenceValue(): key: $key, value: $value")
        if (value is ThemeType || value is AppIcon) {
            Log.w("${process.logPrefix()} TODO: Add ProcessBridge support for enums / ${value::class.simpleName}")
            return
        }

        processBus.send(ProcessBridgeCommand.UpdatePreferenceValues, getProcessBusCommand(key, value))
    }
}
