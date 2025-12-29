//
//  PaddingHeaderView.swift
//  WallApp
//

import WallApp
import UIKit

struct PaddingHeaderViewState {
    let height: CGFloat
}

class PaddingHeaderView: UICollectionReusableView {
    
    static let identifier = "PaddingHeaderView"
    
    let paddingView = UIView()
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        addSubview(paddingView)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    func configureCell(theme: Theme) {
        paddingView.backgroundColor = theme.themeColors.background.uiColor
    }
}
