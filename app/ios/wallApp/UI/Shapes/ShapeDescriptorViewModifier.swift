//
//  ShapeDescriptorViewModifier.swift
//  WallApp
//

import SwiftUI
import WallApp

struct ShapeDescriptorViewModifier: ViewModifier {
    let descriptor: ShapeDescriptor
    let borderWidth: CGFloat
    var borderColor: Color = .white
    
    func body(content: Content) -> some View {
        content
            .clipShape(
                self.shape(from: descriptor)
            )
            .if (borderWidth > 0) { $0.overlay(self.shape(from: descriptor).stroke(borderColor, lineWidth: borderWidth)) }
    }
    
    private func shape(from descriptor: ShapeDescriptor) -> AnyShape {
        switch descriptor.shapeType {
        case .cutCorner:
            return AnyShape(CutCornerShape(topLeading: descriptor.topLeading, topTrailing: descriptor.topTrailing, bottomLeading: descriptor.bottomLeading, bottomTrailing: descriptor.bottomTrailing))
        case .unevenRoundedRectangle:
            return AnyShape(UnevenRoundedRectangle(topLeadingRadius: descriptor.topLeading, bottomLeadingRadius: descriptor.bottomLeading, bottomTrailingRadius: descriptor.bottomTrailing, topTrailingRadius: descriptor.topTrailing))
        case .roundedRectangle:
            return AnyShape(RoundedRectangle(cornerRadius: descriptor.all))
        case .circle:
            return AnyShape(RoundedRectangle(cornerRadius: 50))
        }
    }
}

extension View {
    func applyClipShapes(_ shapeSpec: ShapeSpec, borderWidth: CGFloat = 0, borderColor: Color? = .white) -> some View {
        return self.applyClipShapes(ShapeMapper.map(shapeSpec: shapeSpec), borderWidth: borderWidth, borderColor: borderColor)
    }
    
    func applyClipShapes(_ descriptors: [ShapeDescriptor], borderWidth: CGFloat = 0, borderColor: Color? = .white) -> some View {
        // Check if there are any descriptors to apply
        if let firstDescriptor = descriptors.first {
            // Apply the first descriptor and recurse for the rest
            let remainingDescriptors = Array(descriptors.dropFirst())
            return AnyView(self.modifier(ShapeDescriptorViewModifier(descriptor: firstDescriptor, borderWidth: borderWidth, borderColor: borderColor ?? .white))
                            .applyClipShapes(remainingDescriptors))
        } else {
            // No descriptors left, return the view as is
            return AnyView(self)
        }
    }
}
