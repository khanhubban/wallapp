//
//  UINativeAdLoadingView.swift
//  WallApp
//

import UIKit

class UINativeAdLoadingView: UIView {
    
    let render: RenderIos
    var topShimmerView: UIShimmerView!
    var bottomShimmerView: UIShimmerView!
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    init(render: RenderIos) {
        self.render = render
        super.init(frame: .zero)
        self.setUp()
    }
    
    func setUp() {
        let paddingDefault = render.defaultViewSpec.paddingDefault.toCGFloat()
        topShimmerView = UIShimmerView()
        self.addSubview(topShimmerView)
        topShimmerView.translatesAutoresizingMaskIntoConstraints = false
        
        let titleHeight: CGFloat = 60
        NSLayoutConstraint.activate([
            self.topShimmerView.topAnchor.constraint(equalTo: self.topAnchor, constant: paddingDefault),
            self.topShimmerView.leadingAnchor.constraint(equalTo: self.leadingAnchor, constant: paddingDefault),
            self.topShimmerView.trailingAnchor.constraint(equalTo: self.trailingAnchor, constant: -paddingDefault),
            self.topShimmerView.heightAnchor.constraint(equalToConstant: titleHeight)
        ])
        
        topShimmerView.startAnimating()
        
        bottomShimmerView = UIShimmerView()
        self.addSubview(bottomShimmerView)
        bottomShimmerView.translatesAutoresizingMaskIntoConstraints = false
        
        NSLayoutConstraint.activate([
            self.bottomShimmerView.topAnchor.constraint(equalTo: topShimmerView.bottomAnchor, constant: paddingDefault),
            self.bottomShimmerView.bottomAnchor.constraint(equalTo: self.bottomAnchor, constant: -paddingDefault),
            self.bottomShimmerView.leadingAnchor.constraint(equalTo: self.leadingAnchor, constant: paddingDefault),
            self.bottomShimmerView.trailingAnchor.constraint(equalTo: self.trailingAnchor, constant: -paddingDefault),
        ])
        
        bottomShimmerView.startAnimating()
        
        self.backgroundColor =  UIColor.gray.withAlphaComponent(0.5)
    }
    
    
}
