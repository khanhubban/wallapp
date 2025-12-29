import Foundation
import SwiftUI

struct CutCornerShape: Shape {
    var topLeading: CGFloat = 0
    var topTrailing: CGFloat = 0
    var bottomLeading: CGFloat = 0
    var bottomTrailing: CGFloat = 0

    func path(in rect: CGRect) -> Path {
        var path = Path()

        let topLeadingCut = min(min(topLeading, rect.width / 2), rect.height / 2)
        let topTrailingCut = min(min(topTrailing, rect.width / 2), rect.height / 2)
        let bottomLeadingCut = min(min(bottomLeading, rect.width / 2), rect.height / 2)
        let bottomTrailingCut = min(min(bottomTrailing, rect.width / 2), rect.height / 2)

        path.move(to: CGPoint(x: rect.minX + topLeadingCut, y: rect.minY))
        path.addLine(to: CGPoint(x: rect.maxX - topTrailingCut, y: rect.minY))
        path.addLine(to: CGPoint(x: rect.maxX, y: rect.minY + topTrailingCut))
        path.addLine(to: CGPoint(x: rect.maxX, y: rect.maxY - bottomTrailingCut))
        path.addLine(to: CGPoint(x: rect.maxX - bottomTrailingCut, y: rect.maxY))
        path.addLine(to: CGPoint(x: rect.minX + bottomLeadingCut, y: rect.maxY))
        path.addLine(to: CGPoint(x: rect.minX, y: rect.maxY - bottomLeadingCut))
        path.addLine(to: CGPoint(x: rect.minX, y: rect.minY + topLeadingCut))
        path.closeSubpath()

        return path
    }
    
    func getCGPath(in rect: CGRect) -> UIBezierPath {
        var path = UIBezierPath()

        let topLeadingCut = min(min(topLeading, rect.width / 2), rect.height / 2)
        let topTrailingCut = min(min(topTrailing, rect.width / 2), rect.height / 2)
        let bottomLeadingCut = min(min(bottomLeading, rect.width / 2), rect.height / 2)
        let bottomTrailingCut = min(min(bottomTrailing, rect.width / 2), rect.height / 2)

        path.move(to: CGPoint(x: rect.minX + topLeadingCut, y: rect.minY))
        path.addLine(to: CGPoint(x: rect.maxX - topTrailingCut, y: rect.minY))
        path.addLine(to: CGPoint(x: rect.maxX, y: rect.minY + topTrailingCut))
        path.addLine(to: CGPoint(x: rect.maxX, y: rect.maxY - bottomTrailingCut))
        path.addLine(to: CGPoint(x: rect.maxX - bottomTrailingCut, y: rect.maxY))
        path.addLine(to: CGPoint(x: rect.minX + bottomLeadingCut, y: rect.maxY))
        path.addLine(to: CGPoint(x: rect.minX, y: rect.maxY - bottomLeadingCut))
        path.addLine(to: CGPoint(x: rect.minX, y: rect.minY + topLeadingCut))
        path.close()

        return path
    }
    
    func getUnevenPath(in rect: CGRect) -> UIBezierPath {
        let path = UIBezierPath()
        let cornerRadii = [topLeading, topTrailing, bottomTrailing, bottomLeading]
        path.move(to: CGPoint(x: rect.minX + cornerRadii[0], y: rect.minY))
        path.addLine(to: CGPoint(x: rect.maxX - cornerRadii[1], y: rect.minY))
        path.addArc(withCenter: CGPoint(x: rect.maxX - cornerRadii[1], y: rect.minY + cornerRadii[1]), radius: cornerRadii[1], startAngle: -CGFloat.pi / 2, endAngle: 0, clockwise: true)
        path.addLine(to: CGPoint(x: rect.maxX, y: rect.maxY - cornerRadii[2]))
        path.addArc(withCenter: CGPoint(x: rect.maxX - cornerRadii[2], y: rect.maxY - cornerRadii[2]), radius: cornerRadii[2], startAngle: 0, endAngle: CGFloat.pi / 2, clockwise: true)
        path.addLine(to: CGPoint(x: rect.minX + cornerRadii[3], y: rect.maxY))
        path.addArc(withCenter: CGPoint(x: rect.minX + cornerRadii[3], y: rect.maxY - cornerRadii[3]), radius: cornerRadii[3], startAngle: CGFloat.pi / 2, endAngle: CGFloat.pi, clockwise: true)
        path.addLine(to: CGPoint(x: rect.minX, y: rect.minY + cornerRadii[0]))
        path.addArc(withCenter: CGPoint(x: rect.minX + cornerRadii[0], y: rect.minY + cornerRadii[0]), radius: cornerRadii[0], startAngle: CGFloat.pi, endAngle: -CGFloat.pi / 2, clockwise: true)
        path.close()
        return path
    }
}



struct CutCornerBorderShape: Shape {
    var topLeading: CGFloat = 0
    var topTrailing: CGFloat = 0
    var bottomLeading: CGFloat = 0
    var bottomTrailing: CGFloat = 0
    var strokeWidth: CGFloat = 1 // You can adjust the stroke width as needed
    var strokeColor: Color = .black // You can adjust the stroke color as needed
    
    func path(in rect: CGRect) -> Path {
        var path = Path()
        
        let topLeadingCut = min(min(topLeading, rect.width / 2), rect.height / 2)
        let topTrailingCut = min(min(topTrailing, rect.width / 2), rect.height / 2)
        let bottomLeadingCut = min(min(bottomLeading, rect.width / 2), rect.height / 2)
        let bottomTrailingCut = min(min(bottomTrailing, rect.width / 2), rect.height / 2)
        
        path.move(to: CGPoint(x: rect.minX + topLeadingCut, y: rect.minY))
        path.addLine(to: CGPoint(x: rect.maxX - topTrailingCut, y: rect.minY))
        path.addLine(to: CGPoint(x: rect.maxX, y: rect.minY + topTrailingCut))
        path.addLine(to: CGPoint(x: rect.maxX, y: rect.maxY - bottomTrailingCut))
        path.addLine(to: CGPoint(x: rect.maxX - bottomTrailingCut, y: rect.maxY))
        path.addLine(to: CGPoint(x: rect.minX + bottomLeadingCut, y: rect.maxY))
        path.addLine(to: CGPoint(x: rect.minX, y: rect.maxY - bottomLeadingCut))
        path.addLine(to: CGPoint(x: rect.minX, y: rect.minY + topLeadingCut))
        
        path.closeSubpath()
        
        return path
    }
    
    func strokeBorder(color: Color, lineWidth: CGFloat) -> some View {
        self.stroke(color, lineWidth: lineWidth)
    }
}

