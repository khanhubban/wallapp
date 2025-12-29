//
//  VideoPlayerViewController.swift
//  WallApp
//

import WallApp
import AVFoundation
import AVKit
import UIKit

class VideoPlayerViewController: UIViewController {
    
    var videoURL: URL
    var imageVideoState: ImageVideoState
    var loop: Bool
    private var seekToMillisOnLoop: CMTime!
    private var player: AVPlayer!
    private var playerViewController: AVPlayerViewController!
    
    private var playerItemBufferEmptyObserver: NSKeyValueObservation?
    private var playerItemBufferKeepUpObserver: NSKeyValueObservation?
    private var playerItemBufferFullObserver: NSKeyValueObservation?
    private var playerItemErrorObserver: NSKeyValueObservation?

    // Timer for polling playback position
    private var playbackPositionTimer: DispatchSourceTimer?
    private var lastReportedPlaybackPosition: Int32? = nil  // Cache the last position

    private var pausePlayback: Bool

    init(videoURL: URL, imageVideoState: ImageVideoState) {
        self.videoURL = videoURL
        self.imageVideoState = imageVideoState
        self.loop = imageVideoState.isLooping
        self.pausePlayback = imageVideoState.pausePlayback

        super.init(nibName: nil, bundle: nil)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        
        configureAudioSession(imageVideoState.useAudio)

        setSeekToMillisOnLoop(imageVideoState)

        // Create AVPlayer
        player = AVPlayer(url: videoURL)

        // Set start position if provided
        if let startPositionMillis = startPositionMillis() {
            let startTime = CMTime(seconds: Double(startPositionMillis) / 1000.0, preferredTimescale: 1000)
            player.seek(to: startTime)
        }

        // Create AVPlayerViewController
        playerViewController = AVPlayerViewController()
        playerViewController.player = player
        playerViewController.videoGravity = .resizeAspectFill
        playerViewController.showsPlaybackControls = false

        // Add AVPlayerViewController as a child view controller
        self.addChild(playerViewController)
        playerViewController.view.frame = self.view.bounds
        self.view.addSubview(playerViewController.view)
        playerViewController.didMove(toParent: self)

        // Play the video, respecting `pausePlayback` state
        if !pausePlayback {
            player.play()
        }

        NotificationCenter.default.addObserver(self, selector: #selector(playerItemDidReachEnd(notification:)), name: .AVPlayerItemDidPlayToEndTime, object: player.currentItem)

        NotificationCenter.default.addObserver(self, selector: #selector(handleAppWillResignActive), name: UIApplication.willResignActiveNotification, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(handleAppDidBecomeActive), name: UIApplication.didBecomeActiveNotification, object: nil)
        
        let callbacks = imageVideoState.playbackCallbacks

        playerItemBufferEmptyObserver = player.currentItem?.observe(\AVPlayerItem.isPlaybackBufferEmpty, options: [.new]) { [weak self] (_, _) in
            guard self != nil else { return }
            Log.d("[VideoPlayerViewController] Buffer Empty")
            callbacks?.onPlaybackBufferingStart()
        }

        playerItemBufferKeepUpObserver = player.currentItem?.observe(\AVPlayerItem.isPlaybackLikelyToKeepUp, options: [.new]) { [weak self] (_, _) in
            guard self != nil else { return }
            Log.d("[VideoPlayerViewController] Buffer Keep Up")
            callbacks?.onPlaybackRenderingStart()
        }

        playerItemBufferFullObserver = player.currentItem?.observe(\AVPlayerItem.isPlaybackBufferFull, options: [.new]) { [weak self] (_, _) in
            guard self != nil else { return }
            Log.d("[VideoPlayerViewController] Buffer Full")
            callbacks?.onPlaybackRenderingStart()
        }

        // Error handling: observe the player item status for errors
        playerItemErrorObserver = player.currentItem?.observe(\AVPlayerItem.status, options: [.new]) { [weak self] (item, change) in
            guard let self = self, let status = change.newValue else { return }
            if status == .failed, let error = item.error as NSError? {
                let errorCode = error.code
                let errorMessage = error.localizedDescription
                let message = "Playback Error: Code \(errorCode), Message: \(errorMessage)"
                Log.e("[VideoPlayerViewController] \(errorMessage)")

                callbacks?.onPlaybackError(
                    error: ImageVideoPlaybackError(message: message, code: Int32(errorCode), codeAdditional: nil)
                )
            }
        }

        startPlaybackPositionTimer()
    }
    
    private func configureAudioSession(_ useAudio: Bool) {
        let audioSession = AVAudioSession.sharedInstance()
        do {
            if useAudio {
                try audioSession.setCategory(.playback, options: [])
            } else {
                try audioSession.setCategory(.ambient, options: [])
            }
            try audioSession.setActive(true)
        } catch {
            Log.e("Failed to set audio session category: \(error.localizedDescription)")
        }
    }

    // Polling method to get the current playback position
    private func startPlaybackPositionTimer() {
        guard player.currentItem?.status == .readyToPlay else {
            // Wait for the player to be ready before starting the timer
            DispatchQueue.main.asyncAfter(deadline: .now() + 0.1) {
                self.startPlaybackPositionTimer()
            }
            return
        }
        
        playbackPositionTimer = DispatchSource.makeTimerSource()
        playbackPositionTimer?.schedule(deadline: .now(), repeating: .milliseconds(100))
        playbackPositionTimer?.setEventHandler { [weak self] in
            guard let self = self else { return }
            self.updateCurrentPlaybackPosition()
        }
        playbackPositionTimer?.resume()
    }

    private func updateCurrentPlaybackPosition() {
        let currentPlaybackPosition = Int32(CMTimeGetSeconds(self.player.currentTime()) * 1000)

        if self.lastReportedPlaybackPosition != currentPlaybackPosition {
            self.imageVideoState.playbackCallbacks?.onPlaybackPositionTick(position: currentPlaybackPosition)
            self.lastReportedPlaybackPosition = currentPlaybackPosition
        }
    }

    // Stop the timer when no longer needed
    private func stopPlaybackPositionTimer() {
        playbackPositionTimer?.cancel()
        playbackPositionTimer = nil
    }
    
    private func getViewStatePlayback() -> ImageVideoState.Playback? {
        switch onEnum(of: imageVideoState) {
        case .playback(let data):
            return data
        default:
            return nil
        }
    }

    private func startPositionMillis() -> Int? {
        switch onEnum(of: imageVideoState) {
        case .playback(let data):
            if let startPosition = data.startPositionMillis {
                return Int(truncating: startPosition)
            } else {
                return nil
            }
        default:
            return nil
        }
    }
    
    private func setSeekToMillisOnLoop(_ imageVideoState: ImageVideoState) {
        guard let playbackVideoState = getViewStatePlayback() else { return }
        
        let seekToMillisOnLoop: Double? = playbackVideoState.seekToMillisOnLoop != nil ? Double(truncating: playbackVideoState.seekToMillisOnLoop!) / 1000.0 : nil
        if (seekToMillisOnLoop == nil) {
            self.seekToMillisOnLoop = CMTime(seconds: .zero, preferredTimescale: 1)
        } else {
            self.seekToMillisOnLoop = CMTime(seconds: seekToMillisOnLoop!, preferredTimescale: 1)
        }
    }

    @objc func playerItemDidReachEnd(notification: Notification) {
//         Log.d("[VideoPlayerViewController] Playback Complete")
        updateCurrentPlaybackPosition()
        imageVideoState.playbackCallbacks?.onPlaybackComplete()
        if loop {
            player.seek(to: seekToMillisOnLoop)
            player.play()
        }
    }

    func updateWith(url: URL, imageVideoState: ImageVideoState) {
        NotificationCenter.default.removeObserver(self, name: .AVPlayerItemDidPlayToEndTime, object: player.currentItem)

        self.imageVideoState = imageVideoState
        self.loop = imageVideoState.isLooping
        self.pausePlayback = imageVideoState.pausePlayback
        
        setSeekToMillisOnLoop(imageVideoState)

        // Set start position if provided
        if let startPositionMillis = startPositionMillis() {
            let startTime = CMTime(seconds: Double(startPositionMillis) / 1000.0, preferredTimescale: 1000)
            player.seek(to: startTime)
        }

        if pausePlayback {
            player.pause()
        } else {
            player.play()
        }
        
        NotificationCenter.default.addObserver(self, selector: #selector(playerItemDidReachEnd(notification:)), name: .AVPlayerItemDidPlayToEndTime, object: player.currentItem)
        
        if self.videoURL == url {
            return
        }
        self.videoURL = url
        
        player.replaceCurrentItem(with: AVPlayerItem(url: videoURL))
    }

    @objc func handleAppWillResignActive() {
        player.pause()
    }

    @objc func handleAppDidBecomeActive() {
        if !pausePlayback {
            player.play()
        }
    }
    
    deinit {
        do {
            try AVAudioSession.sharedInstance().setActive(false)
        } catch {
            Log.e("Failed to deactivate audio session: \(error.localizedDescription)")
        }

        player.pause()
        player.replaceCurrentItem(with: nil)
        playerViewController.player = nil

        stopPlaybackPositionTimer()
        NotificationCenter.default.removeObserver(self)

        playerItemBufferEmptyObserver?.invalidate()
        playerItemBufferEmptyObserver = nil

        playerItemBufferKeepUpObserver?.invalidate()
        playerItemBufferKeepUpObserver = nil

        playerItemBufferFullObserver?.invalidate()
        playerItemBufferFullObserver = nil

        playerItemErrorObserver?.invalidate()
        playerItemErrorObserver = nil
    }
}
