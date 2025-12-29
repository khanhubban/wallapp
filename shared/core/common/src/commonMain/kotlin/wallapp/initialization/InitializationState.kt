package wallapp.initialization

enum class InitializationState {
    Uninitialized,
    RequiresNetwork,
    Ready,
}

val InitializationState.isReady: Boolean
    get() = this == InitializationState.Ready