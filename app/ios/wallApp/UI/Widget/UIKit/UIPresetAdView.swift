//
//  UIPresetAdView.swift
//  WallApp
//

import WallApp
import UIKit

class UIPresetAdView: UIView {
    var adViewState: AdViewState
    var theme: Theme
    var render: RenderIos
    
    var headerTitle: StyledLabel!
    var footerTitle: StyledLabel!
    
    private var imageView: CustomImageView!
    var heroButton: UIView!
    
    var closeButton: UIView?
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    init(render: RenderIos, theme: Theme, adViewState: AdViewState) {
        self.adViewState = adViewState
        self.theme = theme
        self.render = render
        super.init(frame: .zero)
        
        let width = render.windowFrame.deviceWidth - (render.defaultViewSpec.paddingDefault * 4)
        let imageHeight = adViewState.viewSpec.imageHeight.toCGFloat()
        
        let paddingDefault = render.defaultViewSpec.paddingDefault.toCGFloat()
        
        headerTitle = StyledLabel(text: adViewState.title, theme: theme)
        headerTitle.translatesAutoresizingMaskIntoConstraints = false
        self.addSubview(headerTitle)
        
        NSLayoutConstraint.activate([
            headerTitle.topAnchor.constraint(equalTo: self.topAnchor),
            headerTitle.centerXAnchor.constraint(equalTo: self.centerXAnchor)
        ])
        
        self.imageView = CustomImageView(theme: theme, image: adViewState.image)
        imageView.layer.cornerRadius = 10.0
        imageView.clipsToBounds = true
        self.addSubview(imageView)
        
        self.imageView.translatesAutoresizingMaskIntoConstraints = false
        
        let height = self.imageView.heightAnchor.constraint(equalToConstant: imageHeight)
        let widthAnchor = self.imageView.widthAnchor.constraint(equalToConstant: CGFloat(width))
        height.priority = UILayoutPriority(999)
        NSLayoutConstraint.activate([
            imageView.centerXAnchor.constraint(equalTo: self.centerXAnchor),
            imageView.topAnchor.constraint(equalTo: headerTitle.bottomAnchor),
            height,
            widthAnchor
        ])
        
        let heroButton = adViewState.heroButton as? MenuItem.MenuItemButton
        self.heroButton = createMenuItemContent(theme: theme, menuItem: adViewState.heroButton)
        self.addSubview(self.heroButton)
        self.heroButton.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint.activate([
            self.heroButton.bottomAnchor.constraint(equalTo: self.bottomAnchor, constant: -paddingDefault),
            self.heroButton.trailingAnchor.constraint(equalTo: headerTitle.trailingAnchor),
            self.heroButton.leadingAnchor.constraint(equalTo: headerTitle.leadingAnchor),
            self.heroButton.heightAnchor.constraint(equalToConstant: heroButton?.height?.toCGFloat() ?? 50)
        ])
        
        footerTitle = StyledLabel(text: adViewState.summary, theme: theme)
        footerTitle.translatesAutoresizingMaskIntoConstraints = false
        self.insertSubview(footerTitle, aboveSubview: imageView)
        
        NSLayoutConstraint.activate([
            footerTitle.topAnchor.constraint(equalTo: imageView.bottomAnchor, constant: paddingDefault),
            footerTitle.bottomAnchor.constraint(equalTo: self.heroButton.topAnchor, constant: -paddingDefault),
            footerTitle.centerXAnchor.constraint(equalTo: self.centerXAnchor)
        ])

        if let closeMenuButton = adViewState.closeButton {
            closeButton = createMenuItemContent(theme: theme, menuItem: closeMenuButton)
            self.addSubview(closeButton!)
            closeButton!.translatesAutoresizingMaskIntoConstraints = false
            let topPadding = adViewState.viewSpec.closeButtonPadding?.top.toCGFloat() ?? 0
            let trailingPadding = adViewState.viewSpec.closeButtonPadding?.end.toCGFloat() ?? 0
            NSLayoutConstraint.activate([
                closeButton!.topAnchor.constraint(equalTo: self.topAnchor, constant: topPadding),
                closeButton!.trailingAnchor.constraint(equalTo: self.trailingAnchor, constant: -trailingPadding)
            ])
        }
        
        imageView.setup()
        
        if let shape = adViewState.shapeSpec {
            self.applyShape(shapeSpec: shape)
        }
        self.backgroundColor = theme.themeColors.surface.uiColor
    }
    
    func updateWith(theme: Theme, adViewState: AdViewState) {
        self.theme = theme
        self.adViewState = adViewState
        
        self.backgroundColor = theme.themeColors.surface.uiColor
        
        imageView.theme = theme
        imageView.imageUI = adViewState.image
        imageView.setup()
        
        let textColor = theme.themeColors.onSurface?.color.uiColor
        headerTitle.text = adViewState.title.string
        headerTitle.textColor = textColor
        footerTitle.text = adViewState.summary.string
        footerTitle.textColor = textColor
        
        updateMenuItemWith(theme: theme, menuItem: adViewState.heroButton, menuItemView: heroButton)
        
        if let closeMenuButton = adViewState.closeButton {
            updateMenuItemWith(theme: theme, menuItem: closeMenuButton, menuItemView: closeButton!)
        }
        
        self.addTapGesture {
            adViewState.viewEventHandler.invoke()
        }
    }
}
