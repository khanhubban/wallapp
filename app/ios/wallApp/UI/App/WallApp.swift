//
//  wallApp.swift
//  wallApp
//

import WallApp
import SwiftUI

@main
struct WallApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate
    
    @StateObject var router = Router.shared
    
    @State var fullScreenCover: ScreenArgument? = nil
    @State var theme: Theme = Themes.shared.Light
    @State private var hasViewAppeared = false
    
    @Environment(\.safeAreaInsets) private var safeAreaInsets
  
    private var googleSignInHelper: GoogleSignInHelper = GoogleSignInHelper()
    private var appleSignInHelper: AppleSignInHelper = AppleSignInHelper()
    private var googleAdInitializer = GoogleAdInitializer()
    
    private var interopModules: InteropModulesIos { InteropModulesIos.shared }
    private var deepLinkManager: DeepLinkManager { interopModules.deepLinkManager }
    private var render: RenderIos { interopModules.render }
    
    lazy var signInProviderController = interopModules.signInProviderController
    
    @StateObject private var sheetManager = SheetManager()
    
    var body: some Scene {
        let _ = updateFrame()
        WindowGroup {
            ZStack {
                if router.firstScreen is Screen.FirstRun {
                    NavigationStack(path: $router.navPath) {
                        ScreenView(for: ScreenArgument.FirstRunScreenArgument())
                            .navigationDestination(for: ScreenArgument.self) { screenArgument in
                                ScreenView(for: screenArgument)
                            }
                    }
                    .transition(.fade)
                    .sheet(item: $router.sheetArgument, onDismiss: { router.sheetDismissed() }) {
                        ContentViewCompose(screenArgument: $0)
                    }
                } else {
                    NavigationStack(path: $router.navPath) {
                        TopLevelView()
                            .navigationDestination(for: ScreenArgument.self) { screenArgument in
                                ScreenView(for: screenArgument)
                            }
                    }
                    .transition(.fade)
                }
            }
            .animation(.easeInOut, value: router.firstScreen)
            .onOpenURL{ url in
                let path = url.absoluteString
                Log.i("[AppDelegate] [UniversalLink] path = \(path)")
                deepLinkManager.handleDeepLink(url: path)
            }
            .onContinueUserActivity(NSUserActivityTypeBrowsingWeb) { userActivity in
                guard let url = userActivity.webpageURL else {
                    Log.e("[AppDelegate] [UniversalLink] onContinueUserActivity: No webpageURL found in userActivity")
                    return
                }
                let path = url.absoluteString
                Log.i("[AppDelegate] [UniversalLink] path = \(path)")
                deepLinkManager.handleDeepLink(url: path)
            }
            .overlay {
                if let globalOverlayState = router.globalOverlayState as? GlobalOverlayViewState.Data {
                    VStack(alignment: .center) {
                        if let image = globalOverlayState.image {
                            SwiftUIImage(
                                theme: theme,
                                image: image,
                                width: globalOverlayState.imageSize?.toCGFloat(),
                                height: globalOverlayState.imageSize?.toCGFloat()
                            )
                        }
                        if let message = globalOverlayState.message {
                            Spacer()
                                .frame(height: render.defaultViewSpec.paddingDefault.toCGFloat())
                            StyledTextView(text: message, theme: theme, colorOverride: Color.white)
                        }
                    }
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
                    .background(globalOverlayState.scrimColor.toColor(themeColors: theme.themeColors))
                    .ignoresSafeArea(.all, edges: .all)
                }
            }
            .task {
                //                try? await Task.sleep(nanoseconds: 20_000_000_000)
                await signOutIfFirstRun()
                
                // This needs to be initialised after attempting signout because it could lead to a false error screen as the network
                // data could be fetched as user is unavailable thus erroring out the FB storage call.
                interopModules.networkRefreshManager.initialize()
                
                await signInAnonymously()
            }
            .task {
                for await navigationEvent in interopModules.signInProviderController.signInNavigationEvents {
                    switch navigationEvent {
                    case .navigateToGoogleSignIn:
                        Log.d("[firebase] navigateToGoogleSignIn()")
                        await googleSignInHelper.performGoogleSignInFlow()
                    case .navigateToAppleSignIn:
                        Log.d("[firebase] navigateToAppleSignIn()")
                        appleSignInHelper.startSignInWithAppleFlow()
                    }
                }
            }
            .task {
                let themeManager = interopModules.themeManager
                for await theme in themeManager.theme {
                    self.theme = theme
                }
            }
            .onReceive(NotificationCenter.default.publisher(for: UIApplication.didBecomeActiveNotification)) { _ in
                AdTrackingTransparencyInitializer.shared.initialize()
                interopModules.appLifecycleManager.appCameToForeground()
            }
            .onReceive(NotificationCenter.default.publisher(for: UIApplication.willResignActiveNotification)) { _ in
                interopModules.appLifecycleManager.appWentToBackground()
            }
            .onAppear {
                guard !hasViewAppeared else { return }
                hasViewAppeared = true
                print("[Ad] onAppear")
                self.googleAdInitializer.initialize()
            }
            .onChange(of: router.fullscreenCoverArgument) { argument in
                if let argument {
                    let vc = ComposeContentViewController(render: render, theme: theme, screenArgument: argument, router: router)
                    vc.modalPresentationStyle = .fullScreen
                    UIApplication.shared.topViewController()?.present(vc, animated: true, completion: nil)
                } else {
                    UIApplication.shared.topViewController()?.dismiss(animated: true)
                }
            }
            .onChange(of: router.sheetArgument) { screenArgument in
                let isFullSheet = screenArgument is ScreenArgument.WallpaperShowcaseScreenArgument
                if screenArgument is ScreenArgument.CollectionActionScreenArgument || screenArgument is ScreenArgument.WallpaperSingleActionScreenArgument || isFullSheet {
                    let argument = screenArgument!
                    let viewModel = interopModules.appViewModelFactory.createViewModelFor(argument: argument) as! ViewModel
                    let viewModelStoreOwner = SharedViewModelStoreOwner<ViewModel>(viewModel)

                    let bottomSheetVC = BottomSheetHostingController(isFullSheet: isFullSheet, isSecondarySheet: false, viewModelStoreOwner: viewModelStoreOwner) {
                        AppRootView(screenArgument: argument, viewModelStoreOwner: viewModelStoreOwner)
                            .environmentObject(router)
                    }

                    bottomSheetVC.modalPresentationStyle = .pageSheet
                    UIApplication.shared.topViewController()?.present(bottomSheetVC, animated: true, completion: nil)

                    sheetManager.primarySheetViewController = bottomSheetVC
                } else if screenArgument == nil {
                    if sheetManager.secondarySheetViewController != nil {
                        // Dismiss the secondary sheet first
                        sheetManager.secondarySheetViewController?.dismiss(animated: true) {
                            Router.shared.secondarySheetDismissed()
                            sheetManager.primarySheetViewController?.dismiss(animated: true) {
                                Router.shared.sheetDismissed()
                            }
                            sheetManager.primarySheetViewController = nil
                        }
                        sheetManager.secondarySheetViewController = nil
                    } else {
                        sheetManager.primarySheetViewController?.dismiss(animated: true) {
                            Router.shared.sheetDismissed()
                        }
                        sheetManager.primarySheetViewController = nil
                    }
                }
            }
            .onChange(of: router.secondarySheetArgument) { screenArgument in
                if let argument = screenArgument as? ScreenArgument {
                    let viewModel = interopModules.appViewModelFactory.createViewModelFor(argument: argument) as! ViewModel
                    let viewModelStoreOwner = SharedViewModelStoreOwner<ViewModel>(viewModel)

                    let bottomSheetVC = BottomSheetHostingController(isFullSheet: false, isSecondarySheet: true, viewModelStoreOwner: viewModelStoreOwner) {
                        AppRootView(screenArgument: argument, viewModelStoreOwner: viewModelStoreOwner)
                            .environmentObject(router)
                    }

                    bottomSheetVC.modalPresentationStyle = .pageSheet
                    
                    // Present the secondary sheet from the primary sheet
                    sheetManager.primarySheetViewController?.present(bottomSheetVC, animated: true, completion: nil)

                    // Keep a reference to the secondary sheet
                    sheetManager.secondarySheetViewController = bottomSheetVC
                } else if screenArgument == nil {
                    // Dismiss the secondary sheet
                    sheetManager.secondarySheetViewController?.dismiss(animated: true) {
                        Router.shared.secondarySheetDismissed()
                    }
                    sheetManager.secondarySheetViewController = nil
                }
            }
            .manageSubscriptionsSheet(isPresented: $router.showManagerSubscriptionsSheet)
            .environmentObject(router)
        }
    }
    
    @ViewBuilder
    private func BottomSheetView(screenArgument: ScreenArgument) -> some View {
        if #available(iOS 16.4, *) {
            let cornerRadius: CGFloat = screenArgument is ScreenArgument.WallpaperShowcaseScreenArgument ? 8 : 28
            ScreenView(for: screenArgument)
                .if (screenArgument.sheetHeight != 0) {
                    $0.presentationDetents([.height(screenArgument.sheetHeight)])
                }
                .presentationCornerRadius(cornerRadius)
        } else {
            ScreenView(for: screenArgument)
                .if (screenArgument.sheetHeight != 0) {
                    $0.presentationDetents([.height(screenArgument.sheetHeight)])
                }
        }
    }
    
    @ViewBuilder
    private func ScreenView(for screenArgument: ScreenArgument) -> some View {
        let _ = Log.d("[NativeNavigation] destination is \(screenArgument)")
        if screenArgument is ScreenArgument.AccountScreenArgument
            || screenArgument is ScreenArgument.CollectionActionScreenArgument
            || screenArgument is ScreenArgument.CollectionIdScreenArgument
            || screenArgument is ScreenArgument.ExploreScreenArgument
            || screenArgument is ScreenArgument.FirstRunScreenArgument
            || screenArgument is ScreenArgument.FolderScreenArgument
            || screenArgument is ScreenArgument.PaywallScreenArgument
            || screenArgument is ScreenArgument.WallpaperSingleActionScreenArgument
            || screenArgument is ScreenArgument.WallpaperShowcaseScreenArgument {
            AppRootView(screenArgument: screenArgument)
        } else if let _ = screenArgument as? ScreenArgument.ShowcaseAdsScreenArgument {
            AppRootView(screenArgument: screenArgument)
        } else if let _ = screenArgument as? ScreenArgument.ShowcaseTypefaceIosScreenArgument {
            ShowcaseTypeface()
        } else if let screenArgument = screenArgument as? ScreenArgument.ArtistIdScreenArgument {
            ArtistScreenView(
                theme: theme,
                render: render,
                screenArgument: screenArgument
            )
        } else if interopModules.appConfig.enableNativeIosCollectionScreen.value == true && screenArgument is ScreenArgument.CollectionIdScreenArgument {
            AppRootView(screenArgument: screenArgument)
        } else {
            ContentViewComposeWithLoading(
                render: render,
                theme: theme,
                screenArgument: screenArgument
            )
        }
    }
    
    private func updateFrame() {
        if safeAreaInsets.top == 0 && safeAreaInsets.bottom == 0 {
            return
        }
        let scale = UIScreen.screenScale
        let width = UIScreen.screenWidth * scale
        let height = UIScreen.screenHeight * scale
        interopModules.windowFrameManager.setSize(deviceWidthPx: width.toInt32(), deviceHeightPx: height.toInt32())
        let topInset = safeAreaInsets.top * scale
        let bottomInset = safeAreaInsets.bottom * scale
        interopModules.windowFrameManager.setInsetsPx(statusBarHeight: topInset.toInt32(), navBarHeight: bottomInset.toInt32())
    }
    
    @ViewBuilder
    private func TopLevelView() -> some View {
        if interopModules.appConfig.enableNativeUiRendering.value == true {
            IndexView(render: render)
        } else {
            ContentViewCompose(screenArgument: nil)
        }
    }
    
    private func signOutIfFirstRun() async {
        let userDefaults = UserDefaults.standard
        if userDefaults.value(forKey: "isFirstRunDone") == nil {
            userDefaults.setValue(true, forKey: "isFirstRunDone")
            // signOut from Firebase Auth, #2189
            let signOutSuccess = try? await interopModules.accountManager.signOutCompletely()
            if signOutSuccess == true {
                Log.d("[firebase] signOut from Firebase Auth: success")
            } else {
                // This could happen if user was not signed in like on a fresh device
                Log.d("[firebase] signOut from Firebase Auth: failure")
            }
        } else {
            Log.d("[firebase] Subsequent run")
        }
    }
    
    private func signInAnonymously() async {
        for await _ in interopModules.networkRefreshTriggerBroadcaster.userSignInRefresh {
            let result = try? await interopModules.accountManager.signIn(signInMethod: SignInMethod.anonymous)
            switch onEnum(of: result) {
            case .error(let error):
                Log.d("[firebase] signInAnonymously(): failure - \(error)")
                interopModules.networkErrorBroadcaster.reportNetworkError(source: "signInAnon")
            case .success(_):
                Log.d("[firebase] signInAnonymously(): success")
            case .none:
                Log.d("[firebase] signInAnonymously(): none")
            }
        }
    }
}

class SheetManager: ObservableObject {
    var primarySheetViewController: UIViewController?
    var secondarySheetViewController: UIViewController?
}
