//
//  ButtonView.swift
//  WallApp
//

import WallApp
import SwiftUI


struct ButtonView: View {
    let render: RenderIos
    let theme: Theme
    let viewState: ButtonViewState
    var modifier: some ViewModifier = EmptyModifier()
    var animationProgress: Float? = 1.0
    var contentPadding: PaddingAll = [.all: 0]
    var width: CGFloat? = nil
    var height: CGFloat? = nil

    var body: some View {
        let menuItem = viewState.menuItem
        let onClick = viewState.eventHandler
        let animatedViewSpec = viewState.animatedViewSpec
        //        let snapshot = animatedViewSpec?.animate(render: render, progress: animationProgress) ?? nil
        let shape = animatedViewSpec?.shapeSpecMax ?? animatedViewSpec?.shapeSpecMin ?? viewState.shapeSpec// snapshot?.shape ?? viewState.shapeSpec?.map(render.shapeMapperComposable)
        let containerColor = viewState.buttonAppearance == .disabled ? theme.themeColors.surface.toColor() : viewState.containerColorToken?.toColor(themeColors: theme.themeColors)
        
        ZStack {
            MenuItemUI(render: render, theme: theme, menuItem: menuItem, width: width, height: height)
        }
        .frame(width: width, height: height)
        .if (width == nil) { $0.frame(maxWidth: .infinity) }
        .applyButtonStyle(shape: shape, theme: theme, containerColor: containerColor)
        .onTapGesture {
            onClick?.invoke()
        }
    }
}

struct ButtonAppearanceView: View {
    let render: RenderIos
    let theme: Theme
    let onClick: (() -> Void)?
    let buttonAppearance: ButtonAppearance
    let shape: ShapeSpec?
    let containerColor: Color?
    var content: () -> AnyView

    var body: some View {
        switch buttonAppearance {
        case .default, .disabled, .outline, .highlight, .none, .disabledWithClick:
            ZStack {
                content()
            }
            .onTapGesture {
                onClick?()
            }
            .applyButtonStyle(shape: shape, theme: theme, containerColor: containerColor)
        }
    }
}

struct ButtonStyleModifier: ViewModifier {
    var shape: ShapeSpec?
    var theme: Theme
    var containerColor: Color? = nil
    var contentColor: Color? = nil

    func body(content: Content) -> some View {
        let containerColor = self.containerColor ?? theme.themeColors.primary.toColor()
        let contentColor = self.contentColor ?? theme.themeColors.onPrimary?.toColor()
        content
            .background(containerColor)
            .applyClipShapes(ShapeMapper.map(shapeSpec: shape))
            .foregroundColor(contentColor)
    }
}

extension View {
    func applyButtonStyle(shape: ShapeSpec?, theme: Theme, containerColor: Color? = nil) -> some View {
        self.modifier(ButtonStyleModifier(shape: shape, theme: theme, containerColor: containerColor))
    }
}
