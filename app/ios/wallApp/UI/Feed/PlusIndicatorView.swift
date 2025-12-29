//
//  PlusIndicator.swift
//  WallApp
//

import Foundation
import WallApp
import SwiftUI

struct PlusIndicatorView: View {
    let theme: Theme
    let viewState: PlusIndicatorViewState
    let render: RenderIos

    var body: some View {
        let imageUI = viewState.image
        let height = viewState.height
        let width = viewState.width
        
        SwiftUIImage(theme: theme, image: imageUI, width: width.toCGFloat(), height: height.toCGFloat())
    }
}

