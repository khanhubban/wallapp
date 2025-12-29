package wallapp.type

enum class SceneAlignment(val code: String) {
    Top("top"),
    Center("center"),
    Bottom("bottom"),
}

fun asSceneAlignment(code: String?): SceneAlignment? {
    return SceneAlignment.entries.find { it.code == code }
}