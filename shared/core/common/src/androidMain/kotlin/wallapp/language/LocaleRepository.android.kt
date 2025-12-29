package wallapp.language

import android.content.Context
import android.content.res.Resources
import android.text.TextUtils
import androidx.core.os.ConfigurationCompat
import androidx.core.os.LocaleListCompat
import wallapp.resources.resolveLocale

class LanguageRepositoryAndroid(context: Context): LanguageRepository {

    private val resources: Resources by lazy { context.resources }

    private val localeLanguageIso: Iso?
        get() {
            val lang = resources.resolveLocale().language
            return if (TextUtils.isEmpty(lang)) null else lang
        }

    private val localeCountry: String?
        get() {
            val country = resources.resolveLocale().country
            return if (TextUtils.isEmpty(country)) null else country
        }

    private val localeList: LocaleListCompat by lazy {
        ConfigurationCompat.getLocales(context.resources.configuration)
    }

    override val systemLanguageIsos: List<Iso>?
        get() {
            val size = localeList.size()
            val languages = mutableListOf<Iso>()
            for (i in 0 until size) {
                val language: String? = localeList.get(i)?.language
                if (language != null) {
                    languages.add(language)
                }
            }
            return if (languages.isEmpty()) null else languages.distinct()
        }

    override val systemLocaleIdentifiers: List<String>?
        get() {
            val size = localeList.size()
            val localeIdentifiers = mutableListOf<String>()
            for (i in 0 until size) {
                val locale = localeList.get(i)
                locale?.let {
                    val language = it.language
                    val country = it.country
                    val localeIdentifier = if (country.isNotEmpty()) {
                        "${language}_$country"
                    } else {
                        language
                    }
                    localeIdentifiers.add(localeIdentifier)
                }
            }
            return if (localeIdentifiers.isEmpty()) null else localeIdentifiers
        }
}