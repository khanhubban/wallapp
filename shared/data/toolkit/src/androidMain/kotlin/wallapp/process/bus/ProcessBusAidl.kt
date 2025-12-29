package wallapp.process.bus

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import wallapp.log.Log
import wallapp.process.Process
import wallapp.process.ProcessBridgeCommand
import wallapp.process.ProcessBridgeCommandData


class ProcessBusAidl(
    private val context: Context,
    private val process: Process,
    private val intentAction: String,
) : ProcessBus {

    private val applicationId: String
        get() = context.packageName

    private var processBusInterface: ProcessBusInterface? = null
    private val connected: Boolean
        get() = processBusInterface != null

    private val cachedCommands = mutableListOf<Pair<ProcessBridgeCommand, ProcessBridgeCommandData>>()

    private fun addCachedCommand(command: ProcessBridgeCommand, commandData: ProcessBridgeCommandData) {
        cachedCommands.add(command to commandData)
    }

    private fun sendCachedCommands() {
        if (processBusInterface == null) return
        cachedCommands.forEach { (command, data) ->
            sendInternal(command, data)
        }
        cachedCommands.clear()
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            logI("onServiceConnected() $name")
            ProcessBusInterface.Stub.asInterface(service).also { busInterface ->
                processBusInterface = busInterface
                sendCachedCommands()
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            logI("onServiceDisconnected() $name")
            processBusInterface = null
        }
    }

    override fun send(command: ProcessBridgeCommand, commandData: ProcessBridgeCommandData) {
        if (processBusInterface == null) {
            addCachedCommand(command, commandData)
            bindService()
        } else {
            sendInternal(command, commandData)
        }
    }

    private fun sendInternal(command: ProcessBridgeCommand, commandData: ProcessBridgeCommandData) {
        log("sendInternal(): ${command.command}, data: $commandData")
        processBusInterface?.send(command.command, commandData)
    }

    private fun bindService() {
        if (processBusInterface != null) return
        val intent = Intent(intentAction).setPackage(applicationId)
        logI("bindService()")
        context.bindService(
            intent,
            serviceConnection,
            Context.BIND_AUTO_CREATE,
        )
    }

    init {
        logI("init, action: $intentAction")
    }

    private fun logI(message: String) {
        Log.i("[aidl/bridge] [${process.processName}] $message")
    }
    private fun log(message: String) {
//        Log.d("[aidl/bridge] [${process.processName}] $message")
    }
}