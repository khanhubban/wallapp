//
//  SearchResultsScreenView.swift
//  WallApp
//

import WallApp
import SwiftUI

struct SearchResultsScreenView: View {
    let render: RenderIos
    @State var theme: Theme
    let viewModel: SearchResultsViewModel
    
    @State var viewState: SearchResultsViewState? = nil
    
    var body: some View {
        ZStack {
            if let viewState {
                AppScreenView(
                    viewState: viewState,
                    theme: theme,
                    render: render
                )
            } else {
                EmptyView()
            }
        }
        .ignoresSafeArea(.all, edges: .all)
        .task {
            for await viewState in viewModel.viewState {
                self.viewState = viewState
            }
        }
        .task {
            for await theme in InteropModulesIos.shared.themeManager.theme {
                self.theme = theme
            }
        }
        .onAppear {
            Router.shared.screenAppeared(ScreenArgument.SearchResultsScreenArgument())
        }
    }
}
