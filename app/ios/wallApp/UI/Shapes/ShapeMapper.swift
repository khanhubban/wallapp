//
//  ShapeMapperSwiftUIDefault.swift
//  WallApp
//

import WallApp
import SwiftUI

struct ShapeMapper {
    
    static func map(shapes: ShapeDescriptors, shapeSize: ShapeSize) -> ShapeDescriptor {
        switch shapeSize {
        case .extraSmall:
            return shapes.extraSmall
        case .small:
            return shapes.small
        case .medium:
            return shapes.medium
        case .large:
            return shapes.large
        case .extraLarge:
            return shapes.extraLarge
        }
    }
    
    static func map(shapeStyle: ClipShapeStyle) -> any Shape {
        switch shapeStyle {
        case .circle:
            return Circle() // SwiftUI's Circle shape
        case .cutCorner:
            return CutCornerShape(topLeading: 12, topTrailing: 0, bottomLeading: 0, bottomTrailing: 0)
        case .cutCorners:
            return CutCornerShape(topLeading: 12, topTrailing: 0, bottomLeading: 0, bottomTrailing: 12)
        case .rectangle:
            return Rectangle() // SwiftUI's Rectangle shape
        case .roundedCornerBottomStart:
            return UnevenRoundedRectangle(bottomLeadingRadius: 16)
        case .roundedCorners:
            return RoundedRectangle(cornerRadius: 16)
        case .roundedCornersBottom:
            return UnevenRoundedRectangle(bottomLeadingRadius: 16, bottomTrailingRadius: 16)
        case .roundedCornersTop:
            return UnevenRoundedRectangle(topLeadingRadius: 16, topTrailingRadius: 16)
        case .roundedCornersTopEndBottomStart:
            return UnevenRoundedRectangle(bottomLeadingRadius: 16, topTrailingRadius: 16)
        }
    }
    
    static func map(shapeSpec: ShapeSpec?) -> [ShapeDescriptor] {
        switch onEnum(of: shapeSpec) {
        case .single(let single):
            if let shape = mapSingle(shapeSpec: single) {
                return [shape]
            } else {
                return []
            }
        case .multiple(let multiple):
            return multiple.shapeSpecs.compactMap { mapSingle(shapeSpec: $0) }
        case .none:
            return []
        }
    }
    
    private static func mapSingle(shapeSpec: ShapeSpec.Single) -> ShapeDescriptor? {
        guard let style = shapeSpec.shapeStyle, let size = shapeSpec.shapeSize else { return nil }
        let shapes = AppShapes.getShapes(shapeStyle: style)
        return map(shapes: shapes, shapeSize: size)
    }
}


extension UIView {
    
    func shape(shapeStyle: ClipShapeStyle) {
        switch shapeStyle {
        case .circle:
            self.layer.cornerRadius = self.frame.height / 2.0
            self.clipsToBounds = true
        case .cutCorner:
            let shapeLayer = CAShapeLayer()
            let path = CutCornerShape(topLeading: 12, topTrailing: 0, bottomLeading: 0, bottomTrailing: 0)
            shapeLayer.path = path.getCGPath(in: self.bounds).cgPath
            self.layer.addSublayer(shapeLayer)
        case .cutCorners:
            let shapeLayer = CAShapeLayer()
            let path = CutCornerShape(topLeading: 12, topTrailing: 0, bottomLeading: 0, bottomTrailing: 12)
            shapeLayer.path = path.getCGPath(in: self.bounds).cgPath
            self.layer.addSublayer(shapeLayer)
        case .rectangle:
            self.layer.cornerRadius = 10.0
            self.clipsToBounds = true
        case .roundedCornerBottomStart:
            self.layer.cornerRadius = 16
            self.layer.maskedCorners = [.layerMinXMaxYCorner]
        case .roundedCorners:
            self.layer.cornerRadius = 16
        case .roundedCornersBottom:
            self.layer.cornerRadius = 16
            self.layer.maskedCorners = [.layerMinXMaxYCorner, .layerMinXMinYCorner]
        case .roundedCornersTop:
            self.layer.cornerRadius = 16
            self.layer.maskedCorners = [.layerMinXMinYCorner, .layerMaxXMinYCorner]
        case .roundedCornersTopEndBottomStart:
            self.layer.cornerRadius = 16
            self.layer.maskedCorners = [.layerMinXMaxYCorner, .layerMaxXMinYCorner]
        }
    }
    
    func shape(from descriptor: ShapeDescriptor)  {
        switch descriptor.shapeType {
        case .cutCorner:
            let path = CutCornerShape(topLeading: descriptor.topLeading, topTrailing: descriptor.topTrailing, bottomLeading: descriptor.bottomLeading, bottomTrailing: descriptor.bottomTrailing)
            let bezierPath = path.getCGPath(in: self.bounds)
            let shapeLayer = CAShapeLayer()
            shapeLayer.path = bezierPath.cgPath
            layer.mask = shapeLayer
            self.layer.masksToBounds = true
            break
        case .unevenRoundedRectangle:
            var maskedCorner: CACornerMask = CACornerMask()
            
            if descriptor.topLeading != 0 {
                maskedCorner = maskedCorner.union(.layerMinXMinYCorner)
                self.layer.cornerRadius = descriptor.topLeading
            }
            
            if descriptor.topTrailing != 0 {
                maskedCorner = maskedCorner.union(.layerMinXMaxYCorner)
                self.layer.cornerRadius = descriptor.topTrailing
            }
            
            if descriptor.bottomLeading != 0 {
                maskedCorner = maskedCorner.union(.layerMinXMaxYCorner)
                self.layer.cornerRadius = descriptor.bottomLeading
            }
            
            if descriptor.bottomTrailing != 0 {
                maskedCorner = maskedCorner.union(.layerMaxXMaxYCorner)
                self.layer.cornerRadius = descriptor.bottomTrailing
            }
            
            self.layer.maskedCorners = maskedCorner
            self.layer.masksToBounds = true
            break
        case .roundedRectangle:
            self.layer.cornerRadius = descriptor.all
            self.clipsToBounds = true
        case .circle:
            self.layer.cornerRadius = self.frame.height / 2.0
            self.clipsToBounds = true
        }
    }
    
    func applyShape(shapeSpec: ShapeSpec) {
        let shape = ShapeMapper.map(shapeSpec: shapeSpec)
        shape.forEach { descriptor in
            self.shape(from: descriptor)
        }
    }
}
