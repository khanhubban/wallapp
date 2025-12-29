package wallapp.content.model

enum class ColorShade(val key: String) {
    Dark("dark"),
    Light("light"),
    Neutral("neutral"),
    ;

    companion object {

        fun fromKey(key: String): ColorShade? {
            return entries.firstOrNull { it.key == key }
        }
    }
}