//
//  WallpaperSingleActionScreenView.swift
//  WallApp
//

import WallApp
import SwiftUI

struct WallpaperSingleActionScreenView: View {
    let viewState: WallpaperSingleActionViewState
    let theme: Theme
    let render: RenderIos
    
    var body: some View {
        switch onEnum(of: viewState) {
        case .loading(_):
            LoadingView(theme: theme, render: render)
        case .success(let data):
            WallpaperSingleActionSuccessView(viewState: data, render: render, theme: theme)
        }
    }
}

struct WallpaperSingleActionSuccessView: View {
    let viewState: WallpaperSingleActionViewState.Success
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
            
            Spacer().frame(height: paddingDefault)
            
            MenuItemUI(render: render, theme: theme, menuItem: viewState.actionButton2)
            
            if let actionButton3 = viewState.actionButton3 as? MenuItem.MenuItemButton {
                Spacer().frame(height: paddingDefault)
                
                let width = render.windowFrame.deviceWidth.toCGFloat() - (horizontalPadding * 2)
                let actionButtonWidthAdjusted = MenuItem.MenuItemButton(
                    button: actionButton3.button,
                    width: DpOptional(dp: Float(width)),
                    height: actionButton3.height
                )
                MenuItemUI(render: render, theme: theme, menuItem: actionButtonWidthAdjusted)
            }
            
            Spacer()
        }
        .frame(maxHeight: .infinity)
        .padding(.horizontal, horizontalPadding)
        .background(backgroundColor)
    }
}
