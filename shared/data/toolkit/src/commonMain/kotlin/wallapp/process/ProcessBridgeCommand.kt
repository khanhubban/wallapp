package wallapp.process


enum class ProcessBridgeCommand(val command: String) {
    UpdatePreferenceValues("update_preference_values"),
    UpdateFirestoreWallpaper("update_firestore_wallpaper"),
    CheckExternalValue("check_external_value"),
    PostExternalValue("post_external_value"),
}

fun asProcessBridgeCommand(command: String): ProcessBridgeCommand {
    return ProcessBridgeCommand.entries.find { it.command == command }!!
}