//
//  TabsIndicatorView.swift
//  WallApp
//

import SwiftUI

struct TabsIndicatorView: View {
    let selectedTab: Int
    let screenWidth: CGFloat
    let tabWidth: CGFloat
    let indicatorHeight: CGFloat
    let indicatorWidth: CGFloat
    let indicatorColor: Color?
    
    var body: some View {
        Rectangle()
            .frame(width: indicatorWidth, height: indicatorHeight)
            .foregroundColor(indicatorColor)
            .offset(x: calculateOffset(), y: 0)
            .animation(.easeInOut(duration: 0.3), value: selectedTab)
    }
    
    func calculateOffset() -> CGFloat {
        let offset = (tabWidth * selectedTab.toCGFloat()) - (screenWidth / 2) + (tabWidth / 2)
        return offset
    }
}
