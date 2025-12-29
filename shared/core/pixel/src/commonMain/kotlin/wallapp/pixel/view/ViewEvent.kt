package wallapp.pixel.view

import androidx.compose.runtime.Immutable

@Immutable
interface ViewEvent


typealias ViewEventSink = (ViewEvent) -> Unit