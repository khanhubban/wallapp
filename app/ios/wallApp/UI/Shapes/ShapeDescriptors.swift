//
//  ShapeDescriptors.swift
//  WallApp
//

import Foundation

struct ShapeDescriptors {
    let extraSmall: ShapeDescriptor
    let small: ShapeDescriptor
    let medium: ShapeDescriptor
    let large: ShapeDescriptor
    let extraLarge: ShapeDescriptor
}


struct ShapeDescriptor {
    var topLeading: CGFloat = 0
    var topTrailing: CGFloat = 0
    var bottomLeading: CGFloat = 0
    var bottomTrailing: CGFloat = 0
    var all: CGFloat = 0
    var shapeType: ShapeType
}

enum ShapeType {
    case cutCorner
    case unevenRoundedRectangle
    case roundedRectangle
    case circle
}
