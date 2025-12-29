//
//  FeedGridViewController.swift
//  WallApp
//

import WallApp
import SwiftUI
import UIKit
import Combine

class FeedGridViewController: UIViewController {
    var render: RenderIos
    var theme: Theme
    var feedViewState: FeedViewState
    var pagingViewEventSink: ((PagingViewEvent) -> Void)?
    var scrollOffsetControllers: [ScrollOffsetController]
    var highlightCarouselViewState: HighlightCarouselViewState?
    
    var scrollToTopCallback: (() -> Void)?
    private var offset: CGFloat = 0
    private var topPaddingConstraint: NSLayoutConstraint?
    private var topPadding: CGFloat = 0 {
        didSet {
            adjustTopPadding()
        }
    }
    
    private var feedCollectionView: FeedCollectionView?
    private var toolbarView: UIView?
    private var exploreStickyStatusBar: UIExploreStatusBarView?
    
    private var scrollToTopTask: Task<Void, Never>?
    
    // MARK: - Initialization
    
    init(
        render: RenderIos,
        theme: Theme,
        feedViewState: FeedViewState,
        pagingViewEventSink: ((PagingViewEvent) -> Void)? = nil,
        scrollOffsetControllers: [ScrollOffsetController] = [],
        highlightCarouselViewState: HighlightCarouselViewState? = nil
    ) {
        self.render = render
        self.theme = theme
        self.feedViewState = feedViewState
        self.pagingViewEventSink = pagingViewEventSink
        self.scrollOffsetControllers = scrollOffsetControllers
        self.highlightCarouselViewState = highlightCarouselViewState
        super.init(nibName: nil, bundle: nil)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    // MARK: - Lifecycle
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setupViews()
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        topPaddingIfNeeded()
    }
    
    // MARK: - Setup
    
    private func setupViews() {
        setupFeedStaggeredVC()
        setupToolbar()
        handleScrollToTopIfNeeded()
    }
    
    private func setupToolbar() {
        if let toolbarState = feedViewState.toolbar as? HomeOnboardingHeaderViewState {
            let toolbarView = UIHomeOnboardingHeaderView(render: render, theme: theme, viewState: toolbarState)
            view.addSubview(toolbarView)
            self.toolbarView = toolbarView
            
            toolbarView.translatesAutoresizingMaskIntoConstraints = false
            NSLayoutConstraint.activate([
                toolbarView.topAnchor.constraint(equalTo: view.topAnchor),
                toolbarView.leadingAnchor.constraint(equalTo: view.leadingAnchor),
                toolbarView.trailingAnchor.constraint(equalTo: view.trailingAnchor),
                toolbarView.heightAnchor.constraint(equalToConstant: toolbarState.viewSpec.height.toCGFloat())
            ])
        } else if let toolbarState = feedViewState.toolbar as? ExploreHeaderViewState {
            setupExploreHeaderView(toolbarState)
        }
    }
    
    private func setupFeedStaggeredVC() {
        let layout = StaggeredGridLayout(
            spacing: render.defaultViewSpec.paddingDefault.toCGFloat(),
            paddingStart: render.defaultViewSpec.paddingDefault.toCGFloat()
        )
        let feedCollectionView = FeedCollectionView(
            collectionViewLayout: layout,
            feedViews: createFeedViews(),
            render: render,
            theme: theme,
            pagingViewEventSink: pagingViewEventSink,
            feedScrollPositionUpdate: feedViewState.feedState?.feedScrollPositionUpdateSink,
            scrollOffsetControllers: scrollOffsetControllers,
            viewsVisibleListener: feedViewState.viewsVisibleListener,
            highlightCarouselViewState: highlightCarouselViewState
        )
        addChild(feedCollectionView)
        view.addSubview(feedCollectionView.view)
        feedCollectionView.didMove(toParent: self)
        self.feedCollectionView = feedCollectionView
        feedCollectionView.collectionView?.contentInsetAdjustmentBehavior = .never
        
        // Adjust layout constraints to ignore the safe area
        feedCollectionView.view.translatesAutoresizingMaskIntoConstraints = false
        topPaddingConstraint = feedCollectionView.view.topAnchor.constraint(equalTo: view.topAnchor, constant: topPadding)
        topPaddingConstraint?.isActive = true
        
        NSLayoutConstraint.activate([
            feedCollectionView.view.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            feedCollectionView.view.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            feedCollectionView.view.bottomAnchor.constraint(equalTo: view.bottomAnchor)
        ])
    }

    
    // MARK: - Adjustments
    
    private func adjustTopPadding() {
        topPaddingConstraint?.constant = topPadding
        UIView.animate(withDuration: 0.3) {
            self.view.layoutIfNeeded()
        }
    }
    
    private func topPaddingIfNeeded() {
        let messageBarHeight = feedViewState.messageBarHeight.toCGFloat()
        if topPadding == 0 {
            topPadding = toolbarView is UIExploreHeaderView ? 0 : messageBarHeight
        }
    }
    
    func update(theme: Theme) {
        self.theme = theme
        if let toolbarView = toolbarView as? UIHomeOnboardingHeaderView {
            toolbarView.updateWith(theme: theme)
        } else if let toolbarView = toolbarView as? UIExploreHeaderView {
            toolbarView.updateWith(theme: theme)
            exploreStickyStatusBar?.updateWith(theme: theme)
        }
        feedCollectionView?.theme = theme
        feedCollectionView?.updateFeedViewList(with: createFeedViews(), and: highlightCarouselViewState)
    }
    
    // MARK: - Update ViewState
    
    func update(viewState: FeedViewState, highlightCarouselViewState: HighlightCarouselViewState? = nil) {
        self.feedViewState = viewState
        self.highlightCarouselViewState = highlightCarouselViewState
        topPadding = toolbarView is UIExploreHeaderView ? 0 : viewState.messageBarHeight.toCGFloat()
        if let toolbarViewState = viewState.toolbar {
            updateToolbarState(toolbarViewState)
        }
        updateFeedViews()
        handleScrollToTopIfNeeded()
    }
    
    private func updateFeedViews() {
        let feedViews = createFeedViews()
        feedCollectionView?.updateFeedViewList(with: feedViews, and: highlightCarouselViewState)
    }
    
    private func handleScrollToTopIfNeeded() {
        scrollToTopTask?.cancel()
        scrollToTopTask = Task {
            for await _ in feedViewState.scrollToTop {
                feedCollectionView?.scrollToTop()
            }
        }
    }
    
    private func createFeedViews() -> [CommonView] {
        return FeedViewsArbitrator.shared.arbitrateFeedViews(feedViewState)
    }
    
    private func updateToolbarState(_ toolbarState: ViewState) {
        if let toolbarState = toolbarState as? HomeOnboardingHeaderViewState {
            (toolbarView as! UIHomeOnboardingHeaderView).updateWith(viewState: toolbarState)
        } else if let toolbarState = toolbarState as? ExploreHeaderViewState {
            (toolbarView as! UIExploreHeaderView).updateWith(viewState: toolbarState)
            exploreStickyStatusBar?.updateWith(viewState: toolbarState)
        }
    }
    
    private func setupExploreHeaderView(_ toolbarState: ExploreHeaderViewState) {
        let toolbarScrollOffsetController = scrollOffsetControllers.first
        let toolbarView = UIExploreHeaderView(
            render: render,
            theme: theme,
            viewState: toolbarState,
            viewSpec: toolbarState.viewSpec,
            scrollOffsetController: toolbarScrollOffsetController
        )
        view.addSubview(toolbarView)
        self.toolbarView = toolbarView
        
        // Adjust constraints to ignore the safe area
        toolbarView.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint.activate([
            toolbarView.topAnchor.constraint(equalTo: view.topAnchor),
            toolbarView.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            toolbarView.trailingAnchor.constraint(equalTo: view.trailingAnchor)
        ])
        
        let exploreStickyStatusBar = UIExploreStatusBarView(
            render: render,
            theme: theme,
            viewState: toolbarState,
            viewSpec: toolbarState.viewSpec,
            scrollOffsetController: toolbarScrollOffsetController
        )
        view.addSubview(exploreStickyStatusBar)
        self.exploreStickyStatusBar = exploreStickyStatusBar
        
        exploreStickyStatusBar.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint.activate([
            exploreStickyStatusBar.topAnchor.constraint(equalTo: view.topAnchor),
            exploreStickyStatusBar.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            exploreStickyStatusBar.trailingAnchor.constraint(equalTo: view.trailingAnchor)
        ])
        
        self.view.layoutIfNeeded()
    }
    
    deinit {
        scrollToTopTask?.cancel()
    }
}
