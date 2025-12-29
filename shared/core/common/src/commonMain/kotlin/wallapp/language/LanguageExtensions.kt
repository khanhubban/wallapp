package wallapp.language

import wallapp.country.Country

/**
 * https://en.wikipedia.org/wiki/List_of_ISO_3166_country_codes#UNI2
 */
typealias Iso = String

fun Iso.isEnglishLanguage(): Boolean {
    return ENGLISH_SPEAKING_ISOS.contains(lowercase())
}

private val ENGLISH_SPEAKING_ISOS : List<Iso> = listOf(
        "en",
        "as" /** American Samoa */,
        "au" /** Australia */,
        "ai" /** Anguilla */,
        "bm" /** Bermuda */,
        "cc" /** Cocos (Keeling) Islands (the) */,
        "cx" /** Christmas Island */,
        "ck" /** Cook Islands (the) */,
        "fk" /** Falkland Islands (the) **/,
        "gb" /** Great Britain / United Kingdom */,
        "gi" /** Gibraltar */,
        "gg" /** Guernsey */,
        "gu" /** Guam */,
        "je" /** Jersey */,
        "im" /** Isle of Man */,
        "io" /** British Indian Ocean Territory (the) */,
        "ky" /** Cayman Islands (the) */,
        "hm" /** Heard Island and McDonald Islands */,
        "ms" /** Montserrat */,
        "nf" /** Norfolk Island */,
        "nz" /** New Zealand */,
        "pn" /** Pitcairn **/,
        "pr" /** Puerto Rico */,
        "sh" /** Saint Helena **/,
        "sg" /** Singapore **/,
        "sb" /** Solomon Islands */,
        "tc" /** Turks and Caicos Islands (the) */,
        "um" /** United States Minor Outlying Islands (the) */,
        "us" /** USA **/,
        "vg" /** The Virgin Islands */
)

fun String.getLanguageAndCountry(): Pair<String, String> {
    return when (this) {
        "english_us" -> Pair("en", "US")
        "espanol_spain" -> Pair("es", "ES")
        "german_germany" -> Pair("de", "DE")
        else -> throw UnsupportedOperationException("Unknown language preset")
    }
}

const val ENGLISH_LANGUAGE_ISO = "en"

fun mapLanguageCodeToLanguage(language: String): String {
    val country = Country.entries.find { it.isoAlpha2  == language }
    if (country != null) {
        return country.name
    }

    return when (language) {
        "aa" -> "Afar"
        "ab" -> "Abkhazian"
        "ae" -> "Avestan"
        "af" -> "Afrikaans"
        "ak" -> "Akan"
        "am" -> "Amharic"
        "an" -> "Aragonese"
        "ar" -> "Arabic"
        "ars" -> "Najdi Arabic"
        "as" -> "Assamese"
        "av" -> "Avaric"
        "ay" -> "Aymara"
        "az" -> "Azerbaijani"
        "ba" -> "Bashkir"
        "be" -> "Belarusian"
        "bg" -> "Bulgarian"
        "bh" -> "Bihari"
        "bi" -> "Bislama"
        "bm" -> "Bambara"
        "bn" -> "Bengali"
        "bo" -> "Tibetan"
        "br" -> "Breton"
        "bs" -> "Bosnian"
        "ca" -> "Catalan"
        "ce" -> "Chechen"
        "ch" -> "Chamorro"
        "co" -> "Corsican"
        "cr" -> "Cree"
        "cs" -> "Czech"
        "cu" -> "Church Slavic"
        "cv" -> "Chuvash"
        "cy" -> "Welsh"
        "da" -> "Danish"
        "de" -> "German"
        "dv" -> "Dhivehi"
        "dz" -> "Dzongkha"
        "ee" -> "Ewe"
        "el" -> "Greek"
        "en" -> "English"
        "eo" -> "Esperanto"
        "es" -> "Spanish"
        "et" -> "Estonian"
        "eu" -> "Basque"
        "fa" -> "Persian"
        "ff" -> "Fulah"
        "fi" -> "Finnish"
        "fj" -> "Fijian"
        "fo" -> "Faroese"
        "fr" -> "French"
        "fy" -> "Western Frisian"
        "ga" -> "Irish"
        "gd" -> "Scottish Gaelic"
        "gl" -> "Galician"
        "gn" -> "Guarani"
        "gu" -> "Gujarati"
        "gv" -> "Manx"
        "ha" -> "Hausa"
        "haw" -> "Hawaiian"
        "he" -> "Hebrew"
        "hi" -> "Hindi"
        "ho" -> "Hiri Motu"
        "hr" -> "Croatian"
        "ht" -> "Haitian"
        "hu" -> "Hungarian"
        "hy" -> "Armenian"
        "hz" -> "Herero"
        "ia" -> "Interlingua"
        "id" -> "Indonesian"
        "ie" -> "Interlingue"
        "ig" -> "Igbo"
        "ii" -> "Sichuan Yi"
        "ik" -> "Inupiaq"
        "io" -> "Ido"
        "is" -> "Icelandic"
        "it" -> "Italian"
        "iu" -> "Inuktitut"
        "ja" -> "Japanese"
        "jv" -> "Javanese"
        "ka" -> "Georgian"
        "kg" -> "Kongo"
        "ki" -> "Kikuyu"
        "kj" -> "Kwanyama"
        "kk" -> "Kazakh"
        "kl" -> "Kalaallisut"
        "km" -> "Khmer"
        "kn" -> "Kannada"
        "ko" -> "Korean"
        "kr" -> "Kanuri"
        "ks" -> "Kashmiri"
        "ku" -> "Kurdish"
        "kv" -> "Komi"
        "kw" -> "Cornish"
        "ky" -> "Kirghiz"
        "la" -> "Latin"
        "lb" -> "Luxembourgish"
        "lg" -> "Ganda"
        "li" -> "Limburgish"
        "ln" -> "Lingala"
        "lo" -> "Lao"
        "lt" -> "Lithuanian"
        "lu" -> "Luba-Katanga"
        "lv" -> "Latvian"
        "mg" -> "Malagasy"
        "mh" -> "Marshallese"
        "mi" -> "Maori"
        "mk" -> "Macedonian"
        "ml" -> "Malayalam"
        "mn" -> "Mongolian"
        "mr" -> "Marathi"
        "ms" -> "Malay"
        "mt" -> "Maltese"
        "my" -> "Burmese"
        "na" -> "Nauru"
        "nb" -> "Norwegian Bokmål"
        "nd" -> "North Ndebele"
        "ne" -> "Nepali"
        "ng" -> "Ndonga"
        "nl" -> "Dutch"
        "nn" -> "Norwegian Nynorsk"
        "no" -> "Norwegian"
        "nr" -> "South Ndebele"
        "nv" -> "Navajo"
        "ny" -> "Chichewa"
        "oc" -> "Occitan"
        "oj" -> "Ojibwa"
        "om" -> "Oromo"
        "or" -> "Oriya"
        "os" -> "Ossetian"
        "pa" -> "Punjabi"
        "pi" -> "Pali"
        "pl" -> "Polish"
        "ps" -> "Pashto"
        "pt" -> "Portuguese"
        "qu" -> "Quechua"
        "rm" -> "Romansh"
        "rn" -> "Rundi"
        "ro" -> "Romanian"
        "ru" -> "Russian"
        "rw" -> "Kinyarwanda"
        "sa" -> "Sanskrit"
        "sc" -> "Sardinian"
        "sd" -> "Sindhi"
        "se" -> "Northern Sami"
        "sg" -> "Sango"
        "si" -> "Sinhala"
        "sk" -> "Slovak"
        "sl" -> "Slovenian"
        "sm" -> "Samoan"
        "sn" -> "Shona"
        "so" -> "Somali"
        "sq" -> "Albanian"
        "sr" -> "Serbian"
        "ss" -> "Swati"
        "st" -> "Southern Sotho"
        "su" -> "Sundanese"
        "sv" -> "Swedish"
        "sw" -> "Swahili"
        "ta" -> "Tamil"
        "te" -> "Telugu"
        "tg" -> "Tajik"
        "th" -> "Thai"
        "ti" -> "Tigrinya"
        "tk" -> "Turkmen"
        "tl" -> "Tagalog"
        "tn" -> "Tswana"
        "to" -> "Tongan"
        "tr" -> "Turkish"
        "ts" -> "Tsonga"
        "tt" -> "Tatar"
        "tw" -> "Twi"
        "ty" -> "Tahitian"
        "ug" -> "Uighur"
        "uk" -> "Ukrainian"
        "ur" -> "Urdu"
        "uz" -> "Uzbek"
        "ve" -> "Venda"
        "vi" -> "Vietnamese"
        "vo" -> "Volapük"
        "wa" -> "Walloon"
        "wo" -> "Wolof"
        "xh" -> "Xhosa"
        "yi" -> "Yiddish"
        "yo" -> "Yoruba"
        "yue" -> "Cantonese"
        "za" -> "Zhuang"
        "zh" -> "Chinese"
        "zu" -> "Zulu"
        "true" -> "English"

        "ain" -> "Ainu" // indigenous language of Japan
        "brx" -> "Bodo"
        "ceb" -> "Cebuano" // spoken in the Philippines
        "chr" -> "Cherokee"
        "cic" -> "Chickasaw" // language of the Chickasaw people, United States
        "ckb" -> "Kurdish"
        "com" -> "Comanche"
        "doi" -> "Dogri"
        "emoji" -> "Emoji" // emoji symbols
        "fil" -> "Filipino"
        "fur" -> "Friulian"
        "gez" -> "Ge'ez" // ancient language of Ethiopia and Eritrea
        "gsw" -> "German"
        "iw" -> "Hebrew"
        "kab" -> "Kabyle" // a Berber language in Algeria
        "kok" -> "Konkani" // spoken in parts of India
        "ksh" -> "Colognian" // also known as Kölsch, spoken in Cologne, Germany
        "mai" -> "Hindi" //“Maithili” (spoken in parts of India and Nepal)
        "mni" -> "Hindi" // Manipuri” (also known as Meitei, spoken in the Indian state of Manipur)
        "nqo" -> "N’Ko" //(a script and language used by Mande speakers in West Africa)
        "nso" -> "NorthernSotho"
        "sat" -> "Santali"
        "scn" -> "Sicilian"
        "sco" -> "Scots"
        "syr" -> "Syriac"
        "zgh" -> "StandardTamazight" // Standard Moroccan Tamazight
        else -> {
                println("Unknown language: $language")
                language
        }
    }
}


