package wallapp.appicon

enum class AppIcon {
    Celebration,
    Citrus,
    Ivory,
    Midnight,
    PlusStandardDark,
    PlusStandardLight,
    PlusUnlimitedDark,
    PlusUnlimitedLight,
    Slate,

    ;

    companion object {
        val Default = Citrus

        val All = listOf(
            Citrus,
            PlusStandardLight,
            PlusStandardDark,
            PlusUnlimitedLight,
            PlusUnlimitedDark,
            Midnight,
            Ivory,
            Slate,
            Celebration,
        )
    }
}