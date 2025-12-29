//
//  StaggeredGrid.swift
//  WallApp
//

import WallApp
import Kingfisher
import SwiftUI
import UIKit

struct FeedStaggeredGrid: UIViewControllerRepresentable {
    let render: RenderIos
    let theme: Theme
    let feedViews: [CommonView]
    let spacing: CGFloat
    let pagingViewEventSink: ((PagingViewEvent) -> Void)?
    let scrollToTopCount: Int
    let feedScrollPositionUpdate: ((FeedScrollPosition) -> Void)?
    let scrollOffsetControllers: [ScrollOffsetController]
    var viewsVisibleListener: ViewsVisibleListener? = nil
    var contentInsetTop: CGFloat? = nil
    var paddingHeaderViewState: PaddingHeaderViewState? = nil
    
    func makeUIViewController(context: Context) -> FeedCollectionView {
        let layout = StaggeredGridLayout(spacing: spacing, paddingStart: render.defaultViewSpec.paddingDefault.toCGFloat())
        let feedStaggeredVC = FeedCollectionView(
            collectionViewLayout: layout,
            feedViews: feedViews,
            render: render,
            theme: theme,
            pagingViewEventSink: pagingViewEventSink,
            feedScrollPositionUpdate: feedScrollPositionUpdate,
            scrollOffsetControllers: scrollOffsetControllers,
            viewsVisibleListener: viewsVisibleListener,
            paddingHeaderViewState: paddingHeaderViewState
        )

        // Additional configuration if needed
        return feedStaggeredVC
    }
    
    func updateUIViewController(_ uiViewController: FeedCollectionView, context: Context) {
        uiViewController.render = render
        if uiViewController.theme.isDark != theme.isDark {
            uiViewController.theme = theme
        }
        uiViewController.updateFeedViewList(with: feedViews)

        uiViewController.scrollToTopIfNeeded(scrollToTopCounter: scrollToTopCount)
        
        if let contentInsetTop {
            uiViewController.updateContentEdgeInset(top: contentInsetTop)
        }
        if let paddingHeaderViewState {
            uiViewController.updatePaddingHeaderViewState(paddingHeaderViewState)
        }
    }
}

class FeedPreviewPreviewCell: UICollectionViewCell {
    var theme: Theme?
    var render: RenderIos?
    
    var view: CommonView? {
        didSet {
            configure()
        }
    }
    
    var hostConfiguration: UIHostingConfiguration<FeedPreview, EmptyView>?
    
    private func configure() {
        guard let view = view else { return }
        guard let theme = theme else { return }
        guard let render = render else { return }
        
        let configuration = UIHostingConfiguration {
            FeedPreview(view: view, theme: theme, render: render)
        }
        
        self.hostConfiguration = configuration
        self.contentConfiguration = configuration
        
    }
    
    override func prepareForReuse() {
//        self.hostConfiguration = nil
//        self.view = nil
//        self.render = nil
//        self.theme = nil
    }

    
}
