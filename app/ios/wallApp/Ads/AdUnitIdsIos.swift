//
//  AdUnitIdsIos.swift
//  WallApp
//

import WallApp
import Foundation

class AdUnitIdsIos: AdUnitIds {
    
    var appOpenAdUnitId: AppOpenAdUnitId? {
        AppOpenAdUnitId(id: getAdUnitId(for: "appOpenAdUnitId") ?? "")
    }
    
    var feedAdUnitId: InlineAdUnitId? {
        InlineAdUnitId(id: getAdUnitId(for: "feedAdUnitId") ?? "")
    }
    
    var feedVideoAdUnitId: InlineAdUnitId? {
        InlineAdUnitId(id: getAdUnitId(for: "feedAdUnitId") ?? "")
    }
    
    var interstitialAdUnitId: InterstitialAdUnitId? = nil
    
    var rewardAdUnitId: RewardAdUnitId? {
        RewardAdUnitId(id: getAdUnitId(for: "rewardAdUnitId") ?? "")
    }
    
    var rewardInterstitialAdUnitId: RewardInterstitialAdUnitId? = nil
    
    private func getAdUnitId(for key: String) -> String? {
        if let path = Bundle.main.path(forResource: "Info", ofType: "plist"), let nsDictionary = NSDictionary(contentsOfFile: path) as? [String: Any] {
            let adUnitIds = nsDictionary["AdUnitIds"] as? [String: String]
            return adUnitIds?[key]
        }
        return nil
    }
}
