//
//  UIApplicationExtensions.swift
//  WallApp
//

import Foundation
import UIKit

extension UIApplication {
    
    var keyWindow: UIWindow? {
        connectedScenes
            .compactMap {
                $0 as? UIWindowScene
            }
            .flatMap {
                $0.windows
            }
            .first {
                $0.isKeyWindow
            }
    }
    
    public var rootViewController: UIViewController? {
        guard let keyWindow = UIApplication.shared.keyWindow, let rootViewController = keyWindow.rootViewController else {
            return nil
        }
        return rootViewController
    }
    
    public func topViewController(controller: UIViewController? = UIApplication.shared.rootViewController) -> UIViewController? {
        
        if controller == nil {
            return topViewController(controller: rootViewController)
        }
        
        if let navigationController = controller as? UINavigationController {
            return topViewController(controller: navigationController.visibleViewController)
        }
        
        if let tabController = controller as? UITabBarController {
            if let selectedViewController = tabController.selectedViewController {
                return topViewController(controller: selectedViewController)
            }
        }
        
        if let presentedViewController = controller?.presentedViewController {
            return topViewController(controller: presentedViewController)
        }
        
        return controller
    }
}
