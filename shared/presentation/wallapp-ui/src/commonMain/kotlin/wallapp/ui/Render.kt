package wallapp.ui

import wallapp.di.resolveDependency
import wallapp.image.cache.ImageCacheKeyManager
import wallapp.image.hash.ImageHashDecoder
import wallapp.image.host.ImageHostManager
import wallapp.image.loader.ImageLoader
import wallapp.pixel.alert.AlertManager
import wallapp.pixel.render.Render
import wallapp.pixel.render.RenderConfig
import wallapp.pixel.render.RenderViewIdFactory
import wallapp.pixel.resource.DefaultResources
import wallapp.pixel.shape.ShapeClipperComposable
import wallapp.pixel.shape.ShapeMapperComposable
import wallapp.pixel.typeface.TypefaceRepository
import wallapp.pixel.view.DefaultViewSpec
import wallapp.pixel.view.UIKitFactory
import wallapp.pixel.view.ViewAlignmentMapper
import wallapp.pixel.view.ViewRenderer
import wallapp.system.window.WindowManager

fun Render(): Render {
    val alertManager: AlertManager = resolveDependency()
    val defaultResources: DefaultResources = resolveDependency()
    val defaultViewSpec: DefaultViewSpec = resolveDependency()
    val imageHashDecoder: ImageHashDecoder = resolveDependency()
    val imageHostManager: ImageHostManager = resolveDependency()
    val imageLoader: ImageLoader = resolveDependency()
    val imageMemoryCacheKeyManager: ImageCacheKeyManager = resolveDependency()
    val renderConfig: RenderConfig = resolveDependency()
    val renderViewIdFactory: RenderViewIdFactory = resolveDependency()
    val shapeClipperComposable: ShapeClipperComposable = resolveDependency()
    val shapeMapperComposable: ShapeMapperComposable = resolveDependency()
    val typefaceRepository: TypefaceRepository = resolveDependency()
    val viewAlignmentMapper: ViewAlignmentMapper = resolveDependency()
    val viewRenderer: ViewRenderer = resolveDependency()
    val windowManager: WindowManager = resolveDependency()
    val uiKitFactory: UIKitFactory = resolveDependency()

    return Render(
        renderConfig,
        imageLoader,
        imageMemoryCacheKeyManager,
        imageHashDecoder,
        imageHostManager,
        viewRenderer,
        viewAlignmentMapper,
        renderViewIdFactory,
        defaultViewSpec,
        defaultResources,
        windowManager,
        alertManager,
        typefaceRepository,
        shapeMapper = shapeMapperComposable,
        shapeClipper = shapeClipperComposable,
        uiKitFactory = uiKitFactory,
    )
}