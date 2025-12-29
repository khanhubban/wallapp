//
//  SearchResultsView.swift
//  WallApp
//

import WallApp
import SwiftUI

struct SearchResultsView: View {
    
    let viewState: SearchResultsViewState
    let theme: Theme
    let render: RenderIos
    
    @State var scrollToTopCallback: (() -> Void)? = nil
    
    @State var offset: CGFloat = 0
    @State var topPadding: CGFloat = 0
    
    @StateObject var toolbarOffsetController = ScrollOffsetController()
    @EnvironmentObject var navBarOffsetController: ScrollOffsetController
    
    var body: some View {
        switch onEnum(of: viewState) {
        case .data(let data):
            TabView {
                let paddingSmall = render.defaultViewSpec.paddingSmall.toCGFloat()
                let statusBarHeight = render.windowFrame.statusBarHeight.toCGFloat()
                let headerHeight = data.headerViewState.viewSpec.searchBarHeight.toCGFloat()
                ZStack(alignment: .top) {
                    SearchResultsHeaderView(
                        render: render,
                        theme: theme,
                        viewState: data.headerViewState
                    )
                    .zIndex(3.0)
                    
                    SwiftUIImage(
                        theme: theme,
                        image: data.headerViewState.topScrim
                    )
                    .frame(height: headerHeight + paddingSmall)
                    .frame(maxWidth: .infinity)
                    .zIndex(2.0)
                    
                    TransparentBlurView()
                        .frame(height: headerHeight + statusBarHeight)
                        .blur(radius: 8, opaque: false)
                        .background(.clear)
                        .zIndex(1.0)
                        
                    FeedGrid(
                        render: render,
                        theme: theme,
                        feedViewState: data.feedViewState,
                        pagingViewEventSink: nil,
                        scrollOffsetControllers: [
                            toolbarOffsetController,
                            navBarOffsetController
                        ]
                    )
                }
                .frame(maxHeight: .infinity)
                .ignoresSafeArea(.all, edges: .all)
            }
            .tabViewStyle(PageTabViewStyle(indexDisplayMode: .never))
            .frame(maxHeight: .infinity)
            .environmentObject(toolbarOffsetController)
            .transition(.fade)
        case .loading(_):
            LoadingView(theme: theme, render: render)
        case .inactive(_):
            EmptyView()
        }
    }
}
