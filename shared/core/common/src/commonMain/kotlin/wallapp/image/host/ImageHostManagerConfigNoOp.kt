package wallapp.image.host

import kotlinx.coroutines.flow.MutableStateFlow

object ImageHostManagerConfigNoOp : ImageHostManagerConfig {

    override val imageFormatCodeInAppCompose: MutableStateFlow<String> = MutableStateFlow("")
}