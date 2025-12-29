package wallapp.reflect

import kotlin.reflect.KClass

actual class PropertyInspector<T : Any> actual constructor(private val clazz: KClass<T>) {

    actual fun getPropertyNames(): List<String> {
        throw UnsupportedOperationException("Reflection is not available on iOS")
    }

    actual fun getPropertyValue(instance: T, propertyName: String): Any? {
        throw UnsupportedOperationException("Reflection is not available on iOS")
    }
}