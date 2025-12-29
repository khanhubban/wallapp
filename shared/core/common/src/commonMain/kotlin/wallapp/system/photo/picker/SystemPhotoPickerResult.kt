package wallapp.system.photo.picker

import co.touchlab.skie.configuration.annotations.SealedInterop


@SealedInterop.Enabled
sealed class SystemPhotoPickerResult {

    data class Success(
        val data: ByteArray,
    ): SystemPhotoPickerResult() {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as Success

            return data.contentEquals(other.data)
        }

        override fun hashCode(): Int {
            return data.contentHashCode()
        }
    }

    data object Failure: SystemPhotoPickerResult()
}