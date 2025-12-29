//
//  OffsetModifier.swift
//  WallApp
//

import SwiftUI

struct OffsetModifier: ViewModifier {
    
    @Binding var offset: CGFloat
    
    @Binding var maxY: CGFloat
    
    @State var startOffset: CGFloat = 0
    
    func body(content: Content) -> some View {
        content
            .overlay(
                GeometryReader { proxy in
                    Color.clear
                        .preference(key: OffsetKey.self, value: proxy.frame(in: .named("scroll")).midY)
                        .onAppear {
                            maxY = proxy.frame(in: .named("scroll")).maxY - proxy.frame(in: .named("scroll")).minY
                        }
                }
            )
            .onPreferenceChange(OffsetKey.self) { offset in
                if startOffset == 0 {
                    startOffset = offset
                }
                
                if (offset - startOffset) < startOffset {
                    self.offset = offset - startOffset
                }
            }
    }
}

struct OffsetKey: PreferenceKey {
    static var defaultValue: CGFloat = 0
    
    static func reduce(value: inout CGFloat, nextValue: () -> CGFloat) {
        value = nextValue()
    }
}
