//
//  SpacerViewCell.swift
//  WallApp
//

import Foundation

class SpacerViewCell: UICollectionViewCell {
    
    //MARK: Variables
    var heightConstraint: NSLayoutConstraint?
    var widthConstraint: NSLayoutConstraint?
    
    //MARK: Configure & Setup Method
  
    func configureCell(viewState: SpacerViewState) {
        if self.heightConstraint == nil {
            self.heightConstraint = self.heightAnchor.constraint(equalToConstant: 0)
        }
        if self.widthConstraint == nil {
            self.widthConstraint = self.widthAnchor.constraint(equalToConstant: 0)
        }
        if let width = viewState.width?.dp, let height = viewState.height?.dp {
            self.heightConstraint?.constant = height.toCGFloat()
            self.widthConstraint?.constant = width.toCGFloat()
            self.heightConstraint?.isActive = true
            self.widthConstraint?.isActive = true
        } else if let width = viewState.width?.dp {
            self.widthConstraint?.constant = width.toCGFloat()
            self.heightConstraint?.isActive = false
            self.widthConstraint?.isActive = true
        } else if let height = viewState.height?.dp {
            self.heightConstraint?.constant = height.toCGFloat()
            self.heightConstraint?.isActive = true
            self.widthConstraint?.isActive = false
        }
    }
}
