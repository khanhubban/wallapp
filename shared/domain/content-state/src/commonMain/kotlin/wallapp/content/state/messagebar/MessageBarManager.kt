package wallapp.content.state.messagebar

import kotlinx.coroutines.flow.Flow
import wallapp.pixel.message.MessageBarViewState

interface MessageBarManager {

    val messageBarViewState: Flow<MessageBarViewState?>
}