import Foundation
import SwiftUI
import WallApp
import UIKit

extension Text {
    func bodyMediumTextStyle() -> Text {
        self
            .font(.custom("Montserrat-SemiBold", size: 17))
    }
    
    func displayMediumTextStyle() -> Text {
        self
            .font(.custom("Montserrat-Bold", size: 36))
            .fontWeight(.bold)
    }
    
    /// Set Style
    func setStyle(_ style: StyledText) -> Text {
        switch onEnum(of: style) {
        case .fixed(let data):
            
            // TODO: Need to manage marquee spacing
            // if let marqueeSpacing = data.marqueeSpacing as? {
            //
            // }
            
            if let fontWeight = data.fontWeight {
                return self.setFontWeight(fontWeight)
            }
            
        case .autoSize(let data):
            if let fontWeight = data.fontWeight {
                return self.setFontWeight(fontWeight)
            }
        }
        
        return self
    }
    
    func setFontWeight(_ weight: FontWeight) -> Text {
        switch weight {
        case .thin:
            return self.fontWeight(.thin)
            
        case .extraLight:
            return self.fontWeight(.ultraLight)
            
        case .light:
            return self.fontWeight(.light)
            
        case .normal:
            return self.fontWeight(.regular)
            
        case .medium:
            return self.fontWeight(.medium)
            
        case .semiBold:
            return self.fontWeight(.semibold)
            
        case .bold:
            return self.fontWeight(.bold)
            
        case .extraBold:
            return self.fontWeight(.heavy)
            
        case .black:
            return self.fontWeight(.black)
        }
    }
    
    /// Set TextStyle
    func setTextStyle(_ style: TextStyle) -> Text {
        switch style {
        case .caption:
            return self
                .font(.custom("Montserrat-Medium", size: 12))
                .fontWeight(.regular)
                .kerning(0.4)
            
        case .body:
            return self
                .font(.custom("Montserrat-Medium", size: 14))
                .fontWeight(.regular)
                .kerning(0.2)
            
        case .subheading:
            return self
                .font(.custom("Montserrat-Medium", size: 16))
                .fontWeight(.regular)
                .kerning(0)
            
        case .subheadingActive:
            return self
                .font(.custom("Montserrat-Bold", size: 16))
                .fontWeight(.bold)
                .kerning(0)
            
        case .callToAction:
            return self
                .font(.custom("Montserrat-Bold", size: 18))
                .fontWeight(.bold)
                .kerning(-0.1)
            
        case .headline:
            return self
                .font(.custom("Montserrat-Bold", size: 24))
                .fontWeight(.bold)
                .kerning(0)
            
        case .display:
            return self
                .font(.custom("Montserrat-Bold", size: 32))
                .fontWeight(.bold)
                .kerning(0)
        }
    }
    
    /// Set Alignment
    @ViewBuilder
    func setTextAlignment(_ textAlign: TextAlign) -> some View {
        switch onEnum(of: textAlign) {
        case .center(_):
            self.multilineTextAlignment(.center)
        case .end(_):
            self.multilineTextAlignment(.trailing)
        case .justify(_), .start(_):
            self.multilineTextAlignment(.leading)
        }
    }
}

//MARK: Styled UILabel
/// Extension regarding Styled Lable
extension UILabel {
    
    func setStyle(_ styledText: StyledText?, theme: Theme, colorOverride: Color? = nil) {
        
        guard let styledText = styledText else {
            self.text = ""
            self.attributedText = NSMutableAttributedString(string: "")
            return
        }
        
        // To apply kern we have to use attributed text
        self.text = styledText.string
        self.attributedText = NSMutableAttributedString(string: styledText.string)
        self.lineBreakMode = .byTruncatingTail
        self.setTextStyle(styledText.style)
        
        var attributedString: NSMutableAttributedString = NSMutableAttributedString.init(attributedString: self.attributedText!)
        if styledText.fontWeight != nil {
            let font = self.font.withWeight(UIFont.Weight(styledText.fontWeight!))
            attributedString.addAttribute(NSAttributedString.Key.font, value: font, range: NSRange(location: 0, length: attributedString.length))
        }
        
        let foregroundColor  = colorOverride ?? styledText.colorToken?.toColor(themeColors: theme.themeColors) ?? theme.themeColors.onSurface?.toColor()
        if foregroundColor != nil {
            self.textColor = foregroundColor!.uiColor()
            attributedString.addAttribute(NSAttributedString.Key.foregroundColor, value: foregroundColor!.uiColor(), range: NSRange(location: 0, length: attributedString.length))
        }
        
        if let alignment = styledText.textAlign {
            let textAlignment = self.getTextAlignment(alignment)
            let style = NSMutableParagraphStyle()
            style.alignment = textAlignment
            style.lineBreakMode = .byTruncatingTail
            attributedString.addAttribute(NSAttributedString.Key.paragraphStyle, value: style, range: NSRange(location: 0, length: attributedString.length))
        }
        
        self.numberOfLines = styledText.maxLines?.intValue ?? 0
        self.attributedText = attributedString
    }
    
    
    /// Set TextStyle
    func setTextStyle(_ style: TextStyle)  {
        switch style {
        case .caption:
            let nwFont = UIFont(name: "Montserrat-Medium", size: 12)?.withWeight(.regular)
            self.font = nwFont
            self.attributedText = NSAttributedString(string: self.text ?? "", attributes: [NSAttributedString.Key.kern: 0.4, NSAttributedString.Key.font: nwFont as Any])
            break
            
        case .body:
            let nwFont = UIFont(name: "Montserrat-Medium", size: 14)?.withWeight(.regular)
            self.font = nwFont
            self.attributedText = NSAttributedString(string: self.text ?? "", attributes: [NSAttributedString.Key.kern: 0.2, NSAttributedString.Key.font: nwFont as Any])
            break
            
        case .subheading:
            let nwFont = UIFont(name: "Montserrat-Medium", size: 16)?.withWeight(.regular)
            self.font = nwFont
            self.attributedText = NSAttributedString(string: self.text ?? "", attributes: [NSAttributedString.Key.font: nwFont as Any])
            break
            
        case .subheadingActive:
            let nwFont = UIFont(name: "Montserrat-Bold", size: 16)?.withWeight(.bold)
            self.font = nwFont
            self.attributedText = NSAttributedString(string: self.text ?? "", attributes: [NSAttributedString.Key.font: nwFont as Any])
            break
            
        case .callToAction:
            let nwFont = UIFont(name: "Montserrat-Bold", size: 18)?.withWeight(.bold)
            self.font = nwFont
            self.attributedText = NSAttributedString(string: self.text ?? "", attributes: [NSAttributedString.Key.font: nwFont as Any, NSAttributedString.Key.kern: -0.1])
            break
            
        case .headline:
            let nwFont = UIFont(name: "Montserrat-Bold", size: 24)?.withWeight(.bold)
            self.font = nwFont
            self.attributedText = NSAttributedString(string: self.text ?? "", attributes: [NSAttributedString.Key.font: nwFont as Any])
            break
            
        case .display:
            let nwFont = UIFont(name: "Montserrat-Bold", size: 32)?.withWeight(.bold)
            self.font = nwFont
            self.attributedText = NSAttributedString(string: self.text ?? "", attributes: [NSAttributedString.Key.font: nwFont as Any])
            break
        }
        
    }
    
    func getTextAlignment(_ textAlign: TextAlign) -> NSTextAlignment {
        switch onEnum(of: textAlign) {
        case .center(_):
            return .center
        case .end(_):
            return .right
        case .justify(_), .start(_):
            return .left
        }
    }
    
    func adjustFontSize(minFontSize: CGFloat, maxFontSize: CGFloat, step: CGFloat, text: String) {
        var fontSize = maxFontSize
        
        /// IF superview is stackview then require below code to get updated bounds
        if let stackView = self.superview as? UIStackView {
            stackView.setNeedsLayout()
            stackView.layoutIfNeeded()
        }
        
        let labelWidth = self.bounds.width
        let font = self.font.withSize(fontSize)
        
        while fontSize >= minFontSize {
            let size = (text as NSString).size(withAttributes: [.font: font])
            if size.width <= labelWidth {
                self.font = self.font.withSize(fontSize)
                break
            }
            fontSize -= step
        }
    }
    
    func setItalic() {
        guard let descriptor = UIFontDescriptor().withSymbolicTraits(UIFontDescriptor.SymbolicTraits([.traitItalic]).union(self.font.fontDescriptor.symbolicTraits)) else {
            return
        }
        self.font = UIFont(descriptor: descriptor, size: self.font.pointSize)
    }
   
}

//MARK: UIFont extension
extension UIFont {
    
    func withWeight(_ weight: Weight) -> UIFont {
        let newDescriptor = fontDescriptor.addingAttributes([.traits: [
            UIFontDescriptor.TraitKey.weight: weight]
                                                            ])
        return UIFont(descriptor: newDescriptor, size: pointSize)
    }
}


//MARK: UIFont Weight extension
extension UIFont.Weight {
    
    init(_ fontWeight: FontWeight) {
        switch fontWeight {
        case .thin:
            self = UIFont.Weight.thin
            break
        case .extraLight:
            self = UIFont.Weight.ultraLight
        case .light:
            self = UIFont.Weight.light
        case .normal:
            self = UIFont.Weight.regular
        case .medium:
            self = UIFont.Weight.medium
        case .semiBold:
            self = UIFont.Weight.semibold
        case .bold:
            self = UIFont.Weight.bold
        case .extraBold:
            self = UIFont.Weight.heavy
        case .black:
            self = UIFont.Weight.black
        }
    }
}

extension TextField {
    
    func setTextStyle(_ style: TextStyle) -> some View {
        switch style {
        case .caption:
            return self
                .font(.custom("Montserrat-Medium", size: 12))
                .fontWeight(.regular)
                .kerning(0.4)
            
        case .body:
            return self
                .font(.custom("Montserrat-Medium", size: 14))
                .fontWeight(.regular)
                .kerning(0.2)
            
        case .subheading:
            return self
                .font(.custom("Montserrat-Medium", size: 16))
                .fontWeight(.regular)
                .kerning(0)
            
        case .subheadingActive:
            return self
                .font(.custom("Montserrat-Bold", size: 16))
                .fontWeight(.bold)
                .kerning(0)
            
        case .callToAction:
            return self
                .font(.custom("Montserrat-Bold", size: 18))
                .fontWeight(.bold)
                .kerning(-0.1)
            
        case .headline:
            return self
                .font(.custom("Montserrat-Bold", size: 24))
                .fontWeight(.bold)
                .kerning(0)
            
        case .display:
            return self
                .font(.custom("Montserrat-Bold", size: 32))
                .fontWeight(.bold)
                .kerning(0)
        }
    }
}
