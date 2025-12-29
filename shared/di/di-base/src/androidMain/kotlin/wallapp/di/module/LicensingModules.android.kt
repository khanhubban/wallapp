package wallapp.di.module

import wallapp.di.Modules

actual val LicensingModules = Modules(
    LicensingModule,
    LicensingPlatformModule,
)