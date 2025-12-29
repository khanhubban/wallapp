//
//  WallpaperPhotoViewCell.swift
//  WallApp
//

import UIKit
import WallApp

class WallpaperPhotoViewCell: UICollectionViewCell {
    
    var customImageView: CustomImageView!
    var imageViewState: ImageViewState!
    var render: RenderIos!
    var theme: Theme!
    var isInitialized = false
    
    func configureCell(render: RenderIos, theme: Theme, imageViewState: ImageViewState) {
        self.imageViewState = imageViewState
        self.theme = theme
        self.render = render
        let width = imageViewState.viewSpec.width
        let height = imageViewState.viewSpec.height
        if !isInitialized {
            let customImageView = CustomImageView(theme: self.theme, image: imageViewState.image, width: width, height: height)
            self.customImageView = customImageView
            self.addSubview(self.customImageView)
            self.customImageView.translatesAutoresizingMaskIntoConstraints = false
            
            NSLayoutConstraint.activate([
                self.customImageView.topAnchor.constraint(equalTo: self.topAnchor),
                self.customImageView.leadingAnchor.constraint(equalTo: self.leadingAnchor),
                self.customImageView.widthAnchor.constraint(equalToConstant: width.toCGFloat()),
                self.customImageView.heightAnchor.constraint(equalToConstant: height.toCGFloat())
            ])
        } else {
            self.customImageView.theme = self.theme
            self.customImageView.imageViewState = imageViewState
        }
        self.customImageView.setup()
    }
}
