package wallapp.strings

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import wallapp.reflect.PropertyInspector
import wallapp.resources.string.StringArbitratorPreset
import wallapp.resources.string.StringRepositoryDefault
import wallapp.resources.string.Strings
import wallapp.resources.translation.TranslationLanguage
import wallapp.resources.translation.TranslationRepository
import wallapp.resources.translation.TranslationRepositorySingle
import wallapp.string.quote
import kotlin.test.Test
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class StringsTest {

    // Function to create TranslationRepository
    suspend fun createRepository(
        coroutineScope: CoroutineScope,
        language: TranslationLanguage = TranslationLanguage.English,
    ): TranslationRepository {
        val repository = TranslationRepositorySingle.from(
            language = language,
            coroutineScopeIo = coroutineScope,
        )
        repository.isReady.filter { it }.first()
        return repository
    }

    // Function to create StringRepositoryDefault
    suspend fun createStringRepository(
        coroutineScope: CoroutineScope,
        language: TranslationLanguage = TranslationLanguage.English,
    ): StringRepositoryDefault {
        val translationRepository = createRepository(coroutineScope, language)
        return StringRepositoryDefault(translationRepository, StringArbitratorPreset())
    }

    @Test
    fun testAllStringsAreValid() = runTest {
        // Use the external function to create the StringRepositoryDefault
        val strings = createStringRepository(this)

        val inspector = PropertyInspector(Strings::class)

        inspector.getPropertyNames().forEach { propertyName ->
            val value = inspector.getPropertyValue(strings, propertyName) as? String
            assertNotNull(value, "Property $propertyName should not be null")
            assertTrue(value.isNotEmpty(), "Property $propertyName should not be empty")

            assertNotEquals(propertyName, value, "Property ${propertyName.quote()} should not be equal to its Json key name - this means the string entry is missing in the translation file")
        }
    }
}