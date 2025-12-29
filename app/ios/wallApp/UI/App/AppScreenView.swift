//
//  AppScreenView.swift
//  WallApp
//

import WallApp
import SwiftUI

struct AppScreenView: View {
    
    let viewState: ViewState?
    let theme: Theme
    let render: RenderIos
    var backgroundColor: Color? = nil
    
    var body: some View {
        let bgColor = backgroundColor ?? theme.themeColors.background.toColor()
        ZStack {
            if let viewState = viewState as? AccountViewState {
                AccountScreenView(viewState: viewState, theme: theme, render: render)
            } else if let viewState = viewState as? CollectionActionViewState {
                CollectionActionScreenView(viewState: viewState, theme: theme, render: render)
            } else if let viewState = viewState as? CollectionViewState {
                CollectionScreenView(viewState: viewState, theme: theme, render: render)
            } else if let viewState = viewState as? ExploreViewState {
                ExploreScreenView(viewState: viewState, theme: theme, render: render)
            } else if let viewState = viewState as? FirstRunViewState {
                FirstRunView(viewState: viewState, theme: theme, render: render)
            } else if let viewState = viewState as? FolderViewState {
                FolderScreenView(viewState: viewState, theme: theme, render: render)
            } else if let viewState = viewState as? HomeViewState {
                HomeScreenView(viewState: viewState, theme: theme, render: render)
            } else if let viewState = viewState as? ProfileViewState {
                ProfileTabView(viewState: viewState, theme: theme, render: render)
            } else if let viewState = viewState as? PaywallViewState {
                PaywallScreenView(viewState: viewState, theme: theme, render: render)
            } else if let viewState = viewState as? SearchResultsViewState {
                SearchResultsView(viewState: viewState, theme: theme, render: render)
            } else if let viewState = viewState as? ShowcaseAdsViewState {
                ShowcaseAdsScreenView(viewState: viewState, theme: theme, render: render)
            } else if let viewState = viewState as? WallpaperSingleActionViewState {
                WallpaperSingleActionScreenView(viewState: viewState, theme: theme, render: render)
            } else if let viewState = viewState as? WallpaperShowcaseViewState {
                WallpaperShowcaseScreenView(viewState: viewState, theme: theme, render: render)
            } else {
                EmptyView()
            }
        }
        .background(bgColor)
        .navigationBarBackButtonHidden()
    }
}
