//
//  InteropFactory.swift
//  WallApp
//

import Foundation
import WallApp

class InteropFactoryIos: InteropFactory {
    
    func createAdUnitIds() -> AdUnitIds {
        return AdUnitIdsIos()
    }
 
    func createBaseCache() -> BaseCache {
        return BaseCacheIos.shared
    }
    
    func createBuildConfig() -> BuildConfig {
        return BuildConfigIos()
    }
    
    func createGoogleAuthCoordinatorForIos() -> GoogleAuthCoordinatorForIos {
        return GoogleAuthCoordinatorForIosDefault()
    }
    
    func createFirebaseStorageDownloadCoordinator() -> FirebaseStorageDownloadCoordinatorIos {
        return FirebaseStorageDownloadCoordinator()
    }
    
    func createCurrentScreenCoordinator() -> CurrentScreenCoordinatorIos {
        return CurrentScreenCoordinatorDefault()
    }
    
    func createPrivacyMessagingManagerDelegate() -> PrivacyMessagingManagerDelegateIos {
        return PrivacyMessagingManagerDelegate.shared
    }
    
    func createImagePrefetcherEx() -> ImagePrefetcherEx {
        return ImagePrefetcherKingfisher()
    }

    func createInAppBrowserDelegate() -> InAppBrowserDelegate {
        return InAppBrowserDelegateIos()
    }
    
    func createNetworkStateNative() -> NetworkStateNative {
        return NetworkStateNativeIos()
    }
    
    func createRevenueCatManager() -> RevenueCatManager {
        return RevenueCatManagerIosDefault()
    }
    
    func createRevenueCatUserManager() -> RevenueCatUserManager {
        return RevenueCatUserManagerIos()
    }
    
    func createRewardAdCoordinator() -> any RewardAdCoordinator {
        return RewardAdCoordinatorDefault.shared
    }
    
    func createSystemPhotoPicker() -> SystemPhotoPicker {
        return SystemPhotoPickerIos()
    }
    
    func createUiControllerManager() -> UiControllerManager {
        return UIControllerManagerIos()
    }
    
    func createUIKitFactory() -> UIKitFactory {
        return UIKitFactoryIosDefault()
    }

    func createUrlDownloadCoordinator() -> UrlDownloadCoordinatorForIos {
        return UrlDownloadCoordinatorDefault()
    }
}
