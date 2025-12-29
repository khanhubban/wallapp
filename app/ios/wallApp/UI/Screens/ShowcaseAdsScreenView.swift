//
//  ShowcaseAdsScreenView.swift
//  WallApp
//

import WallApp
import SwiftUI

struct ShowcaseAdsScreenView: View {
    
    let viewState: ShowcaseAdsViewState
    let theme: Theme
    let render: RenderIos
    
    var body: some View {
        switch onEnum(of: viewState) {
        case .data(let data):
            FeedGrid(render: render,
                     theme: theme,
                     feedViewState: data.feedViewState)
        case .loading(_):
            LoadingView(theme: theme, render: render)
        }
    }
}
