//
//  Toolbar.swift
//  WallApp
//

import WallApp
import SwiftUI

struct ToolbarView: View {
    let render: RenderIos
    let theme: Theme
    let toolbarViewState: ViewState
    let scrollOffsetController: ScrollOffsetController?
    var renderStatusBar: Bool = false
    
    var body: some View {
        if let viewState = toolbarViewState as? ToolbarViewState {
            ToolbarContentView(
                render: render,
                theme: theme,
                viewState: viewState,
                renderStatusBar: renderStatusBar
            )
        } else if let toolbarViewState = toolbarViewState as? ExploreHeaderViewState {
            ExploreHeaderView(render: render, theme: theme, viewState: toolbarViewState, scrollOffsetController: scrollOffsetController)
        }
    }
}

struct ToolbarContentView: View {
    let render: RenderIos
    let theme: Theme
    let viewState: ToolbarViewState
    let renderStatusBar: Bool
    
    var body: some View {
        let navigationIcon = viewState.navigationIcon
        let statusBarHeight = render.windowFrame.statusBarHeight.toCGFloat()
        let title = viewState.title
        let height = viewState.height.toCGFloat()
        let containerColor = viewState.containerColorOverride.toColor(themeColors: theme.themeColors)
        let paddingLarge = render.defaultViewSpec.paddingLarge.toCGFloat()
        VStack(spacing: 0) {
            if renderStatusBar {
                StatusBarView(render: render, theme: theme, color: containerColor)
            }
            ZStack(alignment: .leading) {
                if let navigationIcon {
                    BackButtonView(render: render, theme: theme, menuItem: navigationIcon, topPadding: 0)
                        .zIndex(1.0)
                }
                HStack(spacing: 0) {
                    if let title {
                        MenuItemUI(render: render, theme: theme, menuItem: title)
                            .frame(maxWidth: .infinity)
                            .layoutPriority(1)
                    }
                }
                .frame(maxWidth: .infinity)
                .padding(.horizontal, paddingLarge)
            }
            .frame(height: height)
            .frame(maxWidth: .infinity)
        }
        .frame(height: height + (renderStatusBar ? statusBarHeight : 0))
        .frame(maxWidth: .infinity)
        .background(containerColor)
    }
}
