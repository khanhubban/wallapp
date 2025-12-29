package wallapp.test

import wallapp.di.resolveDependency
import wallapp.resources.string.StringRepository
import kotlin.test.Test

class WaeExampleTest : WaeTest {

    @Test fun helloWorld() = waeTest {
        val strings: StringRepository = resolveDependency()
        println(strings.appName)
    }
}