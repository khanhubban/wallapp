package wallapp.image


sealed class ImageAction {

    data object Loading : ImageAction()

    data object Success : ImageAction()

    sealed class Error : ImageAction() {
        abstract val imageUrl: String
        abstract val error: Throwable

        data class ImageDecodeError(
            override val imageUrl: String,
            override val error: Throwable,
        ) : Error()

        data class GeneralError(
            override val imageUrl: String,
            override val error: Throwable,
        ) : Error()
    }
}

typealias OnImageAction = (ImageAction) -> Unit