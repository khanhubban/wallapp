//
//  StyledText.swift
//  WallApp
//

import WallApp
import SwiftUI
import UIKit


struct StyledTextView : View {
    
    let text: StyledText
    let theme: Theme
    let colorOverride: Color?
    
    init(text: StyledText, theme: Theme, colorOverride: Color? = nil) {
        self.text = text
        self.theme = theme
        self.colorOverride = colorOverride
    }
    
    var body: some View {
        let fontSizeRange = (text as? StyledText.AutoSize)?.fontSizeRange
        let ignoreLargeSystemFontScaling = (text as? StyledText.Fixed)?.ignoreLargeSystemFontScaling == true
        let color  = colorOverride ??
            (text.style == .caption ?
                .gray :
                text.colorToken?.toColor(themeColors: theme.themeColors) ?? theme.themeColors.onSurface?.toColor())
        Text(text.string)
            .setTextStyle(text.style)
            .if (text.fontWeight != nil) { $0.setFontWeight(text.fontWeight!) }
            .if (color != nil) { $0.foregroundColor(color) }
            .if (text.textAlign != nil) { $0.setTextAlignment(text.textAlign!) }
            .lineLimit(text.maxLines?.intValue)
            // TODO: Dynamic fonts need fixing. They are not working as expected and messing with the width of the whole Text view, making it take the entire width available to it.
//            .if (fontSizeRange != nil) { $0.dynamicFontSize(minFontSize: fontSizeRange!.min.toCGFloat(),
//                                                            maxFontSize: fontSizeRange!.max.toCGFloat(),
//                                                            step: fontSizeRange!.step.toCGFloat(),
//                                                            text: text.string) }
            .if (ignoreLargeSystemFontScaling) { $0.dynamicTypeSize(...DynamicTypeSize.large) }
    }
}


class StyledLabel: UILabel {
    
    var styledText: StyledText?
    var theme: Theme
    var colorOverride: Color?
    
    init(text: StyledText?, theme: Theme, colorOverride: Color? = nil) {
        self.styledText = text
        self.theme = theme
        self.colorOverride = colorOverride
        super.init(frame: .zero)
        setup()
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    func updateWith(text: StyledText? = nil, theme: Theme? = nil, colorOverride: Color? = nil) {
        if let text {
            self.styledText = text
        }
        if let theme {
            self.theme = theme
        }
        if let colorOverride {
            self.colorOverride = colorOverride
        }
        setup()
    }
    
    private func setup() {
        self.text = styledText?.string
        self.setStyle(styledText, theme: theme, colorOverride: colorOverride)
        let ignoreLargeSystemFontScaling = (self.styledText as? StyledText.Fixed)?.ignoreLargeSystemFontScaling == true
        if (ignoreLargeSystemFontScaling) {
            self.maximumContentSizeCategory = .large
        }
        self.textAlignment = styledText?.textAlign?.toNSTextAlignment ?? .natural
    }
    
    func adjust() {
        /// Uncomment below changes to apply dynmaic font size
        /*
        if let fontSizeRange = (styledText as? StyledText.AutoSize)?.fontSizeRange {
            self.adjustFontSize(minFontSize: CGFloat(fontSizeRange.min),
                           maxFontSize: CGFloat(fontSizeRange.max),
                           step: CGFloat(fontSizeRange.step),
                           text: styledText.string)
        }
         */
    }
     
}

extension TextAlign {
    var toNSTextAlignment: NSTextAlignment {
        switch onEnum(of: self) {
        case .center:
            return .center
        case .start:
            return .left
        case .end:
            return .right
        case .justify(_):
            return .justified
        }
    }
}
