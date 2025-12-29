//
//  CapsuleTabs.swift
//  WallApp
//

import WallApp
import SwiftUI

struct CapsuleTabs: View {
    let render: RenderIos
    let theme: Theme
    @Binding var selectedTab: Int
    let tabs: [TabViewState]
    let containerWidth: CGFloat
    let containerHeight: CGFloat
    let containerColor: Color
    let indicatorColor: Color
    @Namespace private var animationNamespace

    var body: some View {
        HStack(spacing: 0) {
            ForEach(tabs.indices, id: \.self) { index in
                let isSelected = selectedTab == index
                let tab = tabs[index]
                let text = isSelected ? tab.headerSelected : tab.headerUnselected
                let containerColor = isSelected ? indicatorColor : containerColor
                MenuItemUI(render: render, theme: theme, menuItem: text)
                    .padding(.vertical, 8)
                    .frame(maxWidth: .infinity)
                    .background(
                        ZStack {
                            if selectedTab == index {
                                Capsule()
                                    .fill(containerColor)
                                    .matchedGeometryEffect(id: "capsuleBackground", in: animationNamespace)
                                    .padding(4)
                            } else {
                                Capsule()
                                    .fill(Color.clear)
                            }
                        }
                    )
                    .foregroundColor(.black)
                    .onTapGesture {
                        withAnimation(.easeInOut) {
                            selectedTab = index
                        }
                    }
            }
        }
        .frame(width: containerWidth, height: containerHeight)
        .background(
            Capsule()
                .fill(self.containerColor)
        )
        .padding(.horizontal)
        .animation(.easeInOut(duration: 0.3), value: selectedTab)
    }
}
