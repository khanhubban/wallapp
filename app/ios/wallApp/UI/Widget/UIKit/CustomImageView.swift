//
//  CustomImageView.swift
//  WallApp
//

import UIKit
import WallApp
import SwiftUI
import Kingfisher

class CustomImageView: UIView {
    
    var theme: Theme!
    var imageUI: ImageUI?
    var imageViewState: ImageViewState?
    var width: Float? = nil
    var height: Float? = nil
    var tintColorData: Color? = nil
    var contentScale: ViewContentScale? = nil
    var imageHashDecoder: ImageHashDecoder? = nil

    var imageLoaded = false
    var isPlaceHolder = false
    var error: Error? = nil
    
    private let fadeDuration = 0.25
    
    var imgPreview: UIImageView = UIImageView()
    var errorImageView: UIImageView? = nil
        
    let placeHolder = HashedImage()
    
    private var widthConstraint: NSLayoutConstraint?
    private var heightConstraint: NSLayoutConstraint?
    
    private var finalImageUI: ImageUI? {
        if let imageUI = imageUI {
            return imageUI
        } else if let imageViewState = imageViewState {
            return imageViewState.image
        } else {
            return nil
        }
    }
    private var finalWidth: Float? {
        if let width {
            return width
        } else if let imageViewState {
            return imageViewState.viewSpec.width
        } else {
            return nil
        }
    }
    private var finalHeight: Float? {
        if let height {
            return height
        } else if let imageViewState {
            return imageViewState.viewSpec.height
        } else {
            return nil
        }
    }
    
    private var interopModules: InteropModulesIos {
        return InteropModulesIos.shared
    }
    
    private let retryStrategy = DelayRetryStrategy(maxRetryCount: 2, retryInterval: .seconds(2))
    
    // The following 2 inits are mutually exclusive. If ImageViewState is being provided then the ImageUI should be nil and vice versa.
    init(
        theme: Theme,
        image: ImageUI? = nil,
        width: Float? = nil,
        height: Float? = nil,
        tintColor: Color? = nil,
        contentScale: ViewContentScale? = nil,
        imageHashDecoder: ImageHashDecoder? = nil
    ) {
        self.theme = theme
        self.imageUI = image
        self.width = width
        self.height = height
        self.tintColorData = tintColor
        self.contentScale = contentScale
        self.imageHashDecoder = imageHashDecoder
        imgPreview.image = UIImage.defaultPlaceholder
        super.init(frame: CGRect.zero)
        placeHolder.image = UIImage.defaultPlaceholder
        imgPreview.translatesAutoresizingMaskIntoConstraints = false
        self.addSubview(imgPreview)
        NSLayoutConstraint.activate([
            imgPreview.topAnchor.constraint(equalTo: self.topAnchor),
            imgPreview.bottomAnchor.constraint(equalTo: self.bottomAnchor),
            imgPreview.trailingAnchor.constraint(equalTo: self.trailingAnchor),
            imgPreview.leadingAnchor.constraint(equalTo: self.leadingAnchor)
        ])
    }
    
    init(
        theme: Theme,
        imageViewState: ImageViewState? = nil,
        tintColor: Color? = nil,
        imageHashDecoder: ImageHashDecoder? = nil
    ) {
        self.theme = theme
        self.imageViewState = imageViewState
        self.tintColorData = tintColor
        self.imageHashDecoder = imageHashDecoder
        imgPreview.image = UIImage.defaultPlaceholder
        super.init(frame: CGRect.zero)
        placeHolder.image = UIImage.defaultPlaceholder
        imgPreview.translatesAutoresizingMaskIntoConstraints = false
        self.addSubview(imgPreview)
        NSLayoutConstraint.activate([
            imgPreview.topAnchor.constraint(equalTo: self.topAnchor),
            imgPreview.bottomAnchor.constraint(equalTo: self.bottomAnchor),
            imgPreview.trailingAnchor.constraint(equalTo: self.trailingAnchor),
            imgPreview.leadingAnchor.constraint(equalTo: self.leadingAnchor)
        ])
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    deinit {
        self.placeHolder.image = nil
    }
    
    func setup() {
        hideErrorImageView()
        imgPreview.contentMode = getContentMode()
        if let width = finalWidth, let height = finalHeight {
            if widthConstraint == nil {
                widthConstraint = self.widthAnchor.constraint(equalToConstant: CGFloat(width))
                widthConstraint?.isActive = true
            } else {
                widthConstraint?.constant = CGFloat(width)
            }
            if heightConstraint == nil {
                heightConstraint = self.heightAnchor.constraint(equalToConstant: CGFloat(height))
                heightConstraint?.isActive = true
            } else {
                heightConstraint?.constant = CGFloat(height)
            }
        }
        
        if let imageUI = finalImageUI as? ImageUIImageStates {
            if let successImageUI = imageUI.success as? ImageUIImageResource {
                if let loadingImageUI = imageUI.loadingImage as? ImageUIImageResource {
                    imageResourceView(imageResource: successImageUI, loadingImageResource: loadingImageUI)
                } else {
                    imageResourceView(imageResource: successImageUI)
                }
            }
        } else if let imageUI = finalImageUI as? ImageUIImageResource {
            imageResourceView(imageResource: imageUI)
        }
    }
    
    func imageResourceView(imageResource: ImageUIImageResource, loadingImageResource: ImageUIImageResource? = nil)  {
        imgPreview.image = UIImage.defaultPlaceholder
        
        let tintColor = self.tintColorData ?? imageResource.tintColorToken?.toColor(themeColors: theme.themeColors)
        
        if let imageUrl = (imageResource.resource as? ResourceUrlImage)?.url {
            
            let urlString = CustomImageView.process(imageUrl: imageUrl, width: finalWidth, height: finalHeight)
            
            guard let url = URL(string: urlString) else { return }
            
            let isImageCached = ImageCache.default.isCached(forKey: url.absoluteString)
//            Log.d("[CustomImageView] isImageCached: \(isImageCached), url: \(url.absoluteString)")
            
            if !isImageCached,
               let hashedImage = loadingImageResource?.resource as? ResourceHashedImage,
               let width = finalWidth, let height = finalHeight {
                
                placeHolder.stringTag = hashedImage.toHashString()
                
                placeHolder.configure(hashedImage: hashedImage, imageHashDecoder: imageHashDecoder, width: CGFloat(width), height: CGFloat(height))
                
                placeHolder.retrieveHashedImage(hashedImage: hashedImage, imageHashDecoder: imageHashDecoder, width: CGFloat(width), height: CGFloat(height), completion: { [weak self] image, hashString in
                    guard let self = self else { return }
                    if self.placeHolder.stringTag == hashString {
                        self.placeHolder.image = image
                    }
                })
                
                imgPreview.kf.setImage(
                    with: url,
                    placeholder: placeHolder,
                    options: [
                        .transition(.fade(fadeDuration)),
                        .retryStrategy(self.retryStrategy)
                    ]
                ) { result in
                    switch result {
                    case .success(_):
//                        Log.i("[ImagePrefetcherKingfisher] cacheType: \(result.cacheType), url: \(url.absoluteString)")
                        self.hideErrorImageView()
                    case .failure(let data):
                        if !data.isTaskCancelled && !data.isNotCurrentTask {
                            self.showErrorImageView()
                        }
                    }
                }
            
            } else {
                imgPreview.kf.setImage(
                    with: url,
                    placeholder: UIImage.defaultPlaceholder,
                    options: [.transition(.fade(fadeDuration))]
                ) { [weak self] result in
                    switch result {
                    case .success(_):
                        self?.applyTintColor(tintColor: tintColor)
                    case .failure(_):
                        break
                    }
                }
            }
            
        } else if let localImage = imageResource.resource as? ResourceLocalImageAssetFile {
            imgPreview.image = UIImage(named: localImage.fileName)
        } else if let byteArrayResource = imageResource.resource as? ResourceRawByteArray {
            imgPreview.image = UIImage(data: byteArrayResource.byteArray.toNSData())
        }
        
        applyTintColor(tintColor: tintColor)
    }
    
    private func applyTintColor(tintColor: Color?) {
        if let tintColor {
            imgPreview.tintColor = tintColor.uiColor()
            imgPreview.contentMode = .scaleAspectFit
            let image = imgPreview.image
            imgPreview.image = image?.withRenderingMode(.alwaysTemplate)
        } else {
            imgPreview.contentMode = getContentMode()
            let image = imgPreview.image
            imgPreview.image = image?.withRenderingMode(.alwaysOriginal)
        }
    }
    
    func getContentMode() -> UIView.ContentMode {
        let contentScale = imageViewState?.viewSpec.contentScale ?? self.contentScale
        if let contentScale, [.FillWidth(), .FillHeight()].contains(contentScale) {
            // Android supports FillWidth and FillHeight. iOS seemingly has no such equivalents. Do the best available option
            // by using Fit. See #2161.
            return .scaleAspectFit
        } else {
            return .scaleAspectFill
        }
    }
    
    private func showErrorImageView() {
        if errorImageView == nil {
            errorImageView = UIImageView(image: UIImage(named: "broken_image"))
            guard let errorImageView else { return }
            errorImageView.translatesAutoresizingMaskIntoConstraints = false
            self.addSubview(errorImageView)
            NSLayoutConstraint.activate([
                errorImageView.centerXAnchor.constraint(equalTo: self.centerXAnchor),
                errorImageView.centerYAnchor.constraint(equalTo: self.centerYAnchor),
                errorImageView.widthAnchor.constraint(equalToConstant: 30),
                errorImageView.heightAnchor.constraint(equalToConstant: 30)
            ])
            errorImageView.tintColor = .white
            errorImageView.contentMode = .scaleAspectFit
            let image = errorImageView.image
            errorImageView.image = image?.withRenderingMode(.alwaysTemplate)
        }
        errorImageView?.isHidden = false
    }
    
    private func hideErrorImageView() {
        errorImageView?.isHidden = true
    }
}

//PlaceHolder View
class HashedImage: UIImageView, Placeholder {
    
    var hashedImage: ResourceHashedImage!
    var imageHashDecoder: ImageHashDecoder?
    var width: CGFloat = .zero
    var height: CGFloat = .zero
    var finalHashedImage: UIImage?
    var stringTag: String? = nil
    var retrievalTask: Task<Void, Error>? = nil

    func configure(hashedImage: ResourceHashedImage, imageHashDecoder: ImageHashDecoder?, width: CGFloat, height: CGFloat) {
        self.hashedImage = hashedImage
        self.imageHashDecoder = imageHashDecoder
        self.width = width
        self.height = height
        self.image = UIImage.defaultPlaceholder
    }
    
    func retrieveHashedImage(hashedImage: ResourceHashedImage, imageHashDecoder: ImageHashDecoder?, width: CGFloat, height: CGFloat, completion: ((UIImage?, String) -> Void)?) {
        if let retrievalTask {
            retrievalTask.cancel()
        }
        
        retrievalTask = Task.detached {
            let hashString = (hashedImage.imageHash as! ImageHash.ImageHashBlur).blurHash
            let image = await UIImage.init(blurHash: hashString, size: CGSize(width: self.width, height: height))
            DispatchQueue.main.async {
                completion?(image, hashString)
            }
        }
    }
   
    deinit {
        self.image = nil
    }
    
    //MARK: PlaceHolder Protocol methods
    
    func add(to imageView: Kingfisher.KFCrossPlatformImageView) {
        imageView.addSubview(self)
        self.translatesAutoresizingMaskIntoConstraints = false
        
        NSLayoutConstraint.activate([
            self.leadingAnchor.constraint(equalTo: imageView.leadingAnchor),
            self.trailingAnchor.constraint(equalTo: imageView.trailingAnchor),
            self.bottomAnchor.constraint(equalTo: imageView.bottomAnchor),
            self.topAnchor.constraint(equalTo: imageView.topAnchor)
        ])
    }
    
    func remove(from imageView: Kingfisher.KFCrossPlatformImageView) {
        self.removeFromSuperview()
    }
}


extension CustomImageView {
    static func process(imageUrl: String, width: Float?, height: Float?) -> String {
        return imageUrl
    }
}
