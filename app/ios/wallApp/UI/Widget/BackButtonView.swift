//
//  BackButtonView.swift
//  WallApp
//

import WallApp
import SwiftUI

struct BackButtonView: View {
    let render: RenderIos
    let theme: Theme
    let menuItem: MenuItem
    let topPadding: CGFloat
    
    var body: some View {
        let paddingDefault = render.defaultViewSpec.paddingDefault.toCGFloat()
        if let menuItemIcon = (menuItem as? MenuItem.MenuItemIcon) {
            let width = menuItemIcon.width?.toCGFloat()
            let height = menuItemIcon.height?.toCGFloat()
            HStack {
                ZStack {
                    Image(systemName: "chevron.backward")
                        .foregroundColor(menuItemIcon.tintColor?.toColor(themeColors: theme.themeColors))
                }
                .frame(width: width, height: height)
                .onTapGesture {
                    menuItemIcon.onClick?.invoke()
                }
                Spacer()
            }
            .zIndex(1.0)
            .frame(height: height)
            .padding(.leading, paddingDefault)
            .padding(.top, topPadding)
        } else if let menuItem = menuItem as? MenuItem.MenuItemButton {
            MenuItemUI(render: render, theme: theme, menuItem: menuItem)
        } else {
            EmptyView()

        }
    }
}
