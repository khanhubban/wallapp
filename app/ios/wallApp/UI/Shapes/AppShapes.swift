//
//  AppShapes.swift
//  WallApp
//

import WallApp
import SwiftUI


struct AppShapes {
    
    static let cutCorner = ShapeDescriptors(
        extraSmall: ShapeDescriptor(topLeading: 8, shapeType: .cutCorner),
        small: ShapeDescriptor(topLeading: 12, shapeType: .cutCorner),
        medium: ShapeDescriptor(topLeading: 16, shapeType: .cutCorner),
        large: ShapeDescriptor(topLeading: 20, shapeType: .cutCorner),
        extraLarge: ShapeDescriptor(topLeading: 24, shapeType: .cutCorner)
    )
    
    static let cutCorners = ShapeDescriptors(
        extraSmall: ShapeDescriptor(topLeading: 8, bottomTrailing: 8, shapeType: .cutCorner),
        small: ShapeDescriptor(topLeading: 12, bottomTrailing: 12, shapeType: .cutCorner),
        medium: ShapeDescriptor(topLeading: 16, bottomTrailing: 16, shapeType: .cutCorner),
        large: ShapeDescriptor(topLeading: 20, bottomTrailing: 20, shapeType: .cutCorner),
        extraLarge: ShapeDescriptor(topLeading: 24, bottomTrailing: 24, shapeType: .cutCorner)
    )
    
    static let roundedCornerBottomStart = ShapeDescriptors(
        extraSmall: ShapeDescriptor(bottomLeading: 4, shapeType: .unevenRoundedRectangle),
        small: ShapeDescriptor(bottomLeading: 8, shapeType: .unevenRoundedRectangle),
        medium: ShapeDescriptor(bottomLeading: 12, shapeType: .unevenRoundedRectangle),
        large: ShapeDescriptor(bottomLeading: 16, shapeType: .unevenRoundedRectangle),
        extraLarge: ShapeDescriptor(bottomLeading: 24, shapeType: .unevenRoundedRectangle)
    )
    
    static let roundedCornersBottom = ShapeDescriptors(
        extraSmall: ShapeDescriptor(bottomLeading: 4, bottomTrailing: 4, shapeType: .unevenRoundedRectangle),
        small: ShapeDescriptor(bottomLeading: 8, bottomTrailing: 8, shapeType: .unevenRoundedRectangle),
        medium: ShapeDescriptor(bottomLeading: 12, bottomTrailing: 12, shapeType: .unevenRoundedRectangle),
        large: ShapeDescriptor(bottomLeading: 16, bottomTrailing: 16, shapeType: .unevenRoundedRectangle),
        extraLarge: ShapeDescriptor(bottomLeading: 24, bottomTrailing: 24, shapeType: .unevenRoundedRectangle)
    )
    
    static let roundedCornersTop = ShapeDescriptors(
        extraSmall: ShapeDescriptor(topLeading: 4, topTrailing: 4, shapeType: .unevenRoundedRectangle),
        small: ShapeDescriptor(topLeading: 8, topTrailing: 8, shapeType: .unevenRoundedRectangle),
        medium: ShapeDescriptor(topLeading: 12, topTrailing: 12, shapeType: .unevenRoundedRectangle),
        large: ShapeDescriptor(topLeading: 16, topTrailing: 16, shapeType: .unevenRoundedRectangle),
        extraLarge: ShapeDescriptor(topLeading: 24, topTrailing: 24, shapeType: .unevenRoundedRectangle)
    )
    
    static let roundedCorners = ShapeDescriptors(
        extraSmall: ShapeDescriptor(all: 4, shapeType: .roundedRectangle),
        small: ShapeDescriptor(all: 8, shapeType: .roundedRectangle),
        medium: ShapeDescriptor(all: 12, shapeType: .roundedRectangle),
        large: ShapeDescriptor(all: 16, shapeType: .roundedRectangle),
        extraLarge: ShapeDescriptor(all: 24, shapeType: .roundedRectangle)
    )
    
    static let roundedCornersTopEndBottomStart = ShapeDescriptors(
        extraSmall: ShapeDescriptor(topTrailing: 4, bottomLeading: 4, shapeType: .unevenRoundedRectangle),
        small: ShapeDescriptor(topTrailing: 8, bottomLeading: 8, shapeType: .unevenRoundedRectangle),
        medium: ShapeDescriptor(topTrailing: 12, bottomLeading: 12, shapeType: .unevenRoundedRectangle),
        large: ShapeDescriptor(topTrailing: 16, bottomLeading: 16, shapeType: .unevenRoundedRectangle),
        extraLarge: ShapeDescriptor(topTrailing: 24, bottomLeading: 24, shapeType: .unevenRoundedRectangle)
    )
    
    static let square = ShapeDescriptors(
        extraSmall: ShapeDescriptor(shapeType: .roundedRectangle),
        small: ShapeDescriptor(shapeType: .roundedRectangle),
        medium: ShapeDescriptor(shapeType: .roundedRectangle),
        large: ShapeDescriptor(shapeType: .roundedRectangle),
        extraLarge: ShapeDescriptor(shapeType: .roundedRectangle)
    )
    
    static let circle = ShapeDescriptors(
        extraSmall: ShapeDescriptor(shapeType: .circle),
        small: ShapeDescriptor(shapeType: .circle),
        medium: ShapeDescriptor(shapeType: .circle),
        large: ShapeDescriptor(shapeType: .circle),
        extraLarge: ShapeDescriptor(shapeType: .circle)
    )
    
    static func getShapes(shapeStyle: ClipShapeStyle) -> ShapeDescriptors {
        switch shapeStyle {
        case .cutCorner:
            return cutCorner
        case .cutCorners:
            return cutCorners
        case .roundedCornerBottomStart:
            return roundedCornerBottomStart
        case .roundedCorners:
            return roundedCorners
        case .roundedCornersBottom:
            return roundedCornersBottom
        case .roundedCornersTop:
            return roundedCornersTop
        case .roundedCornersTopEndBottomStart:
            return roundedCornersTopEndBottomStart
        case .rectangle:
            return square
        case .circle:
            return circle
        }
    }
}
