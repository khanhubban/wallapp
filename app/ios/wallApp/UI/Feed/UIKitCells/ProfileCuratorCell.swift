//
//  ProfileCuratorCell.swift
//  WallApp
//

import WallApp
import UIKit

class ProfileCuratorCell: UICollectionViewCell {
    var render: RenderIos!
    var theme: Theme!
    var viewState: ProfileCuratorViewState!
    var viewSpec: ProfileCuratorViewSpec!
    
    var profileCuratorView: UIProfileCuratorView!
    
    var isInitialized: Bool = false
    
    func configureCell(render: RenderIos, theme: Theme, viewState: ProfileCuratorViewState) {
        self.render = render
        self.theme = theme
        self.viewState = viewState
        self.viewSpec = viewState.viewSpec
        
        if !isInitialized {
            profileCuratorView = UIProfileCuratorView(render: render, theme: theme, viewState: viewState)
            
            profileCuratorView.translatesAutoresizingMaskIntoConstraints = false
            
            addSubview(profileCuratorView)
            
            profileCuratorView.fillSuperview()
            isInitialized = true
        }
        
        if isInitialized {
            profileCuratorView.updateWith(viewState: viewState)
        }
    }
    
    override func draw(_ rect: CGRect) {
        profileCuratorView.applyShape(shapeSpec: viewState.viewSpec.containerShapeSpec)
    }
}
