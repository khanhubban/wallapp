//
//  RewardAdCoordinator.swift
//  WallApp
//

import WallApp
import Foundation
import GoogleMobileAds

class RewardAdCoordinatorDefault : NSObject, ObservableObject, RewardAdCoordinator {
    
    static let shared = RewardAdCoordinatorDefault()
    private override init() {
        super.init()
    }
    
    private var rewardedAd: GADRewardedAd? = nil
    
    private lazy var adInitialiserState = InteropModulesIos.shared.adInitializerState
    private lazy var uiControllerManager = InteropModulesIos.shared.uiControllerManager
    
    private var showRewardAdRequested: Bool = false
    
    private var showRewardAdRetries = 0
    private let maxShowRewardAdRetries = 3
    
    private var rewardAdHandleManager: RewardAdHandleManager {
        InteropModulesIos.shared.rewardAdHandleManager
    }
    private var rewardAdStateCallbacks: [(RewardAdState) -> Void] = []
    @Published var currentRewardAdState: RewardAdState = RewardAdState.Unloaded() {
        didSet {
            Log.d("[RewardedAd] Updated currentRewardAdState: \(currentRewardAdState)")
            rewardAdStateCallbacks.forEach { $0(currentRewardAdState) }
        }
    }
    private var currentRewardAdHandle: RewardAdHandle? = nil
    private var rewardAdPlaybackCallbacks: RewardAdPlaybackCallbacks? {
        currentRewardAdHandle?.playbackCallbacks
    }
    
    private var retryingShowAdTask: Task<Void, Never>? = nil
    
    func register(rewardAdStateCallback: @escaping (RewardAdState) -> Void) {
        rewardAdStateCallbacks.append(rewardAdStateCallback)
    }
    
    func unregister(rewardAdStateCallback: @escaping (RewardAdState) -> Void) {
    }
    
    func loadRewardAd(rewardAdPlaybackCallbacks: RewardAdPlaybackCallbacks?) {
        Task {
            await loadAd(rewardAdPlaybackCallbacks)
        }
    }
    
    func showRewardAd(rewardAdPlaybackCallbacks: RewardAdPlaybackCallbacks) {
        Task {
            await showAd(rewardAdPlaybackCallbacks)
        }
    }
    
    private func getOrCreateRewardAdHandle(rewardAdPlaybackCallbacks: RewardAdPlaybackCallbacks?) -> RewardAdHandle {
        if currentRewardAdHandle == nil {
            currentRewardAdHandle = rewardAdHandleManager.createRewardAdHandle(rewardAdPlaybackCallbacks: rewardAdPlaybackCallbacks)
            Log.d("[RewardedAd] Created new RewardAdHandle: \(String(describing: currentRewardAdHandle))")
        }
        return currentRewardAdHandle!
    }
    
    private func clearCurrentRewardAdHandle() {
        Log.d("[RewardedAd] clearCurrentRewardAdHandle(), currentRewardAdHandle: \(String(describing: currentRewardAdHandle))")
        if let currentRewardAdHandle {
            rewardAdHandleManager.destroyRewardAdHandle(rewardAdHandle: currentRewardAdHandle)
            self.currentRewardAdHandle = nil
        }
    }
    
    private func loadAd(_ rewardAdPlaybackCallbacks: RewardAdPlaybackCallbacks?) async {
        Log.d("[RewardedAd] loadRewardAd()")
        if !adInitialiserState.initializeAds || currentRewardAdState is RewardAdState.Loading || currentRewardAdState is RewardAdState.Loaded {
            Log.d("[RewardedAd] loadRewardAd(): loadAd failed, currentRewardAdState: \(currentRewardAdState), initializeAds: \(adInitialiserState.initializeAds)")
            return
        }
        if currentRewardAdHandle != nil {
            Log.w("[RewardedAd] loadRewardAd(): currentRewardAdHandle is not null, clearing it - current rewardAdState: \(currentRewardAdState)")
            clearCurrentRewardAdHandle()
        }
        let _ = getOrCreateRewardAdHandle(rewardAdPlaybackCallbacks: rewardAdPlaybackCallbacks)
        currentRewardAdState = RewardAdState.Loading()
        let rewardedAd: GADRewardedAd? = await withCheckedContinuation { continuation in
            GADRewardedAd.load(withAdUnitID: InteropModulesIos.shared.adUnitIds.rewardAdUnitId?.id ?? "",
                               request: GADRequest()) { [self] ad, error in
                if let error {
                    Log.d("[RewardedAd] Failed to load rewarded ad with error: \(error.localizedDescription)")
                    let nsError = error as NSError
                    let adErrorCode = getAdErrorCode(nsError.code)
                    currentRewardAdState = RewardAdState.ErrorErrorFailedToLoad()
                    rewardAdPlaybackCallbacks?.onRewardAdFailedToShow(
                        adError: AdError(code: adErrorCode, message: error.localizedDescription, domain: nsError.domain)
                    )
                    continuation.resume(returning: nil)
                } else {
                    currentRewardAdState = RewardAdState.Loaded()
                    continuation.resume(returning: ad)
                }
            }
        }
        guard let rewardedAd = rewardedAd else { return }
        Log.d("[RewardedAd] Reward ad loaded")
        self.rewardedAd = rewardedAd
        if showRewardAdRequested {
            showRewardAdRequested = false
            tryAndShowRewardAdAsync()
        }
    }
    
    private func showAdAsync(_ rewardAdPlaybackCallbacks: RewardAdPlaybackCallbacks) {
        Task {
            await showAd(rewardAdPlaybackCallbacks)
        }
    }
    
    private func showAdAsyncWithDelay(delayInMilliseconds: UInt64) {
        retryingShowAdTask = Task {
            if delayInMilliseconds > 0 {
                Log.d("[RewardedAd] Retrying showAd in \(delayInMilliseconds)ms")
                try? await Task.sleep(nanoseconds: delayInMilliseconds * 1_000_000)
            }
            await tryAndShowRewardAd()
        }
    }
    
    @MainActor private func showAd(_ rewardAdPlaybackCallbacks: RewardAdPlaybackCallbacks) {
        Log.d("[RewardedAd] RewardAdCoordinator.showAd(), currentRewardAdState: \(currentRewardAdState)")
        if currentRewardAdState is RewardAdState.Loading {
            Log.d("[RewardedAd] Ad is loading, cannot show")
            showRewardAdRequested = true
            validateAndUpdateCurrentRewardAdHandle(rewardAdPlaybackCallbacks)
            return
        } else if currentRewardAdState.canLoad {
            showRewardAdRequested = true
            loadRewardAd(rewardAdPlaybackCallbacks: rewardAdPlaybackCallbacks)
            return
        } else {
            showRewardAdRetries = 0
            retryingShowAdTask?.cancel()
            validateAndUpdateCurrentRewardAdHandle(rewardAdPlaybackCallbacks)
            tryAndShowRewardAd()
        }
    }
    
    private func validateAndUpdateCurrentRewardAdHandle(_ rewardAdPlaybackCallbacks: RewardAdPlaybackCallbacks) {
        guard let currentRewardAdHandle else {
            Log.w("currentRewardAdHandle should not be null when showing a loaded ad")
            return
        }
        if currentRewardAdHandle.playbackCallbacks == nil {
            Log.d("[RewardedAd] Updating rewardAdHandle callbacks, new: \(rewardAdPlaybackCallbacks)")
            self.currentRewardAdHandle = rewardAdHandleManager.updateRewardHandleCallbacks(
                rewardAdHandle: currentRewardAdHandle,
                rewardAdPlaybackCallbacks: rewardAdPlaybackCallbacks
            )
        }
    }
    
    private func tryAndShowRewardAdAsync() {
        Task {
            await tryAndShowRewardAd()
        }
    }
    
    @MainActor private func tryAndShowRewardAd() {
        if !(currentRewardAdState is RewardAdState.Loaded) || currentRewardAdHandle == nil {
            Log.d("[RewardedAd] Ad not loaded, cannot show")
            return
        }
        guard let rewardedAd = rewardedAd else {
            Log.w("[RewardedAd] Ad not loaded")
            return
        }
        guard let currentUiViewController = uiControllerManager.currentUiViewController else {
            Log.w("[RewardedAd] No current UIViewController")
            return
        }
        rewardedAd.fullScreenContentDelegate = self
        do {
            try rewardedAd.canPresent(fromRootViewController: currentUiViewController)
        } catch {
            Log.w("[RewardedAd] Cannot present ad, error: \(error.localizedDescription), retrying")
            if showRewardAdRetries < maxShowRewardAdRetries {
                showRewardAdRetries += 1
                showAdAsyncWithDelay(delayInMilliseconds: 500)
            }
            return
        }
        rewardedAd.present(fromRootViewController: currentUiViewController) {
            let reward = rewardedAd.adReward
            Log.d("[RewardedAd] Reward received with currency \(reward.amount) and reason \(reward.type), \(self.currentRewardAdHandle)")
            self.rewardAdPlaybackCallbacks?.onUserEarnedReward()
        }
        showRewardAdRetries = 0
    }
    
    private func getAdErrorCode(_ nsErrorCode: Int) -> AdErrorCode {
        let gadErrorCode = GADErrorCode(rawValue: nsErrorCode)
        switch gadErrorCode {
        case .invalidRequest:
            return .invalidRequest
        case .noFill:
            return .noFill
        case .serverError, .timeout, .networkError:
            return .networkError
        case .internalError:
            return .internalError
        case .mediationNoFill:
            return .mediationNoFill
        case .adAlreadyUsed:
            return .adAlreadyUsed
        case .invalidArgument:
            return .invalidArgument
        case .receivedInvalidResponse:
            return .invalidResponse
        default:
            return .unknownError
        }
    }
}

extension RewardAdCoordinatorDefault: GADFullScreenContentDelegate {
    
    func adDidRecordImpression(_ ad: GADFullScreenPresentingAd) {
        Log.d("[RewardedAd] Ad did record impression")
        if let currentRewardAdHandle {
            self.rewardAdPlaybackCallbacks?.onRewardAdImpression()
            self.currentRewardAdState = RewardAdState.Showing()
        }
    }
    
    func adWillPresentFullScreenContent(_ ad: any GADFullScreenPresentingAd) {
        Log.d("[RewardedAd] Ad will present full screen content")
        if let currentRewardAdHandle {
            self.rewardAdPlaybackCallbacks?.onRewardAdShowed()
        }
    }
    
    func adDidDismissFullScreenContent(_ ad: GADFullScreenPresentingAd) {
        Log.d("[RewardedAd] Ad did dismiss full screen content")
        if let currentRewardAdHandle {
            self.currentRewardAdHandle?.playbackCallbacks?.onRewardAdClosed()
            self.currentRewardAdState = RewardAdState.Unloaded()
            clearCurrentRewardAdHandle()
            loadRewardAd(rewardAdPlaybackCallbacks: nil)
        }
    }
    
    func ad(_ ad: GADFullScreenPresentingAd, didFailToPresentFullScreenContentWithError error: Error) {
        let nsError = error as NSError
        let adErrorCode = getAdErrorCode(nsError.code)
        self.currentRewardAdState = RewardAdState.ErrorErrorFailedToShow()
        self.rewardAdPlaybackCallbacks?.onRewardAdFailedToShow(
            adError: AdError(code: adErrorCode, message: error.localizedDescription, domain: nsError.domain)
        )
        Log.d("[RewardedAd] Ad did fail to present full screen content with error: \(error.localizedDescription)")
        clearCurrentRewardAdHandle()
    }
}

