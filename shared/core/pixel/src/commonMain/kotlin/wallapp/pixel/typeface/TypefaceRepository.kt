package wallapp.pixel.typeface

/**
 * Houses the typefaces used in the app.
 *
 * Generally, the typeface is set by Compose globally. This is currently only used for
 * compat text rendering that uses legacy rendering systems such as autosize text).
 *
 * Given this, null values are allowed on platforms that don't implement autosize text rendering.
 */
interface TypefaceRepository {

    val regularTypeface: Typeface?

    val boldTypeface: Typeface?
}