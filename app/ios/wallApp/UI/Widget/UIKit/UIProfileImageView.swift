//
//  UIProfileImageView.swift
//  WallApp
//

import WallApp
import UIKit

class UIProfileImageView: UIView {
    var render: RenderIos
    var theme: Theme {
        didSet {
            updateWith(theme: theme)
        }
    }
    var viewState: ProfileImageViewState
    var imageSize: Float
    
    private var imageView: CustomImageView!
    private var indicatorImage: UIView?
    private var indicatorContainer: CircleView?
    
    private var indicatorTopConstraint: NSLayoutConstraint?
    private var indicatorLeadingConstraint: NSLayoutConstraint?
    
    init(render: RenderIos, theme: Theme, viewState: ProfileImageViewState, imageSize: Float) {
        self.render = render
        self.theme = theme
        self.viewState = viewState
        self.imageSize = imageSize
        super.init(frame: .zero)
        initialize()
        self.backgroundColor = theme.themeColors.background.uiColor
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func layoutSubviews() {
        imageView.layer.cornerRadius = imageSize.toCGFloat() / 2
        imageView.layer.masksToBounds = true
        
        if let indicator = viewState.indicator {
            let viewSpec = indicator.viewSpec
            let indicatorContainerSize = viewSpec.size
            let borderWidth = viewSpec.borderSize.toCGFloat()
            let indicatorContainerSizeCG = indicatorContainerSize.toCGFloat()
            let indicatorSizeCG = indicatorContainerSizeCG - borderWidth * 2
            let indicatorSize = Float(indicatorSizeCG)
            let indicatorOrigin = CGFloat(imageSize - indicatorSize)
            indicatorContainer?.frame = CGRect(x: indicatorOrigin - borderWidth, y: indicatorOrigin - borderWidth, width: indicatorContainerSizeCG, height: indicatorContainerSizeCG)
            
            indicatorContainer?.layer.cornerRadius = indicatorContainerSizeCG / 2
            indicatorContainer?.layer.masksToBounds = true
            indicatorContainer?.layer.borderColor = viewState.indicator?.colorToken.toColor(themeColors: theme.themeColors)?.cgColor ?? Color.white.cgColor
            indicatorContainer?.layer.borderWidth = borderWidth
        }
    }
    
    private func initialize() {
        guard let profileImageItem = viewState.image as? MenuItem.MenuItemImage else { return }
        imageView = CustomImageView(theme: theme, image: profileImageItem.imageViewState.image, width: imageSize, height: imageSize)
        imageView.translatesAutoresizingMaskIntoConstraints = false
        addSubview(imageView)
        NSLayoutConstraint.activate([
            imageView.topAnchor.constraint(equalTo: topAnchor),
            imageView.leadingAnchor.constraint(equalTo: leadingAnchor),
        ])
        imageView.setup()
        
        if let indicator = viewState.indicator {
            let viewSpec = indicator.viewSpec
            let borderWidth = viewSpec.borderSize
            let indicatorContainerSize = viewSpec.size.toCGFloat()
            let indicatorSize = viewSpec.size - borderWidth * 2
            let indicatorSizeCG = indicatorSize.toCGFloat()
            
            indicatorContainer = CircleView(imageSize: indicatorContainerSize, fillColor: .clear)
            addSubview(indicatorContainer!)
            
            if let indicator = indicator.image as? ImageUIImageResource, let animatedImage = indicator.resource as? ResourceAnimatedImage {
                self.indicatorImage = UILottieAnimationView(animatedImageSpec: animatedImage.animatedImageSpec, width: indicatorSizeCG, height: indicatorSizeCG)
                addSubview(self.indicatorImage!)
            } else {
                let indicatorImage = CustomImageView(theme: theme, image: indicator.image, width: indicatorSize, height: indicatorSize)
                addSubview(indicatorImage)
                indicatorImage.setup()
                self.indicatorImage = indicatorImage
            }
            
            indicatorImage?.translatesAutoresizingMaskIntoConstraints = false
            indicatorTopConstraint = indicatorImage?.topAnchor.constraint(equalTo: topAnchor, constant: imageSize.toCGFloat() - indicatorSizeCG)
            indicatorLeadingConstraint = indicatorImage?.leadingAnchor.constraint(equalTo: leadingAnchor, constant: imageSize.toCGFloat() - indicatorSizeCG)
            NSLayoutConstraint.activate([
                indicatorTopConstraint!,
                indicatorLeadingConstraint!,
            ])
        }
        
        if let eventHandler = viewState.eventHandler {
            self.isUserInteractionEnabled = true
            self.addTapGesture { [unowned self] in
                eventHandler.invoke()
            }
        }
    }
    
    func updateProfileViewState(viewState: ProfileImageViewState) {
        
        guard let profileImageItem = viewState.image as? MenuItem.MenuItemImage else { return }
        imageView.imageUI = profileImageItem.imageViewState.image
        imageView.setup()
        self.viewState = viewState
        if let indicatorImage = indicatorImage as? UILottieAnimationView, let indicator = viewState.indicator?.image as? ImageUIImageResource, let animatedImage = indicator.resource as? ResourceAnimatedImage {
            if !animatedImage.animatedImageSpec.arePropertiesEqualTo(indicatorImage.animatedImageSpec) {
                indicatorImage.updateWith(animatedImageSpec: animatedImage.animatedImageSpec)
            }
            
        }
        
        if let eventHandler = viewState.eventHandler {
            self.isUserInteractionEnabled = true
            self.gestureRecognizers?.removeAll(where: { $0 is PanelTapGesture })
            self.addTapGesture {
                eventHandler.invoke()
            }
        } else {
            self.isUserInteractionEnabled = false
        }
    }
    
    func updateWith(theme: Theme) {
        self.backgroundColor = theme.themeColors.background.uiColor
        if let indicator = viewState.indicator {
            indicatorContainer?.layer.borderColor = indicator.colorToken.toColor(themeColors: theme.themeColors)?.cgColor ?? Color.white.cgColor
        }
    }
    
    func updateWith(viewState: ProfileImageViewState, imageSize: Float, theme: Theme) {
        updateWith(theme: theme)
//        Log.d("[UIKitView] updateProfileImage: \(imageSize)")
        guard let profileImageItem = viewState.image as? MenuItem.MenuItemImage else { return }
        self.imageSize = imageSize
        self.viewState = viewState
        imageView.imageUI = profileImageItem.imageViewState.image
        imageView.width = imageSize
        imageView.height = imageSize
        imageView.setup()

        if let indicatorState = viewState.indicator {
            let viewSpec = indicatorState.viewSpec
            let indicatorSize = viewSpec.size - viewSpec.borderSize * 2
            if let indicatorImage = indicatorImage as? CustomImageView {
                indicatorImage.imageUI = indicatorState.image
                indicatorImage.width = indicatorSize
                indicatorImage.height = indicatorSize
                indicatorImage.setup()
            } else if let indicatorImage = indicatorImage as? UILottieAnimationView, let indicator = indicatorState.image as? ImageUIImageResource, let animatedImage = indicator.resource as? ResourceAnimatedImage {
                if !animatedImage.animatedImageSpec.arePropertiesEqualTo(indicatorImage.animatedImageSpec) {
                    indicatorImage.updateWith(animatedImageSpec: animatedImage.animatedImageSpec)
                }
                indicatorImage.updateWith(width: indicatorSize.toCGFloat(), height: indicatorSize.toCGFloat())
            }
            indicatorTopConstraint?.constant = imageSize.toCGFloat() - indicatorSize.toCGFloat()
            indicatorLeadingConstraint?.constant = imageSize.toCGFloat() - indicatorSize.toCGFloat()
        }
        
        if let eventHandler = viewState.eventHandler {
            self.isUserInteractionEnabled = true
            self.gestureRecognizers?.removeAll(where: { $0 is PanelTapGesture })
            self.addTapGesture {
                eventHandler.invoke()
            }
        } else {
            self.isUserInteractionEnabled = false
        }
    }
}


class CircleView: UIView {
    var fillColor: UIColor = .white
    var imageSize: CGFloat = 100
    
    init(imageSize: CGFloat, fillColor: UIColor) {
        self.imageSize = imageSize
        self.fillColor = fillColor
        super.init(frame: CGRect(x: 0, y: 0, width: imageSize, height: imageSize))
        self.backgroundColor = .clear
    }
    
    required init?(coder: NSCoder) {
        super.init(coder: coder)
        self.backgroundColor = .clear
    }
    
    override func draw(_ rect: CGRect) {
        guard let context = UIGraphicsGetCurrentContext() else { return }
        
        context.beginPath()
        context.addEllipse(in: CGRect(x: 0, y: 0, width: imageSize, height: imageSize))
        context.setFillColor(fillColor.cgColor)
        context.fillPath()
    }
    
    func updateCircle(imageSize: CGFloat, fillColor: UIColor) {
        self.imageSize = imageSize
        self.fillColor = fillColor
        self.frame = CGRect(x: self.frame.origin.x, y: self.frame.origin.y, width: imageSize, height: imageSize)
        setNeedsDisplay()
    }
}
