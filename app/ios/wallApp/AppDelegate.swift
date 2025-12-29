import WallApp
import SwiftUI
import FirebaseAuth
import FirebaseCore
import FirebaseCrashlytics
import FirebaseFirestore
import FirebaseMessaging
import FirebaseStorage
import GoogleMobileAds
import GoogleSignIn
import RevenueCat
import AppTrackingTransparency
import AdSupport


class AppDelegate: UIResponder, UIApplicationDelegate, MessagingDelegate, UNUserNotificationCenterDelegate {
    
    var interopModules: InteropModulesIos { return InteropModulesIos.shared }
    private var deepLinkManager: DeepLinkManager { interopModules.deepLinkManager }

    lazy var mainViewController: UIViewController = createViewController(screenArgument: nil)
    
    private var isDebug = false
    
    func createViewController(screenArgument: ScreenArgument?) -> UIViewController {
        Log.i("[AppDelegate] createViewController()")
        let viewController = AppUIViewController(screenArgument: screenArgument)
        
        viewController.view.backgroundColor = .white
        let window = UIWindow(frame: UIScreen.main.bounds)
        window.backgroundColor = .white
    //        window.rootViewController = viewController
    //        window.makeKeyAndVisible()
        
        return viewController
    }

    fileprivate func setupFirebaseForTesting() {
        Auth.auth().useEmulator(withHost: "localhost", port: 9099)
        Storage.storage().useEmulator(withHost: "localhost", port: 9199)
        let settings = Firestore.firestore().settings
        settings.host = "127.0.0.1:8080"
        settings.cacheSettings = MemoryCacheSettings()
        settings.isSSLEnabled = false
        Firestore.firestore().settings = settings
    }
    
    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil
    ) -> Bool {
        Log.i("[AppDelegate] [Lifecycle] didFinishLaunchingWithOptions()")
        checkArguments()
        
        FirebaseApp.configure()
        
#if DEBUG
        isDebug = true
#else
        isDebug = false
#endif
        
        if RUN_FIREBASE_ON_LOCAL_EMULATORS && isDebug {
            setupFirebaseForTesting()
        }

        initializeApp(
            allModules: AllModules,
            interopFactory: InteropFactoryIos(),
            interopBridge: InteropBridgeIos(),
            isDebug: isDebug
        )
        
        MvpApp().createAppUiState()
        
        Router.shared // initialize Router
        
        Messaging.messaging().delegate = self
        UNUserNotificationCenter.current().delegate = self

        // No longer required, as the app controls when the notification permission is requested.
        // See #1905.
//         UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .badge, .sound]) { success, _ in
//             guard success else {
//                 Log.e("[FCM] Failed to request authorization")
//                 return
//             }
//             Log.d("[FCM] Successfully requested authorization")
//         }
        
        application.registerForRemoteNotifications()
        
        Storage.storage().maxDownloadRetryTime = TimeInterval(FirebaseStorageDownloaderKt.MAX_OPERATION_RETRY_TIME_SECONDS)
        return true
    }
    
    func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String?) {
        Log.d("[FCM] Firebase registration token: \(fcmToken ?? "nil")")
        if let fcmToken {
            interopModules.cloudMessagingManager.onNewToken(token: fcmToken)
        } else {
            Log.e("[FCM] Firebase registration token is nil")
            Messaging.messaging().token { token, error in
                if let error = error {
                    Log.e("[FCM] Error fetching FCM registration token: \(error)")
                } else if let token = token {
                    Log.d("[FCM] FCM registration token: \(token)")
                    self.interopModules.cloudMessagingManager.onNewToken(token: token)
                }
            }
        }
    }
    
    func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        Log.d("[FCM] APNs device token: \(deviceToken)")
        Messaging.messaging().apnsToken = deviceToken
    }
    
    func application(
        _ application: UIApplication,
        open url: URL,
        options: [UIApplication.OpenURLOptionsKey : Any] = [:]
    ) -> Bool {
        Log.i("[AppDelegate] [Lifecycle] open url / options: \(url.absoluteString)")
        if GIDSignIn.sharedInstance.handle(url) {
            return true
        }

        return true
    }
    
    func application(
        _ application: UIApplication,
        continue userActivity: NSUserActivity,
        restorationHandler: @escaping ([UIUserActivityRestoring]?) -> Void
    ) -> Bool {
        Log.i("[AppDelegate] [Lifecycle] open userActivity")
        
        // Get URL components from the incoming user activity.
        guard userActivity.activityType == NSUserActivityTypeBrowsingWeb,
              let incomingURL = userActivity.webpageURL,
              let _ = NSURLComponents(url: incomingURL, resolvingAgainstBaseURL: true) else {
            return false
        }

        return false
    }
    
    func application(_ application: UIApplication, performFetchWithCompletionHandler completionHandler: @escaping (UIBackgroundFetchResult) -> Void) {
        Log.i("[AppDelegate] [Lifecycle] background fetch")
        // Since no actual fetching logic is implemented yet, we call the completion handler with .noData
        completionHandler(.noData)
    }
    
    func applicationWillResignActive(_ application: UIApplication) {
        Log.i("[AppDelegate] [Lifecycle] applicationWillResignActive")
    }

    func applicationDidEnterBackground(_ application: UIApplication) {
        Log.i("[AppDelegate] [Lifecycle] applicationDidEnterBackground()")
    }

    func applicationWillEnterForeground(_ application: UIApplication) {
        Log.i("[AppDelegate] [Lifecycle] applicationWillEnterForeground()")
    }

    func applicationDidBecomeActive(_ application: UIApplication) {
        Log.i("[AppDelegate] [Lifecycle] applicationDidBecomeActive()")
    }

    func applicationWillTerminate(_ application: UIApplication) {
        Log.i("[AppDelegate] [Lifecycle] applicationWillTerminate()")
    }
    
    func checkArguments() {
        let args = ProcessInfo.processInfo.arguments
        Log.i("Command-line arguments: \(args)")
        
        if args.contains("resetData") {
            resetAppDataUseWithCaution()
        }
    }
    
    func applicationDidReceiveMemoryWarning(_ application: UIApplication) {
        Log.i("[AppDelegate] [Memory] applicationDidReceiveMemoryWarning()")
        BaseCacheIos.shared.clearInMemoryCache()
    }
    
    func userNotificationCenter(_ center: UNUserNotificationCenter,
                                didReceive response: UNNotificationResponse) async {
        let userInfo = response.notification.request.content.userInfo
        Log.i("userNotificationCenter(_:didReceive:) userInfo: \(userInfo)")
        
        // From Firebase sample - this is only an issue if using `FirebaseAppDelegateProxyEnabled`.
        // With swizzling disabled you must let Messaging know about the message, for Analytics
        // Messaging.messaging().appDidReceiveMessage(userInfo)

        if let appUrl = userInfo["app_url"] as? String {
            print("App URL: \(appUrl)")
            deepLinkManager.handleDeepLink(url: appUrl)
        } else if let shortcutId = userInfo["shortcut_id"] as? String {
            print("shortcut_id: \(shortcutId)")
            deepLinkManager.handleAppShortcut(shortcutId: shortcutId)
        }
    }
}
