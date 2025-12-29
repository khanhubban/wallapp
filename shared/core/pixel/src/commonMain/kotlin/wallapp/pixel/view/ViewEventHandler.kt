package wallapp.pixel.view

import androidx.compose.runtime.Immutable
import kotlin.experimental.ExperimentalObjCName
import kotlin.jvm.JvmName
import kotlin.native.ObjCName

sealed interface ViewEventHandler {

    operator fun invoke()

    @Immutable
    data class Event(
        val eventSink: ViewEventSink,
        val event: ViewEvent,
    ) : ViewEventHandler {

        override fun invoke() {
            eventSink(event)
        }
    }

    @Immutable
    data class OnClick(
        val onClick: () -> Unit,
    ) : ViewEventHandler {

        override fun invoke() {
            onClick()
        }
    }

    data object NoOp : ViewEventHandler {

        override fun invoke() { }
    }

    @OptIn(ExperimentalObjCName::class)
    companion object {
        @JvmName("createOnClick")
        @ObjCName("createOnClick")
        fun createOnClick(onClick: (() -> Unit)): ViewEventHandler = OnClick(onClick = onClick)
        @JvmName("createOnClickNullable")
        @ObjCName("createOnClickNullable")
        fun createOnClick(onClick: (() -> Unit)?): ViewEventHandler? = if (onClick != null) {
            createOnClick(onClick = onClick)
        } else {
            null
        }
    }
}
