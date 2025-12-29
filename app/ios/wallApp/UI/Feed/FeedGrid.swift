//
//  FeedGrid.swift
//  WallApp
//

import WallApp
import SwiftUI

struct FeedGrid: View {
    let render: RenderIos
    let theme: Theme
    let feedViewState: FeedViewState
    let pagingViewEventSink: ((PagingViewEvent) -> Void)?
    let scrollOffsetControllers: [ScrollOffsetController]
    let contentInsetTop: CGFloat?
    let paddingHeaderViewState: PaddingHeaderViewState?
    
    @State var scrollToTopCount: Int = 0
    
    @State var offset: CGFloat = 0
    @State var topPadding: CGFloat = 0
    
    init(
        render: RenderIos,
        theme: Theme,
        feedViewState: FeedViewState,
        pagingViewEventSink: ((PagingViewEvent) -> Void)? = nil,
        scrollOffsetControllers: [ScrollOffsetController] = [],
        contentInsetTop: CGFloat? = nil,
        paddingHeaderViewState: PaddingHeaderViewState? = nil
    ) {
        self.render = render
        self.theme = theme
        self.feedViewState = feedViewState
        self.pagingViewEventSink = pagingViewEventSink
        self.scrollOffsetControllers = scrollOffsetControllers
        self.contentInsetTop = contentInsetTop
        self.paddingHeaderViewState = paddingHeaderViewState
    }
    
    var body: some View {
        let paddingDefault = render.defaultViewSpec.paddingDefault.toCGFloat()
        let feedViews = FeedViewsArbitrator.shared.arbitrateFeedViews(feedViewState)
        ZStack(alignment: .top) {
            let toolbar = feedViewState.toolbar
            if let toolbar {
                let toolbarScrollOffsetController = scrollOffsetControllers.count > 0 ? scrollOffsetControllers[0] : nil
                ToolbarView(render: render, theme: theme, toolbarViewState: toolbar, scrollOffsetController: toolbarScrollOffsetController)
                    .zIndex(1.0)
            }
            FeedStaggeredGrid(
                render: render,
                theme: theme,
                feedViews: feedViews,
                spacing: paddingDefault,
                pagingViewEventSink: pagingViewEventSink,
                scrollToTopCount: scrollToTopCount,
                feedScrollPositionUpdate: feedViewState.feedState?.feedScrollPositionUpdateSink,
                scrollOffsetControllers: scrollOffsetControllers,
                viewsVisibleListener: feedViewState.viewsVisibleListener,
                contentInsetTop: contentInsetTop,
                paddingHeaderViewState: paddingHeaderViewState
            )
            .padding(.top, topPadding)
        }
        .task {
            for await _ in feedViewState.scrollToTop {
                DispatchQueue.main.async {
                    scrollToTopCount += 1
                }
            }
        }
        .onAppear {
            let messageBarHeight = feedViewState.messageBarHeight.toCGFloat()
            if topPadding == 0 {
                topPadding = messageBarHeight
            }
        }
        .onChange(of: feedViewState) { feedViewState in
            let newPadding = feedViewState.messageBarHeight.toCGFloat()
            if newPadding != topPadding {
                withAnimation {
                    topPadding = newPadding
                }
            }
        }
        .frame(minWidth: 0, maxWidth: .infinity, minHeight: 0, maxHeight: .infinity)
    }
}
