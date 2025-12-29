//
//  Extensions.swift
//  WallApp
//

import WallApp
import Foundation
import SwiftUI

extension UIScreen {
    static let screenWidth = UIScreen.main.bounds.size.width
    static let screenHeight = UIScreen.main.bounds.size.height
    static let screenScale = UIScreen.main.scale
}

extension View {
    /// Applies the given transform if the given condition evaluates to `true`.
    /// - Parameters:
    ///   - condition: The condition to evaluate.
    ///   - transform: The transform to apply to the source `View`.
    /// - Returns: Either the original `View` or the modified `View` if the condition is `true`.
    @ViewBuilder func `if`<Content: View>(_ condition: @autoclosure () -> Bool, transform: (Self) -> Content) -> some View {
        if condition() {
            transform(self)
        } else {
            self
        }
    }
    
    func padding(_ edgeLengths: [Edge.Set: CGFloat]) -> some View {
        return self
            .if (edgeLengths.keys.contains(.top)) { $0.padding(.top, edgeLengths[.top]!) }
            .if (edgeLengths.keys.contains(.leading)) { $0.padding(.leading, edgeLengths[.leading]!) }
            .if (edgeLengths.keys.contains(.bottom)) { $0.padding(.bottom, edgeLengths[.bottom]!) }
            .if (edgeLengths.keys.contains(.trailing)) { $0.padding(.trailing, edgeLengths[.trailing]!) }
    }
}

extension Text {
    @ViewBuilder func `if` (_ condition: @autoclosure () -> Bool, transform: (Self) -> Text) -> Text {
        condition() ? transform(self) : self
    }
}

extension Edge.Set : Hashable {
    public func hash(into hasher: inout Hasher) {
        hasher.combine(rawValue)
    }
}

extension Padding {
    func toPadding() -> PaddingAll {
        var edgeSet = [Edge.Set: CGFloat]()
        edgeSet[.top] = top.toCGFloat()
        edgeSet[.leading] = start.toCGFloat()
        edgeSet[.bottom] = bottom.toCGFloat()
        edgeSet[.trailing] = end.toCGFloat()
        return edgeSet
    }
}

extension Float {
    func toCGFloat() -> CGFloat {
        return CGFloat(self)
    }
    func toInt() -> Int32 {
        return Int32(self)
    }
    func toKotlinFloat() -> KotlinFloat {
        return KotlinFloat(float: self)
    }
}

extension CGFloat {
    func toInt32() -> Int32 {
        return Int32(self)
    }
    func toKotlinInt() -> KotlinInt {
        return KotlinInt(int: Int32(self))
    }
}

extension Int64 {
    func toCGFloat() -> CGFloat {
        return CGFloat(self)
    }
}

extension KotlinFloat {
    func toCGFloat() -> CGFloat {
        return CGFloat(self.floatValue)
    }
}

typealias PaddingAll = [Edge.Set: CGFloat]

extension UInt64 {
    func toColor() -> Color {
        Color(red: red(), green: green(), blue: blue(), opacity: alpha())
    }
}

extension ColorOptional {
    func toColor() -> Color {
        Color(red: color.red(), green: color.green(), blue: color.blue(), opacity: color.alpha())
    }
}

extension Comparable {
    func clamped(to limits: ClosedRange<Self>) -> Self {
        return min(max(self, limits.lowerBound), limits.upperBound)
    }
}

extension Binding {
    func onUpdate(_ closure: @escaping () -> Void) -> Binding<Value> {
        Binding(get: {
            wrappedValue
        }, set: { newValue in
            wrappedValue = newValue
            closure()
        })
    }
}

extension Int {
    func toCGFloat() -> CGFloat {
        return CGFloat(self)
    }
}

extension Array where Element: Hashable {
    func distinctUnordered() -> [Element] {
        return Array(Set(self))
    }
}

extension TimeInterval {
    func toInt64() -> Int64 {
        return Int64(self)
    }
}

extension UIImage {
    func resizeWith(newSize: CGSize) -> UIImage {
        let horizontalRatio = newSize.width / self.size.width
        let verticalRatio = newSize.height / self.size.height

        let ratio = max(horizontalRatio, verticalRatio)
        let newSize = CGSize(width: self.size.width * ratio, height: self.size.height * ratio)
        var newImage: UIImage

        let renderFormat = UIGraphicsImageRendererFormat.default()
        renderFormat.opaque = false
        let renderer = UIGraphicsImageRenderer(size: CGSize(width: newSize.width, height: newSize.height), format: renderFormat)
        newImage = renderer.image {
            (context) in
            self.draw(in: CGRect(x: 0, y: 0, width: newSize.width, height: newSize.height))
        }
        return newImage
    }
}

extension UIScrollView {
    
    var minContentOffset: CGPoint {
        return CGPoint(
            x: -contentInset.left,
            y: -contentInset.top
        )
    }
    
    var maxContentOffset: CGPoint {
        return CGPoint(
            x: contentSize.width - bounds.width + contentInset.right,
            y: contentSize.height - bounds.height + contentInset.bottom
        )
    }
}

extension Array {
    func uniques<T: Hashable>(by keyPath: KeyPath<Element, T>) -> [Element] {
        return reduce([]) { result, element in
            let alreadyExists = (result.contains(where: { $0[keyPath: keyPath] == element[keyPath: keyPath] }))
            return alreadyExists ? result : result + [element]
        }
    }
}

struct AnchoredConstraints {
    var top, leading, bottom, trailing, width, height: NSLayoutConstraint?
}

extension UIView {
    
    @discardableResult
    func anchor(top: NSLayoutYAxisAnchor?, leading: NSLayoutXAxisAnchor?, bottom: NSLayoutYAxisAnchor?, trailing: NSLayoutXAxisAnchor?, padding: UIEdgeInsets = .zero, size: CGSize = .zero) -> AnchoredConstraints {
        
        translatesAutoresizingMaskIntoConstraints = false
        var anchoredConstraints = AnchoredConstraints()
        
        if let top = top {
            anchoredConstraints.top = topAnchor.constraint(equalTo: top, constant: padding.top)
        }
        
        if let leading = leading {
            anchoredConstraints.leading = leadingAnchor.constraint(equalTo: leading, constant: padding.left)
        }
        
        if let bottom = bottom {
            anchoredConstraints.bottom = bottomAnchor.constraint(equalTo: bottom, constant: -padding.bottom)
        }
        
        if let trailing = trailing {
            anchoredConstraints.trailing = trailingAnchor.constraint(equalTo: trailing, constant: -padding.right)
        }
        
        if size.width != 0 {
            anchoredConstraints.width = widthAnchor.constraint(equalToConstant: size.width)
        }
        
        if size.height != 0 {
            anchoredConstraints.height = heightAnchor.constraint(equalToConstant: size.height)
        }
        
        [anchoredConstraints.top, anchoredConstraints.leading, anchoredConstraints.bottom, anchoredConstraints.trailing, anchoredConstraints.width, anchoredConstraints.height].forEach{ $0?.isActive = true }
        
        return anchoredConstraints
    }
    
    func fillSuperview(padding: UIEdgeInsets = .zero) {
        translatesAutoresizingMaskIntoConstraints = false
        if let superviewTopAnchor = superview?.topAnchor {
            topAnchor.constraint(equalTo: superviewTopAnchor, constant: padding.top).isActive = true
        }
        
        if let superviewBottomAnchor = superview?.bottomAnchor {
            bottomAnchor.constraint(equalTo: superviewBottomAnchor, constant: -padding.bottom).isActive = true
        }
        
        if let superviewLeadingAnchor = superview?.leadingAnchor {
            leadingAnchor.constraint(equalTo: superviewLeadingAnchor, constant: padding.left).isActive = true
        }
        
        if let superviewTrailingAnchor = superview?.trailingAnchor {
            trailingAnchor.constraint(equalTo: superviewTrailingAnchor, constant: -padding.right).isActive = true
        }
    }
    
    func centerInSuperview(size: CGSize = .zero) {
        translatesAutoresizingMaskIntoConstraints = false
        if let superviewCenterXAnchor = superview?.centerXAnchor {
            centerXAnchor.constraint(equalTo: superviewCenterXAnchor).isActive = true
        }
        
        if let superviewCenterYAnchor = superview?.centerYAnchor {
            centerYAnchor.constraint(equalTo: superviewCenterYAnchor).isActive = true
        }
        
        if size.width != 0 {
            widthAnchor.constraint(equalToConstant: size.width).isActive = true
        }
        
        if size.height != 0 {
            heightAnchor.constraint(equalToConstant: size.height).isActive = true
        }
    }
    
}
