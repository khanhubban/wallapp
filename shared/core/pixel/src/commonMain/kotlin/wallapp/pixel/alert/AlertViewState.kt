package wallapp.pixel.alert

import androidx.compose.runtime.Immutable
import wallapp.pixel.text.Text
import wallapp.pixel.text.TextStyleBody
import wallapp.pixel.text.TextStyleCallToAction
import wallapp.pixel.text.TextStyleSubheadingActive
import wallapp.pixel.view.ViewEventHandler
import wallapp.pixel.view.ViewState

@Immutable
data class AlertViewState(
    val title: Text,
    val message: Text?,
    val buttonPrimary: Text,
    val buttonPrimaryOnClick: ViewEventHandler? = null,
    val buttonSecondary: Text? = null,
    val buttonSecondaryOnClick: ViewEventHandler? = null,
    val onDismissRequest: ViewEventHandler? = null,
): ViewState

fun AlertViewState(
    title: String,
    message: String? = null,
    buttonPrimary: String,
    buttonPrimaryOnClick: ViewEventHandler? = null,
    buttonSecondary: String? = null,
    buttonSecondaryOnClick: ViewEventHandler? = null,
): AlertViewState = AlertViewState(
    title = TextStyleSubheadingActive(title),
    message = message?.let { TextStyleBody(message) },
    buttonPrimary = TextStyleCallToAction(buttonPrimary),
    buttonPrimaryOnClick = buttonPrimaryOnClick,
    buttonSecondary = buttonSecondary?.let { TextStyleCallToAction(it) },
    buttonSecondaryOnClick = buttonSecondaryOnClick,
)

/**
 * Convenience function to create an [AlertViewState] with OK/cancel. If used in production,
 * be sure to provide a proper translation for the labels.
 */
fun AlertViewStateOkCancel(
    title: String,
    message: String? = null,
    okOnClick: ViewEventHandler? = null,
    okLabel: String = "OK",
    cancelOnClick: ViewEventHandler? = null,
    cancelLabel: String = "Cancel"
): AlertViewState = AlertViewState(
    title = title,
    message = message,
    buttonPrimary = okLabel,
    buttonPrimaryOnClick = okOnClick,
    buttonSecondary = cancelLabel,
    buttonSecondaryOnClick = cancelOnClick,
)

fun AlertViewStateOk(
    title: String,
    message: String? = null,
    okLabel: String = "OK",
    okOnClick: ViewEventHandler? = null,
): AlertViewState = AlertViewState(
    title = TextStyleSubheadingActive(title),
    message = message?.let { TextStyleBody(message) },
    buttonPrimary = TextStyleCallToAction(okLabel),
    buttonPrimaryOnClick = okOnClick,
    buttonSecondary = null,
    buttonSecondaryOnClick = null,
)