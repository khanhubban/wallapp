//
//  RatingView.swift
//  WallApp
//

import Foundation
import UIKit

class RatingView: UIStackView {
    
    @IBOutlet weak var star1: UIImageView!
    @IBOutlet weak var star2: UIImageView!
    @IBOutlet weak var star3: UIImageView!
    @IBOutlet weak var star4: UIImageView!
    @IBOutlet weak var star5: UIImageView!
    
    
    func setUp(rating: Double, theme: Theme) {
        let arrStars = [star1, star2, star3, star4, star5]

        arrStars.enumerated().forEach { details in
            details.element?.tintColor = theme.themeColors.secondary.uiColor
            if Double(details.offset) <= rating {
                details.element?.image = UIImage(systemName: "star.fill")?.withRenderingMode(.alwaysTemplate)
            } else if Double(details.offset) == rating + 1 && (rating - Double(details.offset)) != 0 {
                details.element?.image = UIImage(systemName: "star.leadinghalf.filled")?.withRenderingMode(.alwaysTemplate)
            } else {
                details.element?.image = UIImage(systemName: "star")?.withRenderingMode(.alwaysTemplate)
            }
        }
    }
    
}
