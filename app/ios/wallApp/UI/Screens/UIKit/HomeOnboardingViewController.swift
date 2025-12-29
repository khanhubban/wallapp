//
//  HomeOnboardingViewController.swift
//  WallApp
//

import WallApp
import UIKit

class HomeOnboardingViewController: UIViewController {
    var theme: Theme {
        didSet {
            updateTheme()
        }
    }
    var render: RenderIos
    private var viewState: HomeOnboardingViewState? {
        didSet {
            updateViewState()
        }
    }
    
    var toolbarOffsetController = ScrollOffsetController()
    var navBarOffsetController: ScrollOffsetController
    
    private var feedGridViewController: FeedGridViewController?
    private var loadingViewController: LoadingViewController?
    
    private var viewStateObserveTask: Task<Void, Never>?
    private var themeObserverTask: Task<Void, Never>?
    
    init(theme: Theme, render: RenderIos, navBarOffsetController: ScrollOffsetController) {
        self.theme = theme
        self.render = render
        self.navBarOffsetController = navBarOffsetController
        super.init(nibName: nil, bundle: nil)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
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
            guard let vm = InteropModulesIos.shared.appViewModelFactory.createViewModelFor(
                argument: ScreenArgument.HomeOnboardingScreenArgument()
            ) as? HomeOnboardingViewModel else { return }
            for await viewState in vm.viewState {
                Log.d("[ExploreViewUIKit] Received view state")
                self.viewState = viewState
            }
        }
        themeObserverTask?.cancel()
        themeObserverTask = Task {
            for await theme in interopModules.themeManager.theme {
                self.theme = theme
            }
        }
    }
    
    private func updateTheme() {
        updateBackground()
        feedGridViewController?.update(theme: theme)
    }
    
    private func updateViewState() {
        switch onEnum(of: viewState) {
        case .success(let data):
            displayFeedGrid(data: data)
        default:
            displayLoadingView()
        }
    }
    
    private func displayFeedGrid(data: HomeOnboardingViewState.Success) {
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
        
        if feedGridViewController == nil {
            let feedVC = FeedGridViewController(
                render: render,
                theme: theme,
                feedViewState: data.feedViewState,
                pagingViewEventSink: nil,
                scrollOffsetControllers: [toolbarOffsetController, navBarOffsetController]
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
            feedGridViewController!.update(viewState: data.feedViewState)
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
        Log.d("[Lifecycle] viewDidAppear(), screen: HomeOnboardingViewController")
        Router.shared.screenAppeared(ScreenArgument.HomeOnboardingScreenArgument())
    }
    
    override func traitCollectionDidChange(_ previousTraitCollection: UITraitCollection?) {
        super.traitCollectionDidChange(previousTraitCollection)
        updateUIUserInterfaceStyle(traitCollection.userInterfaceStyle, "traitCollectionDidChange()")
    }
}
