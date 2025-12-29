package wallapp.language

import platform.Foundation.NSUserDefaults

object LanguageRepositoryIos : LanguageRepository {

    override val systemLanguageIsos: List<Iso>?
        get() = preferredLocales
            ?.map { it.substringBefore("-").substringBefore("_") }
            ?.distinct()

    override val systemLocaleIdentifiers: List<String>?
        get() {
            return preferredLocales
                ?.takeIf { it.isNotEmpty() }
        }

    private val preferredLocales: List<String>?
        get() = NSUserDefaults.standardUserDefaults.arrayForKey("AppleLanguages") as? List<String>
}
