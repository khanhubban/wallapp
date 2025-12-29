package wallapp.process


const val PROCESS_BRIDGE_INTENT_ACTION_UPDATE_DATA_MAIN = "com.wallapp.action.POKE_MAIN"
const val PROCESS_BRIDGE_INTENT_ACTION_UPDATE_DATA_WALLPAPER = "com.wallapp.action.POKE_LIVE_WALLPAPER"

const val PROCESS_BRIDGE_INTENT_ACTION_SERVICE_UPDATE_DATA_MAIN = "com.wallapp.action.IPC_MAIN"
const val PROCESS_BRIDGE_INTENT_ACTION_SERVICE_UPDATE_DATA_WALLPAPER = "com.wallapp.action.IPC_LIVE_WALLPAPER"

typealias ProcessBridgeCommandData = String
