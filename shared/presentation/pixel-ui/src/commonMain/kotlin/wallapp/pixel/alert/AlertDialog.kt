package wallapp.pixel.alert

import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import wallapp.pixel.button.TextButton
import wallapp.pixel.render.Render
import wallapp.pixel.render.alertManagerComposable
import wallapp.pixel.text.Text

@Composable
fun AlertDialog(
    render: Render,
    dialog: AlertViewState,
    modifier: Modifier = Modifier
) {
    val alertManagerComposable = render.alertManagerComposable

    val onDismissRequest = {
        dialog.onDismissRequest?.invoke()
        alertManagerComposable.dismiss()
    }
    val title = dialog.title
    val message = dialog.message
    val buttonPrimary = dialog.buttonPrimary
    val buttonPrimaryOnClick = {
        dialog.buttonPrimaryOnClick?.invoke()
        alertManagerComposable.dismiss()
    }
    val buttonSecondary = dialog.buttonSecondary
    val buttonSecondaryOnClick = dialog.buttonSecondaryOnClick.let {
        {
            it?.invoke()
            alertManagerComposable.dismiss()
        }
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        confirmButton = {
            TextButton(
                text = buttonPrimary,
                onClick = buttonPrimaryOnClick,
            )
        },
        dismissButton = if (buttonSecondary != null ) {
            {
                TextButton(
                    text = buttonSecondary,
                    onClick = buttonSecondaryOnClick,
                )
            }
        } else {
            null
        },
        icon = null,
        title = {
            Text(text = title)
        },
        text = {
            message?.let {
                Text(text = message)
            }
        },
//        shape =
    )
}