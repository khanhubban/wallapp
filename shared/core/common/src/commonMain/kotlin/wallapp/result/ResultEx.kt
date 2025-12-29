package wallapp.result

import co.touchlab.skie.configuration.annotations.SealedInterop

/**
 * A generic Result type class that holds either a value or an error
 */
@SealedInterop.Enabled
sealed interface ResultEx<out R> {

        data class Success<out T>(val data: T) : ResultEx<T>
        data class Error(val exception: Exception) : ResultEx<Nothing>
}