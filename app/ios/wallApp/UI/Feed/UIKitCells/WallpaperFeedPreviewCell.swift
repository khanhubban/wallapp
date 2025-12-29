//
//  WallpaperFeedPreviewCell.swift
//  WallApp
//

import UIKit
import WallApp
import SwiftUI


class WallpaperFeedPreviewCell: UICollectionViewCell {
    
    //MARK: Variables
    var wallpaperPreview: WallpaperPreviewViewState!
    var theme: Theme!
    var render: RenderIos!
    
    var feedContentView: FeedContentView!
    
    var isInitialized = false
    
    var scrimImage: ImageUI { self.wallpaperPreview.scrimImage }
    
    var favorite: FavoriteViewState { self.wallpaperPreview.favorite }
    
    var title: StyledText? { wallpaperPreview.title }
    
    var imageViewState: ImageViewState { wallpaperPreview.imageViewState }
    
    override func awakeFromNib() {
        super.awakeFromNib()
    }

    //MARK: Configure & Setup Method
    func configureCell(wallpaperPreview: WallpaperPreviewViewState, theme: Theme, render: RenderIos) {
        self.wallpaperPreview = wallpaperPreview
        self.theme = theme
        self.render = render
        
        if !isInitialized {
            feedContentView = FeedContentView(
                title: title,
                imageViewState: imageViewState,
                favoriteViewState: favorite,
                scrimImage: scrimImage,
                viewSpec: wallpaperPreview.viewSpec as! WallpaperPreviewViewSpec.WithFooter,
                theme: theme,
                render: render,
                plusIndicator: wallpaperPreview.plusIndicator
            )
            
            self.feedContentView.translatesAutoresizingMaskIntoConstraints = false
            
            self.addSubview(feedContentView)
            
            NSLayoutConstraint.activate([
                feedContentView.topAnchor.constraint(equalTo: self.topAnchor),
                feedContentView.bottomAnchor.constraint(equalTo: self.bottomAnchor),
                feedContentView.trailingAnchor.constraint(equalTo: self.trailingAnchor),
                feedContentView.leadingAnchor.constraint(equalTo: self.leadingAnchor)
            ])
            
            isInitialized = true
        }
        
        self.setup()
    }
    
    override func draw(_ rect: CGRect) {
        if let shapeSpec = wallpaperPreview.viewSpec.shapeSpec {
            self.applyShape(shapeSpec: shapeSpec)
        }
    }
  
    func setup() {
        let onClick = wallpaperPreview.onClick
        
        feedContentView.title = title
        feedContentView.imageViewState = imageViewState
        feedContentView.favoriteViewState = favorite
        feedContentView.scrimImage = scrimImage
        feedContentView.theme = theme
        feedContentView.render = render
        feedContentView.setup()
        
        self.addTapGesture { [weak self] in
            guard let _ = self else { return }
            onClick.invoke()
        }
    }
}


class FeedContentView: UIView {
    var title: StyledText?
    var imageViewState: ImageViewState
    var favoriteViewState: FavoriteViewState
    var scrimImage: ImageUI?
    var viewSpec: WallpaperPreviewViewSpec
    var theme: Theme
    var render: RenderIos
    var imageView: CustomImageView!
    var footerView: WallpaperFooterView?
    var plusIndicator: PlusIndicatorViewState?
    var indicator: UICollectionPlusIndicatorView?
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    init(title: StyledText?, imageViewState: ImageViewState, favoriteViewState: FavoriteViewState, scrimImage: ImageUI?, viewSpec: WallpaperPreviewViewSpec, theme: Theme, render: RenderIos, plusIndicator: PlusIndicatorViewState?) {
        self.title = title
        self.imageViewState = imageViewState
        self.favoriteViewState = favoriteViewState
        
        self.scrimImage = scrimImage
        self.viewSpec = viewSpec
        self.theme = theme
        self.render = render
        self.plusIndicator = plusIndicator
        super.init(frame: .zero)
        
        let width = viewSpec.width.toCGFloat()
        
        
        self.imageView = CustomImageView(theme: theme, imageViewState: self.imageViewState, imageHashDecoder: render.imageHashDecoder)
        
        self.addSubview(imageView)
        
        self.imageView.translatesAutoresizingMaskIntoConstraints = false
        
        NSLayoutConstraint.activate([
            imageView.topAnchor.constraint(equalTo: self.topAnchor),
            imageView.bottomAnchor.constraint(equalTo: self.bottomAnchor),
            imageView.trailingAnchor.constraint(equalTo: self.trailingAnchor),
            imageView.leadingAnchor.constraint(equalTo: self.leadingAnchor)
        ])
        
        if let wallpaperViewSpec = viewSpec as? WallpaperPreviewViewSpec.WithFooter {
            let footerHeight = wallpaperViewSpec.footerHeight.toCGFloat()
            let footerView = WallpaperFooterView(title: title, favoriteViewState: favoriteViewState, footerContentPadding: wallpaperViewSpec.footerContentPadding.toPadding(), scrimImage: scrimImage, width: width, height: footerHeight, theme: theme)
            self.footerView = footerView
            footerView.translatesAutoresizingMaskIntoConstraints = false
            self.addSubview(footerView)
            NSLayoutConstraint.activate([
                footerView.bottomAnchor.constraint(equalTo: self.bottomAnchor),
                footerView.trailingAnchor.constraint(equalTo: self.trailingAnchor),
                footerView.leadingAnchor.constraint(equalTo: self.leadingAnchor),
                footerView.heightAnchor.constraint(equalToConstant: footerHeight)
            ])
        }
        
        if let plusIndicator = self.plusIndicator {
            self.indicator = UICollectionPlusIndicatorView(theme: theme, plusIndicator: plusIndicator, render: render)
            guard let indicator = self.indicator else { return }
            self.addSubview(indicator)
            indicator.translatesAutoresizingMaskIntoConstraints = false
            indicator.backgroundColor = .clear
            indicator.setContentHuggingPriority(UILayoutPriority(rawValue: 999), for: .horizontal)
            self.addSubview(indicator)
            NSLayoutConstraint.activate([
                indicator.bottomAnchor.constraint(equalTo: footerView?.topAnchor ?? self.bottomAnchor),
                indicator.trailingAnchor.constraint(equalTo: self.trailingAnchor)
            ])
            indicator.clipsToBounds = true
        }
    }
    
    //MARK: Setup Method
    func setup() {
        imageView.theme = theme
        imageView.imageViewState = self.imageViewState
        imageView.setup()
        
        if let footerViewSpec = viewSpec as? WallpaperPreviewViewSpec.WithFooter {
            footerView?.title = title
            footerView?.favoriteViewState = favoriteViewState
            footerView?.footerContentPadding = footerViewSpec.footerContentPadding.toPadding()
            footerView?.scrimImage = scrimImage
            footerView?.setup()
        }
        
        if let plusIndicator = self.plusIndicator {
            self.indicator?.update(theme: self.theme, plusIndicator: plusIndicator, render: self.render)
        }
    }
}


class WallpaperFooterView: UIView {
    
    //MARK: Variables
    var title: StyledText?
    var favoriteViewState: FavoriteViewState
    var contentColor = Color.white
    var footerContentPadding: PaddingAll
    var scrimImage: ImageUI?
    var width: CGFloat
    var height: CGFloat
    var theme: Theme
    
    var vwStack: UIStackView!
    var backGroundImage: CustomImageView!
    var textView: StyledLabel?
    var isInitialized = false
    var styledLabel: StyledLabel?
    var favButton: UIFavoriteAnimatedButton?
    
    private let horizontalStackView: UIStackView = {
        let stackView = UIStackView()
        stackView.axis = .horizontal
        stackView.alignment = .center
        stackView.distribution = .fill
        return stackView
    }()
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    init(title: StyledText?, favoriteViewState: FavoriteViewState, footerContentPadding: PaddingAll, scrimImage: ImageUI?, width: CGFloat, height: CGFloat, theme: Theme) {
        self.title = title
        self.favoriteViewState = favoriteViewState
        self.footerContentPadding = footerContentPadding
        self.scrimImage = scrimImage
        self.width = width
        self.height = height
        self.theme = theme
        super.init(frame: .zero)
        
        if let scrimImage {
            self.backGroundImage = CustomImageView(theme: theme, image: scrimImage, width: Float(width), height: Float(height))
            self.backGroundImage.clipsToBounds = true
            self.backGroundImage.translatesAutoresizingMaskIntoConstraints = false
            self.addSubview(backGroundImage)
            
            NSLayoutConstraint.activate([
                backGroundImage.bottomAnchor.constraint(equalTo: self.bottomAnchor),
                backGroundImage.trailingAnchor.constraint(equalTo: self.trailingAnchor),
                backGroundImage.leadingAnchor.constraint(equalTo: self.leadingAnchor),
                backGroundImage.topAnchor.constraint(equalTo: self.topAnchor)
            ])
            
            self.backGroundImage.setup()
        }
        
        let defaultFooterPadding: CGFloat = 8
        
        self.horizontalStackView.translatesAutoresizingMaskIntoConstraints = false
        addSubview(horizontalStackView)
        NSLayoutConstraint.activate([
            horizontalStackView.bottomAnchor.constraint(equalTo: self.bottomAnchor),
            horizontalStackView.trailingAnchor.constraint(equalTo: self.trailingAnchor, constant: -defaultFooterPadding),
            horizontalStackView.leadingAnchor.constraint(equalTo: self.leadingAnchor, constant: footerContentPadding[.leading] ?? defaultFooterPadding),
            horizontalStackView.topAnchor.constraint(equalTo: self.topAnchor)
        ])
        
        if let title = title {
            self.styledLabel = StyledLabel(text: title, theme: theme, colorOverride: contentColor)
            horizontalStackView.addArrangedSubview(self.styledLabel!)
        }
        
        self.favButton = UIFavoriteAnimatedButton(theme: theme, viewState: favoriteViewState, contentColor: contentColor)
        self.favButton!.setContentHuggingPriority(UILayoutPriority(rawValue: 999), for: .horizontal)
        self.favButton!.translatesAutoresizingMaskIntoConstraints = false
        let width = favoriteViewState.viewSpec.size.toCGFloat()
        let height = favoriteViewState.viewSpec.size.toCGFloat()
        self.favButton!.widthAnchor.constraint(equalToConstant: width).isActive = true
        self.favButton!.heightAnchor.constraint(equalToConstant: height).isActive = true
        horizontalStackView.addArrangedSubview(self.favButton!)
    }
    
    
    override func layoutSubviews() {
        if let textView = self.textView {
            textView.adjust()
        }
    }
    
    func setup() {
        self.styledLabel?.updateWith(
            text: title,
            theme: theme,
            colorOverride: contentColor
        )
       
        self.favButton?.theme = self.theme
        self.favButton?.viewState = self.favoriteViewState
        self.favButton?.contentColor = self.contentColor
        self.favButton?.setup()
    }
}
