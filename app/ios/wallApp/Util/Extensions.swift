//
//  Extensions.swift
//  WallApp
//

import Foundation
import SwiftUI
import WallApp
import UIKit
import ObjectiveC
import Kingfisher

extension NSObject {
    @discardableResult
    func apply(closure: (Self) -> ()) -> Self {
        closure(self)
        return self
    }
}

extension AnyTransition {
    
    /// Fade-in transition
    public static var fade: AnyTransition {
        let insertion = AnyTransition.opacity
        let removal = AnyTransition.identity
        return AnyTransition.asymmetric(insertion: insertion, removal: removal)
    }
    
    /// Fade-in transition with duration
    /// - Parameter duration: transition duration, use ease-in-out
    /// - Returns: A transition with duration
    public static func fade(duration: Double) -> AnyTransition {
        let insertion = AnyTransition.opacity.animation(.easeInOut(duration: duration))
        let removal = AnyTransition.identity
        return AnyTransition.asymmetric(insertion: insertion, removal: removal)
    }
}

//MARK: UIColor Extensions
extension UIColor {
    func imageWithColor(width: Int, height: Int) -> UIImage {
        let size = CGSize(width: width, height: height)
        return UIGraphicsImageRenderer(size: size).image { rendererContext in
            self.setFill()
            rendererContext.fill(CGRect(origin: .zero, size: size))
        }
    }
    
    var rgba: (red: CGFloat, green: CGFloat, blue: CGFloat, alpha: CGFloat) {
        var red: CGFloat = 0
        var green: CGFloat = 0
        var blue: CGFloat = 0
        var alpha: CGFloat = 0
        getRed(&red, green: &green, blue: &blue, alpha: &alpha)
        
        return (red, green, blue, alpha)
    }
}

//MARK: Color Extensions
extension Color {
 
    func uiColor() -> UIColor {

        if #available(iOS 14.0, *) {
            return UIColor(self)
        }

        let components = self.components()
        return UIColor(red: components.r, green: components.g, blue: components.b, alpha: components.a)
    }

    func components() -> (r: CGFloat, g: CGFloat, b: CGFloat, a: CGFloat) {

        let scanner = Scanner(string: self.description.trimmingCharacters(in: CharacterSet.alphanumerics.inverted))
        var hexNumber: UInt64 = 0
        var r: CGFloat = 0.0, g: CGFloat = 0.0, b: CGFloat = 0.0, a: CGFloat = 0.0

        let result = scanner.scanHexInt64(&hexNumber)
        if result {
            r = CGFloat((hexNumber & 0xff000000) >> 24) / 255
            g = CGFloat((hexNumber & 0x00ff0000) >> 16) / 255
            b = CGFloat((hexNumber & 0x0000ff00) >> 8) / 255
            a = CGFloat(hexNumber & 0x000000ff) / 255
        }
        return (r, g, b, a)
    }
}

//MARK: UIView Extensions
extension UIView {
    
    /// Add Tap Gesture
    func  addTapGesture(action : @escaping ()->Void ){
        self.gestureRecognizers?.removeAll(where: { $0 is PanelTapGesture })
        let tap = PanelTapGesture(target: self , action: #selector(self.handleTap(_:)))
        tap.action = action
        tap.numberOfTapsRequired = 1
        
        self.addGestureRecognizer(tap)
        self.isUserInteractionEnabled = true
        
    }

    @objc func handleTap(_ sender: PanelTapGesture) {
        sender.action!()
    }
    
    /**
     Rotate a view by specified degrees
     parameter angle: angle in degrees
     */
    
    func rotate(angle: CGFloat) {
        let radians = angle / 180.0 * CGFloat.pi
        let rotation = CGAffineTransformRotate(self.transform, radians);
        self.transform = rotation
    }
}

//MARK: UITapGestureRecognizer helper class
class PanelTapGesture: UITapGestureRecognizer {
    var action : (()->Void)? = nil
}

//MARK: Color Extensions
extension UIImage {
    
    static var defaultPlaceholder: UIImage! {
        get {
            if let whitePlaceholder = ImageCache.default.retrieveImageInMemoryCache(forKey: "white_placeholder") {
                return whitePlaceholder
            }
            guard let whiteImage = UIColor.white.imageWithColor(width: 100, height: 100).image(alpha: 0.1) else {
                return UIImage()
            }
            
            ImageCache.default.store(whiteImage, forKey: "white_placeholder")

            return whiteImage
        }
    }
    
}

extension UIImage {
    func image(alpha: CGFloat) -> UIImage? {
        UIGraphicsBeginImageContextWithOptions(size, false, scale)
        draw(at: .zero, blendMode: .normal, alpha: alpha)
        let newImage = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        return newImage
    }
}
