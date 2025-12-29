//
//  FeedPreview.swift
//  WallApp
//

import WallApp
import SwiftUI

struct FeedPreview: View {
    let view: CommonView
    let theme: Theme
    let render: RenderIos
    
    var body: some View {
        RenderedView(render: render, theme: theme, view: view)
    }
}
