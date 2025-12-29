//
//  CollectionWallpaperFeedPreviewCell.swift
//  WallApp
//

import UIKit
import WallApp
import SwiftUI


class CollectionWallpaperFeedPreviewCell: UICollectionViewCell {
    
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
    func configureCell(wallpaperPreview: WallpaperPreviewViewState, viewSpec: FeedContentPreviewViewSpec, theme: Theme, render: RenderIos) {
        self.wallpaperPreview = wallpaperPreview
        self.theme = theme
        self.render = render
        
        if !isInitialized {
            feedContentView = FeedContentView(
                title: title,
                imageViewState: imageViewState,
                favoriteViewState: favorite,
                scrimImage: scrimImage,
                viewSpec: wallpaperPreview.viewSpec,
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
