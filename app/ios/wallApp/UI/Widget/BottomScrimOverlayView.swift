//
//  BottomScrimOverlayView.swift
//  WallApp
//

import WallApp
import SwiftUI

struct BottomScrimOverlayView: View {
    let render: RenderIos
    let theme: Theme
    
    private var interopModules: InteropModulesIos {
        InteropModulesIos.shared
    }
    
    var body: some View {
        let waterfallImage = theme.isLight ? interopModules.imageRepository.waterfallGradientWhite : interopModules.imageRepository.waterfallGradientBlack
        let tintColor = theme.themeColors.surface.toColor()
        VStack {
            Spacer()
            SwiftUIImage(
                theme: theme,
                image: waterfallImage,
                width: render.windowFrame.deviceWidth.toCGFloat(),
                height: render.defaultViewSpec.screenScrimHeight.toCGFloat(),
                tintColor: tintColor
            )
            .rotationEffect(.degrees(180))
            .allowsHitTesting(false)
        }
    }
}
