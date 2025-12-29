package wallapp.runmode

enum class RunMode {
    App,
    Test,
    Cron,
}

val RunMode.isNotApp: Boolean
    get() = this != RunMode.App
val RunMode.isApp: Boolean
    get() = this == RunMode.App
val RunMode.isCron: Boolean
    get() = this == RunMode.Cron
val RunMode.isTest: Boolean
    get() = this == RunMode.Test
