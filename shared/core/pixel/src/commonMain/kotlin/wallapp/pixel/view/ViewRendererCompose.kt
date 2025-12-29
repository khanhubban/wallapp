package wallapp.pixel.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import wallapp.pixel.render.Render

interface ViewRendererCompose : ViewRenderer {

    @Composable
    fun render(render: Render, view: View, modifier: Modifier, alignment: Alignment): Boolean
}
