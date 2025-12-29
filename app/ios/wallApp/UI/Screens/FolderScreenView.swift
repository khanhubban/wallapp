//
//  FolderScreenView.swift
//  WallApp
//

import WallApp
import SwiftUI

struct FolderScreenView: View {
    let viewState: FolderViewState
    let theme: Theme
    let render: RenderIos
    
    var body: some View {
        ZStack {
            switch(onEnum(of: viewState)) {
            case .loading(_):
                LoadingView(theme: theme, render: render)
            case .success(let data):
                FolderSuccessView(viewState: data, theme: theme, render: render)
            }
        }
        .animation(.easeInOut, value: viewState)
    }
}

struct FolderSuccessView: View {
    let viewState: FolderViewState.Success
    let theme: Theme
    let render: RenderIos
    
    var body: some View {
        VStack(spacing: 0) {
            StatusBarView(render: render, theme: theme)
            FolderToolbarView(
                viewState: viewState.toolbarViewState,
                viewSpec: viewState.viewSpec.toolbarViewSpec,
                theme: theme,
                render: render
            )
            FeedGrid(
                render: render,
                theme: theme,
                feedViewState: viewState.feedViewState
            )
        }
        .frame(maxHeight: .infinity)
    }
}

struct FolderToolbarView: View {
    let viewState: FolderToolbarViewState
    let viewSpec: FolderToolbarViewSpec
    let theme: Theme
    let render: RenderIos
    
    private var themeManager: ThemeManager {
        InteropModulesIos.shared.themeManager
    }
    private var actualTheme: Theme {
        theme.isLight ? themeManager.darkTheme : themeManager.lightTheme
    }
    
    var body: some View {
        let backgroundColor = actualTheme.themeColors.background.toColor()
        let paddingDefault = render.defaultViewSpec.paddingDefault.toCGFloat()
        let minToolbarHeight = viewSpec.minToolbarHeight.toCGFloat()
        let maxToolbarHeight = viewSpec.maxToolbarHeight.toCGFloat()
        let statusBarHeight = render.windowFrame.statusBarHeight.toCGFloat()
        ZStack(alignment: .top) {
            if let navigationIcon = viewState.toolbarViewState.navigationIcon {
                BackButtonView(
                    render: render,
                    theme: actualTheme,
                    menuItem: navigationIcon,
                    topPadding: paddingDefault
                )
            }
            
            VStack {
                Spacer()
                StyledTextView(text: viewState.name, theme: actualTheme)
                Spacer()
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
        }
        .frame(height: maxToolbarHeight)
        .background(backgroundColor)
    }
}
