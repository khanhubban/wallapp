package wallapp.string

actual fun String.fmt(vararg args: Any?): String = format(*args)