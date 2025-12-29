package wallapp.timber

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import wallapp.coroutine.CoroutineContexts
import wallapp.process.Process
import wallapp.process.isLiveWallpaperProcess
import wallapp.system.wallpaper.SystemWallpaperManager
import wallapp.time.getDateTimeString
import java.io.FileWriter
import java.util.Date
import java.util.concurrent.TimeUnit

@RequiresApi(Build.VERSION_CODES.Q)
class PeriodicFileLoggingTree(
    private val context: Context,
    coroutineContexts: CoroutineContexts,
    systemWallpaperManager: SystemWallpaperManager,
    process: Process
) : Timber.DebugTree() {

    private val logStringBuilder = StringBuilder()

    init {
        require(process.isLiveWallpaperProcess())
        GlobalScope.launch(coroutineContexts.io) {
            while (true) {
                delay(TimeUnit.MINUTES.toMillis(5))
                if (systemWallpaperManager.isCurrentSystemWallpaperApp.value == true) {
                    writeLogsToFile()
                }
            }
        }
    }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        val prefix = when (priority) {
            Log.VERBOSE -> "Log.V"
            Log.DEBUG -> "Log.D"
            Log.INFO -> "Log.I"
            Log.WARN -> "Log.W"
            Log.ERROR -> "Log.E"
            else -> "Log.A"
        }
        writeToLogString(Date().time.getDateTimeString(), prefix, tag, message)
    }

    @Synchronized
    private fun writeToLogString(logTimeStamp: String, prefix: String, tag: String?, message: String) {
        logStringBuilder.append("$logTimeStamp: $prefix, $tag - $message\n")
    }

    @Synchronized
    private fun readAndFlushLogString(): String {
        return logStringBuilder.toString().also {
            logStringBuilder.clear()
        }
    }

    private fun writeLogsToFile() {
        try {
            val logString = readAndFlushLogString()

            val resolver = context.contentResolver
            val values = ContentValues()

            values.put(MediaStore.MediaColumns.DISPLAY_NAME, "${Date().time.getDateTimeString()}.txt")
            values.put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/" + "swirlwalls")
            resolver.insert(MediaStore.Files.getContentUri("external"), values)?.let { uri ->
                FileWriter(resolver.openFileDescriptor(uri, "w")?.fileDescriptor).write(logString)
            }
        } catch (e: Exception) {
            Log.e("FileLogging", "Error while logging into file : $e")
        }
    }

    override fun createStackElementTag(element: StackTraceElement): String? {
        // Add log statements line number to the log
        return super.createStackElementTag(element) + " - " + element.lineNumber
    }
}