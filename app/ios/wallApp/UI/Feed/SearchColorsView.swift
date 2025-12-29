//
//  SearchColorsView.swift
//  WallApp
//

import WallApp
import SwiftUI

struct SearchColorsView: View {
    let render: RenderIos
    let theme: Theme
    let viewState: SearchColorsViewState

    var body: some View {
        let itemSize = viewState.itemSize.toCGFloat()
        HStack(spacing: 0) {
            ForEach(viewState.colors.indices, id: \.self) { index in
                let colorViewState = viewState.colors[index]
                SearchColorView(
                    render: render,
                    theme: theme,
                    viewState: colorViewState,
                    backgroundSize: viewState.insideItemSize.toCGFloat(),
                    borderShape: viewState.borderShape,
                    backgroundShape: viewState.backgroundShape,
                    itemSize: itemSize
                )
                if index < viewState.colors.count - 1 {
                    Spacer()
                }
            }
        }
        .frame(height: itemSize)
    }
}

struct SearchColorView: View {
    var render: RenderIos
    var theme: Theme
    var viewState: SearchColorViewState
    var backgroundSize: CGFloat
    var borderShape: ShapeSpec
    var backgroundShape: ShapeSpec
    var itemSize: CGFloat

    var body: some View {
        let isSelected = viewState.isSelected
        let color = viewState.color.toColor()
        let canShowBorder = viewState.canShowBorder

        let borderColor = isSelected ? theme.themeColors.onBackground?.toColor() : nil
        let borderWidth: CGFloat = isSelected ? 2 : 0
        let borderColorInside = canShowBorder ? theme.themeColors.onBackground?.toColor().opacity(0.47) : nil
        let borderWidthInside: CGFloat = canShowBorder ? 2 : 0

        ZStack {
            Rectangle()
                .fill(color)
                .frame(width: backgroundSize, height: backgroundSize)
                .applyClipShapes(ShapeMapper.map(shapeSpec: backgroundShape), borderWidth: borderWidthInside, borderColor: borderColorInside)
        }
        .frame(width: itemSize, height: itemSize)
        .applyClipShapes(ShapeMapper.map(shapeSpec: borderShape), borderWidth: borderWidth, borderColor: borderColor)
        .onTapGesture {
            viewState.eventSink(viewState.event)
        }
    }
}
