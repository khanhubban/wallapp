//
//  FavoriteButton.swift
//  WallApp
//

import WallApp
import Lottie
import SwiftUI
import UIKit

struct FavoriteButton: View {
    let theme: Theme
    let viewState: FavoriteViewState
    let contentColor: Color
    var padding: CGFloat = 0

    var body: some View {
        Button(action: {
            viewState.onClick.invoke()
        }) {
            let imageUI = viewState.isFavorite ? viewState.selectedImage : viewState.unselectedImage
            let tintColor = viewState.isFavorite ? nil : contentColor
            SwiftUIImage(theme: theme, image: imageUI, tintColor: tintColor)
                .font(.system(size: viewState.viewSpec.size.toCGFloat() * 0.8))
                .frame(width: viewState.viewSpec.size.toCGFloat(), height: viewState.viewSpec.size.toCGFloat())
                .padding(padding)
        }
    }
}

struct FavoriteAnimatedView: View {
    let theme: Theme
    let viewState: FavoriteViewState
    let contentColor: Color? = nil
    var padding: CGFloat = 0
    
    @State private var currentFavoriteState: Bool? = nil
    @State private var currentFavoriteId: String? = nil
    @State private var clickOriginated: Bool = false
    @State private var playbackMode: LottiePlaybackMode = LottiePlaybackMode.paused
    
    var body: some View {
        let image: ImageUIImageResource? = viewState.isFavorite ? viewState.selectedImageAnimated as? ImageUIImageResource : viewState.unselectedImageAnimated as? ImageUIImageResource
        if let animatedImage = image?.resource as? ResourceAnimatedImage {
            let animatedImageSpec = animatedImage.animatedImageSpec
            
            let speed = animatedImageSpec.speed
            let tintColor = viewState.tintColor != nil ? viewState.tintColor?.toColor(themeColors: theme.themeColors)?.uiColor().lottieColorValue : nil
            
            if let animatedAssetFile = animatedImageSpec.resource as? ResourceAnimatedAssetFile {
                ZStack {
                    if let tintColor {
                        // the keypaths are hardcoded.
                        // Ref: https://medium.com/@mobileatexxeta/lottie-magic-in-swiftui-5d600f068287
                        // Ref: https://swiftsenpai.com/development/lottie-value-providers/
                        let colorProvider = ColorValueProvider(
                            .init(r: tintColor.r, g: tintColor.g, b: tintColor.b, a: tintColor.a)
                        )
                        LottieView(animation: .named(animatedAssetFile.fileName))
                            .animationSpeed(Double(speed))
                            .playbackMode(playbackMode)
                            .valueProvider(
                                colorProvider,
                                for: AnimationKeypath(
                                    keypath: "**.Fill 1.Color"
                                )
                            ).valueProvider(
                                colorProvider,
                                for: AnimationKeypath(
                                    keypath: "**.Group 1.Stroke 1.Color"
                                )
                            )
                        
                    } else {
                        LottieView(animation: .named(animatedAssetFile.fileName))
                            .animationSpeed(Double(speed))
                            .playbackMode(playbackMode)
                    }
                }
                .frame(width: viewState.viewSpec.size.toCGFloat(), height: viewState.viewSpec.size.toCGFloat())
                .padding(padding)
                .onAppear {
                    updatePlaybackMode()
                }
                .onChange(of: viewState) { viewState in
                    updatePlaybackMode(viewState)
                }
                .onTapGesture {
                    clickOriginated = true
                    viewState.onClick.invoke()
                }
            }
        }
    }
    
    private func updatePlaybackMode(_ viewState: FavoriteViewState? = nil) {
        let viewState = viewState ?? self.viewState
        if currentFavoriteState == viewState.isFavorite && currentFavoriteId == viewState.id {
            return
        }
        let image: ImageUIImageResource? = viewState.isFavorite ? viewState.selectedImageAnimated as? ImageUIImageResource : viewState.unselectedImageAnimated as? ImageUIImageResource
        if let animatedImage = image?.resource as? ResourceAnimatedImage {
            let animatedImageSpec = animatedImage.animatedImageSpec
            let clipSpec = animatedImageSpec.clipSpec
            let minProgress = (clipSpec as? AnimatedImageClipSpec.Progress)?.min.toCGFloat() ?? 0
            let maxProgress = (clipSpec as? AnimatedImageClipSpec.Progress)?.max.toCGFloat() ?? 1
            if currentFavoriteState != nil && currentFavoriteState != viewState.isFavorite && currentFavoriteId == viewState.id && clickOriginated {
                playbackMode = .playing(.fromProgress(minProgress, toProgress: maxProgress, loopMode: .playOnce))
                clickOriginated = false
            } else {
                playbackMode = .paused(at: .progress(maxProgress))
            }
            currentFavoriteState = viewState.isFavorite
            currentFavoriteId = viewState.id
        }
    }
}


class UIFavoriteButton: UIView {
    
    var theme: Theme
    var viewState: FavoriteViewState
    var contentColor: Color
    var padding: CGFloat = 0
    var customImageView: CustomImageView!
    
    init(theme: Theme, viewState: FavoriteViewState, contentColor: Color, padding: CGFloat) {
        self.theme = theme
        self.viewState = viewState
        self.contentColor = contentColor
        self.padding = padding
        super.init(frame: CGRect.zero)
        
        let imageUI = viewState.isFavorite ? viewState.selectedImage : viewState.unselectedImage
        let tintColor = viewState.isFavorite ? nil : contentColor
        self.customImageView = CustomImageView(theme: self.theme, image: imageUI, tintColor: tintColor)
        self.customImageView.addTapGesture { [unowned self] in
            self.viewState.onClick.invoke()
        }
        
        self.addSubview(customImageView)
        customImageView.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint.activate([
            customImageView.leadingAnchor.constraint(equalTo: self.leadingAnchor, constant: padding),
            customImageView.trailingAnchor.constraint(equalTo: self.trailingAnchor, constant: -padding),
            customImageView.topAnchor.constraint(equalTo: self.topAnchor, constant: padding),
            customImageView.bottomAnchor.constraint(equalTo: self.bottomAnchor, constant: -padding),
            customImageView.heightAnchor.constraint(equalToConstant: viewState.viewSpec.size.toCGFloat()),
            customImageView.widthAnchor.constraint(equalToConstant: viewState.viewSpec.size.toCGFloat()),
        ])
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    func setup() {
        let imageUI = viewState.isFavorite ? viewState.selectedImage : viewState.unselectedImage
        let tintColor = viewState.isFavorite ? nil : contentColor
        customImageView.theme = self.theme
        customImageView.imageUI = imageUI
        customImageView.tintColorData = tintColor
        self.customImageView.setup()
    }
}

class UIFavoriteAnimatedButton: UIView {
    
    var theme: Theme
    var viewState: FavoriteViewState
    var contentColor: Color?
    private var lottieAnimationView: LottieAnimationView = LottieAnimationView()
    
    private var animationSet = false
    private var animatedImageSpec: AnimatedImageSpec? = nil
    private var previousIsFavorite: Bool? = nil
    private var clickOriginated: Bool = false
    private var previousFavoriteId: String? = nil
    
    init(theme: Theme, viewState: FavoriteViewState, contentColor: Color? = nil) {
        self.theme = theme
        self.viewState = viewState
        self.contentColor = contentColor
        super.init(frame: CGRect.zero)
        
        self.lottieAnimationView.addTapGesture { [unowned self] in
            self.clickOriginated = true
            self.viewState.onClick.invoke()
        }
        
        self.addSubview(lottieAnimationView)
        lottieAnimationView.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint.activate([
            lottieAnimationView.leadingAnchor.constraint(equalTo: self.leadingAnchor),
            lottieAnimationView.trailingAnchor.constraint(equalTo: self.trailingAnchor),
            lottieAnimationView.topAnchor.constraint(equalTo: self.topAnchor),
            lottieAnimationView.bottomAnchor.constraint(equalTo: self.bottomAnchor)
        ])
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    func setup() {
        let image: ImageUIImageResource?
        if viewState.isFavorite {
            image = viewState.selectedImageAnimated as? ImageUIImageResource
        } else {
            image = viewState.unselectedImageAnimated as? ImageUIImageResource
        }
        guard let animatedImageSpec = (image?.resource as? ResourceAnimatedImage)?.animatedImageSpec else {
            return
        }
        let previousAnimatedImageSpec = self.animatedImageSpec
        self.animatedImageSpec = animatedImageSpec
        if previousAnimatedImageSpec?.id == animatedImageSpec.id && animationSet {
            playAnimation(self, animatedImageSpec, viewState.isFavorite)
        } else {
            if let animatedAssetFile = animatedImageSpec.resource as? ResourceAnimatedAssetFile {
                self.lottieAnimationView.animation = LottieAnimation.named(animatedAssetFile.fileName)
                self.lottieAnimationView.contentMode = .scaleAspectFill
                self.animationSet = true
                playAnimation(self, animatedImageSpec, viewState.isFavorite)
            }
        }
    }
    
    private func playAnimation(_ self: UIFavoriteAnimatedButton, _ animatedImageSpec: AnimatedImageSpec, _ isFavorite: Bool) {
        // Exit early if the state is same
        if previousIsFavorite == isFavorite && previousFavoriteId == viewState.id {
            return
        }
        self.lottieAnimationView.animationSpeed = animatedImageSpec.speed.toCGFloat()
        if previousIsFavorite != nil && previousIsFavorite != isFavorite && clickOriginated {
            if let clipSpec = animatedImageSpec.clipSpec {
                if let frame = clipSpec as? AnimatedImageClipSpec.Frame, let min = frame.min, let max = frame.max {
                    self.lottieAnimationView.play(
                        fromFrame: CGFloat(min as! Int32), toFrame: CGFloat(max as! Int32)
                    )
                } else if let progress = clipSpec as? AnimatedImageClipSpec.Progress {
                    self.lottieAnimationView.play(
                        fromProgress: CGFloat(progress.min), toProgress: CGFloat(progress.max)
                    )
                }
            }
            clickOriginated = false
        } else {
            let progress = (animatedImageSpec.clipSpec as? AnimatedImageClipSpec.Progress)?.max ?? 1
            self.lottieAnimationView.currentProgress = CGFloat(progress)
        }
        previousIsFavorite = isFavorite
        previousFavoriteId = viewState.id
    }
}
