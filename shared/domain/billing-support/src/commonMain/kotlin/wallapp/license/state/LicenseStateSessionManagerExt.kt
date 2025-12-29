package wallapp.license.state

fun extractIdFromAppUrl(url: String): String? {
    val regex = Regex("""https://example\.com/w/([\w-]+).*""")
    return regex.matchEntire(url)?.groupValues?.get(1)
}

fun isUuid(string: String): Boolean {
    val uuidRegex = Regex("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")
    return uuidRegex.matches(string)
}