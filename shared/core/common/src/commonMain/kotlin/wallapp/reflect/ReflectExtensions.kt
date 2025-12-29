package wallapp.reflect

import kotlin.reflect.KClass

expect fun KClass<*>.isSubclassOf(base: KClass<*>): Boolean

expect fun Any.verifyAllProperties()
