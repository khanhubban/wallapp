//
//  MessageBar.swift
//  WallApp
//

import WallApp
import SwiftUI

struct MessageBarView: View {
    let render: RenderIos
    let theme: Theme
    let viewState: MessageBarViewState
    
    @State var messageBarViewState: MessageBarViewState? = nil

    var body: some View {
        RenderedView(render: render, theme: theme, view: viewState.content)
            .frame(height: viewState.viewSpec.maxHeight.toCGFloat())
    }
}

extension AnyTransition {
    static var upAndFade: AnyTransition {
        .asymmetric(
            insertion: .move(edge: .top),
            removal: .move(edge: .top)
        )
    }
}
