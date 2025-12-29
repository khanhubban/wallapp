//
//  Router.swift
//  WallApp
//

import Foundation
import SwiftUI
import WallApp

@MainActor
final class Router: ObservableObject {
    
    static let shared = Router()
    
    private let interopModules = InteropModulesIos.shared
    
    @Published var sheetArgument: ScreenArgument? = nil
    @Published var secondarySheetArgument: ScreenArgument? = nil
    @Published var fullscreenCoverArgument: ScreenArgument? = nil
    @Published var navPath = NavigationPath()
    @Published var showRewardAd: RewardAdArgument? = nil
    @Published var firstScreen: Screen? = nil
    @Published var globalOverlayState: GlobalOverlayViewState = .None()
    @Published var showManagerSubscriptionsSheet = false
    
    // Tracks the current ScreenArgument on the NavigationStack, does not include sheets
    private var currentScreenArgument: ScreenArgument?
    
    private var pendingScreenArgumentForNavPath: ScreenArgument?
    private var pendingScreenArgumentForSheet: ScreenArgument?
    private var pendingSheetArgument: ScreenArgument?
    private var pendingSecondarySheetArgument: ScreenArgument?
    
    private init() {
        firstScreen = interopModules.appViewModelFactory.createAppViewModel().initialScreen
        Task {
            for await screen in interopModules.navigationManager.navigationEvent {
                Log.d("[NativeNavigation] NavigationEvent received: \(screen), current path: \(navPath.count)")
                switch onEnum(of: screen) {
                case .noOpEvent(_):
                    let _ = 0 // do nothing
                case .popScreenEvent(_):
                    handlePopScreenEvent()
                case .toScreenEvent(let navigationEvent):
                    handleToScreenEvent(navigationEvent)
                }
            }
        }
        Task {
            for await globalOverlayState in interopModules.globalOverlayManager.globalOverlayViewState {
                self.globalOverlayState = globalOverlayState
            }
        }
    }
    
    private func isFullscreenCoverShowing() -> Bool {
        return self.fullscreenCoverArgument != nil
    }
    
    private func isShowingSheet() -> Bool {
        return self.sheetArgument != nil
    }
    
    private func isShowingSecondarySheet() -> Bool {
        return self.secondarySheetArgument != nil
    }
    
    private func dismissFullscreenCover() {
        self.fullscreenCoverArgument = nil
    }
    
    private func dismissSheet() {
        self.sheetArgument = nil
    }
    
    private func dismissSecondarySheet() {
        self.secondarySheetArgument = nil
    }
    
    private func handleToScreenEvent(_ navigationEvent: NavigationEvent.ToScreenEvent) {
        if interopModules.appConfig.enableIosNativeNavigation.value == false {
            return
        }
        guard let screenArgument = navigationEvent.argument as? ScreenArgument else {
            return
        }
        if screenArgument.screen.nativeNavigationSupported {
            if screenArgument is ScreenArgument.IndexScreenArgument {
                checkFirstRunAndNavigateToIndex()
            } else if let screenArgument = screenArgument as? ScreenArgument.PaywallScreenArgument {
                navigateToPaywall(screenArgument: screenArgument)
            } else if let screenArgument = screenArgument as? ScreenArgument.WallpaperSingleActionScreenArgument {
                showSecondarySheet(for: screenArgument)
            } else if let screenArgument = screenArgument as? ScreenArgument.CollectionActionScreenArgument, isShowingSheet() { //only show secondary sheet if primary one is on screen
                showSecondarySheet(for: screenArgument)
            } else if let screenArgument = screenArgument as? ScreenArgument.RewardAdInternalScreenArgument {
                fullscreenCoverArgument = screenArgument
            } else if screenArgument is ScreenArgument.ManageSubscriptionScreenArgument {
                showManagerSubscriptionsSheet.toggle()
            } else if screenArgument.screen.showAsModalSheet {
                showModalSheet(for: screenArgument)
            } else {
                checkSheetAndNavigate(to: screenArgument)
            }
        }
    }
    
    private func checkFirstRunAndNavigateToIndex() {
        Log.d("[NativeNavigation] checkFirstRunAndNavigateToIndex, firstScreen: \(String(describing: firstScreen))")
        if firstScreen is Screen.FirstRun {
            self.firstScreen = Screen.Index()
        }
    }
    
    private func checkSheetAndNavigate(to screenArgument: ScreenArgument) {
        // if we are currently showing a sheet, wait for it to be dismissed before navigating
        if isShowingSheet() && isShowingSecondarySheet() {
            Log.d("[NativeNavigation] dismissing both sheet and secondary sheet")
            dismissSecondarySheet()
            dismissSheet()
            self.pendingScreenArgumentForNavPath = screenArgument
        } else if isShowingSheet() {
            dismissSheet()
            self.pendingScreenArgumentForNavPath = screenArgument
        } else if isShowingSecondarySheet() {
            // this is not needed but adding for completeness as secondary sheet
            // will always be accompanied by primary which is handled in the first `if` case
            dismissSecondarySheet()
            self.pendingScreenArgumentForNavPath = screenArgument
        } else {
            self.navigateViaNavPath(to: screenArgument)
        }
    }
    
    private func showModalSheet(for screenArgument: ScreenArgument) {
        Log.d("[NativeNavigation] presenting sheet - \(screenArgument)")
        if isShowingSheet() {
            Log.d("[NativeNavigation] sheet already presented - \(String(describing: self.sheetArgument))")
            if sheetArgument?.id != screenArgument.id {
                self.pendingScreenArgumentForSheet = screenArgument
            }
            return
        }
        self.sheetArgument = screenArgument
    }
    
    private func showSecondarySheet(for screenArgument: ScreenArgument) {
        Log.d("[NativeNavigation] presenting secondary sheet - \(screenArgument)")
        if isShowingSecondarySheet() {
            Log.d("[NativeNavigation] secondary sheet already presented - \(String(describing: self.secondarySheetArgument))")
            return
        }
        self.secondarySheetArgument = screenArgument
    }
    
    private func handlePopScreenEvent() {
        if interopModules.appConfig.enableIosNativeNavigation.value == false {
            return
        }
        if isFullscreenCoverShowing() {
            dismissFullscreenCover()
        } else if isShowingSecondarySheet() {
            dismissSecondarySheet()
        } else if isShowingSheet() {
            dismissSheet()
        } else {
            self.navigateBackViaNavPath()
        }
    }
    
    private func navigateViaNavPath(to screenArgument: ScreenArgument) {
        if currentScreenArgument?.id == screenArgument.id {
            Log.d("[NativeNavigation] not navigating to same screen - \(screenArgument.id)")
            return
        }
        self.navPath.append(screenArgument)
        Log.d("[NativeNavigation] new path: \(self.navPath.count)")
    }
    
    private func navigateBackViaNavPath() {
        Log.d("[NativeNavigation] navigating back, navPathCount: \(self.navPath.count)")
        if navPath.count > 0 {
            self.navPath.removeLast()
            Log.d("[NativeNavigation] new path: \(self.navPath.count)")
        }
    }
    
    private func navigateToRootViaNavPath() {
        Log.d("[NativeNavigation] navigating to root, navPathCount: \(self.navPath.count)")
        navPath.removeLast(navPath.count)
    }
    
    func sheetDismissed() {
        Log.d("[NativeNavigation] sheet dismissed - \(String(describing: sheetArgument))")
        self.sheetArgument = nil
        if let pendingArgument = pendingScreenArgumentForNavPath {
            pendingScreenArgumentForNavPath = nil
            self.navigateViaNavPath(to: pendingArgument)
        } else if let pendingArgument = pendingScreenArgumentForSheet {
            pendingScreenArgumentForSheet = nil
            showModalSheet(for: pendingArgument)
        }
    }
    
    func secondarySheetDismissed() {
        Log.d("[NativeNavigation] secondary sheet dismissed - \(String(describing: secondarySheetArgument))")
        self.secondarySheetArgument = nil
    }
    
    func screenAppeared(_ screenArgument: ScreenArgument?) {
        Log.d("[NativeNavigation] screen appeared: \(String(describing: screenArgument))")
        
        // Only logging the screenArguments on the NavigationStack
        if screenArgument?.screen.showAsModalSheet ?? false {
            return
        }
        currentScreenArgument = screenArgument
    }
    
    private func navigateToPaywall(screenArgument: ScreenArgument.PaywallScreenArgument) {
        if isShowingSecondarySheet() && secondarySheetArgument?.persistentModalSheet == true {
            pendingSecondarySheetArgument = self.secondarySheetArgument
        }
        if isShowingSheet() && sheetArgument?.persistentModalSheet == true {
            pendingSheetArgument = self.sheetArgument
        }
        checkSheetAndNavigate(to: screenArgument)
    }
    
    func paywallDismissed() {
        Log.d("[NativeNavigation] paywall dismissed")
        if let pendingArgument = pendingSheetArgument {
            pendingSheetArgument = nil
            self.showModalSheet(for: pendingArgument)
        }
        if let pendingArgument = pendingSecondarySheetArgument {
            pendingSecondarySheetArgument = nil
            self.showSecondarySheet(for: pendingArgument)
        }
    }
    
    func currentScreen() -> Screen? {
        if isShowingSheet() && !(sheetArgument is ScreenArgument.CollectionActionScreenArgument || sheetArgument is ScreenArgument.WallpaperSingleActionScreenArgument) {
            return sheetArgument?.screen
        }
        // This is a workaround because search results aren't shown on their own screen
        // in Android and some checks rely on checking for ExploreScreen
        if currentScreenArgument is ScreenArgument.SearchResultsScreenArgument {
            return ScreenArgument.ExploreScreenArgument().screen
        }
        return currentScreenArgument?.screen
    }
}

extension ScreenArgument : @retroactive Identifiable {
    public var id: String {
        return self.id()
    }
}
