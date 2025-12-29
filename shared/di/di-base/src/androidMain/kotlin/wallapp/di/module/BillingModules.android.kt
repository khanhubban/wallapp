package wallapp.di.module

import wallapp.di.Modules

actual val BillingModules = Modules(
    BillingModule,
    BillingPlatformModule,
)