package wallapp.test

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.koin.core.context.stopKoin
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.test.KoinTest
import wallapp.coroutine.CoroutineContexts
import wallapp.coroutine.CoroutineContextsPreset
import wallapp.coroutine.CoroutineScopes
import wallapp.coroutine.CoroutineScopesPreset
import wallapp.di.NamedScope
import wallapp.initializer.app.appInitializeTest

/**
 * Usage:
 *
 * class MyTest : WaeTest {
 *
 *   @Test fun myTest() = waeTest {
 *      ...
 *   }
 * }
 *
 */

interface WaeTest : KoinTest

fun waeTest(
    additionalModules: List<Module>? = listOf(WaeTestModule),
    configureTestCoroutineModule: Boolean = true,
    testBody: suspend TestScope.() -> Unit
): TestResult {
    return runTest {
        val coroutineScope = this.backgroundScope
        val testModule: Module = module {
            single<CoroutineContexts> { CoroutineContextsPreset(coroutineScope) }
            single<CoroutineScope>(NamedScope.CoroutineScopeIo) { coroutineScope }
            single<CoroutineScope>(NamedScope.CoroutineScopeMain) { coroutineScope }
            single<CoroutineScope>(NamedScope.CoroutineScopeMainImmediate) { coroutineScope }
            single<CoroutineScopes> { CoroutineScopesPreset(coroutineScope) }
        }

        val modules = (additionalModules ?: emptyList()) +
                if (configureTestCoroutineModule) { listOf(testModule) } else { emptyList() }

        try {
            appInitializeTest(additionalModules = modules)
            testBody()
        } finally {
            stopKoin()
        }
    }
}

fun waeTest(
    additionalModule: Module,
    configureTestCoroutineModule: Boolean = true,
    testBody: suspend TestScope.() -> Unit
): TestResult = waeTest(
    additionalModules = listOf(additionalModule),
    configureTestCoroutineModule = configureTestCoroutineModule,
    testBody = testBody
)

