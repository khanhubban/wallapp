package wallapp.resources

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIos
import wallapp.resource.Resource

private val _arrowBack by lazy { Resource.from(Icons.Rounded.ArrowBackIos) }
actual val Icon.ArrowBack: Resource get() = _arrowBack
