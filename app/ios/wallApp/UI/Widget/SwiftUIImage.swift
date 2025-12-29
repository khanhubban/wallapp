//
//  SwiftUIImage.swift
//  WallApp
//

import WallApp
import Foundation
import SwiftUI
import Kingfisher
import Lottie

struct SwiftUIImage: View {
    let theme: Theme
    let image: ImageUI
    let width: CGFloat?
    let height: CGFloat?
    let tintColor: Color?
    let imageHashDecoder: ImageHashDecoder?
    let imageVideoState: ImageVideoState?
    var contentScale: ViewContentScale? = nil

    @State var error: Error? = nil
    @State var lottieAnimProgress: CGFloat = 0
    
    var scaledToFill: Bool = false
    
    init(theme: Theme,
         image: ImageUI,
         width: CGFloat? = nil,
         height: CGFloat? = nil,
         tintColor: Color? = nil,
         imageHashDecoder: ImageHashDecoder? = nil,
         imageVideoState: ImageVideoState? = nil
    ) {
        
        self.theme = theme
        self.image = image
        self.width = width
        self.height = height
        self.tintColor = tintColor
        self.imageHashDecoder = imageHashDecoder
        self.imageVideoState = imageVideoState
    }
    
    init(
        render: RenderIos,
        theme: Theme,
        imageViewState: ImageViewState,
        tintColor: Color? = nil,
        scaledToFill: Bool = false
    ) {
        self.theme = theme
        self.image = imageViewState.image
        self.width = imageViewState.viewSpec.width.toCGFloat()
        self.height = imageViewState.viewSpec.height.toCGFloat()
        self.tintColor = tintColor
        self.imageHashDecoder = render.imageHashDecoder
        self.scaledToFill = scaledToFill
        self.imageVideoState = imageViewState.videoState
        self.contentScale = imageViewState.viewSpec.contentScale
    }
    
    var body: some View {
        let fullWidthDp = width == FullWidthDp.toCGFloat()
        ImageView()
            .if (width != nil && height != nil && !fullWidthDp) {
                $0.frame(width: width, height: height)
            }
            .if ((width == nil || fullWidthDp) && height == nil) {
                $0.frame(maxWidth: .infinity, maxHeight: .infinity)
            }
            .if ((width != nil && !fullWidthDp) && height == nil) {
                $0.frame(width: width)
            }
            .if ((width == nil || fullWidthDp) && height != nil) {
                $0.frame(height: height)
            }
            .if (fullWidthDp) {
                $0.frame(maxWidth: .infinity)
            }
    }
    
    @ViewBuilder
    func ImageView() -> some View {
        if let imageUI = image as? ImageUIImageStates {
            if let successImageUI = imageUI.success as? ImageUIImageResource {
                if let loadingImageUI = imageUI.loadingImage as? ImageUIImageResource {
                    ImageResourceView(imageResource: successImageUI, loadingImageResource: loadingImageUI)
                        .transition(.asymmetric(insertion: .fade(duration: 0.5), removal: .fade(duration: 0.5)))
                } else {
                    ImageResourceView(imageResource: successImageUI)
                        .transition(.asymmetric(insertion: .fade(duration: 0.5), removal: .fade(duration: 0.5)))
                }
            }
        } else if let imageUI = image as? ImageUIImageResource {
            ImageResourceView(imageResource: imageUI)
                .transition(.asymmetric(insertion: .fade(duration: 0.5), removal: .fade(duration: 0.5)))
        }
    }
    
    struct ImageHashUI: View {
        let hashedImage: ResourceHashedImage
        let imageHashDecoder: ImageHashDecoder?
        let width: CGFloat
        let height: CGFloat
        let tintColorToken: ColorToken?
        let theme: Theme?
        
        @State var imageData: Data? = nil
        
        var body: some View {
            ZStack {
                if let imageData, let uiImage = UIImage(data: imageData) {
                    if let theme, let tintColor = tintColorToken?.toColor(themeColors: theme.themeColors) {
                        Image(uiImage: uiImage)
                            .resizable()
                            .renderingMode(.template)
                            .colorMultiply(tintColor)
                            .scaledToFit()
                    } else {
                        Image(uiImage: uiImage)
                            .resizable()
                            .scaledToFit()
                    }
                } else {
                    Color.white.opacity(0.2)
                }
            }
            .task {
                await loadHash()
            }
        }
        
        private func loadHash() async {
            await Task.detached(priority: .background) {
                if let imageHashDecoder = self.imageHashDecoder {
                    let byteArrayResult = try? await imageHashDecoder.decode(imageHash: hashedImage.imageHash,
                                                                             width: width.toInt32(),
                                                                             height: height.toInt32(),
                                                                             asByteArray: true)
                    if let byteArray = (byteArrayResult as? ImageHashDecoderResult.SuccessData)?.data {
                        await MainActor.run {
                            self.imageData = byteArray.toNSData()
                        }
                    }
                }
            }
        }
    }
    
    private func process(imageUrl: String) -> String {
        if let width = width, let height = height {
            let imageHostUrlMapper = InteropModulesIos.shared.imageHostUrlMapper
            let imageHostManager = InteropModulesIos.shared.imageHostManager
            if imageHostUrlMapper.canMapDynamicImageUrl(imageUrl: imageUrl) {
                let imageScaler = InteropModulesIos.shared.imageScaler
                let imageHostFormat = imageHostManager.getImageHostFormat(imageHostDisplayTarget: ImageHostDisplayTarget.InAppNative())

                let options = ImageHostOptionsDefault(imageHostFormat: imageHostFormat)
                imageScaler.apply(options: options, widthDp: Float(width), heightDp: Float(height))
                options.crop = true
                let urlResult = imageHostUrlMapper.mapDynamicImageUrl(imageUrl: imageUrl, options: options, alignment: nil)
//                Log.i("Image url: \(urlResult.url)")
                return urlResult.url
            }
        }
        return imageUrl
    }
}

extension SwiftUIImage {
    @ViewBuilder
    func ImageResourceView(imageResource: ImageUIImageResource, loadingImageResource: ImageUIImageResource? = nil) -> some View {
        let tintColor = tintColor ?? imageResource.tintColorToken?.toColor(themeColors: theme.themeColors)
        if let imageUrl = (imageResource.resource as? ResourceUrlImage)?.url {
            let finalisedUrl = self.process(imageUrl: imageUrl)
            if error != nil {
                Image(systemName: "exclamationmark.icloud")
                    .resizable()
                    .scaledToFit()
            } else {
                if let tintColor {
                    KFImage(URL(string: finalisedUrl))
                        .placeholder {
                            if let hashedImage = loadingImageResource?.resource as? ResourceHashedImage, let width, let height {
                                ImageHashUI(hashedImage: hashedImage, imageHashDecoder: imageHashDecoder, width: width, height: height, tintColorToken: loadingImageResource?.tintColorToken, theme: theme)
                            } else {
                                Color.white.opacity(0.2)
                            }
                        }
                        .onFailure { e in
                            self.error = e
                        }.resizable()
                        .renderingMode(.template)
                        .foregroundStyle(tintColor)
                        
                    
                } else {
                    KFImage(URL(string: finalisedUrl))
                        .placeholder {
                            if let hashedImage = loadingImageResource?.resource as? ResourceHashedImage, let width, let height {
                                ImageHashUI(hashedImage: hashedImage, imageHashDecoder: imageHashDecoder, width: width, height: height, tintColorToken: loadingImageResource?.tintColorToken, theme: theme)
                            } else {
                                Color.white.opacity(0.2)
                            }
                        }
                        .onFailure { e in
                            self.error = e
                        }.resizable()
                        .if (contentScale == .FillWidth()) {
                            $0.aspectRatio(contentMode: .fill)
                                .frame(maxHeight: height!)
                                .clipped()
                        }
                }
            }
        } else if let localImage = imageResource.resource as? ResourceLocalImageAssetFile, let uiImage = UIImage(named: localImage.fileName) {
            if let tintColor {
                Image(uiImage: uiImage)
                    .resizable()
                    .renderingMode(.template)
                    .foregroundColor(tintColor)
                    .if (scaledToFill) {
                        $0.scaledToFill()
                    }
            } else {
                Image(uiImage: uiImage)
                    .resizable()
                    .if (scaledToFill) {
                        $0.scaledToFill()
                    }
            }
        } else if let byteArrayResource = imageResource.resource as? ResourceRawByteArray, let uiImage = UIImage(data: byteArrayResource.byteArray.toNSData()) {
            if let tintColor {
                Image(uiImage: uiImage)
                    .resizable()
                    .renderingMode(.template)
                    .foregroundColor(tintColor)
            } else {
                Image(uiImage: uiImage)
                    .resizable()
            }
        } else if let animatedImage = imageResource.resource as? ResourceAnimatedImage, let animatedAssetFile = animatedImage.animatedImageSpec.resource as? ResourceAnimatedAssetFile {
            let animatedImageSpec = animatedImage.animatedImageSpec
            
            if animatedImageSpec.startAnimation {
                if animatedImageSpec.infiniteRepeat {
                    LottieView(animation: .named(animatedAssetFile.fileName))
                        .animationSpeed(Double(animatedImageSpec.speed))
                        .looping()
                        .if(animatedImageSpec.fillAspectRatio) {
                            $0.aspectRatio(contentMode: .fill)
                        }
                } else {
                    LottieView(animation: .named(animatedAssetFile.fileName))
                        .animationSpeed(Double(animatedImageSpec.speed))
                        .playing()
                        .animationDidFinish { completed in
                            animatedImageSpec.animationCompleted()
                        }
                        .getRealtimeAnimationProgress($lottieAnimProgress)
                        .onChange(of: lottieAnimProgress) { progress in
                            animatedImageSpec.animationProgressUpdates(KotlinFloat(float: Float(progress)))
                        }
                        .if(animatedImageSpec.fillAspectRatio) {
                            $0.aspectRatio(contentMode: .fill)
                        }
                }
            } else {
                let progress = (animatedImageSpec.clipSpec as? AnimatedImageClipSpec.Progress)?.max ?? 1
                LottieView(animation: .named(animatedAssetFile.fileName))
                    .currentProgress(progress.toCGFloat())
                    .if(animatedImageSpec.fillAspectRatio) {
                        $0.aspectRatio(contentMode: .fill)
                    }
            }
        } else if let videoUrl = imageResource.resource as? ResourceUrlVideo, let videoUrl = URL.init(string: videoUrl.url) {
            VideoPlayerView(videoURL: videoUrl, imageVideoState: self.imageVideoState!)
        } else if let animatedAssetFile = imageResource.resource as? ResourceAnimatedAssetFile {
            let fileName = animatedAssetFile.fileName
            // currently this resource is only used for bundled videos
            if let url = Bundle.main.url(forResource: fileName, withExtension: "mp4") {
                VideoPlayerView(videoURL: url, imageVideoState: self.imageVideoState!)
            }
        }
    }
}
