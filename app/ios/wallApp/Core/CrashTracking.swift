import WallApp
import FirebaseCrashlytics

class CrashTrackingSetEnabledActionIos: CrashTrackingSetEnabledAction {
 func invoke(enabled: Bool) {
     Crashlytics.crashlytics().setCrashlyticsCollectionEnabled(enabled)
 }
}
