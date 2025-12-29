//
//  UIArtistPreviewCell.swift
//  WallApp
//

import WallApp
import UIKit

class ArtistPreviewCell: UICollectionViewCell {
    var render: RenderIos!
    var theme: Theme!
    var viewState: ArtistPreviewViewState!
    var viewSpec: ArtistPreviewViewSpecOnboarding!
    
    var artistPreviewView: UIArtistPreviewView!
    
    var isInitialized: Bool = false
    
    func configureCell(render: RenderIos, theme: Theme, viewState: ArtistPreviewViewState, viewSpec: ArtistPreviewViewSpecOnboarding, updateFollowIndicator: Bool) {
        self.render = render
        self.theme = theme
        self.viewState = viewState
        self.viewSpec = viewSpec
        
        if !isInitialized {
            artistPreviewView = UIArtistPreviewView(render: render, theme: theme, viewState: viewState, viewSpec: viewSpec)
            
            artistPreviewView.translatesAutoresizingMaskIntoConstraints = false
            
            addSubview(artistPreviewView)
            
            NSLayoutConstraint.activate([
                artistPreviewView.widthAnchor.constraint(equalToConstant: CGFloat(viewSpec.width)),
                artistPreviewView.heightAnchor.constraint(equalToConstant: CGFloat(viewSpec.height))
            ])
            isInitialized = true
        }
        
        if isInitialized {
            artistPreviewView.updateWith(viewState: viewState, updateFollowIndicator: updateFollowIndicator)
        }
    }
    
    override func draw(_ rect: CGRect) {
        if let shape = viewState.containerShapeSpec {
            artistPreviewView.applyShape(shapeSpec: shape)
        }
    }
}
