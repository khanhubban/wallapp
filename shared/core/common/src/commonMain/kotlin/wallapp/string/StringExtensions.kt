package wallapp.string

import kotlin.jvm.JvmName

expect fun String.fmt(vararg args: Any?): String
fun stringFormat(format: String, vararg args: Any?): String = format.fmt(*args)

fun String.clamp(maxLength: Int, appendEllipsis: Boolean = true): String =
    if (length < maxLength) this
    else substring(0, maxLength - 1) + (if (appendEllipsis) "…" else "")

fun String.substringOccurrenceCount(substring: String): Int = split(substring).size - 1

@JvmName("quoteNullable")
fun String?.quote(): String? {
    return this?.quote()
}

fun String.quote(): String = "\"$this\""

const val ANONYMIZED_STRING = "***"

fun String?.anonymize(anonymizedString: String = ANONYMIZED_STRING): String? {
    return if (this != null) anonymizedString else null
}

fun isEmpty(str: CharSequence?): Boolean {
    return str == null || str.isEmpty()
}



fun String.stripWhitespace(): String =
    StringBuilder().apply {
        toCharArray().forEach {
            if (!it.isWhitespace()) append(it)
        }
    }.toString()

fun String?.nonEmptyString(): String? {
    if (isNullOrBlank() || isEmpty()) return null
    return this
}

fun String.containsAny(strings: List<String>): Boolean =
    strings.any { this.contains(it) }

fun String.splitIntoLinesConditional(numLines: Int, splitThreshold: Int = 14): String {
    return if (length < splitThreshold) {
        this
    } else {
        splitIntoLines(numLines)
    }
}

/**
 * Note: the split occurs based on the number of characters, not the number of words.
 */
fun String.splitIntoLines(numLines: Int): String {
    val wordPairs = this.split(" ")
        .map { word -> Pair(word, word.length) }

    val totalCharacters = wordPairs.sumOf { it.second }
    val charactersPerLine = totalCharacters / numLines

    val lines = mutableListOf<String>()
    var currentLine = ""
    var currentLineLength = 0

    for ((word, length) in wordPairs) {
        // Check if adding the word will exceed the target line length and if we can add more lines
        if (currentLine.isNotEmpty() && currentLineLength + length + 1 > charactersPerLine && lines.size < numLines - 1) {
            lines.add(currentLine.trim())
            currentLine = word
            currentLineLength = length
        } else {
            if (currentLine.isNotEmpty()) {
                currentLine += " "
                currentLineLength += 1
            }
            currentLine += word
            currentLineLength += length
        }
    }

    // Add the final line
    if (currentLine.isNotEmpty()) {
        lines.add(currentLine.trim())
    }

    return lines.joinToString("\n")
}

/**
 * Alternate implementation of [splitIntoLines] that splits based on the number of words.
 */
fun String.splitIntoLinesAlt(numLines: Int): String {
    if (numLines <= 0) return this

    val words = this.split(" ")
    val avgWordsPerLine = (words.size / numLines.toDouble()).coerceAtLeast(1.0)
    val lines = mutableListOf<String>()

    var currentLine = StringBuilder()
    var wordsInCurrentLine = 0

    for (word in words) {
        if (wordsInCurrentLine >= avgWordsPerLine) {
            lines.add(currentLine.toString().trim())
            currentLine = StringBuilder()
            wordsInCurrentLine = 0
        }
        currentLine.append("$word ")
        wordsInCurrentLine++
    }

    if (currentLine.isNotEmpty()) {
        lines.add(currentLine.toString().trim())
    }

    // If lines are fewer than requested, adjust accordingly
    while (lines.size < numLines) {
        lines.add("")
    }

    return lines.joinToString("\n")
}

private fun findEndIndex(words: List<String>, startIndex: Int, maxCharacters: Int): Int {
    var endIndex = startIndex
    var currentLength = 0

    while (endIndex < words.size && currentLength + words[endIndex].length <= maxCharacters) {
        currentLength += words[endIndex].length
        endIndex++
    }

    return endIndex
}
fun String.capitalizeFirstLetter(): String {
    return replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}

fun String?.takeIfNotEmpty(): String? {
    return if (this.isNullOrEmpty()) null else this
}
