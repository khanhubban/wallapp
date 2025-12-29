//
//  AppUIViewController.swift
//  WallApp
//

import Foundation
import UIKit
import WallApp
import Combine


class AppUIViewController: UIViewController {
    private var screenArgument: ScreenArgument?
    
    init(screenArgument: ScreenArgument?) {
        Log.d("[AppUIViewController] [Lifecycle] init() - screenArgument: \(String(describing: screenArgument))")
        super.init(nibName: nil, bundle: nil)
        self.screenArgument = screenArgument
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    lazy var mainViewController: UIViewController = interopModules.mainViewController(screenArgument: screenArgument)
    var accountManager: AccountManager { return interopModules.accountManager }
    
    override func loadView() {
        super.loadView()
        Log.d("[AppUIViewController] [Lifecycle] loadView()")
        
        let mainViewController = self.mainViewController

        mainViewController.willMove(toParent: self)
        mainViewController.view.frame = view.frame
        view.addSubview(mainViewController.view)
        addChild(mainViewController)
        mainViewController.didMove(toParent: self)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        Log.d("[AppUIViewController] [Lifecycle] viewDidLoad(), screenArgument: \(String(describing: screenArgument))")
        
        updateUIUserInterfaceStyle(traitCollection.userInterfaceStyle, "viewDidLoad()")
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        Log.d("[AppUIViewController] [Lifecycle] viewWillAppear(), screenArgument: \(String(describing: screenArgument))")
    }

    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        Router.shared.screenAppeared(screenArgument)
        Log.d("[AppUIViewController] [Lifecycle] viewDidAppear(), screenArgument: \(String(describing: screenArgument))")
    }

    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        Log.d("[AppUIViewController] [Lifecycle] viewWillDisappear(), screenArgument: \(String(describing: screenArgument))")
    }

    override func viewDidDisappear(_ animated: Bool) {
        super.viewDidDisappear(animated)
        Log.d("[AppUIViewController] [Lifecycle] viewDidDisappear(), screenArgument: \(String(describing: screenArgument))")
    }

    override func viewWillLayoutSubviews() {
        super.viewWillLayoutSubviews()
        Log.d("[AppUIViewController] [Lifecycle] viewWillLayoutSubviews()")
    }

    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        Log.d("[AppUIViewController] [Lifecycle] viewDidLayoutSubviews()")
    }
    
    override func traitCollectionDidChange(_ previousTraitCollection: UITraitCollection?) {
        super.traitCollectionDidChange(previousTraitCollection)
        
        updateUIUserInterfaceStyle(traitCollection.userInterfaceStyle, "traitCollectionDidChange()")
    }
    
    override func didReceiveMemoryWarning() {
        Log.d("[AppUIViewController] [Memory] didReceiveMemoryWarning()")
        super.didReceiveMemoryWarning()
    }
}

extension AppUIViewController {
    static func create(from screenArgument: ScreenArgument?) -> AppUIViewController {
        Log.i("[AppDelegate] createViewController() - screenArgument: \(String(describing: screenArgument))")
        return AppUIViewController(screenArgument: screenArgument)
    }
}
