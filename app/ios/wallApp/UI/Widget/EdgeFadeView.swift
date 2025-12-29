//
//  EdgeFadeView.swift
//  WallApp
//

import WallApp
import SwiftUI

struct EdgeFadeView: View {
    let edgeFade: EdgeFadeViewState
    let theme: Theme
    let render: RenderIos
    
    var body: some View {
        let edgeImage = edgeFade.image
        let edgeFadeViewSpec = edgeFade.viewSpec
        let tintColor = edgeFadeViewSpec.tintColorToken.toColor(themeColors: theme.themeColors)
        
        let startPadding = edgeFadeViewSpec.startPadding.toCGFloat()
        let endPadding = edgeFadeViewSpec.endPadding.toCGFloat()
        let height = edgeFadeViewSpec.height?.toCGFloat()
        
        let endOffsetX = edgeFadeViewSpec.endOffsetX.toCGFloat()
        
        HStack(spacing: 0) {
            if startPadding > 0 {
                Spacer().frame(width: startPadding)
            }
            
            SwiftUIImage(
                theme: theme,
                image: edgeImage,
                width: edgeFadeViewSpec.startWidth.toCGFloat(),
                height: height,
                tintColor: tintColor
            )
            Spacer()
            
            SwiftUIImage(
                theme: theme,
                image: edgeImage,
                width: edgeFadeViewSpec.endWidth.toCGFloat(),
                height: height,
                tintColor: tintColor
            )
            .offset(x: endOffsetX)
            .rotationEffect(.degrees(180))
            
            if endPadding > 0 {
                Spacer().frame(width: endPadding)
            }
        }
    }
}
