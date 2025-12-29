package wallapp.resource

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

/**
 * Hack to some-gracefully handle serialization of [ImageVector]s.
 */
@Serializable(with = ImageVectorWrapperSerializer::class)
@Immutable
data class ImageVectorWrapper(val imageVector: ImageVector)

