package wallapp.di

import org.koin.core.qualifier.Qualifier

/**
 * Get dependency from Koin
 */
expect inline fun <reified T> resolveDependency(qualifier: Qualifier?): T

inline fun <reified T> resolveDependency(): T = resolveDependency(qualifier = null)

/**
 * Call once during app init
 */
expect fun initDependencyResolver(any: Any)
