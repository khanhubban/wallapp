package wallapp.resource

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ColorWrapper(val color: ULong)


