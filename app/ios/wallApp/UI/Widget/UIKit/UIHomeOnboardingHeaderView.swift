//
//  UIHomeOnboardingHeaderView.swift
//  WallApp
//

import WallApp
import UIKit

class UIHomeOnboardingHeaderView: UIView {
    private var render: RenderIos!
    private var theme: Theme!
    private var viewState: HomeOnboardingHeaderViewState!
    private var titleLabel: StyledLabel!
    private var subheaderLabel: StyledLabel!
    
    private var themeManager: ThemeManager {
        InteropModulesIos.shared.themeManager
    }
    private var actualTheme: Theme {
        theme.isLight ? themeManager.darkTheme : themeManager.lightTheme
    }
    
    init(render: RenderIos, theme: Theme, viewState: HomeOnboardingHeaderViewState) {
        let themeManager = InteropModulesIos.shared.themeManager
        let actualTheme: Theme = theme.isLight ? themeManager.darkTheme : themeManager.lightTheme
        self.render = render
        self.theme = theme
        self.viewState = viewState
        self.titleLabel = StyledLabel(text: viewState.title, theme: actualTheme)
        self.subheaderLabel = StyledLabel(text: viewState.summary, theme: actualTheme)
        super.init(frame: .zero)
        setupViews()
        setupConstraints()
    }
    
    override init(frame: CGRect) {
        super.init(frame: frame)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    private func setupViews() {
        backgroundColor = actualTheme.themeColors.background.uiColor
        addSubview(titleLabel)
        addSubview(subheaderLabel)
    }
    
    private func setupConstraints() {
        let viewSpec = viewState.viewSpec
        let paddingDefault = render.defaultViewSpec.paddingDefault.toCGFloat()
        let verticalPadding = viewSpec.verticalPadding.toCGFloat()
        titleLabel.translatesAutoresizingMaskIntoConstraints = false
        subheaderLabel.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint.activate([
            // Title label constraints
            titleLabel.centerXAnchor.constraint(equalTo: centerXAnchor),
            titleLabel.topAnchor.constraint(equalTo: topAnchor, constant: verticalPadding + render.windowFrame.statusBarHeight.toCGFloat()),
            
            // Subheader label constraints
            subheaderLabel.topAnchor.constraint(equalTo: titleLabel.bottomAnchor, constant: viewSpec.itemSpacing.toCGFloat()),
            subheaderLabel.centerXAnchor.constraint(equalTo: centerXAnchor),
            subheaderLabel.leadingAnchor.constraint(greaterThanOrEqualTo: leadingAnchor, constant: paddingDefault),
            subheaderLabel.trailingAnchor.constraint(lessThanOrEqualTo: trailingAnchor, constant: -paddingDefault),
            subheaderLabel.bottomAnchor.constraint(equalTo: bottomAnchor, constant: -verticalPadding)
        ])
    }
    
    func updateWith(viewState: HomeOnboardingHeaderViewState) {
        self.viewState = viewState
        titleLabel.updateWith(text: viewState.title)
        subheaderLabel.updateWith(text: viewState.summary)
    }
    
    func updateWith(theme: Theme) {
        self.theme = theme
        titleLabel.updateWith(theme: actualTheme)
        subheaderLabel.updateWith(theme: actualTheme)
        backgroundColor = actualTheme.themeColors.background.uiColor
    }
}
