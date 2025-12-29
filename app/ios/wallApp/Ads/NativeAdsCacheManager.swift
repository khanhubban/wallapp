//
//  NativeAdsCache.swift
//  WallApp
//

import WallApp
import Foundation
import GoogleMobileAds

final class NativeAdsCacheManager {
    
    private var cache: [Int: GADNativeAd] = [:]
    private var adLoaders: [Int: NativeAdLoader] = [:]
    private var allAdIndices: [Int] = []
    private var lastFetchedIndex: Int = -1
    
    private var maxPrefetchCount: Int = 2
    
    var cacheCount: Int {
        return cache.count
    }
    
    func prefetchAds(for feedViews: [CommonView]) {
        Log.d("[NativeAdsCacheManager] prefetchAds for feedViews")
        allAdIndices = []
        feedViews.enumerated().forEach { index, view in
            if view.viewState is FeedAdViewState {
                allAdIndices.append(index)
            }
        }
        prefetchAds()
    }
    
    private func prefetchAds() {
        Log.d("[NativeAdsCacheManager] prefetchAds, lastFetchedIndex: \(lastFetchedIndex)")
        let prefetchIndices = allAdIndices.filter { $0 > lastFetchedIndex }
        for index in prefetchIndices.prefix(maxPrefetchCount) {
            fetchAdInternal(for: index)
        }
    }
    
    func prefetchAd(for index: Int) {
        Log.d("[NativeAdsCacheManager] prefetch for index: \(index)")
        fetchAdInternal(for: index)
    }
    
    func fetchAd(for index: Int, fetchAdResult: @escaping (NativeAdResult) -> Void) {
        Log.d("[NativeAdsCacheManager] fetch for index: \(index)")
        fetchAdInternal(for: index, fetchAdResult: fetchAdResult)
        prefetchAds()
    }
    
    private func fetchAdInternal(for index: Int, fetchAdResult: ((NativeAdResult) -> Void)? = nil) {
        Log.d("[NativeAdsCacheManager] fetch for index: \(index), exists: \(cache[index] != nil)")
        if let ad = cache[index] {
            if let fetchAdResult {
                updateLastFetchedIndex(index)
                fetchAdResult(.success(ad, index))
            }
        } else {
            
            let cachedLoader = adLoaders[index]
            // If ad is already loading and a new callback is not provided, return
            if cachedLoader != nil && fetchAdResult == nil {
                Log.d("[NativeAdsCacheManager] ad is already loading for index: \(index), no callback provided")
                return
            }
            
            // Create a new callback with the given callback
            let loaderFetchAdResult: (NativeAdResult) -> Void = { [weak self] result in
                guard let self else { return }
                self.adLoaders[index] = nil
                switch result {
                case .success(let ad, let resultIndex):
                    Log.d("[NativeAdsCacheManager] ad loaded and cached for index: \(resultIndex)")
                    self.cache[resultIndex] = ad
                    if let fetchAdResult {
                        self.updateLastFetchedIndex(resultIndex)
                        fetchAdResult(.success(ad, resultIndex))
                    }
                case .failure:
                    fetchAdResult?(.failure)
                }
            }
            
            // If ad is already loading and a new callback is provided, set the new callback
            if let cachedLoader {
                Log.d("[NativeAdsCacheManager] ad is already loading for index: \(index), setting new callback")
                cachedLoader.fetchAdResult = loaderFetchAdResult
                return
            }
            
            // Load ad
            let nativeAdLoader = NativeAdLoader(index: index)
            adLoaders[index] = nativeAdLoader
            nativeAdLoader.loadAd(fetchAdResult: loaderFetchAdResult)
        }
    }
    
    private func updateLastFetchedIndex(_ index: Int) {
        Log.d("[NativeAdsCacheManager] updateLastFetchedIndex, index: \(index), lastFetchedIndex: \(lastFetchedIndex)")
        lastFetchedIndex = max(lastFetchedIndex, index)
    }
}

enum NativeAdResult {
    case success(GADNativeAd, Int)
    case failure
}


class NativeAdLoader: NSObject {
    
    var adLoader: GADAdLoader!
    
    var index: Int = 0
    
    var fetchAdResult: ((NativeAdResult) -> Void)?
    
    init(index: Int) {
        self.index = index
        adLoader = GADAdLoader(
            adUnitID: InteropModulesIos.shared.adUnitIds.feedVideoAdUnitId?.id ?? "",
            rootViewController: nil,
            adTypes: [.native],
            options: nil
        )
    }
    
    func loadAd(fetchAdResult: @escaping (NativeAdResult) -> Void) {
        self.fetchAdResult = fetchAdResult
        self.adLoader.delegate = self
        self.adLoader.load(GADRequest())
    }
    
    func isAdLoading() -> Bool {
        return adLoader.isLoading
    }
}

extension NativeAdLoader: GADNativeAdLoaderDelegate {
    
    func adLoader(_ adLoader: GADAdLoader, didReceive nativeAd: GADNativeAd) {
        Log.d("[NativeAdLoader] Received native ad for index: \(index), fetchAdResult is nil: \(fetchAdResult == nil)")
        fetchAdResult?(.success(nativeAd, index))
    }
    
    func adLoader(_ adLoader: GADAdLoader, didFailToReceiveAdWithError error: Error) {
        Log.e("[NativeAdLoader] Failed to receive ad with error: \(error.localizedDescription)")
        fetchAdResult?(.failure)
    }
    
    func adLoaderDidFinishLoading(_ adLoader: GADAdLoader) {
        Log.d("[NativeAdLoader] Finished loading ad for index: \(index)")
    }
}

extension NativeAdLoader: GADNativeAdDelegate {
    func nativeAdDidRecordClick(_ nativeAd: GADNativeAd) {
        Log.d("[NativeAdLoader] \(#function) called for index: \(index)")
    }
    
    func nativeAdDidRecordImpression(_ nativeAd: GADNativeAd) {
        Log.d("[NativeAdLoader] \(#function) called for index: \(index)")
    }
    
    func nativeAdWillPresentScreen(_ nativeAd: GADNativeAd) {
        Log.d("[NativeAdLoader] \(#function) called for index: \(index)")
    }
    
    func nativeAdWillDismissScreen(_ nativeAd: GADNativeAd) {
        Log.d("[NativeAdLoader] \(#function) called for index: \(index)")
    }
    
    func nativeAdDidDismissScreen(_ nativeAd: GADNativeAd) {
        Log.d("[NativeAdLoader] \(#function) called for index: \(index)")
    }
}
