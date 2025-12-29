package wallapp.utils


import android.content.res.Resources
import android.os.Bundle
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.res.ResourcesCompat
import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader
import java.io.PrintWriter
import java.io.StringWriter
import java.util.Random


/**
 * Implementation of lazy that is not thread safe. Useful when you know what thread you will be
 * executing on and are not worried about synchronization.
 */
fun <T> lazyFast(operation: () -> T): Lazy<T> = lazy(LazyThreadSafetyMode.NONE) {
    operation()
}

fun View.getColor(@ColorRes color: Int, theme: Resources.Theme? = null): Int =
    ResourcesCompat.getColor(resources, color, theme)

fun Bundle.getAll(): Map<String, Any?>? {
    val results = mutableMapOf<String, Any?>()
    for (key in keySet()) {
        results[key] = getString(key)
    }
    return results.ifEmpty {
        null
    }
}

fun <T> List<T>.random(random: Random): T? = if (size > 0) get(random.nextInt(size)) else null

fun File.deleteDir() {
    listFiles()?.forEach { it.deleteDir() }
    delete()
}

fun File.deleteAllContents() {
    listFiles()?.forEach { it.deleteDir() }
}

fun File.makeDirIfItDoesNotExist(): File {
    return apply {
        if (isDirectory && !exists()) {
            mkdir()
        }
    }
}

fun InputStream.toFile(path: String) {
    use { input ->
        File(path).outputStream().use { input.copyTo(it) }
    }
}

inline fun <reified K, V> Map<K, V>.toLinkedHashMap(): LinkedHashMap<K, V> {
    return LinkedHashMap<K, V>(this)
}

inline fun <reified K, V> Iterable<Pair<K, V>>.toLinkedHashMap(): LinkedHashMap<K, V> {
    return toMap().toLinkedHashMap()
}

fun Exception.stackTraceAsString(): String {
    val stringWriter = StringWriter()
    val printWriter = PrintWriter(stringWriter)
    printStackTrace(printWriter)
    return stringWriter.toString()
}

fun InputStream.asString(): String {
    val stringBuilder = StringBuilder()
    with(BufferedReader(InputStreamReader(this))) {
        var line = readLine()
        while (line != null) {
            stringBuilder.append(line)
            line = readLine()
            if (line != null) {
                stringBuilder.append("\n")
            }
        }
        close()
    }
    return stringBuilder.toString()
}