//
//  RenderedView.swift
//  WallApp
//

import WallApp
import SwiftUI

struct RenderedView: View {
    let render: RenderIos
    let theme: Theme
    let view: CommonView
    
    var body: some View {
        view.render(render: render, theme: theme)
    }
}
