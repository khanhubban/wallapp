package wallapp.content.state.collection

sealed class CollectionActionState {

    data object Locked : CollectionActionState()
    data object Unlocked : CollectionActionState()
}