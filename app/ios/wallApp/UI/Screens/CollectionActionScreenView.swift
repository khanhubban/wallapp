//
//  CollectionActionScreenView.swift
//  WallApp
//

import WallApp
import SwiftUI

struct CollectionActionScreenView: View {
    let viewState: CollectionActionViewState
    let theme: Theme
    let render: RenderIos
    
    var body: some View {
        switch onEnum(of: viewState) {
        case .loading(_):
            LoadingView(theme: theme, render: render)
        case .success(let data):
            CollectionActionSuccessView(viewState: data, render: render, theme: theme)
        }
    }
}

struct CollectionActionSuccessView: View {
    let viewState: CollectionActionViewState.Success
    let render: RenderIos
    let theme: Theme
    
    @State private var expandedProgress: CGFloat = 1
    
    var body: some View {
        let viewSpec = viewState.viewSpec
        let paddingSmall = render.defaultViewSpec.paddingSmall.toCGFloat()
        let paddingDefault = render.defaultViewSpec.paddingDefault.toCGFloat()
        let horizontalPadding = viewSpec.horizontalPadding.toCGFloat()
        let height = viewSpec.height.toCGFloat()
        let backgroundColor = theme.themeColors.surface.toColor()
        VStack(spacing: 0) {
            Spacer().frame(height: paddingDefault)
            ToolbarView(
                render: render,
                theme: theme,
                toolbarViewState: viewState.toolbarViewState,
                scrollOffsetController: nil,
                renderStatusBar: false
            )
            Spacer().frame(height: paddingSmall)
            MenuItemUI(render: render, theme: theme, menuItem: viewState.actionButton1)
            
            if let actionButton2 = viewState.actionButton2 {
                Spacer().frame(height: paddingDefault)
                MenuItemUI(render: render, theme: theme, menuItem: actionButton2)
            }
            
            if let infoMessage = viewState.infoMessage {
                Spacer().frame(height: paddingDefault)
                MenuItemUI(render: render, theme: theme, menuItem: infoMessage)
            }
            
            Spacer()
        }
        .frame(maxHeight: .infinity)
        .padding(.horizontal, horizontalPadding)
        .background(backgroundColor)
    }
}
