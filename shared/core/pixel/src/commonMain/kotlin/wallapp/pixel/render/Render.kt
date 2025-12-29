package wallapp.pixel.render

import androidx.compose.runtime.Immutable
import wallapp.image.cache.ImageCacheKeyManager
import wallapp.image.cache.ImageCacheKeyManagerNoOp
import wallapp.image.hash.ImageHashDecoder
import wallapp.image.hash.ImageHashDecoderNoOp
import wallapp.image.host.ImageHostManager
import wallapp.image.host.ImageHostManagerNoOp
import wallapp.image.loader.ImageLoader
import wallapp.image.loader.ImageLoaderNoOp
import wallapp.pixel.alert.AlertManager
import wallapp.pixel.alert.AlertManagerNoOp
import wallapp.pixel.resource.DefaultResources
import wallapp.pixel.resource.DefaultResourcesNoOp
import wallapp.pixel.shape.ShapeClipper
import wallapp.pixel.shape.ShapeClipperNoOp
import wallapp.pixel.shape.ShapeMapper
import wallapp.pixel.shape.ShapeMapperNoOp
import wallapp.pixel.typeface.TypefaceRepository
import wallapp.pixel.typeface.TypefaceRepositoryNoOp
import wallapp.pixel.view.DefaultViewSpec
import wallapp.pixel.view.DefaultViewSpecPreset
import wallapp.pixel.view.UIKitFactory
import wallapp.pixel.view.UIKitFactoryNoOp
import wallapp.pixel.view.ViewAlignmentMapper
import wallapp.pixel.view.ViewAlignmentMapperNoOp
import wallapp.pixel.view.ViewRenderer
import wallapp.pixel.view.ViewRendererNoOp
import wallapp.system.window.WindowFrame
import wallapp.system.window.WindowManager
import wallapp.system.window.WindowManagerNoOp

/**
 * Holds common data used for rendering.
 *
 * Ideally all [Composable] functions should take this as an argument.
 */
@Immutable
data class Render(
    val config: RenderConfig,
    val imageLoader: ImageLoader,
    val imageMemoryCacheKeyManager: ImageCacheKeyManager,
    val imageHashDecoder: ImageHashDecoder,
    val imageHostManager: ImageHostManager,
    val viewRenderer: ViewRenderer,
    val viewAlignmentMapper: ViewAlignmentMapper,
    val renderViewIdFactory: RenderViewIdFactory,
    val defaultViewSpec: DefaultViewSpec,
    val defaultResources: DefaultResources,
    val windowManager: WindowManager,
    val alertManager: AlertManager,
    val typefaceRepository: TypefaceRepository,
    val shapeMapper: ShapeMapper,
    val shapeClipper: ShapeClipper,
    val uiKitFactory: UIKitFactory,
) {
    val windowFrame: WindowFrame
        get() = requireNotNull(windowManager.windowFrame)

    companion object {
        val Preset = Render(
            config = RenderConfig.Preset,
            imageLoader = ImageLoaderNoOp,
            imageMemoryCacheKeyManager = ImageCacheKeyManagerNoOp,
            imageHashDecoder = ImageHashDecoderNoOp,
            imageHostManager = ImageHostManagerNoOp,
            viewRenderer = ViewRendererNoOp,
            renderViewIdFactory = RenderViewIdFactoryNoOp,
            viewAlignmentMapper = ViewAlignmentMapperNoOp,
            defaultViewSpec = DefaultViewSpecPreset,
            defaultResources = DefaultResourcesNoOp,
            windowManager = WindowManagerNoOp,
            alertManager = AlertManagerNoOp,
            typefaceRepository = TypefaceRepositoryNoOp,
            shapeMapper = ShapeMapperNoOp,
            shapeClipper = ShapeClipperNoOp,
            uiKitFactory = UIKitFactoryNoOp,
        )
    }

}