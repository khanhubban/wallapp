//
//  UIKitFactoryIosDefault.swift
//  WallApp
//

import WallApp
import UIKit

class UIKitFactoryIosDefault : UIKitFactoryIos {
    
    private var uiViewMap = [String: UIView]()
    private var uiViewControllerMap = [String: UIViewController]()
    private var render: RenderIos {
        InteropModulesIos.shared.render
    }
    private var theme: Theme {
        InteropModulesIos.shared.themeManager.theme.value
    }
    
    func createProfileImage(profileViewState: ProfileImageViewState, imageSize: DpOptional?) -> UIView {
        guard let key = profileViewState.id() else { return UIView() }
        Log.d("[UIKitView] createProfileImage: \(key)")
        let imageSize = imageSize?.dp ?? profileViewState.imageViewSpec.size
        let view = UIProfileImageView(render: render, theme: theme, viewState: profileViewState, imageSize: imageSize)
        uiViewMap[key] = view
        return view
    }
    
    func updateProfileImage(profileViewState: ProfileImageViewState, imageSize: DpOptional?) {
        guard let key = profileViewState.id() else { return }
        Log.d("[UIKitView] updateProfileImage: \(key)")
        if let view = uiViewMap[key] as? UIProfileImageView {
            let imageSize = imageSize?.dp ?? profileViewState.imageViewSpec.size
            view.updateWith(viewState: profileViewState, imageSize: imageSize, theme: self.theme)
        }
    }
    
    func releaseProfileImage(profileViewState: ProfileImageViewState) {
        guard let key = profileViewState.id() else { return }
        Log.d("[UIKitView] releaseProfileImage: \(key)")
        uiViewMap.removeValue(forKey: key)
    }
    
    func createVideoPlayer(url: String?, fileName: String?, imageVideoState: ImageVideoState) -> UIView {
        if let url, let videoUrl = URL(string: url) {
            let videoPlayerController = VideoPlayerViewController(videoURL: videoUrl, imageVideoState: imageVideoState)
            uiViewControllerMap[videoUrl.absoluteString] = videoPlayerController
            return videoPlayerController.view
        } else if let fileName, let videoUrl = Bundle.main.url(forResource: fileName, withExtension: "mp4") {
            let videoPlayerController = VideoPlayerViewController(videoURL: videoUrl, imageVideoState: imageVideoState)
            uiViewControllerMap[videoUrl.absoluteString] = videoPlayerController
            return videoPlayerController.view
        } else {
            return UIView()
        }
    }
    
    func updateVideoPlayer(url: String?, fileName: String?, imageVideoState: ImageVideoState) {
        if let url, let videoUrl = URL(string: url), let videoPlayerController = uiViewControllerMap[videoUrl.absoluteString] as? VideoPlayerViewController {
            videoPlayerController.updateWith(url: videoUrl, imageVideoState: imageVideoState)
        } else if let fileName, let videoUrl = Bundle.main.url(forResource: fileName, withExtension: "mp4"), let videoPlayerController = uiViewControllerMap[videoUrl.absoluteString] as? VideoPlayerViewController {
            videoPlayerController.updateWith(url: videoUrl, imageVideoState: imageVideoState)
        }
    }
    
    func releaseVideoPlayer(url: String?, fileName: String?, imageVideoState: ImageVideoState) {
        if let url, let videoUrl = URL(string: url), let videoPlayerController = uiViewControllerMap[videoUrl.absoluteString] {
            videoPlayerController.removeFromParent()
            videoPlayerController.view.removeFromSuperview()
            uiViewControllerMap.removeValue(forKey: videoUrl.absoluteString)
        } else if let fileName, let videoUrl = Bundle.main.url(forResource: fileName, withExtension: "mp4"), let videoPlayerController = uiViewControllerMap[videoUrl.absoluteString] {
            videoPlayerController.removeFromParent()
            videoPlayerController.view.removeFromSuperview()
            uiViewControllerMap.removeValue(forKey: videoUrl.absoluteString)
        }
    }
}
