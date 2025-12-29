package wallapp.remotecontent

import kotlinx.coroutines.flow.MutableStateFlow

interface RemoteServerContentCache {

    val endPoints: MutableStateFlow<String>

    val baseContent: MutableStateFlow<String>

    val mediaMap: MutableStateFlow<String>

    val searchContent: MutableStateFlow<String>
}