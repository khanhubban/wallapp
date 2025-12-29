package wallapp.resources.translation

object TranslationExt {

    fun verifyFormatting(correct: String, other: String): Boolean {
        // Regex to match formatting tokens (e.g., %s, %d)
        val regex = "%[sd]".toRegex()

        // Find all formatting tokens in both strings
        val correctMatches = regex.findAll(correct).map { it.value }.toList()
        val otherMatches = regex.findAll(other).map { it.value }.toList()

        // Compare the lists of formatting tokens
        return correctMatches == otherMatches
    }
}