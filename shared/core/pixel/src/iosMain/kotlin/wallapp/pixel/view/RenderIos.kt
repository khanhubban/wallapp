package wallapp.pixel.view

import wallapp.image.hash.ImageHashDecoder
import wallapp.pixel.resource.DefaultResources
import wallapp.system.window.WindowFrame
import wallapp.system.window.WindowManager

data class RenderIos(
    val defaultViewSpec: DefaultViewSpec,
    val windowManager: WindowManager,
    val imageHashDecoder: ImageHashDecoder,
    val defaultResources: DefaultResources,
) {
    val windowFrame: WindowFrame
        get() = requireNotNull(windowManager.windowFrame)
}
