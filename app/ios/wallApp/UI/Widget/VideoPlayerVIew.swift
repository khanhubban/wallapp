//
//  VideoPlayerVIew.swift
//  WallApp
//

import AVFoundation
import AVKit
import SwiftUI

struct VideoPlayerView: UIViewControllerRepresentable {
    var videoURL: URL
    var imageVideoState: ImageVideoState

    init(videoURL: URL, imageVideoState: ImageVideoState) {
        self.videoURL = videoURL
        self.imageVideoState = imageVideoState
    }

    func makeUIViewController(context: Context) -> UIViewController {
        let controller = VideoPlayerViewController(videoURL: videoURL, imageVideoState: imageVideoState)
        
        return controller
    }

    func updateUIViewController(_ playerController: UIViewController, context: Context) {
        guard let controller = playerController as? VideoPlayerViewController else {
            return
        }
        
        controller.updateWith(url: videoURL, imageVideoState: imageVideoState)
    }
}
