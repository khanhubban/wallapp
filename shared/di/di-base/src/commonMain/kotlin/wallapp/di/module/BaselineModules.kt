package wallapp.di.module

val BaselineModules = listOf(
    AppModule,
    ContentModule,
    WallAppModule,
    SearchModule,
) + listOf(
    AccountModules,
    AdsModules,
    BillingModules,
    BuildConfigModules,
    LicensingModules,
    PlatformModules,
).map { it.modules }.flatten()