//
//  PresetAdViewCell.swift
//  WallApp
//

import WallApp
import UIKit

class PresetAdViewCell: UICollectionViewCell {
    var render: RenderIos?
    var theme: Theme?
    var adViewState: AdViewState?
    
    private var presetAdView: UIPresetAdView!
    
    private var isInitialized = false
    
    override init(frame: CGRect) {
        super.init(frame: frame)
    }
    
    required init?(coder: NSCoder) {
        super.init(coder: coder)
    }
    
    override func prepareForReuse() {
        super.prepareForReuse()
    }
    
    func configureCell(render: RenderIos, theme: Theme, adViewState: AdViewState) {
        self.render = render
        self.theme = theme
        self.adViewState = adViewState
     
        if !isInitialized {
            presetAdView = UIPresetAdView(render: render, theme: theme, adViewState: adViewState)
            presetAdView.translatesAutoresizingMaskIntoConstraints = false
            self.addSubview(presetAdView)
            
            NSLayoutConstraint.activate([
                presetAdView.topAnchor.constraint(equalTo: self.topAnchor),
                presetAdView.bottomAnchor.constraint(equalTo: self.bottomAnchor),
                presetAdView.trailingAnchor.constraint(equalTo: self.trailingAnchor),
                presetAdView.leadingAnchor.constraint(equalTo: self.leadingAnchor)
            ])
            
            isInitialized = true
        }
        presetAdView.updateWith(theme: theme, adViewState: adViewState)
    }
}
