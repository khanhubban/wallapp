package wallapp.image.host

import kotlinx.coroutines.flow.MutableStateFlow

interface ImageHostManagerConfig {

    val imageFormatCodeInAppCompose: MutableStateFlow<String>
}