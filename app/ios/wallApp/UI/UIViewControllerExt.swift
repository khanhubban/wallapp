//
//  UIViewControllerExt.swift
//  WallApp
//

import Foundation
import UIKit
import WallApp

extension UIViewController {
    
    var interopModules: InteropModulesIos { return InteropModulesIos.shared }
    
    var systemTheme: SystemThemeIos { return interopModules.systemTheme }
    var windowFrameManager: WindowFrameManagerDefault { return interopModules.windowFrameManager }
    var signInProviderController: SignInProviderController { return interopModules.signInProviderController }
    
    func updateWindowFrame() {
        updateWindowSize()
        updateWindowInsets()
    }
    
    func updateWindowSize() {
        windowFrameManager.updateSize()
    }
    
    func updateWindowInsets() {
        //        windowFrameManager.setInsetsPx(
        //            statusBarHeight: Float(view.safeAreaInsets.top),
        //            navBarHeight: Float(view.safeAreaInsets.bottom)
        //        )
    }
    
    func updateUIUserInterfaceStyle(
        _ currentStyle: UIUserInterfaceStyle?,
        _ via: String
    ) {
        //        let prefix = "[InterfaceStyle via \(via)]: "
        if (currentStyle == nil) {
            //            print("\(prefix) nil!")
            return
        }
        
        switch currentStyle {
        case .dark:
            systemTheme.setIsDarkTheme()
            //            print("\(prefix) Dark Mode")
        case .light:
            systemTheme.setIsLightTheme()
            //            print("\(prefix) Light Mode")
            //        case .none:
            //            print("\(prefix) None")
            //        case .unspecified:
            //            print("\(prefix) Unspecified")
            //        @unknown default:
            //            print("\(prefix) Unknown")
        default:
            break
        }
    }
}

extension UIViewController {
    public func displayError(_ error: Error?, from function: StaticString = #function) {
        guard let error = error else { return }
        Log.e("ⓧ Error in \(function): \(error.localizedDescription)")
        let message = "\(error.localizedDescription)\n\n Ocurred in \(function)"
        let errorAlertController = UIAlertController(
            title: "Error",
            message: message,
            preferredStyle: .alert
        )
        errorAlertController.addAction(UIAlertAction(title: "OK", style: .default))
        present(errorAlertController, animated: true, completion: nil)
    }
}
