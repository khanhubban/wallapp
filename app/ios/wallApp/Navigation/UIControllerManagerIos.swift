//
//  UIControllerManagerIos.swift
//  WallApp
//

import WallApp
import Foundation

final class UIControllerManagerIos: UiControllerManager {
    var currentUiController: UiController? {
        if let uiViewController = UIApplication.shared.topViewController() {
            return UiControllerIos(uiViewController: uiViewController)
        } else {
            return nil
        }
    }
}
