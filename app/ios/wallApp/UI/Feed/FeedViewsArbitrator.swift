//
//  FeedViewsArbitrator.swift
//  WallApp
//

import WallApp

final class FeedViewsArbitrator {
    static let shared: FeedViewsArbitrator = FeedViewsArbitrator()
    
    private init() {}
    
    func arbitrateFeedViews(_ feedViewState: FeedViewState) -> [CommonView] {
        let feedViews = feedViewState.views.enumerated().compactMap { (index, view) -> CommonView? in
            var newView = view
            if let viewState = view.viewState as? SpacerViewState {
                let newViewState = SpacerViewState(
                    width: viewState.width,
                    height: viewState.height,
                    id: KotlinInt(integerLiteral: index),
                    skipRendering: viewState.skipRendering
                )
                
                newView = CommonView(viewState: newViewState, viewSpec: view.viewSpec, applyScrollParallax: view.applyScrollParallax)
            } else if let viewState = view.viewState as? FeedAdViewState {
                let newViewState = FeedAdViewState(
                    inlineAdItem: viewState.inlineAdItem,
                    fallbackAdViewState: viewState.fallbackAdViewState,
                    id: KotlinInt(integerLiteral: index)
                )
                
                newView = CommonView(viewState: newViewState, viewSpec: view.viewSpec, applyScrollParallax: view.applyScrollParallax)
            }
            return newView
        }
        return feedViews
    }
}
