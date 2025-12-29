package wallapp.content.state.collection

import androidx.compose.runtime.Immutable
import wallapp.pixel.view.ViewEvent

@Immutable
sealed interface CollectionActionViewEvent : ViewEvent {

    @Immutable
    data object BuyCollection : CollectionActionViewEvent

}