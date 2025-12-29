package wallapp.resources.translation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import wallapp.resources.translation.TranslationExt.verifyFormatting
import wallapp.resources.translation.TranslationLanguage.Companion.NonEnglishLanguages
import wallapp.string.quote
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class TranslationRepositorySingleTest {

    suspend fun createRepository(
        coroutineScope: CoroutineScope,
        language: TranslationLanguage = TranslationLanguage.English,
    ): TranslationRepositorySingle {
        val repository = TranslationRepositorySingle.from(
            language = language,
            coroutineScopeIo = coroutineScope,
        )
        repository.isReady.filter { it }.first()
        return repository
    }

    @Test fun `create and load`() = runTest {
        val repository = createRepository(this)
        assertEquals("WallApp", repository.getString("appName"))
    }

    @Test fun `formatted strings`() = runTest {
        val repositoryEnglish = createRepository(this)
        val englishFormattedKeys = repositoryEnglish.translations.getFormattedStringKeys()
        requireNotNull(englishFormattedKeys)
        println("Formatted strings: size: ${englishFormattedKeys.size}")

        NonEnglishLanguages.forEach { language ->
            val repository = createRepository(this, language)

            englishFormattedKeys.forEach { key ->
                val english = repositoryEnglish.getString(key)
                assertNotNull(english)
                val translated = repository.getString(key)
                if (translated != null) {
                    assertTrue(
                        verifyFormatting(english, translated),
                        "Key: ${key.quote()} is formatted differently:\n  English: $english\n  ${language.name}: $translated"
                    )
                }
            }
        }
    }

    @Test
    fun `non-English keys exist in English translation`() = runTest {
        val repositoryEnglish = createRepository(this)
        val englishKeys = repositoryEnglish.translations.getAllKeys()

        NonEnglishLanguages.forEach { language ->
            val repository = createRepository(this, language)
            val translations = repository.translations

            translations.getAllKeys().forEach { key ->
                assertTrue(
                    englishKeys.contains(key),
                    "Missing English translation for key: ${key.quote()} in language: ${language.name} (${language.iso})"
                )
            }
        }
    }

    @Test
    fun `English keys exist in non-English translations`() = runTest {
        val repositoryEnglish = createRepository(this)
        val englishFormattedKeys = repositoryEnglish.translations.getAllKeys()

        NonEnglishLanguages.forEach { language ->
            val repository = createRepository(this, language)
            val translations = repository.translations
            val nonEnglishFormattedKeys = translations.getAllKeys()

            englishFormattedKeys.forEach { key ->
                if (!nonEnglishFormattedKeys.contains(key)) {
                    println("Missing translation for key ${key.quote()} in language ${language.name}: ${repositoryEnglish.getString(key).quote()}")
                }
            }
        }
    }

    @Test
    fun `no triple dots in translations`() = runTest {
        val repositoryEnglish = createRepository(this)

        repositoryEnglish.translations.getAllKeys().forEach { key ->
            val value = repositoryEnglish.getString(key)
            assertNotNull(value, "String at key ${key.quote()} is null")
            assertTrue(!value.contains("..."), "Key: $key contains triple dots: $value")
        }

        NonEnglishLanguages.forEach { language ->
            val repository = createRepository(this, language)

            repository.translations.getAllKeys().forEach { key ->
                val value = repository.getString(key)
                if (value != null) {
                    assertTrue(
                        !value.contains("..."),
                        "String at key ${key.quote()} in ${language.name} (${language.iso}) contains triple dots: $value"
                    )
                }
            }
        }
    }

    // TODO: Map the English punctuation to the non-English punctuation
    @Ignore
    @Test
    fun `translations end with same punctuation`() = runTest {
        val endingPunctuation = listOf(".", "!", "?")
        val repositoryEnglish = createRepository(this)

        NonEnglishLanguages.forEach { language ->
            val repository = createRepository(this, language)

            repositoryEnglish.translations.getAllKeys().forEach { key ->
                val englishValue = repositoryEnglish.getString(key)
                assertNotNull(englishValue, "String with key: ${key.quote()} is null")
                val englishEnding = endingPunctuation.find { englishValue.endsWith(it) }
                val translatedValue = repository.getString(key)
                if (englishEnding != null && translatedValue != null) {
                    assertTrue(
                        translatedValue.endsWith(englishEnding),
                        "String at key: ${key.quote()} in ${language.name} does not end with $englishEnding:\n  English: $englishValue\n  ${language.name}: $translatedValue"
                    )
                }
            }
        }
    }
}