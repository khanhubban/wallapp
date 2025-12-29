package wallapp.device

typealias OsVersion = String

enum class OsVersionCompareResult {
    Equal,
    Newer,
    Older,
    Unknown,
}

fun compareOsVersion(newVersion: OsVersion, oldVersion: OsVersion): OsVersionCompareResult {
    return try {
        compareOsVersionInternal(newVersion, oldVersion)
    } catch (e: Exception) {
        /**
         * Handle any exceptions that may occur during version comparison.
         */
        OsVersionCompareResult.Unknown
    }
}

fun OsVersion.coarsenMajorMinor(): OsVersion {
    val trimmed = trim()
    if (!isValidVersion(trimmed)) {
        return trimmed
    }
    val parts = trimmed.split(".")
    val major = parts.getOrNull(0).orEmpty()
    val minor = parts.getOrNull(1)
    return if (minor == null) major else "${major}.${minor}"
}

private fun compareOsVersionInternal(newVersion: String, oldVersion: String): OsVersionCompareResult {
    if (newVersion == oldVersion) return OsVersionCompareResult.Equal

    if (!isValidVersion(newVersion) || !isValidVersion(oldVersion)) {
        return OsVersionCompareResult.Unknown
    }

    val newVersionParts = newVersion.split(".").mapNotNull { it.sanitizeVersionPart() }
    val oldVersionParts = oldVersion.split(".").mapNotNull { it.sanitizeVersionPart() }

    val maxLength = maxOf(newVersionParts.size, oldVersionParts.size)
    for (i in 0 until maxLength) {
        val newPart = newVersionParts.getOrNull(i)?.toIntOrNull()
        val oldPart = oldVersionParts.getOrNull(i)?.toIntOrNull()

        // Compare parts; use string comparison as fallback
        val comparisonResult = when {
            newPart != null && oldPart != null -> newPart.compareTo(oldPart)
            else -> newVersionParts.getOrNull(i).orEmpty().compareTo(oldVersionParts.getOrNull(i).orEmpty())
        }

        when {
            comparisonResult > 0 -> return OsVersionCompareResult.Newer
            comparisonResult < 0 -> return OsVersionCompareResult.Older
        }
    }

    return OsVersionCompareResult.Equal
}

fun String.sanitizeVersionPart(): String {
    return this.filter { it.isDigit() || it == '.' }
}

/**
 * Checks if a given version string is valid based on a specific pattern.
 *
 * The pattern requires that the version string:
 * - Starts with one or more digits (\d+).
 * - Can be followed by any number of dot-separated digit groups (.\\d+)*.
 * - Ends with the end of the string ($), ensuring no extra characters are present.
 *
 * Examples of valid version strings according to this pattern:
 * - "1"
 * - "1.2"
 * - "10.20.30"
 *
 * This function uses a regular expression to verify if the version string adheres
 * to the standard semantic versioning format without pre-release identifiers or build metadata.
 *
 */
fun isValidVersion(version: String): Boolean {
    return version.matches(Regex("^\\d+(\\.\\d+)*\$"))
}
