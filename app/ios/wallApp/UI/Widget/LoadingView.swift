//
//  LoadingView.swift
//  WallApp
//

import WallApp
import Lottie
import SwiftUI

struct LoadingView: View {
    let theme: Theme
    let render: RenderIos
    let imageSize: CGFloat = 120
    
    var body: some View {
        ZStack {
            SwiftUIImage(
                theme: theme,
                image: render.defaultResources.loading,
                width: imageSize,
                height: imageSize
            )
        }.frame(maxWidth: .infinity, maxHeight: .infinity)
            .transition(.fade)
    }
}
