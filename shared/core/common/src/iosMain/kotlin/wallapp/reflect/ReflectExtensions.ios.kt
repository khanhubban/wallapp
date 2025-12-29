package wallapp.reflect

import kotlin.reflect.KClass

fun getActualClassName(kClass: KClass<*>): String? {
    val kClassString = kClass.toString()
    val classNameRegex = "class (.+)".toRegex()
    val matchResult = classNameRegex.find(kClassString)
    return matchResult?.groups?.get(1)?.value
}

actual fun KClass<*>.isSubclassOf(base: KClass<*>): Boolean {
    val baseClassName = getActualClassName(base)
    val thisClassName = getActualClassName(this)
    return baseClassName == thisClassName
}

actual fun Any.verifyAllProperties() {
//    this::class.memberProperties.forEach { property: KProperty1<out Any, *> ->
//        property.isAccessible = true
//        // Cast the property's getter to the correct receiver type
//        val getter = property.getter as KFunction1<Any, Any?>
//        val value = getter.call(this)?.toString() ?: "null"
//        println("Property: ${property.name}, Value: $value")
//    }
}
