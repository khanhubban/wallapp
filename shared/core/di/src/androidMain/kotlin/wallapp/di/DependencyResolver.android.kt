package wallapp.di

import org.koin.core.qualifier.Qualifier
import org.koin.java.KoinJavaComponent

actual fun initDependencyResolver(any: Any) = Unit

/**
 * Get dependency from Koin
 */
actual inline fun <reified T> resolveDependency(qualifier: Qualifier?): T {
    return KoinJavaComponent.get(
        clazz = T::class.java,
        qualifier = qualifier,
    )
}