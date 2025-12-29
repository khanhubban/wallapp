package wallapp.reflect

import kotlin.reflect.KClass

expect class PropertyInspector<T : Any>(clazz: KClass<T>) {
    fun getPropertyNames(): List<String>
    fun getPropertyValue(instance: T, propertyName: String): Any?
}