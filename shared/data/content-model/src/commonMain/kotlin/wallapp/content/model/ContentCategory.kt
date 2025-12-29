package wallapp.content.model

enum class ContentCategory {
    Collection,
    Singles,        // free single wallpapers
    ;

    companion object {
        val All = entries
    }
}

fun List<ContentCategory>.sorted(): List<ContentCategory> {
    return sortedBy { it.ordinal }
}