package wallapp.currency

import wallapp.country.Country


enum class Currency(val code: String) {
    AlgerianDinar("DZD"),
    AustralianDollar("AUD"),
    BangladeshiTaka("BDT"),
    BolivianBoliviano("BOB"),
    BrazilianReal("BRL"),
    BritishPound("GBP"),
    BulgarianLev("BGN"),
    CanadianDollar("CAD"),
    ChileanPeso("CLP"),
    ChineseYuan("CNY"),
    ColombianPeso("COP"),
    CostaRicanColon("CRC"),
    CroatianKuna("HRK"),
    CzechKoruna("CZK"),
    DanishKrone("DKK"),
    EgyptianPound("EGP"),
    Euro("EUR"),
    GeorgianLari("GEL"),
    GhanaianCedi("GHS"),
    HongKongDollar("HKD"),
    HungarianForint("HUF"),
    IndianRupee("INR"),
    IndonesianRupiah("IDR"),
    IraqiDinar("IQD"),
    IsraeliShekel("ILS"),
    JapaneseYen("JPY"),
    JordanianDinar("JOD"),
    KazakhstaniTenge("KZT"),
    KenyanShilling("KES"),
    MacanesePataca("MOP"),
    MalaysianRinggit("MYR"),
    MexicanPeso("MXN"),
    MoroccanDirham("MAD"),
    MyanmarKyat("MMK"),
    NewZealandDollar("NZD"),
    NigerianNaira("NGN"),
    NorwegianKrone("NOK"),
    PakistaniRupee("PKR"),
    ParaguayanGuarani("PYG"),
    PeruvianSol("PEN"),
    PhilippinePeso("PHP"),
    PolishZloty("PLN"),
    QatariRiyal("QAR"),
    RomanianLeu("RON"),
    RussianRuble("RUB"),
    SaudiRiyal("SAR"),
    SerbianDinar("RSD"),
    SingaporeDollar("SGD"),
    SouthAfricanRand("ZAR"),
    SouthKoreanWon("KRW"),
    SriLankanRupee("LKR"),
    SwedishKrona("SEK"),
    SwissFranc("CHF"),
    TaiwaneseDollar("TWD"),
    TanzanianShilling("TZS"),
    ThaiBaht("THB"),
    TurkishLira("TRY"),
    UkrainianHryvnia("UAH"),
    UnitedArabEmiratesDirham("AED"),
    UnitedStatesDollar("USD"),
    VietnameseDong("VND"),

    Unknown("<not_set>");
    
    companion object {
        private fun validCurrencies() = entries.filterNot { it == Unknown }

        fun findBy(currency: String): Currency? {
            val currencyUpper = currency.uppercase()
            return validCurrencies()
                .find { it.code == currencyUpper }
        }
    }
}

val Currency.isSingleCountryBillingCurrency: Boolean
    get() = Country.fromBillingCurrency(this)?.size == 1
