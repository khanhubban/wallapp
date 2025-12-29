package wallapp.di.module

import wallapp.di.Modules

actual val AccountModules = Modules(
    AccountModule,
    AccountPlatformModule,
)