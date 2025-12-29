//
//  BackgroundDecorationView.swift
//  WallApp
//

import UIKit

class BackgroundDecorationView: UICollectionReusableView {
    static let identifier = "BackgroundDecorationView"
    
    override func apply(_ layoutAttributes: UICollectionViewLayoutAttributes) {
        super.apply(layoutAttributes)
        if let layoutAttributes = layoutAttributes as? BackgroundDecorationViewLayoutAttributes {
            backgroundColor = layoutAttributes.backgroundColor
        }
    }
    
    override func draw(_ rect: CGRect) {
        self.applyShape(shapeSpec: ShapeSpec(shapeStyle: .roundedCorners, shapeSize: .medium))
    }
}

class BackgroundDecorationViewLayoutAttributes : UICollectionViewLayoutAttributes {
    var backgroundColor: UIColor = .white
    
}
