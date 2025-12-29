//
//  ProfileImageView.swift
//  WallApp
//

import WallApp
import SwiftUI

struct ProfileImageView: View {
    let render: RenderIos
    let theme: Theme
    let profileImage: ProfileImageViewState
    let imageSize: CGFloat
    let eventHandler: ViewEventHandler?
    var shapeSpec: ShapeSpec = ShapeSpec.Single(shapeStyle: .circle, shapeSize: .small)
    var outlineColor: Color? = nil

    var body: some View {
        let outlineColor = outlineColor ?? theme.themeColors.tertiary?.toColor()
        ZStack(alignment: .bottomTrailing) {
            ProfileImageContainer(
                render: render,
                theme: theme,
                profileImage: profileImage,
                imageSize: imageSize,
                shapeSpec: shapeSpec,
                eventHandler: eventHandler,
                outlineColor: outlineColor!
            )
            
        }.if (eventHandler != nil) { $0.onTapGesture { eventHandler!.invoke() } }
    }
}

struct ProfileImageContainer: View {
    let render: RenderIos
    let theme: Theme
    let profileImage: ProfileImageViewState
    let imageSize: CGFloat
    let shapeSpec: ShapeSpec
    let eventHandler: ViewEventHandler?
    let outlineColor: Color

    var body: some View {
        ZStack {
            ProfileImageMenuItemView(
                render: render,
                theme: theme,
                image: profileImage.image,
                shapeSpec: shapeSpec,
                outlineColor: outlineColor,
                borderSize: profileImage.borderSize.toCGFloat(),
                imageSize: imageSize
            )
        }.if(profileImage.indicator != nil) {
            $0.overlay(alignment: .bottomTrailing) {
                IndicatorImage(render: render, theme: theme, viewState: profileImage.indicator!, shapeSpec: profileImage.imageViewSpec.shapeSpec, eventHandler: eventHandler)
            }
        }
        .frame(width: imageSize, height: imageSize)
    }
}

struct IndicatorImage: View {
    let render: RenderIos
    let theme: Theme
    let viewState: ProfileImageIndicatorViewState
    let shapeSpec: ShapeSpec?
    let eventHandler: ViewEventHandler?

    var body: some View {
        let viewSpec = viewState.viewSpec
        let borderWidth = viewSpec.borderSize
        let indicatorSize = viewSpec.size - borderWidth * 2
        let indicatorBorderColor = viewState.colorToken.toColor(themeColors: theme.themeColors)
        let indicatorImage = viewState.image
        SwiftUIImage(theme: theme, image: indicatorImage, width: indicatorSize.toCGFloat(), height: indicatorSize.toCGFloat())
            .applyClipShapes(ShapeMapper.map(shapeSpec: shapeSpec), borderWidth: borderWidth.toCGFloat(), borderColor: indicatorBorderColor)
    }
}


struct ProfileImageMenuItemView: View {
    let render: RenderIos
    let theme: Theme
    let image: MenuItem
    let shapeSpec: ShapeSpec
    let outlineColor: Color
    let borderSize: CGFloat
    let imageSize: CGFloat

    var body: some View {
        MenuItemUI(render: render, theme: theme, menuItem: image, width: imageSize, height: imageSize)
            .applyClipShapes(ShapeMapper.map(shapeSpec: shapeSpec), borderWidth: borderSize, borderColor: outlineColor)
    }
}
