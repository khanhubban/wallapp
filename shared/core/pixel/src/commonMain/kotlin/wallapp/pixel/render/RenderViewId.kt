package wallapp.pixel.render


/**
 * This is a String because Compose requires the ID be a type that can be stored in a Bundle.
 *
 * Differs from [wallapp.pixel.view.ViewId] in that [RenderViewId]s may have an internal render index
 * applied.
 */
typealias RenderViewId = String

fun RenderViewId(id: String): RenderViewId = id