//
//  WallpaperViewController.swift
//  WallApp
//

import WallApp
import UIKit

class ComposeWithLoadingViewController: UIViewController {
    
    let theme: Theme
    let render: RenderIos
    let screenArgument: ScreenArgument
    let viewModel: ViewModel
    private var appViewModelFactory: AppViewModelFactory {
        interopModules.appViewModelFactory
    }
    private var currentViewController: UIViewController?
    private var stateObservationTask: Task<Void, Never>?
    private var currentViewState: ViewState?
    private let loadingVC: LoadingViewController
    
    init(theme: Theme, render: RenderIos, screenArgument: ScreenArgument, viewModel: ViewModel) {
        self.theme = theme
        self.render = render
        self.screenArgument = screenArgument
        self.viewModel = viewModel
        self.loadingVC = LoadingViewController(theme: theme, render: render)
        super.init(nibName: nil, bundle: nil)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = theme.themeColors.background.uiColor
        Log.d("[ComposeWithLoadingViewController] [Lifecycle] viewDidLoad(), screenArgument: \(String(describing: screenArgument))")
        updateUIUserInterfaceStyle(traitCollection.userInterfaceStyle, "viewDidLoad()")
        observeViewState()
    }
    
    private func observeViewState() {
        stateObservationTask?.cancel()
        
        stateObservationTask = Task {
            guard let viewModel = self.viewModel as? ScreenViewStateProvider else {
                Log.e("[ComposeWithLoadingViewController] viewModel is not ScreenViewStateProvider")
                return
            }
            for await viewState in viewModel.viewState {
                if Task.isCancelled { break }
                DispatchQueue.main.async { [weak self] in
                    guard let self = self else { return }
                    Log.d("[ComposeWithLoadingViewController] viewState: \(viewState)")
                    if viewState.isLoading() {
                        self.manualTransition(to: self.loadingVC, animated: false)
                    } else {
                        let successVC = interopModules.mainViewController(screenArgument: screenArgument)
                        self.manualTransition(to: successVC, animated: true)
                    }
                }
                // Uncomment to simulate longer loading
//                if viewState.isLoading() {
//                    try? await Task.sleep(nanoseconds: 2_000_000_000)
//                }
                if !viewState.isLoading() { break }
            }
        }
    }
    
    private func manualTransition(to newViewController: UIViewController, animated: Bool = true) {
        addChild(newViewController)
        newViewController.view.frame = view.frame
        newViewController.view.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        newViewController.view.alpha = 0 // Start with new view transparent
        
        // Add the new view to the hierarchy
        view.addSubview(newViewController.view)
        
        if animated {
            UIView.animate(withDuration: 0.25, animations: {
                // Fade in the new view
                newViewController.view.alpha = 1
                
                // Fade out the old view, if there is one
                self.currentViewController?.view.alpha = 0
            }, completion: { _ in
                // Remove the old view controller
                self.currentViewController?.willMove(toParent: nil)
                self.currentViewController?.view.removeFromSuperview()
                self.currentViewController?.removeFromParent()
                
                // Finalize the new view controller setup
                newViewController.didMove(toParent: self)
                self.currentViewController = newViewController
            })
        } else {
            // For non-animated, just swap immediately
            currentViewController?.willMove(toParent: nil)
            currentViewController?.view.removeFromSuperview()
            currentViewController?.removeFromParent()
            
            newViewController.didMove(toParent: self)
            newViewController.view.alpha = 1
            currentViewController = newViewController
        }
    }
    
    deinit {
        stateObservationTask?.cancel()
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        Log.d("[ComposeWithLoadingViewController] [Lifecycle] viewDidAppear(), screenArgument: \(String(describing: screenArgument))")
    }
    
    override func traitCollectionDidChange(_ previousTraitCollection: UITraitCollection?) {
        super.traitCollectionDidChange(previousTraitCollection)
        
        updateUIUserInterfaceStyle(traitCollection.userInterfaceStyle, "traitCollectionDidChange()")
    }
    
    override func didReceiveMemoryWarning() {
        Log.d("[ComposeWithLoadingViewController] [Memory] didReceiveMemoryWarning()")
        super.didReceiveMemoryWarning()
    }
}
