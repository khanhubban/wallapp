//
//  NavigationTabBarView.swift
//  WallApp
//

import WallApp
import SwiftUI

struct NavigationTabBarView: View {
    
    let navigationBarItems: [NavBarItemState]
    let navigationBarItemHeight: CGFloat
    let theme: Theme
    
    var body: some View {
        HStack {
            ForEach(navigationBarItems, id: \.text) { item in
                NavBarItemView(state: item, theme: theme)
                if item != navigationBarItems.last {
                    Spacer()
                }
            }
        }
        .frame(maxWidth: .infinity)
        .frame(height: navigationBarItemHeight)
    }
}
