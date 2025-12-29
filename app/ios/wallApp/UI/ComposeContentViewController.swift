//
//  ComposeContentViewController.swift
//  WallApp
//

import WallApp
import UIKit

class ComposeContentViewController: UIViewController {
    
    private let render: RenderIos
    private let theme: Theme
    private let screenArgument: ScreenArgument
    private var viewModelStoreOwner: SharedViewModelStoreOwner<ViewModel>
    private var composeViewController: ComposeWithLoadingViewController!
    private var router: Router
    
    init(render: RenderIos, theme: Theme, screenArgument: ScreenArgument, router: Router) {
        self.render = render
        self.theme = theme
        self.screenArgument = screenArgument
        self.router = router
        
        let viewModel = InteropModulesIos.shared.appViewModelFactory.createViewModelFor(argument: screenArgument) as! ViewModel
        self.viewModelStoreOwner = SharedViewModelStoreOwner<ViewModel>(viewModel)
        
        super.init(nibName: nil, bundle: nil)
        
        self.composeViewController = ComposeWithLoadingViewController(
            theme: theme,
            render: render,
            screenArgument: screenArgument,
            viewModel: viewModelStoreOwner.instance
        )
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        addChild(composeViewController)
        composeViewController.view.frame = self.view.bounds
        self.view.addSubview(composeViewController.view)
        composeViewController.didMove(toParent: self)
    }
    
    override func viewWillAppear(_ animated: Bool) {
        router.screenAppeared(screenArgument)
    }
}
