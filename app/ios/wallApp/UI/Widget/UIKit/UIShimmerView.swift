//
//  UIShimmerView.swift
//  WallApp
//

import UIKit

class UIShimmerView: UIView {

    func startAnimating() {
        backgroundColor = UIColor.black.withAlphaComponent(0.2)
        layer.cornerRadius = 8.0
        clipsToBounds = true
    }

}
