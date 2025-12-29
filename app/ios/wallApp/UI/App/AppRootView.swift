//
//  AppRoot.swift
//  WallApp
//

import WallApp
import SwiftUI

struct AppRootView: View {
    
    let screenArgument: ScreenArgument
    
    @EnvironmentObject var router: Router
    
    private let interopModules = InteropModulesIos.shared
    private let render = InteropModulesIos.shared.render
    
    @StateObject var viewModelStoreOwner: SharedViewModelStoreOwner<ViewModel>
    
    @State var viewState: ViewState? = nil
    @State var theme: Theme = InteropModulesIos.shared.themeManager.theme.value
    
    @Environment(\.colorScheme) var colorScheme
    var systemTheme: SystemThemeIos { return InteropModulesIos.shared.systemTheme }
    
    init(screenArgument: ScreenArgument, viewModelStoreOwner: SharedViewModelStoreOwner<ViewModel>) {
        self.screenArgument = screenArgument
        _viewModelStoreOwner = StateObject(wrappedValue: viewModelStoreOwner)
    }
    
    init(screenArgument: ScreenArgument) {
        self.screenArgument = screenArgument
        _viewModelStoreOwner = StateObject(
            wrappedValue: SharedViewModelStoreOwner<ViewModel>(
                createViewModel(for: screenArgument, with: InteropModulesIos.shared.appViewModelFactory) as! ViewModel
            )
        )
    }
    
    var body: some View {
        AppScreenView(viewState: viewState, theme: theme, render: render)
            .ignoresSafeArea(.all, edges: .all)
            .task {
                if let viewModel = viewModelStoreOwner.instance as? ScreenViewStateProvider {
                    for await viewState in viewModel.viewState {
                        self.viewState = viewState
                    }
                }
            }
            .task {
                let themeManager = interopModules.themeManager
                for await theme in themeManager.theme {
                    self.theme = theme
                }
            }
            .onAppear {
                router.screenAppeared(screenArgument)
            }.onChange(of: colorScheme) { newValue in
                newValue == .dark ? systemTheme.setIsDarkTheme() : systemTheme.setIsLightTheme()
            }
    }
}

func createViewModel(for screenArgument: ScreenArgument, with appViewModelFactory: AppViewModelFactory) -> ScreenViewStateProvider? {
    if let _ = screenArgument as? ScreenArgument.ExploreScreenArgument {
        return appViewModelFactory.createExploreViewModel()
    }
    if let _ = screenArgument as? ScreenArgument.HomeScreenArgument {
        return appViewModelFactory.createHomeViewModel()
    }
    if screenArgument is ScreenArgument.SearchResultsScreenArgument {
        return appViewModelFactory.createSearchResultsViewModel()
    }
    if let screenArgument = screenArgument as? ScreenArgument.ShowcaseAdsScreenArgument {
        return appViewModelFactory.createShowcaseAdsViewModel(argument: screenArgument)
    }
    return appViewModelFactory.createViewModelFor(argument: screenArgument)
}
