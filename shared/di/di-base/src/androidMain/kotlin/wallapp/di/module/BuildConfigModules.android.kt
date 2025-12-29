package wallapp.di.module

import wallapp.di.Modules


actual val BuildConfigModules = Modules(
    BuildConfigModule,
    BuildConfigPlatformModule,
)