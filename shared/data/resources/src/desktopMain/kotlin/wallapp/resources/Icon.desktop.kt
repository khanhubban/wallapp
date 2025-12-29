package wallapp.resources

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import wallapp.resource.Resource

private val _arrowBack by lazy { Resource.from(Icons.Rounded.ArrowBack) }
actual val Icon.ArrowBack: Resource get() = _arrowBack
