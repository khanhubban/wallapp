//
//  CurrentScreenProviderIos.swift
//  WallApp
//

import WallApp

class CurrentScreenCoordinatorDefault: CurrentScreenCoordinatorIos {
    
    @MainActor
    func currentScreen() -> Screen? {
        let currentScreen = Router.shared.currentScreen()
        Log.d("[NativeNavigation] [CurrentScreenProviderIos] currentScreen: \(String(describing: currentScreen))")
        return currentScreen
    }
}
