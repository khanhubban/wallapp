//
//  ExploreViewController.swift
//  WallApp
//

import WallApp
import UIKit

class ExploreViewController: UIViewController {
    var theme: Theme {
        didSet {
            updateTheme()
        }
    }
    var render: RenderIos
    private var viewState: ExploreViewState? {
        didSet {
            updateViewState()
        }
    }
    
    var toolbarOffsetController: ScrollOffsetController?
    var navBarOffsetController: ScrollOffsetController
    
    // Child View Controllers
    private var feedGridViewController: FeedGridViewController?
    private var loadingViewController: LoadingViewController?
    
    private var viewStateObserveTask: Task<Void, Never>?
    private var themeObserverTask: Task<Void, Never>?
    
    // Initialization
    init(theme: Theme, render: RenderIos, navBarOffsetController: ScrollOffsetController) {
        self.theme = theme
        self.render = render
        self.navBarOffsetController = navBarOffsetController
        super.init(nibName: nil, bundle: nil)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    // Lifecycle
    override func viewDidLoad() {
        super.viewDidLoad()
        updateBackground()
        observeStates()
    }
    
    private func updateBackground() {
        view.backgroundColor = theme.themeColors.background.uiColor
    }
    
    private func observeStates() {
        viewStateObserveTask?.cancel()
        viewStateObserveTask = Task {
            let vm = InteropModulesIos.shared.appViewModelFactory.createExploreViewModel()
            for await viewState in vm.viewState {
                Log.d("[ExploreViewUIKit] Received view state")
                self.viewState = viewState
            }
        }
        themeObserverTask?.cancel()
        themeObserverTask = Task {
            for await theme in interopModules.themeManager.theme {
                if theme.isDark == self.theme.isDark {
                    continue
                }
                self.theme = theme
            }
        }
    }
    
    private func updateTheme() {
        // Update background color
        updateBackground()
        
        // remove existing feed grid view controller and create it again and add it
        if let feedVC = feedGridViewController {
            feedVC.willMove(toParent: nil)
            feedVC.view.removeFromSuperview()
            feedVC.removeFromParent()
            feedGridViewController = nil
        }
        
        if let viewState = viewState as? ExploreViewState.Success {
            displayFeedGrid(data: viewState)
        }
    }
    
    // Handling ViewState Changes
    private func updateViewState() {
        switch onEnum(of: viewState) {
        case .success(let data):
            displayFeedGrid(data: data)
        default:
            displayLoadingView()
        }
    }
    
    private func displayFeedGrid(data: ExploreViewState.Success) {
        // Remove loading view if present
        if let loadingVC = loadingViewController {
            // Prepare for removal with animation
            UIView.animate(withDuration: 0.3, animations: {
                loadingVC.view.alpha = 0
            }) { _ in
                loadingVC.willMove(toParent: nil)
                loadingVC.view.removeFromSuperview()
                loadingVC.removeFromParent()
                self.loadingViewController = nil
            }
        }
        
        // Check if feedGridViewController is already added
        if feedGridViewController == nil {
            let fallbackHighlightsHeight = interopModules.viewSpecArbitrator.carouselHeight.toCGFloat()
            let toolbarStartOffset = data.highlightCarouselViewState?.carouselViewSpec.height.toCGFloat() ?? fallbackHighlightsHeight
            let statusBarHeight = render.windowFrame.statusBarHeight.toCGFloat()
            let toolbarOffsetController = ScrollOffsetController(startOffset: toolbarStartOffset, scrollTarget: SearchBarExploreScrollTarget(topStickyOffset: statusBarHeight))
            let feedVC = FeedGridViewController(
                render: render,
                theme: theme,
                feedViewState: data.feedViewState,
                pagingViewEventSink: data.feedViewState.feedState?.pagingViewEventSink,
                scrollOffsetControllers: [toolbarOffsetController, navBarOffsetController],
                highlightCarouselViewState: data.highlightCarouselViewState
            )
            addChild(feedVC)
            feedVC.view.alpha = 0 // Start transparent for fade in
            view.addSubview(feedVC.view)
            feedVC.didMove(toParent: self)
            feedVC.view.frame = self.view.bounds
            
            // Animate fade in
            UIView.animate(withDuration: 0.3) {
                feedVC.view.alpha = 1
            }
            
            feedGridViewController = feedVC
        } else {
            // If already present, just update the state
            feedGridViewController!.update(viewState: data.feedViewState, highlightCarouselViewState: data.highlightCarouselViewState)
        }
    }
    
    private func displayLoadingView() {
        // Remove feed grid view if present
        if let feedVC = feedGridViewController {
            // Prepare for removal with animation
            UIView.animate(withDuration: 0.3, animations: {
                feedVC.view.alpha = 0
            }) { _ in
                feedVC.willMove(toParent: nil)
                feedVC.view.removeFromSuperview()
                feedVC.removeFromParent()
                self.feedGridViewController = nil
            }
        }
        
        // Check if loadingViewController is already added
        if loadingViewController == nil {
            let loadingVC = LoadingViewController(theme: theme, render: render)
            addChild(loadingVC)
            loadingVC.view.alpha = 0 // Start transparent for fade in
            view.addSubview(loadingVC.view)
            loadingVC.didMove(toParent: self)
            loadingVC.view.frame = self.view.bounds
            
            // Animate fade in
            UIView.animate(withDuration: 0.3) {
                loadingVC.view.alpha = 1
            }
            
            loadingViewController = loadingVC
        }
    }

    deinit {
        viewStateObserveTask?.cancel()
        themeObserverTask?.cancel()
    }
    
    override func viewDidAppear(_ animated: Bool) {
        Log.d("[ExploreViewUIKit] [Lifecycle] viewDidAppear()")
        Router.shared.screenAppeared(ScreenArgument.ExploreScreenArgument())
    }
    
    override func traitCollectionDidChange(_ previousTraitCollection: UITraitCollection?) {
        super.traitCollectionDidChange(previousTraitCollection)
        updateUIUserInterfaceStyle(traitCollection.userInterfaceStyle, "traitCollectionDidChange()")
    }
}
