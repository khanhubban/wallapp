package wallapp.type

enum class Side(val code: String) {
    Left("left"),
    Right("right"),
}

fun asSide(code: String?): Side? {
    return Side.entries.find { it.code == code }
}
