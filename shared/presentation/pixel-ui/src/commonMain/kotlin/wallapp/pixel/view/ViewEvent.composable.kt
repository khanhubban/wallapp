package wallapp.pixel.view

val ViewEventHandler.onClick: (() -> Unit)
    get() = {
        invoke()
    }