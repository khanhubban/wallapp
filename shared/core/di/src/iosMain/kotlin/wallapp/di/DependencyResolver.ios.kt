package wallapp.di

import org.koin.core.Koin
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier

inline fun <reified T> getKoinInstance(
    koin: Koin,
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null,
): T {
    return koin.get(qualifier, parameters)
}

lateinit var koinInternal: Koin
actual fun initDependencyResolver(any: Any) {
    koinInternal = any as Koin
}

actual inline fun <reified T> resolveDependency(qualifier: Qualifier?): T {
    return getKoinInstance<T>(koinInternal, qualifier = qualifier)
}