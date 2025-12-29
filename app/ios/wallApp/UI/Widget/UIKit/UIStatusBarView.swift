//
//  UIStatusBarView.swift
//  WallApp
//

import WallApp
import UIKit

class UIStatusBarView: UIView {
    var render: RenderIos
    var theme: Theme
    var color: UIColor?

    init(render: RenderIos, theme: Theme, color: UIColor? = nil) {
        self.render = render
        self.theme = theme
        self.color = color
        super.init(frame: .zero)
        setupView()
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    private func setupView() {
        translatesAutoresizingMaskIntoConstraints = false
        backgroundColor = color ?? theme.themeColors.surfaceVariant.uiColor

        // Since UIKit doesn't directly use SwiftUI's .frame modifier concept,
        // you must manually set the height constraint to match the status bar height.
        let statusBarHeight = render.windowFrame.statusBarHeight.toCGFloat()
        heightAnchor.constraint(equalToConstant: statusBarHeight).isActive = true

        // The maxWidth equivalent in UIKit is handled by constraining the view's leading and trailing to its superview,
        // which you should do when adding this view to a superview, not here.
    }
    
    func updateWith(render: RenderIos? = nil, theme: Theme? = nil, color: UIColor? = nil) {
        if let render = render {
            self.render = render
            let statusBarHeight = render.windowFrame.statusBarHeight.toCGFloat()
            heightAnchor.constraint(equalToConstant: statusBarHeight).isActive = true
        }
        if let theme = theme {
            self.theme = theme
            if color == nil {
                backgroundColor = theme.themeColors.surfaceVariant.uiColor
            }
        }
        if let color = color {
            self.color = color
            backgroundColor = color
        }
    }
}

