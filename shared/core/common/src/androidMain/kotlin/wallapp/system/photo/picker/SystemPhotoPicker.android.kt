package wallapp.system.photo.picker

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.io.IOException

class SystemPhotoPickerAndroid(
    private val context: Context,
    private val coroutineScopeMain: CoroutineScope,
) : SystemPhotoPicker {

    override val enabled: Boolean
        get() = true

    private val onCompleteMap: MutableMap<Int, SystemPhotoPickerCallback> = mutableMapOf()

    override fun navigateToSystemPhotoPicker(onComplete: SystemPhotoPickerCallback) {
        nextActivityRequestCode++
        onCompleteMap[nextActivityRequestCode] = onComplete
        pickerRequestCode.value = nextActivityRequestCode

        navigateToSystemPhotoPicker()
    }

    private var nextActivityRequestCode = 10001
    val pickerRequestCode: MutableStateFlow<Int?> = MutableStateFlow(null)

    fun onPickerResult(requestCode: Int, success: Boolean, data: Any?) {
        val onComplete = onCompleteMap.remove(requestCode)

        val successResult: SystemPhotoPickerResult.Success? = if (success && data is Uri) {
            mapToImageByteArray(data)?.let { byteArray ->
                SystemPhotoPickerResult.Success(byteArray)
            }
        } else {
            null
        }
        onComplete?.invoke(successResult ?: SystemPhotoPickerResult.Failure)

        coroutineScopeMain.launch {
            pickerRequestCode.emit(null)
        }
    }

    private fun navigateToSystemPhotoPicker() {
        coroutineScopeMain.launch {
            pickerRequestCode.emit(nextActivityRequestCode)
        }
    }

    private fun mapToImageByteArray(data: Uri): ByteArray? {
        val inputStream = context.contentResolver.openInputStream(data)

        return try {
            inputStream?.readBytes()
        } catch (e: IOException) {
            null
        } finally {
            inputStream?.close()
        }
    }
}