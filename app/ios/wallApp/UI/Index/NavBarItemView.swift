//
//  NavBarItemView.swift
//  WallApp
//

import WallApp
import SwiftUI

struct NavBarItemView: View {
    
    let state: NavBarItemState
    let theme: Theme
    
    var body: some View {
        let contentColor = state.isSelected ? theme.themeColors.onSurface?.color.toColor() : theme.themeColors.onSurfaceVariant.toColor()
        let image = state.isSelected ? state.selectedImage : state.unselectedImage
        let imageSize = state.imageSize.toCGFloat()
        VStack {
            SwiftUIImage(theme: theme,
                         image: image, 
                         width: imageSize,
                         height: imageSize,
                         tintColor: contentColor)
                .frame(width: imageSize, height: imageSize)
            if let text = state.text {
                StyledTextView(text: text, theme: theme, colorOverride: contentColor)
                    .foregroundColor(contentColor)
            }
        }
        .onTapGesture {
            state.onClick()
        }
    }
}
