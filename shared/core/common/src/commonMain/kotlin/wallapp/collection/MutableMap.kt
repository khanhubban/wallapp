package wallapp.collection

/**
 * Use of this is preferred over [mutableMapOf] due to crashes on iOS with the use of the Kotlin
 * default.
 *
 * Because of the concurrency, this is not as optimal as the [LinkedHashMap] provided by the
 * standard library, so should be used sparingly and with caution.
 */
inline fun <K, V> mutableMapOfConcurrent(): MutableMap<K, V> = MutableMapConcurrent()