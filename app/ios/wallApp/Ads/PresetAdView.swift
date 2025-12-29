//
//  FallbackAdView.swift
//  WallApp
//

import WallApp
import SwiftUI

struct PresetAdView: View {
    let render: RenderIos
    let theme: Theme
    let adViewState: AdViewState
    
    var body: some View {
        let paddingDefault: CGFloat = render.defaultViewSpec.paddingDefault.toCGFloat()
        let fallbackImageWidth = render.windowFrame.deviceWidth.toCGFloat() - paddingDefault * 2
        SwiftUIImage(
            theme: theme,
            image: adViewState.image,
            width: fallbackImageWidth,
            height: adViewState.viewSpec.height.toCGFloat()
        )
        .onTapGesture {
            adViewState.viewEventHandler.invoke()
        }
    }
}
