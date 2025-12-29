package wallapp.di.module

import org.koin.core.module.Module
import org.koin.dsl.module
import wallapp.di.FactoryIos
import wallapp.di.NamedScope
import wallapp.permission.SystemPermissionManager
import wallapp.permission.SystemPermissionManagerIos
import wallapp.system.SystemContext
import wallapp.system.SystemContextIos
import wallapp.system.navigation.SystemNavigator
import wallapp.system.navigation.SystemNavigatorIos
import wallapp.system.photo.status.SystemPhotoStatusChecker
import wallapp.system.photo.status.SystemPhotoStatusCheckerIos
import wallapp.system.share.SystemShareManager
import wallapp.system.share.SystemShareManagerIos
import wallapp.system.ui.controller.UiControllerManager
import wallapp.system.version.SystemVersion
import wallapp.system.version.SystemVersionIos
import wallapp.telephony.TelephonyManager
import wallapp.telephony.TelephonyManagerNoOp
import wallapp.time.TimeRepository
import wallapp.time.TimeRepositoryIos
import wallapp.time.TimeZoneRepository
import wallapp.time.TimeZoneRepositoryIos

@Suppress("RemoveExplicitTypeArguments")
val PlatformModule: Module = module {
    single<SystemContext> { SystemContextIos }
    single<SystemNavigator> { SystemNavigatorIos(get()) }
    single<SystemPermissionManager> { get<SystemPermissionManagerIos>() }
    single<SystemPermissionManagerIos> { SystemPermissionManagerIos(get(), get(NamedScope.CoroutineScopeMain), get(NamedScope.CoroutineScopeIo)) }
    single<SystemPhotoStatusChecker> { SystemPhotoStatusCheckerIos(get()) }
    single<SystemShareManager> { SystemShareManagerIos(get()) }
    single<SystemVersion> { SystemVersionIos }
    single<TelephonyManager> { TelephonyManagerNoOp }
    single<TimeRepository> { TimeRepositoryIos() }
    single<TimeZoneRepository> { TimeZoneRepositoryIos }
    single<UiControllerManager> { FactoryIos.uiControllerManager() }
}
