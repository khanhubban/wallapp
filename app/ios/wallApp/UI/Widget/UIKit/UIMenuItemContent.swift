//
//  UIMenuItemContent.swift
//  WallApp
//

import WallApp
import Lottie


// MARK: - UIMenuItemImage
class UIMenuItemImage: UIView {
    var theme: Theme
    var menuItemImage: MenuItem.MenuItemImage
    
    private var image: CustomImageView!
    
    init(theme: Theme, menuItemImage: MenuItem.MenuItemImage) {
        self.theme = theme
        self.menuItemImage = menuItemImage
        super.init(frame: .zero)
        setupView()
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    private func setupView() {
        image = CustomImageView(
            theme: theme,
            imageViewState: menuItemImage.imageViewState
        )
        addSubview(image)
        updateWith()
        if let onClick = menuItemImage.onClick {
            isUserInteractionEnabled = true
            addTapGesture { onClick.invoke() }
        }
    }
    
    func updateWith(theme: Theme? = nil, menuItemImage: MenuItem.MenuItemImage? = nil) {
        if let theme = theme {
            self.theme = theme
            self.image.theme = theme
        }
        if let menuItemImage = menuItemImage {
            self.menuItemImage = menuItemImage
            image.imageViewState = menuItemImage.imageViewState
            if let onClick = menuItemImage.onClick {
                isUserInteractionEnabled = true
                addTapGesture { onClick.invoke() }
            } else {
                isUserInteractionEnabled = false
            }
        }
        image.setup()
    }
}

class UIMenuItemButton: UIView {
    var theme: Theme
    var menuItemButton: MenuItem.MenuItemButton
    var width: CGFloat? = nil
    var height: CGFloat? = nil
    var buttonView: UIButtonView?
    
    init(theme: Theme, menuItemButton: MenuItem.MenuItemButton, width: CGFloat? = nil, height: CGFloat? = nil) {
        self.theme = theme
        self.menuItemButton = menuItemButton
        self.width = width
        self.height = height
        super.init(frame: .zero)
        setupView()
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    private func setupView() {
        let button = menuItemButton.button
        let width = menuItemButton.width?.toCGFloat()
        let height = menuItemButton.height?.toCGFloat()
        
        let buttonView = UIButtonView(theme: theme, viewState: button, contentPadding: [.all: 0], width: width, height: height)
        self.buttonView = buttonView
        self.addSubview(buttonView)
        buttonView.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint.activate([
            buttonView.topAnchor.constraint(equalTo: self.topAnchor),
            buttonView.bottomAnchor.constraint(equalTo: self.bottomAnchor),
            buttonView.leadingAnchor.constraint(equalTo: self.leadingAnchor),
            buttonView.trailingAnchor.constraint(equalTo: self.trailingAnchor)
        ])
    }
    
    func updateWith(theme: Theme? = nil, menuItemButton: MenuItem.MenuItemButton? = nil) {
        if let theme {
            self.theme = theme
        }
        if let menuItemButton {
            self.menuItemButton = menuItemButton
            self.buttonView?.updateWith(theme: self.theme, viewState: menuItemButton.button)
        }
    }
}

class UIButtonView: UIView {
    var theme: Theme
    var viewState: ButtonViewState
    var animationProgress: Float? = 1.0
    var contentPadding: PaddingAll = [.all: 0]
    var width: CGFloat? = nil
    var height: CGFloat? = nil
    
    var menuView: UIView = UIView()
    
    init(theme: Theme, viewState: ButtonViewState, animationProgress: Float? = nil, contentPadding: PaddingAll, width: CGFloat? = nil, height: CGFloat? = nil) {
        self.theme = theme
        self.viewState = viewState
        self.animationProgress = animationProgress
        self.contentPadding = contentPadding
        self.width = width
        self.height = height
        super.init(frame: .zero)
        setupView()
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func layoutSubviews() {
        if let _ = viewState.animatedViewSpec {
            guard let animatedViewSpec = viewState.animatedViewSpec, let shape = animatedViewSpec.shapeSpecMax ?? animatedViewSpec.shapeSpecMin ?? viewState.shapeSpec else { return }
            self.applyShape(shapeSpec: shape)
        } else if let shape = viewState.shapeSpec {
            self.applyShape(shapeSpec: shape)
        }
    }
    
    private func setupView() {
        let menuItem = viewState.menuItem
        let animatedViewSpec = viewState.animatedViewSpec
        let shape = animatedViewSpec?.shapeSpecMax ?? animatedViewSpec?.shapeSpecMin ?? viewState.shapeSpec//
        
        self.menuView = createMenuItemContent(theme: theme, menuItem: menuItem)
        self.addSubview(menuView)
        menuView.translatesAutoresizingMaskIntoConstraints = false
        if menuItem.itemWidth() != nil {
            NSLayoutConstraint.activate([
                menuView.centerXAnchor.constraint(equalTo: self.centerXAnchor),
                menuView.centerYAnchor.constraint(equalTo: self.centerYAnchor)
            ])
        } else {
            NSLayoutConstraint.activate([
                menuView.leadingAnchor.constraint(equalTo: self.leadingAnchor),
                menuView.trailingAnchor.constraint(equalTo: self.trailingAnchor),
                menuView.topAnchor.constraint(equalTo: self.topAnchor),
                menuView.bottomAnchor.constraint(equalTo: self.bottomAnchor)
            ])
        }
        
        
        if width != nil {
            NSLayoutConstraint.activate([
                self.widthAnchor.constraint(equalToConstant: width!)
            ])
        }
        
        if height != nil {
            NSLayoutConstraint.activate([
                self.heightAnchor.constraint(equalToConstant: height!)
            ])
        }
        
        if let shape = shape {
            self.applyShape(shapeSpec: shape)
        }
        
        self.addTapGesture {
            self.viewState.eventHandler?.invoke()
        }
        
        self.applyModifier(shape: shape, theme: self.theme, containerColor: viewState.containerColorToken?.toColor(themeColors: theme.themeColors))
    }
    
    func updateWith(theme: Theme? = nil, viewState: ButtonViewState? = nil) {
        if let theme {
            self.theme = theme
        }
        
        if let viewState {
            self.viewState = viewState
            
            let menuItem = viewState.menuItem
            let onClick = viewState.eventHandler
            let animatedViewSpec = viewState.animatedViewSpec
            let shape = animatedViewSpec?.shapeSpecMax ?? animatedViewSpec?.shapeSpecMin ?? viewState.shapeSpec
            
            updateMenuItemWith(theme: self.theme, menuItem: menuItem, menuItemView: menuView)
            
            self.applyModifier(shape: shape, theme: self.theme, containerColor: viewState.containerColorToken?.toColor(themeColors: self.theme.themeColors))

            self.addTapGesture {
                onClick?.invoke()
            }
            
        }
    }
}

// MARK: - UIMenuItemIcon
class UIMenuItemIcon: UIView {
    var theme: Theme
    var menuItemIcon: MenuItem.MenuItemIcon
    var ignoreInherentSize: Bool
    
    private var icon: CustomImageView!
    
    init(theme: Theme, menuItemIcon: MenuItem.MenuItemIcon, ignoreInherentSize: Bool = false) {
        self.theme = theme
        self.menuItemIcon = menuItemIcon
        self.ignoreInherentSize = ignoreInherentSize
        super.init(frame: .zero)
        setupView()
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    private func setupView() {
        let width = menuItemIcon.width?.toCGFloat() ?? 10.0
        let height = menuItemIcon.height?.toCGFloat() ?? 10.0
        self.icon = CustomImageView(
            theme: theme,
            image: menuItemIcon.icon,
            width: !ignoreInherentSize ? menuItemIcon.width?.dp : nil,
            height: !ignoreInherentSize ? menuItemIcon.height?.dp : nil,
            tintColor: menuItemIcon.tintColor?.toColor(themeColors: theme.themeColors)
        )
        self.addSubview(icon)
        self.icon.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint.activate([
            icon.centerXAnchor.constraint(equalTo: centerXAnchor),
            icon.centerYAnchor.constraint(equalTo: centerYAnchor)
        ])
        if ignoreInherentSize {
            icon.bottomAnchor.constraint(equalTo: bottomAnchor).isActive = true
            icon.trailingAnchor.constraint(equalTo: trailingAnchor).isActive = true
        } else {
            self.widthAnchor.constraint(equalToConstant: width).isActive = true
            self.heightAnchor.constraint(equalToConstant: height).isActive = true
        }
        updateWith()
        if let onClick = menuItemIcon.onClick {
            isUserInteractionEnabled = true
            addTapGesture { onClick.invoke() }
        } else {
            self.isUserInteractionEnabled = false
        }
    }
    
    func updateWith(theme: Theme? = nil, menuItemIcon: MenuItem.MenuItemIcon? = nil) {
        if let theme {
            self.theme = theme
            self.icon.theme = theme
        }
        if let menuItemIcon {
            self.menuItemIcon = menuItemIcon
            icon.imageUI = menuItemIcon.icon
            icon.width = menuItemIcon.width?.dp
            icon.height = menuItemIcon.height?.dp
            icon.tintColorData = menuItemIcon.tintColor?.toColor(themeColors: self.theme.themeColors)
            if let onClick = menuItemIcon.onClick {
                isUserInteractionEnabled = true
                addTapGesture { onClick.invoke() }
            } else {
                self.isUserInteractionEnabled = false
            }
        }
        icon.setup()
    }
}

// MARK: - UIMenuItemLabel
class UIMenuItemLabel: UIView {
    var theme: Theme
    var menuItemLabel: MenuItem.MenuItemLabel
    
    private var label: StyledLabel!
    
    init(theme: Theme, menuItemLabel: MenuItem.MenuItemLabel) {
        self.theme = theme
        self.menuItemLabel = menuItemLabel
        super.init(frame: .zero)
        setupView()
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    private func setupView() {
        label = StyledLabel(
            text: menuItemLabel.text,
            theme: theme
        )
        updateSizeIfNeeded()
        
        addSubview(label)
        if let onClick = menuItemLabel.onClick {
            isUserInteractionEnabled = true
            addTapGesture { onClick.invoke() }
        }
    }
    
    func updateWith(theme: Theme? = nil, menuItemLabel: MenuItem.MenuItemLabel? = nil) {
        if let theme = theme {
            self.theme = theme
        }
        if let menuItemLabel = menuItemLabel {
            self.menuItemLabel = menuItemLabel
            if let onClick = menuItemLabel.onClick {
                isUserInteractionEnabled = true
                addTapGesture { onClick.invoke() }
            } else {
                isUserInteractionEnabled = false
            }
            updateSizeIfNeeded()
            label.updateWith(text: self.menuItemLabel.text, theme: self.theme)
        }
        
    }
    
    private func updateSizeIfNeeded() {
        if !self.subviews.contains(label) {
            self.addSubview(label)
        }
        
        label.translatesAutoresizingMaskIntoConstraints = false
                
        if let width = menuItemLabel.width as? Width.WidthDp, let height = menuItemLabel.height as? Height.HeightDp {
            self.widthAnchor.constraint(equalToConstant: width.dp.toCGFloat()).isActive = true
            self.heightAnchor.constraint(equalToConstant: height.dp.toCGFloat()).isActive = true
            label.centerXAnchor.constraint(equalTo: self.centerXAnchor, constant: 0).isActive = true
            label.centerYAnchor.constraint(equalTo: self.centerYAnchor, constant: 0).isActive = true
        } else {
            label.topAnchor.constraint(equalTo: self.topAnchor, constant: 0).isActive = true
            label.leadingAnchor.constraint(equalTo: self.leadingAnchor, constant: 0).isActive = true
            label.trailingAnchor.constraint(equalTo: self.trailingAnchor, constant: 0).isActive = true
            label.bottomAnchor.constraint(equalTo: self.bottomAnchor, constant: 0).isActive = true
        }
    }
}

// MARK: - UIMenuItemContainer
class UIMenuItemContainer: UIView {
    var theme: Theme
    var menuItemContainer: MenuItem.MenuItemContainer
    
    private var stackView: UIStackView!
    
    init(theme: Theme, menuItemContainer: MenuItem.MenuItemContainer) {
        self.theme = theme
        self.menuItemContainer = menuItemContainer
        super.init(frame: .zero)
        setupView()
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    private func setupView() {
        let contentView = createMenuItemContent(theme: theme, menuItem: menuItemContainer.menuItem, ignoreInherentSize: true) // Assuming a 'default' theme
        addSubview(contentView)
        contentView.translatesAutoresizingMaskIntoConstraints = false
        
        // Apply padding
        if let paddingInsets = menuItemContainer.padding {
            NSLayoutConstraint.activate([
                contentView.topAnchor.constraint(equalTo: topAnchor, constant: paddingInsets.top.toCGFloat()),
                contentView.bottomAnchor.constraint(equalTo: bottomAnchor, constant: -paddingInsets.bottom.toCGFloat()),
                contentView.leadingAnchor.constraint(equalTo: leadingAnchor, constant: paddingInsets.start.toCGFloat()),
                contentView.trailingAnchor.constraint(equalTo: trailingAnchor, constant: -paddingInsets.end.toCGFloat())
            ])
        }
        
        // Apply width and height
        if let width = menuItemContainer.width {
            widthAnchor.constraint(equalToConstant: width.toCGFloat()).isActive = true
        }
        if let height = menuItemContainer.height {
            heightAnchor.constraint(equalToConstant: height.toCGFloat()).isActive = true
        }
        
        if let onClick = menuItemContainer.onClick {
            isUserInteractionEnabled = true
            addTapGesture { onClick.invoke() }
        }
    }
    
    func updateWith(theme: Theme? = nil, menuItemContainer: MenuItem.MenuItemContainer? = nil) {
        if let theme = theme {
            self.theme = theme
        }
        if let menuItemContainer = menuItemContainer {
            self.menuItemContainer = menuItemContainer
            if let onClick = menuItemContainer.onClick {
                isUserInteractionEnabled = true
                addTapGesture { onClick.invoke() }
            } else {
                isUserInteractionEnabled = false
            }
        }
        updateMenuItemWith(theme: self.theme, menuItem: self.menuItemContainer.menuItem, menuItemView: subviews.first!)
    }
}

class UIMenuItemGroupHorizontal: UIView {
    let theme: Theme
    var menuItemGroupHorizontal: MenuItem.MenuItemGroupHorizontal
    var width: CGFloat?
    var height: CGFloat?
    
    private let horizontalStackView: UIStackView = {
        let stackView = UIStackView()
        stackView.axis = .horizontal
        stackView.alignment = .center
        return stackView
    }()
    
    init(theme: Theme, menuItemGroupHorizontal: MenuItem.MenuItemGroupHorizontal, width: CGFloat? = nil, height: CGFloat? = nil) {
        self.theme = theme
        self.menuItemGroupHorizontal = menuItemGroupHorizontal
        self.width = width
        self.height = height
        super.init(frame: .zero)
        self.setUpView()
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    func setUpView() {
        let menuItems = menuItemGroupHorizontal.menuItems
        let height = height ?? menuItemGroupHorizontal.height.toCGFloat()
 
        menuItems.forEach { menu in
            let vwMenu = createMenuItemContent(theme: self.theme, menuItem: menu)
            horizontalStackView.addArrangedSubview(vwMenu)
        }
 
        self.addSubview(horizontalStackView)
        horizontalStackView.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint.activate([
            horizontalStackView.topAnchor.constraint(equalTo: self.topAnchor),
            horizontalStackView.centerXAnchor.constraint(equalTo: self.centerXAnchor),
            horizontalStackView.bottomAnchor.constraint(equalTo: self.bottomAnchor)
        ])
        
        if height != 0.0 {
            self.heightAnchor.constraint(equalToConstant: height).isActive = true
        }
        
        horizontalStackView.setContentHuggingPriority(.defaultHigh, for: .horizontal)
        if width != nil {
            self.widthAnchor.constraint(equalToConstant: width!).isActive = true
        }
    }
    
    func updateView(menuItem:  MenuItem.MenuItemGroupHorizontal) {
        let menuItems = menuItem.menuItems
        self.menuItemGroupHorizontal = menuItem
        if horizontalStackView.arrangedSubviews.count != menuItems.count {
            horizontalStackView.arrangedSubviews.forEach{ $0.removeFromSuperview() }
            menuItems.forEach { menu in
                let vwMenu = createMenuItemContent(theme: self.theme, menuItem: menu)
                horizontalStackView.addArrangedSubview(vwMenu)
            }
        } else {
            horizontalStackView.arrangedSubviews.enumerated().forEach { menu in
                updateMenuItemWith(theme: self.theme, menuItem: menuItems[menu.offset], menuItemView: menu.element)
            }
        }
    }
}

class UIMenuItemGroupVertical: UIView {
    let theme: Theme
    var menuItemGroupVertical: MenuItem.MenuItemGroupVertical
    var width: CGFloat?
    var height: CGFloat?
    
    private let verticalStackView: UIStackView = {
        let stackView = UIStackView()
        stackView.axis = .vertical
        stackView.alignment = .center
        stackView.distribution = .equalSpacing
        return stackView
    }()
    
    init(theme: Theme, menuItemGroupVertical: MenuItem.MenuItemGroupVertical, width: CGFloat? = nil, height: CGFloat? = nil) {
        self.theme = theme
        self.menuItemGroupVertical = menuItemGroupVertical
        self.width = width
        self.height = height
        super.init(frame: .zero)
        self.setUpView()
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    func setUpView() {
        let menuItems = menuItemGroupVertical.menuItems
        let height = height ?? menuItemGroupVertical.height?.toCGFloat()
 
        menuItems.forEach { menu in
            let vwMenu = createMenuItemContent(theme: self.theme, menuItem: menu)
            verticalStackView.addArrangedSubview(vwMenu)
        }
 
        self.addSubview(verticalStackView)
        verticalStackView.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint.activate([
            verticalStackView.topAnchor.constraint(equalTo: self.topAnchor),
            verticalStackView.centerXAnchor.constraint(equalTo: self.centerXAnchor),
            verticalStackView.bottomAnchor.constraint(equalTo: self.bottomAnchor),
            verticalStackView.trailingAnchor.constraint(equalTo: self.trailingAnchor),
            verticalStackView.leadingAnchor.constraint(greaterThanOrEqualTo: self.leadingAnchor),
        ])
        
        if height != nil && height != 0.0 {
            self.heightAnchor.constraint(equalToConstant: height!).isActive = true
        }
        
        if width != nil {
            self.widthAnchor.constraint(equalToConstant: width!).isActive = true
        }
        
    }
    
    func updateView(menuItem: MenuItem.MenuItemGroupVertical) {
        let menuItems = menuItem.menuItems
        self.menuItemGroupVertical = menuItem
        verticalStackView.arrangedSubviews.enumerated().forEach { menu in
            updateMenuItemWith(theme: self.theme, menuItem: menuItems[menu.offset], menuItemView: menu.element)
        }
    }
}

// MARK: - UIMenuItemProgressButton
class UIMenuItemProgressButton: UIButton {
    var theme: Theme!
    var menuItem: MenuItem.MenuItemProgressButton!
    
    private var overlayLabel: StyledLabel?
    private let progressView = UIView()
    
    init(theme: Theme, menuItem: MenuItem.MenuItemProgressButton) {
        self.theme = theme
        self.menuItem = menuItem
        super.init(frame: .zero)
        setupViews()
    }

    private var progress: Float = 0.0 {
        didSet {
            updateProgress()
        }
    }

    private var onClick: (() -> Void)?

    var containerBackgroundColor: UIColor {
        theme.themeColors.tertiary?.color.uiColor ?? .clear
    }

    var progressBackgroundColor: UIColor {
        theme.themeColors.secondary.uiColor
    }
    
    var labelColor: Color {
        theme.themeColors.onSurface?.color.toColor() ?? .white
    }

    required init?(coder: NSCoder) {
        super.init(coder: coder)
        setupViews()
    }

    private func setupViews() {
        overlayLabel = StyledLabel(text: menuItem.overlayText, theme: theme, colorOverride: labelColor)
        addSubview(progressView)
        addSubview(overlayLabel!)

        progressView.translatesAutoresizingMaskIntoConstraints = false
        overlayLabel?.translatesAutoresizingMaskIntoConstraints = false

        progressView.topAnchor.constraint(equalTo: topAnchor).isActive = true
        progressView.bottomAnchor.constraint(equalTo: bottomAnchor).isActive = true
        progressView.leadingAnchor.constraint(equalTo: leadingAnchor).isActive = true
        progressView.backgroundColor = progressBackgroundColor

        overlayLabel?.centerYAnchor.constraint(equalTo: centerYAnchor).isActive = true
        overlayLabel?.centerXAnchor.constraint(equalTo: centerXAnchor).isActive = true
        overlayLabel?.backgroundColor = .clear
        
        self.onClick = menuItem.onClick?.invoke
        
        if let height = menuItem?.height?.toCGFloat(), height != 0 {
            self.heightAnchor.constraint(equalToConstant: height).isActive = true
        }
        if let width = menuItem?.width?.toCGFloat(), width != 0 {
            self.widthAnchor.constraint(equalToConstant: width).isActive = true
        }

        addTarget(self, action: #selector(buttonTapped), for: .touchUpInside)
    }
    
    func updateWith(theme: Theme, menuItem: MenuItem.MenuItemProgressButton) {
        self.theme = theme
        self.menuItem = menuItem
        overlayLabel?.updateWith(text: menuItem.overlayText, theme: theme, colorOverride: labelColor)
        progress = menuItem.progress
        if let height = menuItem.height {
            self.heightAnchor.constraint(equalToConstant: height.toCGFloat()).isActive = true
        }
        self.onClick = menuItem.onClick?.invoke
    }

    private func updateProgress() {
        let progressWidth = bounds.width * CGFloat(progress)
        progressView.frame.size.width = progressWidth
    }

    @objc private func buttonTapped() {
        onClick?()
    }

    override func layoutSubviews() {
        super.layoutSubviews()
        updateProgress()
        if let shape = menuItem?.shapeSpec {
            applyShape(shapeSpec: shape)
        }
    }
}

class UIMenuItemViewStateWrapper: UIView {
    var theme: Theme
    var menuItem: MenuItemMenuItemViewStateWrapper<AnyObject>
    
    private var view: UIView!
    
    init(theme: Theme, menuItem: MenuItemMenuItemViewStateWrapper<AnyObject>) {
        self.theme = theme
        self.menuItem = menuItem
        super.init(frame: .zero)
        setupView()
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    private func setupView() {
        guard let viewState = menuItem.viewState as? FavoriteViewState else { return }
        let favoriteButton = UIFavoriteAnimatedButton(theme: theme, viewState: viewState)
        view = favoriteButton
        let width = menuItem.width?.toCGFloat()
        let height = menuItem.height?.toCGFloat()
        addSubview(view)
        view.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint.activate([
            view.topAnchor.constraint(equalTo: topAnchor),
            view.bottomAnchor.constraint(equalTo: bottomAnchor),
            view.leadingAnchor.constraint(equalTo: leadingAnchor),
            view.trailingAnchor.constraint(equalTo: trailingAnchor)
        ])
        if let width {
            self.widthAnchor.constraint(equalToConstant: width).isActive = true
        }
        if let height {
            self.heightAnchor.constraint(equalToConstant: height).isActive = true
        }
        favoriteButton.setup()
    }
    
    func updateWith(theme: Theme? = nil, menuItem: MenuItemMenuItemViewStateWrapper<AnyObject>? = nil) {
        if let theme {
            self.theme = theme
            if let view = view as? UIFavoriteAnimatedButton {
                view.theme = theme
            }
        }
        if let menuItem {
            self.menuItem = menuItem
            guard let viewState = menuItem.viewState as? FavoriteViewState else { return }
            if let view = view as? UIFavoriteAnimatedButton {
                view.viewState = viewState
                view.setup()
            }
        }
    }
}

// MARK: - Utils
func createMenuItemContent(
    theme: Theme,
    menuItem: MenuItem,
    ignoreInherentSize: Bool = false
) -> UIView {
    if let menuItem = menuItem as? MenuItem.MenuItemImage {
        return UIMenuItemImage(theme: theme, menuItemImage: menuItem)
    } else if let menuItem = menuItem as? MenuItem.MenuItemLabel {
        return UIMenuItemLabel(theme: theme, menuItemLabel: menuItem)
    } else if let menuItem = menuItem as? MenuItem.MenuItemIcon {
        return UIMenuItemIcon(theme: theme, menuItemIcon: menuItem, ignoreInherentSize: ignoreInherentSize)
    } else if let menuItem = menuItem as? MenuItem.MenuItemContainer {
        return UIMenuItemContainer(theme: theme, menuItemContainer: menuItem)
    } else if let menuItem = menuItem as? MenuItem.MenuItemButton {
        return UIMenuItemButton(theme: theme, menuItemButton: menuItem)
    } else if let menuItem = menuItem as? MenuItem.MenuItemSpacer {
        return UIMenuItemSpacer(menuItemSpacer: menuItem)
    } else if let menuItem = menuItem as? MenuItem.MenuItemGroupHorizontal {
        return UIMenuItemGroupHorizontal(theme: theme, menuItemGroupHorizontal: menuItem)
    } else if let menuItem = menuItem as? MenuItem.MenuItemGroupVertical {
        return UIMenuItemGroupVertical(theme: theme, menuItemGroupVertical: menuItem)
    } else if let menuItem = menuItem as? MenuItem.MenuItemProgressButton {
        return UIMenuItemProgressButton(theme: theme, menuItem: menuItem)
    } else if let menuItem = menuItem as? MenuItemMenuItemViewStateWrapper<AnyObject> {
        return UIMenuItemViewStateWrapper(theme: theme, menuItem: menuItem)
    } else {
        return UIView()
    }
}

func isViewEquivalent(menuItem: MenuItem, menuItemView: UIView) -> Bool {
    switch (menuItem, menuItemView) {
    case (is MenuItem.MenuItemLabel, is UIMenuItemLabel):
        return true
    case (is MenuItem.MenuItemImage, is UIMenuItemImage):
        return true
    case (is MenuItem.MenuItemIcon, is UIMenuItemIcon):
        return true
    case (is MenuItem.MenuItemContainer, is UIMenuItemContainer):
        return true
    case (is MenuItem.MenuItemButton, is UIMenuItemButton):
        return menuItem.isEqual((menuItemView as! UIMenuItemButton).menuItemButton)
    case (is MenuItem.MenuItemSpacer, is UIMenuItemSpacer):
        return true
    case (is MenuItem.MenuItemGroupHorizontal, is UIMenuItemGroupHorizontal):
        return true
    case (is MenuItem.MenuItemGroupVertical, is UIMenuItemGroupVertical):
        return true
    case (is MenuItem.MenuItemProgressButton, is UIMenuItemProgressButton):
        return true
    default:
        return false
    }
}

func updateMenuItemWith(theme: Theme, menuItem: MenuItem, menuItemView: UIView) {
    switch onEnum(of: menuItem) {
    case .menuItemImage(let menuItemImage):
        (menuItemView as? UIMenuItemImage)?.updateWith(theme: theme, menuItemImage: menuItemImage)
    case .menuItemIcon(let menuItemIcon):
        (menuItemView as? UIMenuItemIcon)?.updateWith(theme: theme, menuItemIcon: menuItemIcon)
    case .menuItemLabel(let menuItemLabel):
        (menuItemView as? UIMenuItemLabel)?.updateWith(theme: theme, menuItemLabel: menuItemLabel)
    case .menuItemContainer(let menuItemContainer):
        (menuItemView as? UIMenuItemContainer)?.updateWith(theme: theme, menuItemContainer: menuItemContainer)
    case .menuItemButton(let menuItemButton):
        (menuItemView as? UIMenuItemButton)?.updateWith(theme: theme, menuItemButton: menuItemButton)
        break
    case .menuItemDivider(_):
        break
    case .menuItemGroup(let menuItemGroup):
        if let horizontal = menuItemGroup as? MenuItem.MenuItemGroupHorizontal {
            (menuItemView as? UIMenuItemGroupHorizontal)?.updateView(menuItem: horizontal)
        }
        if let vertical = menuItemGroup as? MenuItem.MenuItemGroupVertical {
            (menuItemView as? UIMenuItemGroupVertical)?.updateView(menuItem: vertical)
        }
        break
    case .menuItemPopup(_):
        break
    case .menuItemProgressButton(let menuItemProgress):
        (menuItemView as? UIMenuItemProgressButton)?.updateWith(theme: theme, menuItem: menuItemProgress)
        break
    case .menuItemSpacer(_):
        break
    case .menuItemViewStateWrapper(let menuItemViewStateWrapper):
        (menuItemView as? UIMenuItemViewStateWrapper)?.updateWith(theme: theme, menuItem: menuItemViewStateWrapper)
    }
}

class UIMenuItemSpacer: UIView {
    
    let menuItemSpacer: MenuItem.MenuItemSpacer
    
    init(menuItemSpacer: MenuItem.MenuItemSpacer) {
        self.menuItemSpacer = menuItemSpacer
        super.init(frame: .zero)
        setUpView()
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    func setUpView() {
        let vw = UIView()
        vw.backgroundColor = .clear
        self.addSubview(vw)
        vw.translatesAutoresizingMaskIntoConstraints = false
        
        NSLayoutConstraint.activate([
            vw.topAnchor.constraint(equalTo: self.topAnchor),
            vw.leadingAnchor.constraint(equalTo: self.leadingAnchor),
            vw.trailingAnchor.constraint(equalTo: self.trailingAnchor),
            vw.bottomAnchor.constraint(equalTo: self.bottomAnchor),
        ])
        
        
        if let height = menuItemSpacer.height {
            NSLayoutConstraint.activate([
                self.heightAnchor.constraint(equalToConstant: height.toCGFloat()),
            ])
        }
        
        if let width = menuItemSpacer.width {
            NSLayoutConstraint.activate([
                self.widthAnchor.constraint(equalToConstant: width.toCGFloat())
            ])
        }
    
    }
}

extension UIButtonView {
    
    func applyModifier(shape: ShapeSpec?, theme: Theme,containerColor: Color? = nil, contentColor: Color? = nil) {
         if let bgColor = containerColor {
            self.backgroundColor = bgColor.uiColor()
        }
        
        if let forGroundColor = contentColor {
            self.tintColor = forGroundColor.uiColor()
        }
        
        if let shape = shape {
            self.applyShape(shapeSpec: shape)
        }
    }
    
}
