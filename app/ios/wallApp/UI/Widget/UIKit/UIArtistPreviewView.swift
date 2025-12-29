//
//  UIArtistPreviewView.swift
//  WallApp
//

import WallApp
import UIKit

class UIArtistPreviewView: UIView {
    
    private var render: RenderIos!
    private var theme: Theme!
    private var viewState: ArtistPreviewViewState!
    private var viewSpec: FeedViewViewSpec!
    
    var leftImageView: CustomImageView?
    var rightImageView: CustomImageView?
    
    var profileImageView: CustomImageView?
    var shadowImageView: CustomImageView?
    
    var followIndicatorView: UILottieAnimationView?
    
    private let horizontalStackView: UIStackView = {
        let stackView = UIStackView()
        stackView.axis = .horizontal
        stackView.alignment = .center
        stackView.distribution = .fillEqually
        return stackView
    }()
    
    init(render: RenderIos, theme: Theme, viewState: ArtistPreviewViewState, viewSpec: FeedViewViewSpec) {
        self.render = render
        self.theme = theme
        self.viewState = viewState
        self.viewSpec = viewSpec
        super.init(frame: .zero)
        setupViews()
    }
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        setupViews()
    }
    
    required init?(coder: NSCoder) {
        super.init(coder: coder)
        setupViews()
    }
    
    private func setupViews() {
        guard let viewSpec = viewSpec as? ArtistPreviewViewSpecOnboarding else {
            return
        }
        for index in viewState.backgroundImages.indices {
            let image = viewState.backgroundImages[index]
            let imageView = CustomImageView(theme: theme, imageViewState: image)
            if index == 0 {
                leftImageView = imageView
                addSubview(leftImageView!)
            } else {
                rightImageView = imageView
                addSubview(rightImageView!)
            }
            if index == 1 {
                break
            }
        }
        
        if let leftImageView = self.leftImageView, let rightImageView = self.rightImageView {
            self.horizontalStackView.addArrangedSubview(leftImageView)
            self.horizontalStackView.addArrangedSubview(rightImageView)
            
            leftImageView.setup()
            rightImageView.setup()
        }
        
        self.addSubview(self.horizontalStackView)
        horizontalStackView.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint.activate([
            self.horizontalStackView.topAnchor.constraint(equalTo: self.topAnchor),
            self.horizontalStackView.bottomAnchor.constraint(equalTo: self.bottomAnchor),
            self.horizontalStackView.leadingAnchor.constraint(equalTo: self.leadingAnchor),
            self.horizontalStackView.trailingAnchor.constraint(equalTo: self.trailingAnchor),
        ])
        
        if let profileImageItem = viewState.profileImage.image as? MenuItem.MenuItemImage {
            let imageViewSpec = profileImageItem.imageViewState.viewSpec
            profileImageView = CustomImageView(theme: theme, imageViewState: profileImageItem.imageViewState)
            profileImageView?.layer.cornerRadius = imageViewSpec.size.toCGFloat() / 2
            profileImageView?.layer.masksToBounds = true
            profileImageView?.layer.borderColor = theme.themeColors.tertiary?.color.uiColor.cgColor ?? UIColor.white.cgColor
            profileImageView?.layer.borderWidth = viewState.profileImage.borderSize.toCGFloat()
            addSubview(profileImageView!)
            
            profileImageView?.translatesAutoresizingMaskIntoConstraints = false
            
            let circularImageSize: CGFloat = viewState.profileImage.imageViewSpec.size.toCGFloat()
            
            guard let profileImageView = self.profileImageView else { return }
            
            NSLayoutConstraint.activate([
                profileImageView.centerYAnchor.constraint(equalTo: self.centerYAnchor),
                profileImageView.centerXAnchor.constraint(equalTo: self.centerXAnchor),
                profileImageView.heightAnchor.constraint(equalToConstant: circularImageSize),
                profileImageView.widthAnchor.constraint(equalToConstant: circularImageSize),
            ])
            
            profileImageView.setup()
        }
        
        if let shadowImage = viewState.profileShadowImage {
            shadowImageView = CustomImageView(theme: theme, image: shadowImage)
            shadowImageView?.translatesAutoresizingMaskIntoConstraints = false
            
            guard let shadowImageView = self.shadowImageView else { return }
            self.insertSubview(shadowImageView, belowSubview: self.profileImageView!)
            
            let shadowImageSize: CGFloat = viewSpec.profileShadowImageSize.toCGFloat()
            NSLayoutConstraint.activate([
                shadowImageView.centerYAnchor.constraint(equalTo: self.centerYAnchor),
                shadowImageView.centerXAnchor.constraint(equalTo: self.centerXAnchor),
                shadowImageView.heightAnchor.constraint(equalToConstant: shadowImageSize),
                shadowImageView.widthAnchor.constraint(equalToConstant: shadowImageSize),
            ])
            
            shadowImageView.setup()
        }
        
        if let followIndicator = viewState.followIndicator, let image = followIndicator.background as? ImageUIImageResource, let animatedImage = image.resource as? ResourceAnimatedImage {
            followIndicatorView = UILottieAnimationView(
                animatedImageSpec: animatedImage.animatedImageSpec,
                width: viewSpec.width.toCGFloat(),
                height: viewSpec.height.toCGFloat()
            )
            addSubview(followIndicatorView!)
            followIndicatorView?.isHidden = !followIndicator.visible
            
            guard let followIndicatorView = self.followIndicatorView else { return }
            followIndicatorView.translatesAutoresizingMaskIntoConstraints = false
            NSLayoutConstraint.activate([
                followIndicatorView.topAnchor.constraint(equalTo: self.topAnchor),
                followIndicatorView.bottomAnchor.constraint(equalTo: self.bottomAnchor),
                followIndicatorView.leadingAnchor.constraint(equalTo: self.leadingAnchor),
                followIndicatorView.trailingAnchor.constraint(equalTo: self.trailingAnchor),
            ])
        }
        
        self.addTapGesture {
            self.viewState.eventHandler.invoke()
        }
        
        leftImageView?.clipsToBounds = true
        rightImageView?.clipsToBounds = true
    }
    
    func updateWith(viewState: ArtistPreviewViewState, updateFollowIndicator: Bool) {
        self.viewState = viewState
        leftImageView?.imageViewState = viewState.backgroundImages[0]
        rightImageView?.imageViewState = viewState.backgroundImages[1]
        if let profileImageItem = viewState.profileImage.image as? MenuItem.MenuItemImage {
            profileImageView?.imageViewState = profileImageItem.imageViewState
        }
        shadowImageView?.imageUI = viewState.profileShadowImage
        leftImageView?.setup()
        rightImageView?.setup()
        profileImageView?.setup()
        shadowImageView?.setup()
        
        if let followIndicator = viewState.followIndicator {
            followIndicatorView?.isHidden = !followIndicator.visible
            if updateFollowIndicator {
                followIndicatorView?.startAnimation()
            }
        }
    }
}
