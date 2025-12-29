//
//  ShimmerView.swift
//
//

import SwiftUI

struct ShimmerView: View {
    private let color: Color
    private let size: CGSize
    private let cornerRadius: CGFloat
    
    init(rotationAngle: Angle = Angle(degrees: 70),
         animationSpeed: CGFloat = 0.2,
         primaryColor: Color = .black.opacity(0.2),
         shimmerColor: Color = .white.opacity(0.6),
         size: CGSize = CGSize(width: 350, height: 200),
         cornerRadius: CGFloat = 10) {
        _ = rotationAngle
        _ = animationSpeed
        _ = shimmerColor
        self.color = primaryColor
        self.size = size
        self.cornerRadius = cornerRadius
    }
    
    var body: some View {
        color
            .frame(width: size.width, height: size.height)
            .cornerRadius(cornerRadius)
    }
}

struct ShimmerView_Previews: PreviewProvider {
    static var previews: some View {
        VStack {
            HStack {
                ShimmerView(size: CGSize(width: 60, height: 60))
                VStack(alignment: .leading) {
                    ShimmerView(size: CGSize(width: 180, height: 13))
                    ShimmerView(size: CGSize(width: 120, height: 13))
                    ShimmerView(size: CGSize(width: 60, height: 13))
                }
            }
            
            ShimmerView()
        }
        .padding()
        .previewLayout(.sizeThatFits)
    }
}
