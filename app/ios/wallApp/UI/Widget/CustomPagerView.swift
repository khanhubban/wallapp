//
//  CustomPagerView.swift
//  WallApp
//

import SwiftUI

struct CustomPagerView<Content: View>: View {
    @Binding var selectedTab: Int
    let pageCount: Int
    let content: Content
    
    @GestureState private var dragOffset: CGFloat = 0
    
    init(selectedTab: Binding<Int>, pageCount: Int, @ViewBuilder content: () -> Content) {
        self._selectedTab = selectedTab
        self.pageCount = pageCount
        self.content = content()
    }
    
    var body: some View {
        GeometryReader { geometry in
            let pageWidth = geometry.size.width
            let totalWidth = pageWidth * CGFloat(pageCount)
            let currentOffset = -CGFloat(selectedTab) * pageWidth
            let totalOffset = currentOffset + dragOffset
            let lowerBound = -(pageWidth * CGFloat(pageCount - 1) + pageWidth/3)
            let upperBound = pageWidth/3
            let clampedTotalOffset = totalOffset.clamped(to: lowerBound...upperBound)
            
            HStack(spacing: 0) {
                content
                    .frame(width: pageWidth)
            }
            .frame(width: totalWidth, alignment: .leading)
            .offset(x: clampedTotalOffset)
            .animation(.easeInOut, value: selectedTab)
            .animation(.easeInOut, value: totalOffset)
            .contentShape(Rectangle())
            .gesture(
                DragGesture()
                    .updating($dragOffset) { value, state, _ in
                        state = value.translation.width
                    }
                    .onEnded { value in
                        let threshold = pageWidth / 2
                        let dragDistance = -value.translation.width
                        let predictedEndOffset = dragDistance + -value.predictedEndTranslation.width
                        var newIndex = selectedTab
                        
                        if dragDistance > threshold || predictedEndOffset > threshold {
                            newIndex = min(selectedTab + 1, pageCount - 1)
                        } else if dragDistance < -threshold || predictedEndOffset < -threshold {
                            newIndex = max(selectedTab - 1, 0)
                        }
                        
                        selectedTab = newIndex
                    }
            )
        }
    }
}
