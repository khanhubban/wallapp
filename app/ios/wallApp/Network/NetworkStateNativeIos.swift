//
//  NetworkStateNativeDefault.swift
//  WallApp
//

import WallApp
import Foundation
import Network

final class NetworkStateNativeIos: NetworkStateNative {
    
    var listeners: [((NetworkConnectionState) -> Void)] = []
    let monitor = NWPathMonitor()
    
    init() {
        monitor.pathUpdateHandler = { path in
            Log.d("[NetworkStateNativeDefault] Path updated: \(path)")
            let state = self.getState(path: path)
            self.listeners.forEach { $0(state) }
        }
        monitor.start(queue: DispatchQueue(label: "wallapp.networkstatemonitor"))
    }
    
    func registerNetworkStateListener(listener: @escaping (NetworkConnectionState) -> Void) {
        listeners.append(listener)
        listener(getState(path: monitor.currentPath))
    }
    
    private func getState(path: NWPath) -> NetworkConnectionState {
        if path.status == .satisfied {
            return .connected
        } else {
            return .disconnected
        }
    }
}
