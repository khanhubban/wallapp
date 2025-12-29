import Foundation
import SwiftUI
import WallApp

struct DynamicFontSizeModifier: ViewModifier {
    var minFontSize: CGFloat
    var maxFontSize: CGFloat
    let step: CGFloat
    var text: String
    
    func body(content: Content) -> some View {
        GeometryReader { geometry in
            let actualFontSize = optimalFontSize(in: geometry.size)
            VStack {
                Spacer()
                content
                    .font(.system(size: actualFontSize))
                    .lineLimit(1)
                Spacer()
            }
        }
    }
    
    private func optimalFontSize(in availableSize: CGSize) -> CGFloat {
        let desiredFontSizeRange = Array(stride(from: maxFontSize, through: minFontSize, by: step))
        return desiredFontSizeRange.first(where: { fontSize in
            let size = (text as NSString).size(withAttributes: [.font: UIFont.systemFont(ofSize: fontSize)])
            return size.width <= availableSize.width
        }) ?? minFontSize
    }
    
    private func textHeight(for fontSize: CGFloat) -> CGFloat {
        return (text as NSString).size(withAttributes: [.font: UIFont.systemFont(ofSize: fontSize)]).height
    }
}

extension View {
    func dynamicFontSize(minFontSize: CGFloat, maxFontSize: CGFloat, step: CGFloat = -1, text: String) -> some View {
        self.modifier(DynamicFontSizeModifier(minFontSize: minFontSize, maxFontSize: maxFontSize, step: step, text: text))
    }
}
