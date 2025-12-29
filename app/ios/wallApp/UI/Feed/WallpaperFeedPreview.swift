//
//  WallpaperFeedPreview.swift
//  WallApp
//

import WallApp
import SwiftUI

struct WallpaperFeedPreview: View {
    let wallpaperPreview: WallpaperPreviewViewState
    let viewSpec: FeedContentPreviewViewSpec
    let theme: Theme
    let render: RenderIos
    
    var body: some View {
        let image = wallpaperPreview.imageViewState.image
        let title = wallpaperPreview.title
        let onClick = wallpaperPreview.onClick
        let favorite = wallpaperPreview.favorite
        let scrimImage = wallpaperPreview.scrimImage
        
        ContentFeedPreview(
            title: title,
            image: image,
            favoriteViewState: favorite,
            onClick: onClick,
            scrimImage: scrimImage,
            viewSpec: wallpaperPreview.viewSpec as! WallpaperPreviewViewSpec.WithFooter,
            theme: theme,
            render: render
        )
    }
}

struct WallpaperPreviewFooter: View {
    let title: StyledText?
    let favoriteViewState: FavoriteViewState
    let contentColor = Color.white
    let footerContentPadding: PaddingAll
    let scrimImage: ImageUI?
    let width: CGFloat
    let height: CGFloat
    let theme: Theme
    
    var body: some View {
        ZStack {
            if let scrimImage = scrimImage {
                SwiftUIImage(theme: theme, image: scrimImage, width: width, height: height)
                    .scaledToFill()
                    .clipped()
            }
            HStack {
                VStack {
                    if let title = title {
                        Spacer()
                        StyledTextView(text: title, theme: theme, colorOverride: contentColor)
                        Spacer()
                    }
                }
                FavoriteButton(theme: theme, viewState: favoriteViewState, contentColor: contentColor)
            }
            .padding(8)
            .foregroundColor(contentColor)
        }
    }
}
