package wallapp.di.module

import org.koin.core.module.Module
import org.koin.dsl.module
import wallapp.permission.SystemPermissionManager
import wallapp.permission.SystemPermissionManagerNoOp
import wallapp.system.SystemContext
import wallapp.system.navigation.SystemNavigator
import wallapp.system.navigation.SystemNavigatorNoOp
import wallapp.system.photo.status.SystemPhotoStatusChecker
import wallapp.system.photo.status.SystemPhotoStatusCheckerNoOp
import wallapp.system.platform.SystemContextDesktop
import wallapp.system.share.SystemShareManager
import wallapp.system.share.SystemShareManagerNoOp
import wallapp.system.ui.controller.UiControllerManager
import wallapp.system.ui.controller.UiControllerManagerNoOp
import wallapp.system.version.SystemVersion
import wallapp.system.version.SystemVersionDesktop
import wallapp.telephony.TelephonyManager
import wallapp.telephony.TelephonyManagerNoOp
import wallapp.time.TimeRepository
import wallapp.time.TimeRepositoryDesktop
import wallapp.time.TimeZoneRepository
import wallapp.time.TimeZoneRepositoryDesktop

@Suppress("RemoveExplicitTypeArguments")
val PlatformModule: Module = module {
    single<SystemContext> { SystemContextDesktop }
    single<SystemNavigator> { SystemNavigatorNoOp }
    single<SystemPermissionManager> { SystemPermissionManagerNoOp }
    single<SystemPhotoStatusChecker> { SystemPhotoStatusCheckerNoOp }
    single<SystemShareManager> { SystemShareManagerNoOp }
    single<SystemVersion> { SystemVersionDesktop }
    single<TelephonyManager> { TelephonyManagerNoOp }
    single<TimeRepository> { TimeRepositoryDesktop() }
    single<TimeZoneRepository> { TimeZoneRepositoryDesktop }
    single<UiControllerManager> { UiControllerManagerNoOp }
}