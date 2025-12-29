@file:Suppress("RemoveExplicitTypeArguments")

package wallapp.di.module

import android.app.UiModeManager
import android.app.job.JobScheduler
import android.content.pm.LauncherApps
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.hardware.SensorManager
import android.net.ConnectivityManager
import android.os.Handler
import android.os.UserManager
import android.view.WindowManager
import androidx.core.app.NotificationManagerCompat
import org.koin.core.module.Module
import org.koin.dsl.module
import wallapp.di.FactoryAndroid
import wallapp.di.Lazy
import wallapp.di.NamedScope
import wallapp.di.PlatformInjectionFactory
import wallapp.di.getLazy
import wallapp.environment.Environment
import wallapp.environment.EnvironmentSystem
import wallapp.image.prefetch.ImagePrefetcherCoilAndroid
import wallapp.keyguard.KeyguardManager
import wallapp.keyguard.KeyguardManagerSystem
import wallapp.permission.SystemPermissionManager
import wallapp.permission.SystemPermissionManagerAndroid
import wallapp.power.PowerManager
import wallapp.power.PowerManagerSystem
import wallapp.system.SystemContext
import wallapp.system.SystemContextAndroid
import wallapp.system.navigation.SystemNavigator
import wallapp.system.navigation.SystemNavigatorAndroid
import wallapp.system.photo.status.SystemPhotoStatusChecker
import wallapp.system.photo.status.SystemPhotoStatusCheckerAndroid
import wallapp.system.photo.viewer.SystemPhotoViewerAndroid
import wallapp.system.settings.SystemSettingsRepository
import wallapp.system.settings.SystemSettingsRepositorySystem
import wallapp.system.share.SystemShareManager
import wallapp.system.share.SystemShareManagerAndroid
import wallapp.system.ui.controller.UiControllerManager
import wallapp.system.ui.controller.UiControllerManagerAndroid
import wallapp.system.version.SystemVersion
import wallapp.system.version.SystemVersionAndroid
import wallapp.telephony.TelephonyManager
import wallapp.telephony.TelephonyManagerAndroid
import wallapp.time.TimeRepository
import wallapp.time.TimeRepositoryAndroid
import wallapp.time.TimeZoneRepository
import wallapp.time.TimeZoneRepositoryAndroid
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService

@Suppress("RemoveExplicitTypeArguments")
val PlatformModule: Module = module {
    single<AssetManager> { PlatformInjectionFactory.assetManager(get()) }
    single<ConnectivityManager> { PlatformInjectionFactory.connectivityManager(get()) }
    single<Environment> { EnvironmentSystem(get()) }
    single<Executor> { FactoryAndroid.executor() }
    single<ExecutorService> { FactoryAndroid.executorService() }
    single<Handler>(NamedScope.MainThreadHandler) { PlatformInjectionFactory.provideMainThreadHandler() }
    single<ImagePrefetcherCoilAndroid> { ImagePrefetcherCoilAndroid(get(), get()) }
    single<JobScheduler> { PlatformInjectionFactory.jobsSchedulerService(get()) }
    single<KeyguardManager> { KeyguardManagerSystem(get()) }
    single<LauncherApps> { PlatformInjectionFactory.launcherApps(get()) }
    single<Lazy<TimeRepository>>(NamedScope.LazyTimeRepository) { getLazy() }
    single<NotificationManagerCompat> { PlatformInjectionFactory.notificationManagerCompat(get()) }
    single<PackageManager> { PlatformInjectionFactory.packageManager(get()) }
    single<PowerManager> { PowerManagerSystem(get()) }
    single<SensorManager> { PlatformInjectionFactory.sensorManager(get()) }
    single<SystemContext> { SystemContextAndroid(get()) }
    single<SystemNavigator> { SystemNavigatorAndroid(get(), get(), get(), get(), get(), get(), get()) }
    single<SystemPermissionManager> { get<SystemPermissionManagerAndroid>() }
    single<SystemPermissionManagerAndroid> { SystemPermissionManagerAndroid(get(), get(), get(NamedScope.CoroutineScopeMain), get(NamedScope.CoroutineScopeIo)) }
    single<SystemPhotoStatusChecker> { SystemPhotoStatusCheckerAndroid(get(), get()) }
    single<SystemPhotoViewerAndroid> { SystemPhotoViewerAndroid(get()) }
    single<SystemSettingsRepository> { SystemSettingsRepositorySystem(get()) }
    single<SystemShareManager> { SystemShareManagerAndroid(get()) }
    single<SystemVersion> { SystemVersionAndroid }
    single<TelephonyManager> { TelephonyManagerAndroid(get()) }
    single<TimeRepository> { TimeRepositoryAndroid(get()) }
    single<TimeZoneRepository> { TimeZoneRepositoryAndroid }
    single<UiControllerManager> { get(UiControllerManagerAndroid::class) }
    single<UiControllerManagerAndroid> { UiControllerManagerAndroid() }
    single<UiModeManager> { PlatformInjectionFactory.uiModeManager(get()) }
    single<UserManager> { PlatformInjectionFactory.userManager(get()) }
    single<WindowManager> { PlatformInjectionFactory.windowManager(get()) }
}