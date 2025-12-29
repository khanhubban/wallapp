@file:Suppress("unused")

package wallapp.unit

data class BooleanOptional(val boolean: Boolean)
val Boolean?.optional: BooleanOptional?
    get() = this?.let { BooleanOptional(it) }

data class IntOptional(val int: Int)
val Int?.optional: IntOptional?
    get() = this?.let { IntOptional(it) }

data class LongOptional(val long: Long)
val Long?.optional: LongOptional?
    get() = this?.let { LongOptional(it) }

data class FloatOptional(val float: Float)
val Float?.optional: FloatOptional?
    get() = this?.let { FloatOptional(it) }

data class DoubleOptional(val double: Double)
val Double?.optional: DoubleOptional?
    get() = this?.let { DoubleOptional(it) }

data class StringOptional(val string: String)
val String?.optional: StringOptional?
    get() = this?.let { StringOptional(it) }
