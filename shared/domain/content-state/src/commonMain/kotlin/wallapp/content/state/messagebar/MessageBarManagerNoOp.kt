package wallapp.content.state.messagebar

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import wallapp.pixel.message.MessageBarViewState

object MessageBarManagerNoOp : MessageBarManager {

    override val messageBarViewState: Flow<MessageBarViewState?> = flowOf(null)
}