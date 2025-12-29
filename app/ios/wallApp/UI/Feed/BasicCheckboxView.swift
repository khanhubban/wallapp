//
//  BasicCheckboxView.swift
//  WallApp
//

import WallApp
import SwiftUI

struct BasicCheckboxView: View {
    let render: RenderIos
    let theme: Theme
    let checked: Bool
    let shape: ShapeSpec
    let onCheckedChange: (Bool) -> Void
    let size: CGFloat = 24
    
    var body: some View {
        let color = theme.themeColors.onBackground?.toColor() ?? Color.black
        let borderColor = color

        ZStack {
            Rectangle()
                .fill(checked ? color : Color.clear)
                .frame(width: size, height: size)
                .applyClipShapes(ShapeMapper.map(shapeSpec: shape), borderWidth: 2, borderColor: borderColor)
                .animation(.easeInOut, value: checked)
                .onTapGesture {
                    onCheckedChange(!checked)
                }
        }
    }
}
