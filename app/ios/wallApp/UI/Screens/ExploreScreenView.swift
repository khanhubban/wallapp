//
//  ExploreScreenView.swift
//  WallApp
//

import WallApp
import SwiftUI

struct ExploreScreenView: View {
    
    @StateObject var toolbarOffsetController = ScrollOffsetController()
    @EnvironmentObject var navBarOffsetController: ScrollOffsetController
    
    let viewState: ExploreViewState
    let theme: Theme
    let render: RenderIos
    
    var body: some View {
        ZStack {
            switch onEnum(of: viewState) {
            case .success(let data):
                TabView {
                    FeedGrid(
                        render: render,
                        theme: theme,
                        feedViewState: data.feedViewState,
                        pagingViewEventSink: data.feedViewState.feedState?.pagingViewEventSink,
                        scrollOffsetControllers: [
                            toolbarOffsetController,
                            navBarOffsetController
                        ]
                    )
                    .frame(maxHeight: .infinity)
                    .ignoresSafeArea(.all, edges: .all)
                }
                .tabViewStyle(PageTabViewStyle(indexDisplayMode: .never))
                .frame(maxHeight: .infinity)
                .environmentObject(toolbarOffsetController)
                .transition(.fade)
            case .loading(_):
                LoadingView(theme: theme, render: render)
            }
        }
        .animation(.easeInOut, value: viewState)
    }
}
