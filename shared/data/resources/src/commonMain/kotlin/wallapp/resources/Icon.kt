package wallapp.resources

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Search
import wallapp.resource.Resource

expect val Icon.ArrowBack: Resource

object Icon {
    val CheckCircle: Resource by lazy { Resource.from(Icons.Outlined.CheckCircle) }
    val Search: Resource by lazy { Resource.from(Icons.Outlined.Search) }
}