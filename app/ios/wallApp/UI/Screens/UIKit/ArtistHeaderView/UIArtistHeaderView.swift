//
//  UIArtistHeaderView.swift
//  WallApp
//

import UIKit
@_exported import Combine

class UIArtistHeaderView: UIView {
    
    var render: RenderIos
    var theme: Theme {
        didSet {
            self.updateTheme()
        }
    }
    var viewState: ArtistToolbarViewState
    var viewSpec: ArtistViewSpec
    var tabs: TabsViewState.Indicator
    
    var selectedTab: TabSelectionController?
    private var heightConstraint: NSLayoutConstraint?
    var cancellable: AnyCancellable?
    var scrollOffsetController: ScrollOffsetController?
    var profileImage: UIProfileImageView?
    var name: StyledLabel?
    var tabBarView: UITopTabsView?
    
    private var labelTopConstraint: NSLayoutConstraint?
    private var tabBarTopConstraint: NSLayoutConstraint?
    private var profileImageTopConstraint: NSLayoutConstraint?
    private var profileImageWidthConstraint: NSLayoutConstraint?
    private var profileImageHeightConstraint: NSLayoutConstraint?
    
    private var labelHeight: CGFloat {
        viewSpec.toolbarViewSpec.titleHeight.toCGFloat()
    }
    
    private var socialLinksHeight: CGFloat {
        viewSpec.toolbarViewSpec.socialLinksHeight.toCGFloat()
    }
    
    private var paddingDefault: CGFloat {
        viewSpec.toolbarViewSpec.paddingDefault.toCGFloat()
    }
    
    private var maxScrollOffset: CGFloat {
        CGFloat(viewSpec.toolbarViewSpec.maxToolbarHeight) - CGFloat(viewSpec.toolbarViewSpec.minToolbarHeight)
    }
    
    private var headerMinHeight: CGFloat {
        viewSpec.toolbarViewSpec.minToolbarHeight.toCGFloat()
    }
    
    private var profileImageSizeMax: CGFloat {
        viewSpec.toolbarViewSpec.profileAnimatedViewSpec.heightMax!.toCGFloat()
    }
    
    let socialView: UIStackView = {
        let stackView = UIStackView()
        stackView.axis = .horizontal
        stackView.alignment = .center
        stackView.distribution = .fillEqually
        stackView.spacing = 10
        return stackView
    }()
    
    private var headerHeight: CGFloat = 0 {
        didSet {
            updateLayoutWithHeaderHeight()
        }
    }
    
    init(render: RenderIos, theme: Theme, viewState: ArtistToolbarViewState, viewSpec: ArtistViewSpec, tabs: TabsViewState.Indicator, scrollOffsetController: ScrollOffsetController, selectedTab: TabSelectionController) {
        self.render = render
        self.theme = theme
        self.viewState = viewState
        self.tabs = tabs
        self.scrollOffsetController = scrollOffsetController
        self.selectedTab = selectedTab
        self.viewSpec = viewSpec
        super.init(frame: .zero)
        calculateHeaderHeight()
    }
    
    deinit {
        cancellable?.cancel()
    }
    
    override func draw(_ rect: CGRect) {
        self.updateFrame()
        profileImage?.layer.cornerRadius = (profileImage?.frame.size.height ?? 0.0) / 2.0
    }
    
    func updateFrame() {
        if cancellable != nil {
            return
        }
        cancellable = scrollOffsetController?.$offset.sink(receiveValue: { value in
            self.updateContentBasedOn(offset: value)
        })
        
        scrollOffsetController?.scrollEndCompletion = { value in
            UIView.animate(withDuration: 0.2) {
                self.updateContentBasedOn(offset: value)
            }
        }
    }
    
    func updateTheme() {
        profileImage?.theme = self.theme
        self.name?.updateWith(text: self.viewState.name, theme: self.theme)
        if self.socialView.arrangedSubviews.count == self.viewState.socialLinks?.socialLinks.count {
            self.socialView.arrangedSubviews.enumerated().forEach { details in
                updateMenuItemWith(theme: self.theme, menuItem: self.viewState.socialLinks!.socialLinks[details.offset], menuItemView: details.element)
            }
        }
        self.backgroundColor = viewState.containerColorOverride.toColor(themeColors: self.theme.themeColors)?.uiColor()
    }
    
    private func updateContentBasedOn(offset: CGFloat) {
        self.heightConstraint?.constant = max(self.headerHeight + offset, self.headerMinHeight)
        let progress = abs(offset) / self.maxScrollOffset
        self.labelTopConstraint?.constant = progress * -(self.labelHeight + self.socialLinksHeight)
        if let profileImage = self.profileImage {
            let profileMaxSize = self.viewSpec.toolbarViewSpec.profileAnimatedViewSpec.heightMax!.toCGFloat()
            let profileMinSize = self.viewSpec.toolbarViewSpec.profileAnimatedViewSpec.heightMin!.toCGFloat()
            let profileImageSize = Float(profileMaxSize - ((profileMaxSize - profileMinSize) * progress))
            self.profileImageTopConstraint?.constant = progress * ((profileMaxSize - profileMinSize) / 2)
            self.profileImageWidthConstraint?.constant = CGFloat(profileImageSize)
            self.profileImageHeightConstraint?.constant = CGFloat(profileImageSize)
            profileImage.updateWith(viewState: self.viewState.profileImage, imageSize: profileImageSize, theme: self.theme)
        }
        self.superview?.layoutIfNeeded()
        let alpha = 1 - (progress * 3)
        self.socialView.alpha = (offset == 0.0 ? 1 : alpha)
        self.name?.alpha = (offset == 0.0 ? 1 : alpha)
        self.profileImage?.layer.cornerRadius = (self.profileImage?.frame.size.height ?? 0.0) / 2.0
    }
    
    func setup() {
        if let socialLinks = viewState.socialLinks?.socialLinks {
            socialView.arrangedSubviews.forEach { vw in
                vw.removeFromSuperview()
            }
            socialLinks.forEach { menu in
                let menuItemView = createMenuItemContent(theme: theme, menuItem: menu)
                socialView.addArrangedSubview(menuItemView)
            }
        }
        
        self.tabBarView = UITopTabsView(tabsState: tabs, selectedTab: selectedTab!, render: self.render, theme: self.theme)
        self.addSubview(self.tabBarView!)
        
        self.addSubview(socialView)
          
        let text = viewState.name
        self.name = StyledLabel(text: text, theme: self.theme)
        guard let lblName = self.name else { return }
        self.addSubview(lblName)
        
        self.profileImage = UIProfileImageView(render: render, theme: theme, viewState: viewState.profileImage, imageSize: Float(profileImageSizeMax))
        if let profile = self.profileImage {
            self.addSubview(profile)
            
            profile.translatesAutoresizingMaskIntoConstraints = false
            
            profileImageWidthConstraint = profile.widthAnchor.constraint(equalToConstant: profileImageSizeMax)
            profileImageHeightConstraint = profile.heightAnchor.constraint(equalToConstant: profileImageSizeMax)
            profileImageTopConstraint = profile.topAnchor.constraint(equalTo: self.topAnchor, constant: 0.0)
            NSLayoutConstraint.activate([
                profileImageTopConstraint!,
                profileImageWidthConstraint!,
                profileImageHeightConstraint!,
                profile.centerXAnchor.constraint(equalTo: self.centerXAnchor, constant: 0)
            ])
            
            lblName.translatesAutoresizingMaskIntoConstraints = false
            labelTopConstraint = lblName.topAnchor.constraint(equalTo: profile.bottomAnchor, constant: 0)
            NSLayoutConstraint.activate([
                labelTopConstraint!,
                lblName.centerXAnchor.constraint(equalTo: self.centerXAnchor, constant: 0)
            ])
            
            socialView.translatesAutoresizingMaskIntoConstraints = false
            NSLayoutConstraint.activate([
                socialView.topAnchor.constraint(equalTo: lblName.bottomAnchor, constant: 0),
                socialView.centerXAnchor.constraint(equalTo: self.centerXAnchor, constant: 0),
                socialView.heightAnchor.constraint(equalToConstant: self.viewState.socialLinks?.height.toCGFloat() ?? 30)
            ])
            
            self.tabBarView!.translatesAutoresizingMaskIntoConstraints = false
            self.tabBarTopConstraint = self.tabBarView!.topAnchor.constraint(equalTo: socialView.bottomAnchor, constant: 0)
            NSLayoutConstraint.activate([
                self.tabBarView!.bottomAnchor.constraint(equalTo: self.bottomAnchor, constant: 0),
                self.tabBarTopConstraint!,
                self.tabBarView!.leadingAnchor.constraint(equalTo: self.leadingAnchor, constant: 0),
                self.tabBarView!.trailingAnchor.constraint(equalTo: self.trailingAnchor, constant: 0),
                self.tabBarView!.heightAnchor.constraint(equalToConstant: CGFloat(tabs.viewSpec.tabHeight))
            ])
        }
        
        self.backgroundColor = viewState.containerColorOverride.toColor(themeColors: self.theme.themeColors)?.uiColor()
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    func update(render: RenderIos, theme: Theme, viewState: ArtistToolbarViewState, viewSpec: ArtistViewSpec, tabs: TabsViewState.Indicator) {
        self.viewState = viewState
        self.theme = theme
        self.render = render
        self.viewSpec = viewSpec
        self.tabs = tabs
        self.tabBarView?.update(tabsState: self.tabs, render: self.render, theme: self.theme)
        self.backgroundColor = viewState.containerColorOverride.toColor(themeColors: self.theme.themeColors)?.uiColor()
        self.profileImage?.updateProfileViewState(viewState: viewState.profileImage)
    }
    
    func updateIndicator() {
        self.tabBarView?.updateIndicatorPosition(animated: true)
    }
    
    private func calculateHeaderHeight() {
        headerHeight = CGFloat(viewSpec.toolbarViewSpec.maxToolbarHeight) + render.windowFrame.statusBarHeight.toCGFloat()
        scrollOffsetController?.update(maxOffset: maxScrollOffset)
    }
    
    private func updateLayoutWithHeaderHeight() {
        if heightConstraint == nil {
            heightConstraint = heightAnchor.constraint(equalToConstant: headerHeight)
            heightConstraint?.isActive = true
        } else {
            heightConstraint?.constant = headerHeight
        }
    }
}
