package wallapp.pixel.render

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import wallapp.pixel.typeface.LocalTypefaceRepository
import wallapp.pixel.typeface.LocalTypography3rdParty

@Composable
fun RenderLocalProvider(
    render: Render,
    typography3rdParty: Typography,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalRenderCompat provides render,
        LocalTypography3rdParty provides typography3rdParty,
        LocalTypefaceRepository provides render.typefaceRepository,
    ) {
        content()
    }
}