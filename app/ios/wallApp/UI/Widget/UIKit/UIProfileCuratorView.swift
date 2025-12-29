//
//  UIProfileCuratorView.swift
//  WallApp
//

import WallApp
import UIKit

class UIProfileCuratorView: UIView {

    private var render: RenderIos!
    private var theme: Theme!
    private var viewState: ProfileCuratorViewState!
    private var viewSpec: ProfileCuratorViewSpec!
    
    var profileImageView: CustomImageView?
    var shadowImageView: CustomImageView?
    private var nameLabel: StyledLabel!
    
    init(render: RenderIos, theme: Theme, viewState: ProfileCuratorViewState) {
        self.render = render
        self.theme = theme
        self.viewState = viewState
        self.viewSpec = viewState.viewSpec
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
        
        self.backgroundColor = viewState.backgroundColor.toColor(themeColors: theme.themeColors)?.uiColor()
        
        let verticalStackView = UIStackView()
        verticalStackView.axis = .vertical
        verticalStackView.alignment = .center
        
        addSubview(verticalStackView)

        // Use negative spacing so items appear correctly spaced
        verticalStackView.spacing = -2
        verticalStackView.translatesAutoresizingMaskIntoConstraints = false
        
        // Setup shadow image and profile image together in a "box"
        let profileContainerView = UIView()
        profileContainerView.translatesAutoresizingMaskIntoConstraints = false
        verticalStackView.addArrangedSubview(profileContainerView)
        
        let profileImageSize = viewState.profileImage.imageViewSpec.width.toCGFloat()
        let profileShadowImageSize = viewSpec.profileShadowImageSize.toCGFloat()
        let containerHeight = max(profileImageSize, profileShadowImageSize)
        
        NSLayoutConstraint.activate([
            profileContainerView.heightAnchor.constraint(equalToConstant: containerHeight),
            profileContainerView.widthAnchor.constraint(equalTo: verticalStackView.widthAnchor)
        ])
        
        if let shadowImage = viewState.profileShadowImage {
            shadowImageView = CustomImageView(theme: theme, image: shadowImage)
            shadowImageView?.translatesAutoresizingMaskIntoConstraints = false
            shadowImageView?.clipsToBounds = false
            
            if let shadowImageView = shadowImageView {
                profileContainerView.addSubview(shadowImageView)
                
                shadowImageView.centerInSuperview(size: CGSize(width: profileShadowImageSize, height: profileShadowImageSize))
            }
        }
        
        profileImageView = CustomImageView(theme: theme, imageViewState: viewState.profileImage.imageViewState)
        profileImageView?.layer.cornerRadius = profileImageSize / 2
        profileImageView?.clipsToBounds = true
        profileImageView?.layer.borderColor = UIColor.white.cgColor
        profileImageView?.layer.borderWidth = viewState.profileImage.borderSize.toCGFloat()
        profileImageView?.translatesAutoresizingMaskIntoConstraints = false
        
        if let profileImageView {
            profileContainerView.addSubview(profileImageView)
            
            profileImageView.centerInSuperview(size: CGSize(width: profileImageSize, height: profileImageSize))
        }
        
        nameLabel = StyledLabel(text: viewState.name, theme: theme)
        nameLabel.translatesAutoresizingMaskIntoConstraints = false
        verticalStackView.addArrangedSubview(nameLabel)
        
        let paddingDefault = render.defaultViewSpec.paddingDefault.toCGFloat()
        // Setup container constraints
        NSLayoutConstraint.activate([
            verticalStackView.centerXAnchor.constraint(equalTo: self.centerXAnchor),
            verticalStackView.topAnchor.constraint(lessThanOrEqualTo: self.topAnchor, constant: paddingDefault),
            verticalStackView.bottomAnchor.constraint(lessThanOrEqualTo: self.bottomAnchor, constant: -paddingDefault)
        ])
        
        // Add tap gesture for event handling
        self.addTapGesture {
            self.viewState.eventHandler.invoke()
        }
    }
    
    func updateWith(viewState: ProfileCuratorViewState) {
        self.viewState = viewState
        profileImageView?.imageViewState = viewState.profileImage.imageViewState
        shadowImageView?.imageUI = viewState.profileShadowImage
        nameLabel.updateWith(text: viewState.name, theme: theme)
        
        profileImageView?.setup()
        shadowImageView?.setup()
    }
}
