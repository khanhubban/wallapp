//
//  ContentFeedPreview.swift
//  WallApp
//

import WallApp
import SwiftUI

struct ContentFeedPreview: View {
    
    let title: StyledText?
    let image: ImageUI
    let favoriteViewState: FavoriteViewState
    let onClick: ViewEventHandler
    let scrimImage: ImageUI?
    let viewSpec: WallpaperPreviewViewSpec.WithFooter
    let theme: Theme
    let render: RenderIos
    
    var body: some View {
        let width = viewSpec.width.toCGFloat()
        let height = viewSpec.height.toCGFloat()
        let footerHeight = viewSpec.footerHeight.toCGFloat()
        ZStack {
            SwiftUIImage(theme: theme,
                         image: image,
                         width: width,
                         height: height,
                         imageHashDecoder: render.imageHashDecoder)
                .scaledToFill()
                .onTapGesture {
                    onClick.invoke()
                }
        }
        .overlay(alignment: .bottom) {
            WallpaperPreviewFooter(
                title: title,
                favoriteViewState: favoriteViewState,
                footerContentPadding: viewSpec.footerContentPadding.toPadding(),
                scrimImage: scrimImage,
                width: width,
                height: footerHeight,
                theme: theme
            )
            .frame(width: width, height: footerHeight)
        }
    }
}
