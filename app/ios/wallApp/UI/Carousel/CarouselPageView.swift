//
//  CarouselPageView.swift
//  WallApp
//

import WallApp
import SwiftUI

import SwiftUI

struct CarouselPageView: View {
    let render: RenderIos
    let theme: Theme
    let viewState: ExhibitViewState
    let viewSpec: ExhibitViewSpec
    let contentColor = Color.white
    
    var body: some View {
        let shape = ShapeMapper.map(shapeSpec: viewState.shapeSpec)
        ZStack {
            SwiftUIImage(
                theme: theme,
                image: viewState.imageViewState.image,
                width: viewSpec.width,
                height: viewSpec.height
            )
            
            if let label = viewState.label {
                let scrimColor = theme.themeColors.scrim?.toColor() ?? Color.black.opacity(0.5)
                Rectangle()
                    .fill(scrimColor)
                    .overlay(
                        StyledTextView(
                            text: label,
                            theme: theme,
                            colorOverride: contentColor
                        ),
                        alignment: .center
                    )
            }
        }
        .frame(width: viewSpec.width, height: viewSpec.height)
        .applyClipShapes(shape)
    }
}

