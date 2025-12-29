package wallapp.ui.bottomsheet

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import wallapp.pixel.bottomsheet.ModalBottomSheetViewState
import wallapp.pixel.render.Render
import wallapp.pixel.render.shapeMapperComposable
import wallapp.ui.AppScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalBottomSheet(
    render: Render,
    modalBottomSheetViewState: ModalBottomSheetViewState?,
) {
    val coroutineScope = rememberCoroutineScope()
    val modalBottomSheetState: SheetState = rememberModalBottomSheetState()
    var isModalBottomSheetOpen by remember {
        mutableStateOf(false)
    }

    if (modalBottomSheetViewState != null) {
        if (!isModalBottomSheetOpen) {
            isModalBottomSheetOpen = true
        }
        if (modalBottomSheetViewState.requestHideState
            && modalBottomSheetState.targetValue != SheetValue.Hidden
        ) {
            coroutineScope.launch {
                modalBottomSheetState.hide()
                isModalBottomSheetOpen = false
                modalBottomSheetViewState.onDismissed()
            }
        }
    }

    if (isModalBottomSheetOpen) {
        // There are cases where the modalBottomSheetViewState is null,
        // but the modalBottomSheetState is still open. #2227
        if (modalBottomSheetViewState != null) {
            ModalBottomSheet(
                render,
                modalBottomSheetViewState,
                modalBottomSheetState,
            ) {
                isModalBottomSheetOpen = false
                modalBottomSheetViewState.onDismissed()
            }
        } else {
            isModalBottomSheetOpen = false
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModalBottomSheet(
    render: Render,
    modalBottomSheetViewState: ModalBottomSheetViewState,
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
) {
    val modalBottomSheetScreenViewState = modalBottomSheetViewState.screenViewState
    val shape = render.shapeMapperComposable.map(modalBottomSheetViewState.shapeSpec)!!

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismissRequest,
//        windowInsets = WindowInsets(top = 0.dp),
        shape = shape,
        dragHandle = null,
    ) {
        AppScreen(render, modalBottomSheetScreenViewState)
    }
}
