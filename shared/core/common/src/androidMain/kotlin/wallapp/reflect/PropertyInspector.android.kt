package wallapp.reflect

import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties

actual class PropertyInspector<T : Any> actual constructor(private val clazz: KClass<T>) {

    actual fun getPropertyNames(): List<String> {
        return clazz.declaredMemberProperties.map { it.name }
    }

    actual fun getPropertyValue(instance: T, propertyName: String): Any? {
        val property = clazz.declaredMemberProperties.firstOrNull { it.name == propertyName }
        return property?.get(instance)
    }
}