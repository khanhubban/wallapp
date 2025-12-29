package wallapp.pixel.typeface

object TypefaceRepositoryNoOp : TypefaceRepository {
    override val regularTypeface: Typeface?
        get() = null
    override val boldTypeface: Typeface?
        get() = null
}
