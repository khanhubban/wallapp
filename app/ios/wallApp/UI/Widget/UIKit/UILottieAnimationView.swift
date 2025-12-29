//
//  LottieAnimationCustomView.swift
//  WallApp
//

import WallApp
import Lottie
import UIKit

class UILottieAnimationView: UIView {
    
    let lottieAnimationView = LottieAnimationView()
    var animatedImageSpec: AnimatedImageSpec
    var width: CGFloat?
    var height: CGFloat?
    
    private var widthConstraint: NSLayoutConstraint?
    private var heightConstraint: NSLayoutConstraint?
    
    private var animationSet = false
    
    var displayLink: CADisplayLink?
    
    init(
        animatedImageSpec: AnimatedImageSpec,
        width: CGFloat? = nil,
        height: CGFloat? = nil
    ) {
        self.animatedImageSpec = animatedImageSpec
        self.width = width
        self.height = height
        super.init(frame: CGRect.zero)
        lottieAnimationView.translatesAutoresizingMaskIntoConstraints = false
        self.addSubview(lottieAnimationView)
        updateWith(width: width, height: height)
        updateWith(animatedImageSpec: animatedImageSpec)
        
        displayLink = CADisplayLink(target: self, selector: #selector(animationCallback))
        displayLink?.add(to: .current, forMode: RunLoop.Mode.default)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    @objc func animationCallback() {
        if lottieAnimationView.isAnimationPlaying {
            animatedImageSpec.animationProgressUpdates(KotlinFloat(float: Float(lottieAnimationView.realtimeAnimationProgress)))
        }
    }
    
    func updateWith(width: CGFloat?, height: CGFloat?) {
        self.width = width
        self.height = height
        if let width, let height {
            if widthConstraint == nil {
                widthConstraint = lottieAnimationView.widthAnchor.constraint(equalToConstant: CGFloat(width))
                widthConstraint?.isActive = true
            } else {
                widthConstraint?.constant = CGFloat(width)
            }
            if heightConstraint == nil {
                heightConstraint = lottieAnimationView.heightAnchor.constraint(equalToConstant: CGFloat(height))
                heightConstraint?.isActive = true
            } else {
                heightConstraint?.constant = CGFloat(height)
            }
        }
    }
    
    fileprivate func playAnimation(_ self: UILottieAnimationView) {
        self.lottieAnimationView.animationSpeed = self.animatedImageSpec.speed.toCGFloat()
        if self.animatedImageSpec.infiniteRepeat == true {
            self.lottieAnimationView.loopMode = .loop
        }
        Log.d("[UILottieAnimationView] isAnimationPlaying: \(self.lottieAnimationView.isAnimationPlaying), \(self.lottieAnimationView.isAnimationQueued)")
        if self.animatedImageSpec.startAnimation {
            if let clipSpec = animatedImageSpec.clipSpec {
                if let frame = clipSpec as? AnimatedImageClipSpec.Frame, let min = frame.min, let max = frame.max {
                    Log.d("[UILottieAnimationView] play(fromFrame: \(min), toFrame: \(max))")
                    self.lottieAnimationView.play(
                        fromFrame: CGFloat(min as! Int32), toFrame: CGFloat(max as! Int32), completion: { completed in
                            self.animatedImageSpec.animationCompleted()
                        }
                    )
                } else if let progress = clipSpec as? AnimatedImageClipSpec.Progress {
                    Log.d("[UILottieAnimationView] play(fromProgress: \(progress.min), toProgress: \(progress.max))")
                    self.lottieAnimationView.play(
                        fromProgress: CGFloat(progress.min), toProgress: CGFloat(progress.max), completion: { completed in
                            self.animatedImageSpec.animationCompleted()
                        }
                    )
                }
            } else {
                Log.d("[UILottieAnimationView] play()")
                self.lottieAnimationView.play(completion: { completed in
                    self.animatedImageSpec.animationCompleted()
                })
            }
            self.animatedImageSpec.animationStarted()
        } else if !self.lottieAnimationView.isAnimationPlaying {
            let progress = (self.animatedImageSpec.clipSpec as? AnimatedImageClipSpec.Progress)?.max ?? 1
            Log.d("[UILottieAnimationView] setting progress: \(progress)")
            self.lottieAnimationView.currentProgress = CGFloat(progress)
        }
    }
    
    func updateWith(animatedImageSpec: AnimatedImageSpec) {
        Log.d("[UILottieAnimationView] updateWith(animatedImageSpec: \(animatedImageSpec))")
        let previousAnimatedImageSpec = self.animatedImageSpec
        self.animatedImageSpec = animatedImageSpec
        if previousAnimatedImageSpec.id == animatedImageSpec.id && animationSet {
            Log.d("[UILottieAnimationView] only playing")
            playAnimation(self)
        } else {
            if let animatedAssetFile = animatedImageSpec.resource as? ResourceAnimatedAssetFile {
                self.lottieAnimationView.animation = LottieAnimation.named(animatedAssetFile.fileName)
                self.lottieAnimationView.contentMode = .scaleAspectFill
                self.animationSet = true
                playAnimation(self)
            }
        }
    }
    
    func startAnimation() {
        lottieAnimationView.currentProgress = 0
        lottieAnimationView.play()
    }
}

extension AnimatedImageSpec {
    func arePropertiesEqualTo(_ other: AnimatedImageSpec) -> Bool {
        return self.id == other.id
        && self.infiniteRepeat == other.infiniteRepeat
        && self.speed == other.speed
        && self.startAnimation == other.startAnimation
        && self.clipSpec == other.clipSpec
    }
}
