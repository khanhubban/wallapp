//
//  IndexView.swift
//  WallApp
//

import WallApp
import SwiftUI
import UIKit
import Combine

struct IndexView: View {

    @Environment(\.colorScheme) var colorScheme
    
    let render: RenderIos
    
    @State var currentTab: IndexTab = IndexTab.explore
    @State var viewState: IndexViewState? = nil
    @State var theme: Theme = InteropModulesIos.shared.themeManager.theme.value
    @State var showSearchOverlay: Float = 0
    @State var overlayViewState: SearchInputViewState? = nil
    @State var showSearchResults: Bool = false
    
    @StateObject var navBarScrollOffsetController = ScrollOffsetController(scrollScaler: 1.5)
    
    private var interopModules = InteropModulesIos.shared
    private var systemTheme: SystemThemeIos { return interopModules.systemTheme }
    private var indexViewModel: IndexViewModel {
        interopModules.appViewModelFactory.createAppViewModel().indexViewModel
    }
    
    init(render: RenderIos) {
        self.render = render
        UITabBar.appearance().isHidden = true
    }
    
    var body: some View {
        let systemNavBarHeight = render.windowFrame.navigationBarHeight.toCGFloat() / 2
        let overlayViewStateData = overlayViewState as? SearchInputViewState.Data
        ZStack {
            if let viewState = viewState as? IndexViewState.Success {
                let navTabContainerHeight = viewState.navigationBar.viewSpec.navBarItemsHeight.toCGFloat() + systemNavBarHeight
                let _ = navBarScrollOffsetController.update(maxOffset: navTabContainerHeight)
                UIKitTabView(
                    currentTab: $currentTab,
                    homeViewController: {
                        if viewState.currentScreenViewState is HomeOnboardingViewState {
                            return HomeOnboardingViewController(theme: theme, render: render, navBarOffsetController: navBarScrollOffsetController)
                        } else if viewState.currentScreenViewState is HomeViewState {
                            let vc = UIHostingController(rootView: AppRootView(screenArgument: ScreenArgument.HomeScreenArgument()))
                            vc.view.backgroundColor = theme.themeColors.background.uiColor
                            return vc
                        } else {
                            let vc = UIHostingController(rootView: LoadingView(theme: theme, render: render))
                            vc.view.backgroundColor = theme.themeColors.background.uiColor
                            return vc
                        }
                    },
                    exploreViewController: {
                        if showSearchResults {
                            let vc = UIHostingController(
                                rootView: SearchResultsScreenView(
                                    render: render,
                                    theme: theme,
                                    viewModel: indexViewModel.searchResultsViewModel
                                )
                            )
                            vc.view.backgroundColor = theme.themeColors.background.uiColor
                            return vc
                        } else {
                            return ExploreViewController(theme: theme, render: render, navBarOffsetController: navBarScrollOffsetController)
                        }
                    },
                    profileView: {
                        let vc = UIHostingController(
                            rootView: ProfileScreenView(
                                viewModel: indexViewModel.profileViewModel,
                                render: render,
                                navBarOffsetController: navBarScrollOffsetController
                            )
                        )
                        vc.view.backgroundColor = theme.themeColors.background.uiColor
                        return vc
                    },
                    viewState: viewState,
                    showSearchResults: showSearchResults
                )
                .if (overlayViewStateData != nil) {
                    $0.overlay(
                        SearchInputView(
                            render: render,
                            theme: theme,
                            viewState: overlayViewStateData!,
                            isShowSheet: $showSearchOverlay,
                            onSheetToggled: { visible in
                                if visible {
                                    navBarScrollOffsetController.setToHide()
                                } else {
                                    navBarScrollOffsetController.setToShow()
                                    viewState.onOverlayDismissed()
                                }
                            },
                            sheetVisibilityProgress: { progress in
                                viewState.onOverlayVisibilityProgress(KotlinFloat(value: progress))
                            }
                        )
                    )
                }
                IndexNavigationTabBarView(
                    viewState: viewState.navigationBar,
                    systemNavigationBarHeight: systemNavBarHeight,
                    theme: theme
                )
                .offset(y: -navBarScrollOffsetController.offset)
                .animation(.spring(duration: 0.2), value: navBarScrollOffsetController.offset)
            }
        }
        .ignoresSafeArea(.all, edges: .all)
        .task {
            for await currentTab in indexViewModel.currentTab {
                self.currentTab = currentTab
            }
        }
        .task {
            for await viewState in indexViewModel.viewState {
                self.viewState = viewState
                if let viewState = viewState as? IndexViewState.Success {
                    self.overlayViewState = viewState.overlayScreen as? SearchInputViewState
                    if (viewState.overlayVisible && showSearchOverlay == 0) || (!viewState.overlayVisible && showSearchOverlay == 1) {
                        withAnimation {
                            showSearchOverlay = viewState.overlayVisible ? 1 : 0
                        }
                    }
                }
            }
        }
        .task {
            for await showSearchResults in indexViewModel.showSearchResults {
                self.showSearchResults = showSearchResults == true
            }
        }
        .task {
            for await theme in interopModules.themeManager.theme {
                self.theme = theme
            }
        }
        .onAppear {
            if colorScheme == .dark {
                systemTheme.setIsDarkTheme()
            } else {
                systemTheme.setIsLightTheme()
            }
        }
        .environmentObject(navBarScrollOffsetController)
    }
}

struct IndexNavigationTabBarView: View {
    
    let viewState: NavigationBarViewState
    let systemNavigationBarHeight: CGFloat
    let theme: Theme
    
    var body: some View {
        let edgePadding = viewState.viewSpec.edgePadding.toCGFloat() * 2
        VStack(spacing: 0) {
            Spacer()
            
            HStack(spacing: 0) {
                Spacer().frame(width: edgePadding)
                NavigationTabBarView(navigationBarItems: viewState.items,
                                     navigationBarItemHeight: viewState.viewSpec.navBarItemsHeight.toCGFloat(),
                                     theme: theme)
                Spacer().frame(width: edgePadding)
            }.background(viewState.containerColor?.color.toColor())
            
            viewState.containerColor?.color.toColor()
                .frame(height: systemNavigationBarHeight)
        }
    }
}


struct UIKitTabView: UIViewControllerRepresentable {
    @Binding var currentTab: IndexTab
    var homeViewController: () -> UIViewController
    var exploreViewController: () -> UIViewController
    var profileView: () -> UIViewController
    var viewState: IndexViewState.Success
    var showSearchResults: Bool
    
    class Coordinator {
        var lastKnownHomeSubScreen: HomeSubScreen = .none
        var lastKnownShowSearchResults: Bool = false
        
        init(initialHomeSubScreen: HomeSubScreen, initialShowSearchResults: Bool) {
            lastKnownHomeSubScreen = initialHomeSubScreen
            lastKnownShowSearchResults = initialShowSearchResults
        }
    }
    
    func makeCoordinator() -> Coordinator {
        return Coordinator(initialHomeSubScreen: getSubScreen(for: viewState), initialShowSearchResults: showSearchResults)
    }

    func makeUIViewController(context: Context) -> UITabBarController {
        let tabBarController = UITabBarController()
        
        let homeVC = homeViewController()
        let coordinator = context.coordinator
        coordinator.lastKnownHomeSubScreen = getSubScreen(for: viewState)
        let homeNavVC = UINavigationController(rootViewController: homeVC)
        homeNavVC.isNavigationBarHidden = true
        homeVC.tabBarItem = UITabBarItem(title: "Home", image: UIImage(systemName: "house"), tag: 0)

        let exploreVC = exploreViewController()
        let exploreNavVC = UINavigationController(rootViewController: exploreVC)
        exploreNavVC.isNavigationBarHidden = true
        exploreVC.tabBarItem = UITabBarItem(title: "Explore", image: UIImage(systemName: "magnifyingglass"), tag: 1)

        let profileVC = profileView()
        let profileNavVC = UINavigationController(rootViewController: profileVC)
        profileNavVC.isNavigationBarHidden = true
        profileVC.tabBarItem = UITabBarItem(title: "Profile", image: UIImage(systemName: "person"), tag: 2)
        
        tabBarController.viewControllers = [homeNavVC, exploreNavVC, profileNavVC]
        tabBarController.selectedIndex = currentTab.rawValue
        tabBarController.tabBar.isHidden = true

        return tabBarController
    }

    func updateUIViewController(_ uiViewController: UITabBarController, context: Context) {
        let coordinator = context.coordinator
        if currentTab == .home, (viewState.currentScreenViewState is HomeViewState || viewState.currentScreenViewState is HomeOnboardingViewState) {
            let newSubScreen = getSubScreen(for: viewState)
            if coordinator.lastKnownHomeSubScreen != newSubScreen {
                coordinator.lastKnownHomeSubScreen = newSubScreen
                if let homeNavVC = uiViewController.viewControllers?[0] as? UINavigationController {
                    let animator = AnimationUINavigationControllerDelegate()
                    animator.setFadeAnimation(homeNavVC)
                    homeNavVC.setViewControllers([homeViewController()], animated: false)
                }
            }
        }
        if currentTab == .explore, coordinator.lastKnownShowSearchResults != showSearchResults {
            coordinator.lastKnownShowSearchResults = showSearchResults
            if let exploreNavVC = uiViewController.viewControllers?[1] as? UINavigationController {
                let animator = AnimationUINavigationControllerDelegate()
                animator.setFadeAnimation(exploreNavVC)
                if showSearchResults {
                    let newVC = exploreViewController()
                    exploreNavVC.pushViewController(newVC, animated: true)
                } else {
                    exploreNavVC.popToRootViewController(animated: true)
                }
            }
        }
        uiViewController.selectedIndex = currentTab.rawValue
        uiViewController.tabBar.isHidden = true
    }
    
    private func getSubScreen(for viewState: IndexViewState.Success) -> HomeSubScreen {
        if viewState.currentScreenViewState is HomeViewState {
            return .home
        } else if viewState.currentScreenViewState is HomeOnboardingViewState {
            return .onboarding
        } else {
            return .none
        }
    }
}

extension IndexTab {
    var rawValue: Int {
        switch self {
        case .home: return 0
        case .explore: return 1
        case .account: return 2
        }
    }
    
    init(rawValue: Int) {
        switch rawValue {
        case 0: self = .home
        case 1: self = .explore
        case 2: self = .account
        default: self = .home
        }
    }
}

enum HomeSubScreen {
    case home
    case onboarding
    case none
}
