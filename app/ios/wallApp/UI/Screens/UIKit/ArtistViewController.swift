//
//  ArtistViewController.swift
//  WallApp
//

import WallApp
import UIKit

extension UIView {
    class func fromNib<T: UIView>() -> T {
        return Bundle.main.loadNibNamed(String(describing: T.self), owner: nil, options: nil)![0] as! T
    }
}

class TabSelectionController: ObservableObject {
    @Published var tabIndex = 0
}

class ArtistViewController : UIViewController {
    
    private let screenArgument: ScreenArgument.ArtistIdScreenArgument
    var theme: Theme {
        didSet {
            updateTheme()
        }
    }
    var render: RenderIos
    private var viewState: ArtistViewState? {
        didSet {
            updateViewState()
        }
    }
    private var viewModel: ArtistViewModel
    
    private var loadingViewController: LoadingViewController?
    
    private var viewStateObserveTask: Task<Void, Never>?
    private var themeObserverTask: Task<Void, Never>?
    
    var toolbarOffsetController = ScrollOffsetController()
    private var feedGridViewController: FeedGridViewController?
    private var artistHeaderView: UIArtistHeaderView?
    var commonPageView: CommonPageView?
    var btnBack: UIImageView?
    private var stickyStatusBarView: UIStatusBarView!
    
    var tabSelection = TabSelectionController()
    var cancellable: AnyCancellable?
    var arrViewController: [UIViewController] = []
    private var messageBar: UIMessageBarView!
    
    let messageBarVerticalStackView: UIStackView = {
        let stackView = UIStackView()
        stackView.axis = .vertical
        stackView.alignment = .center
        stackView.distribution = .fill
        stackView.spacing = 5
        return stackView
    }()
    
    init(theme: Theme, render: RenderIos, screenArgument: ScreenArgument.ArtistIdScreenArgument, viewModel: ArtistViewModel) {
        self.theme = theme
        self.render = render
        self.screenArgument = screenArgument
        self.viewModel = viewModel
        super.init(nibName: nil, bundle: nil)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        updateBackground()
        observeStates()
        self.navigationItem.hidesBackButton = true
    }
    
    private func updateBackground() {
        view.backgroundColor = theme.themeColors.background.uiColor
        if stickyStatusBarView != nil {
            stickyStatusBarView.updateWith(theme: self.theme)
        }
        
        if artistHeaderView != nil {
            artistHeaderView?.theme = self.theme
        }

    }
    
    private func observeStates() {
        viewStateObserveTask?.cancel()
        viewStateObserveTask = Task {
            for await viewState in viewModel.viewState {
                Log.d("[ArtistViewController] Received view state")
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
        // Update background color
        updateBackground()
        // TODO: Update theme in tabs
    }
    
    // Handling ViewState Changes
    private func updateViewState() {
        switch onEnum(of: viewState) {
        case .success(let data):
            setupToolbarAndDisplayTabs(data: data)
        default:
            displayLoadingView()
        }
    }
    
    private func setupToolbarAndDisplayTabs(data: ArtistViewState.Success) {
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
        
        if stickyStatusBarView == nil {
            stickyStatusBarView = UIStatusBarView(render: render, theme: theme, color: data.artistToolbar.containerColorOverride.toColor(themeColors: self.theme.themeColors)?.uiColor())
            self.view.addSubview(stickyStatusBarView)
            
            stickyStatusBarView.translatesAutoresizingMaskIntoConstraints = false
            NSLayoutConstraint.activate([
                stickyStatusBarView.topAnchor.constraint(equalTo: self.view.topAnchor),
                stickyStatusBarView.leadingAnchor.constraint(equalTo: self.view.leadingAnchor),
                stickyStatusBarView.trailingAnchor.constraint(equalTo: self.view.trailingAnchor)
            ])
        }
        
        if messageBar == nil {
            self.messageBar = UIMessageBarView(render: render, theme: theme, viewState: data.artistToolbar.messageBar)
            messageBarVerticalStackView.addArrangedSubview(self.messageBar)
            
            let spacer = UIView(frame: CGRect(x: 0, y: 0, width: 10, height: 10))
            messageBarVerticalStackView.addArrangedSubview(spacer)
  
            self.view.addSubview(messageBarVerticalStackView)
            messageBarVerticalStackView.translatesAutoresizingMaskIntoConstraints = false
            NSLayoutConstraint.activate([
                messageBarVerticalStackView.topAnchor.constraint(equalTo: self.view.topAnchor, constant: render.windowFrame.statusBarHeight.toCGFloat()),
                messageBarVerticalStackView.leadingAnchor.constraint(equalTo: self.view.leadingAnchor, constant: 0),
                messageBarVerticalStackView.trailingAnchor.constraint(equalTo: self.view.trailingAnchor, constant: 0),
                messageBarVerticalStackView.heightAnchor.constraint(equalToConstant: data.artistToolbar.messageBar == nil ? 0.0 : (CGFloat(data.artistToolbar.messageBar?.viewSpec.maxHeight ?? 30) + 10.0))
            ])
            
            let color = data.artistToolbar.containerColorOverride.toColor(themeColors: self.theme.themeColors)?.uiColor()
            messageBarVerticalStackView.backgroundColor = color
        } else {
            messageBar.updateWith(viewState: data.artistToolbar.messageBar)
            if let heightConstant = messageBarVerticalStackView.constraints.first(where: { $0.firstAttribute == .height }) {
                UIView.animate(withDuration: 0.4) {
                    heightConstant.constant = data.artistToolbar.messageBar == nil ? 0.0 : (CGFloat(data.artistToolbar.messageBar?.viewSpec.maxHeight ?? 30) + 10.0)
                    self.view.layoutIfNeeded()
                }
            }
            
        }
   
        guard let tabsViewState = data.tabs as? TabsViewState.Indicator else {
            fatalError("Expected TabsViewState.Indicator, but got \(type(of: data.tabs))")
        }

        if artistHeaderView == nil {
            self.artistHeaderView = UIArtistHeaderView(render: self.render, theme: self.theme, viewState: data.artistToolbar, viewSpec: data.viewSpec, tabs: tabsViewState, scrollOffsetController: self.toolbarOffsetController, selectedTab: tabSelection)
            guard let headerView = self.artistHeaderView else { return }
            self.view.addSubview(headerView)
            headerView.translatesAutoresizingMaskIntoConstraints = false
            NSLayoutConstraint.activate([
                headerView.topAnchor.constraint(equalTo: messageBarVerticalStackView.bottomAnchor, constant: 0),
                headerView.leadingAnchor.constraint(equalTo: self.view.leadingAnchor),
                headerView.trailingAnchor.constraint(equalTo: self.view.trailingAnchor),
            ])
            
            headerView.setup()
            self.artistHeaderView = headerView
        } else {
            self.artistHeaderView?.update(render: self.render, theme: self.theme, viewState: data.artistToolbar, viewSpec: data.viewSpec, tabs: tabsViewState)
        }
        
        let tabs = data.tabs.tabs
        
        if self.btnBack == nil {
            self.btnBack = UIImageView(image: UIImage(systemName: "chevron.backward")?.withRenderingMode(.alwaysTemplate))
            self.btnBack?.contentMode = .center
            guard let backImage = self.btnBack else { return }
            self.view.insertSubview(backImage, aboveSubview: self.artistHeaderView!)
            backImage.translatesAutoresizingMaskIntoConstraints = false
            NSLayoutConstraint.activate([
                backImage.topAnchor.constraint(equalTo: self.artistHeaderView!.topAnchor, constant: 0),
                backImage.leadingAnchor.constraint(equalTo: self.view.leadingAnchor, constant: 0),
                backImage.heightAnchor.constraint(equalToConstant: render.defaultViewSpec.iconButtonLayerSize.toCGFloat()),
                backImage.widthAnchor.constraint(equalToConstant: render.defaultViewSpec.iconButtonLayerSize.toCGFloat())
            ])
        }
        
        if let menuItem = data.artistToolbar.toolbarViewState.navigationIcon as? MenuItem.MenuItemIcon, let click = menuItem.onClick {
            let color = menuItem.tintColor?.toColor(themeColors: theme.themeColors)
            self.btnBack?.tintColor = color?.uiColor()
            self.btnBack?.addTapGesture { [unowned self] in
                click.invoke()
            }
        }
        
        if self.commonPageView == nil {
            let commonPageView = CommonPageView()
            self.view.insertSubview(commonPageView, belowSubview: self.artistHeaderView!)
            commonPageView.translatesAutoresizingMaskIntoConstraints = false
            
            NSLayoutConstraint.activate([
                commonPageView.topAnchor.constraint(equalTo: self.artistHeaderView!.bottomAnchor, constant: -data.tabs.viewSpec.tabHeight.toCGFloat()),
                commonPageView.bottomAnchor.constraint(equalTo: self.view.bottomAnchor, constant: 0),
                commonPageView.leadingAnchor.constraint(equalTo: self.view.leadingAnchor, constant: 0),
                commonPageView.trailingAnchor.constraint(equalTo: self.view.trailingAnchor, constant: 0)
            ])
            
            self.commonPageView = commonPageView
            self.commonPageView!.setUpView()
        }
        
        let tabsHaveEqualViews = tabs.compactMap { $0.view }.count == tabs.count
        if arrViewController.count < tabs.count && tabsHaveEqualViews {
            tabs.indices.forEach { index in
                let tab = tabs[index]
                if let feedView = tab.view, let viewState = feedView.viewState as? FeedViewState {
                    let feedVC = FeedGridViewController(
                        render: self.render,
                        theme: self.theme,
                        feedViewState: viewState,
                        pagingViewEventSink: viewState.feedState?.pagingViewEventSink,
                        scrollOffsetControllers: [self.toolbarOffsetController]
                    )
                    arrViewController.insert(feedVC, at: index)
                }
            }
            self.commonPageView!.arrViewController = arrViewController
        } else if tabsHaveEqualViews {
            arrViewController.forEach { viewController in
                tabs.indices.forEach { index in
                    let tab = tabs[index]
                    if let vc = arrViewController[safe: index] as? FeedGridViewController, let feedView = tab.view, let viewState = feedView.viewState as? FeedViewState {
                        vc.update(viewState: viewState)
                    }
                }
            }
        }
        
        if cancellable == nil {
            cancellable = self.tabSelection.$tabIndex.sink(receiveValue: { value in
                NSObject.cancelPreviousPerformRequests(withTarget: self)
                self.perform(#selector(self.setViewController), with: nil, afterDelay: 0.1)
            })
        }
        
        if self.commonPageView?.selectionChange == nil {
            self.commonPageView?.selectionChange = { [unowned self] index in
                self.tabSelection.tabIndex = index
                self.updateIndicator()
            }
        }
    }
    
    func updateIndicator() {
        self.artistHeaderView?.updateIndicator()
    }
    
    @objc func setViewController() {
        Log.d("[ArtistViewController] Setting view controller: \(tabSelection.tabIndex)")
        self.commonPageView?.setViewController(index: tabSelection.tabIndex)
    }
    
    private func displayLoadingView() {
        // TODO: Remove toolbar if present
        // TODO: Remove tabs view if present
        
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
        cancellable?.cancel()
    }
    
    override func viewDidAppear(_ animated: Bool) {
        Log.d("[ArtistViewController] [Lifecycle] viewDidAppear()")
        Router.shared.screenAppeared(screenArgument)
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        self.navigationController?.isNavigationBarHidden = true
        self.navigationItem.hidesBackButton = true
    }
    
    override func traitCollectionDidChange(_ previousTraitCollection: UITraitCollection?) {
        super.traitCollectionDidChange(previousTraitCollection)
        updateUIUserInterfaceStyle(traitCollection.userInterfaceStyle, "traitCollectionDidChange()")
    }
}
