//
//  NoDataScreen.swift
//  WallApp
//

import WallApp
import SwiftUI

struct NoDataScreen: View {
    let render: RenderIos
    let theme: Theme
    let viewState: NoDataViewState
    
    var body: some View {
        NoDataView(render: render, theme: theme, viewState: viewState)
    }
}

struct NoDataView: View {
    let render: RenderIos
    let theme: Theme
    let viewState: NoDataViewState
    
    var body: some View {
        let title = viewState.title
        let summary = viewState.summary
        let image = viewState.image
        VStack(spacing: 0) {
            Spacer()
            StyledTextView(text: title, theme: theme)
            if let image {
                SwiftUIImage(theme: theme, image: image, width: 150, height: 150)
            }
            if let summary {
                StyledTextView(text: summary, theme: theme)
            }
            Spacer()
        }
    }
}
