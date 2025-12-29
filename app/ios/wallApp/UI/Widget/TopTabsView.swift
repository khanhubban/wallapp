//
//  TopTabsView.swift
//  WallApp
//

import WallApp
import SwiftUI

struct TopTabsView: View {
    let tabsState: TabsViewState.Indicator
    @Binding var selectedTab: Int
    let render: RenderIos
    let theme: Theme
    
    var body: some View {
        let tabs = tabsState.tabs
        let viewSpec = tabsState.viewSpec
        let containerColor = tabsState.containerColorToken?.toColor(themeColors: theme.themeColors)
        let screenWidth = render.windowFrame.deviceWidth.toCGFloat()
        let tabWidth = screenWidth / tabs.count.toCGFloat()
        let tabHeight = viewSpec.tabHeight.toCGFloat()
        VStack(spacing: 0) {
            HStack {
                ForEach(tabs.indices, id: \.self) { index in
                    TopTabView(render: render, tab: tabs[index], index: index, selectedTab: $selectedTab, tabWidth: tabWidth, theme: theme)
                }
            }
            .frame(height: tabHeight)
            .frame(maxWidth: .infinity)
            .background(containerColor)
            
            TabsIndicatorView(
                selectedTab: selectedTab,
                screenWidth: screenWidth,
                tabWidth: tabWidth,
                indicatorHeight: viewSpec.tabIndicatorHeight.toCGFloat(),
                indicatorWidth: tabWidth - (render.defaultViewSpec.paddingDefault.toCGFloat() * 2),
                indicatorColor: tabsState.indicatorColorToken?.toColor(themeColors: theme.themeColors)
            )
                
        }
        .frame(height: viewSpec.tabContainerHeight.toCGFloat())
    }
    
    
    func calculateOffset(screenWidth: CGFloat, tabWidth: CGFloat) -> CGFloat {
        let offset = (tabWidth * selectedTab.toCGFloat()) - (screenWidth / 2) + (tabWidth / 2)
        return offset
    }
}

struct TopTabView: View {
    let render: RenderIos
    let tab: TabViewState
    let index: Int
    @Binding var selectedTab: Int
    let tabWidth: CGFloat
    let theme: Theme
    
    var body: some View {
        Button(action: {
            withAnimation {
                selectedTab = index
            }
        }) {
            if selectedTab == index {
                MenuItemUI(render: render, theme: theme, menuItem: tab.headerSelected, ignoreInherentSize: true)
            } else {
                MenuItemUI(render: render, theme: theme, menuItem: tab.headerUnselected, ignoreInherentSize: true)
            }
        }
    }
}


class UITopTabsView: UIView {
    
    var tabsState: TabsViewState.Indicator
    var selectedTab: TabSelectionController
    var render: RenderIos
    var theme: Theme
    
    private var tabViews = [UITopTabView]()
    private var indicatorView = UIView()
    
    let horizontalStackView: UIStackView = {
        let stackView = UIStackView()
        stackView.axis = .horizontal
        stackView.alignment = .center
        stackView.distribution = .fillEqually
        stackView.spacing = 0
        return stackView
    }()
    
    init(tabsState: TabsViewState.Indicator, selectedTab: TabSelectionController, render: RenderIos, theme: Theme) {
        self.tabsState = tabsState
        self.selectedTab = selectedTab
        self.render = render
        self.theme = theme
        super.init(frame: .zero)
        setupViews()
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    func update(tabsState: TabsViewState.Indicator, render: RenderIos, theme: Theme) {
        self.render = render
        self.theme = theme
        self.tabsState = tabsState
        if let containerColor = tabsState.containerColorToken?.toColor(themeColors: theme.themeColors) {
            horizontalStackView.backgroundColor = containerColor.uiColor()
        }
        tabViews.forEach { tab in
            tab.update(render: render, selectedTab: selectedTab.tabIndex, theme: self.theme)
        }
    }
    
    private func setupViews() {
        guard let containerColor = tabsState.containerColorToken?.toColor(themeColors: theme.themeColors) else {
            return
        }
        
        let screenWidth = render.windowFrame.deviceWidth.toCGFloat()
        let tabWidth = screenWidth / CGFloat(tabsState.tabs.count)
        let tabHeight = tabsState.viewSpec.tabHeight.toCGFloat()
        
        // Add tab views
        for (index, tab) in tabsState.tabs.enumerated() {
            let tabView = UITopTabView(render: render, tab: tab, index: index, selectedTab: selectedTab.tabIndex, tabWidth: tabWidth, theme: theme)
            tabView.isUserInteractionEnabled = false
            tabViews.append(tabView)
            horizontalStackView.addArrangedSubview(tabView)
        }
        
        self.addSubview(horizontalStackView)
        horizontalStackView.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint.activate([
            horizontalStackView.topAnchor.constraint(equalTo: self.topAnchor),
            horizontalStackView.leadingAnchor.constraint(equalTo: self.leadingAnchor),
            horizontalStackView.trailingAnchor.constraint(equalTo: self.trailingAnchor),
            horizontalStackView.heightAnchor.constraint(equalToConstant: tabHeight)
        ])
        
        // Setup indicator view
        indicatorView.backgroundColor = tabsState.indicatorColorToken?.toColor(themeColors: theme.themeColors)?.uiColor()
        addSubview(indicatorView)
        
        // Layout indicator view
        let indicatorHeight = tabsState.viewSpec.tabIndicatorHeight.toCGFloat()
        let indicatorWidth = tabWidth - (render.defaultViewSpec.paddingDefault.toCGFloat() * 2)
        indicatorView.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint.activate([
            indicatorView.topAnchor.constraint(equalTo: self.horizontalStackView.bottomAnchor),
            indicatorView.heightAnchor.constraint(equalToConstant: indicatorHeight),
            indicatorView.widthAnchor.constraint(equalToConstant: indicatorWidth)
        ])
        
        updateIndicatorPosition(animated: false)
        
        // Set background color
        horizontalStackView.backgroundColor = containerColor.uiColor()
        
        // Add tap gesture recognizer
        let tapGesture = UITapGestureRecognizer(target: self, action: #selector(tabTapped(_:)))
        horizontalStackView.isUserInteractionEnabled = true
        horizontalStackView.addGestureRecognizer(tapGesture)
        
        DispatchQueue.main.async {
            self.updateIndicatorPosition(animated: false)
        }
        
    }
    
    @objc private func tabTapped(_ gesture: UITapGestureRecognizer) {
        let location = gesture.location(in: self)
        let screenWidth = render.windowFrame.deviceWidth.toCGFloat()
        let tabWidth = screenWidth / CGFloat(tabsState.tabs.count)
        let tappedTab = Int(location.x / tabWidth)
        
        if tappedTab != selectedTab.tabIndex {
            selectedTab.tabIndex = tappedTab
            updateIndicatorPosition(animated: true)
            return
        }
        
        tabViews.forEach { tab in
            tab.update(render: render, selectedTab: selectedTab.tabIndex, theme: self.theme)
        }
    }
    
    func updateIndicatorPosition(animated: Bool) {
        let screenWidth = render.windowFrame.deviceWidth.toCGFloat()
        let tabWidth = screenWidth / CGFloat(tabsState.tabs.count)
        let indicatorWidth = tabWidth - (render.defaultViewSpec.paddingDefault.toCGFloat() * 2)
        let indicatorX = (CGFloat(selectedTab.tabIndex) * tabWidth) + (tabWidth / 2) - (indicatorWidth / 2)
        
        if animated {
            UIView.animate(withDuration: 0.2) {
                self.indicatorView.frame.origin.x = indicatorX
            }
        } else {
            indicatorView.frame.origin.x = indicatorX
        }
        
        tabViews.forEach { tab in
            tab.update(render: render, selectedTab: selectedTab.tabIndex, theme: self.theme)
        }
    }
}

class UITopTabView: UIView {
    
    var render: RenderIos
    var tab: TabViewState
    var index: Int
    var selectedTab: Int
    var tabWidth: CGFloat
    var theme: Theme
    
    private var lblMenu: StyledLabel!
    
    init(render: RenderIos, tab: TabViewState, index: Int, selectedTab: Int, tabWidth: CGFloat, theme: Theme) {
        self.render = render
        self.tab = tab
        self.index = index
        self.selectedTab = selectedTab
        self.tabWidth = tabWidth
        self.theme = theme
        super.init(frame: .zero)
        self.setupViews()
        self.backgroundColor = .clear
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    private func setupViews() {
        let menuItem = selectedTab == index ? tab.headerSelected : tab.headerUnselected

        if let menu = menuItem as? MenuItem.MenuItemLabel {
            self.lblMenu = StyledLabel(text: menu.text, theme: theme)
            addSubview(lblMenu)
            
            lblMenu.translatesAutoresizingMaskIntoConstraints = false
            NSLayoutConstraint.activate([
                lblMenu.topAnchor.constraint(equalTo: topAnchor),
                lblMenu.leadingAnchor.constraint(equalTo: leadingAnchor),
                lblMenu.widthAnchor.constraint(equalToConstant: tabWidth),
                lblMenu.heightAnchor.constraint(equalTo: heightAnchor)
            ])
        }
    }
    
    func update(render: RenderIos, selectedTab: Int, theme: Theme) {
        let menuItem = selectedTab == index ? tab.headerSelected : tab.headerUnselected
        self.render = render
        self.selectedTab = selectedTab
        self.theme = theme
        if let menu = menuItem as? MenuItem.MenuItemLabel {
            lblMenu.updateWith(text: menu.text, theme: self.theme)
        }
    }
    
}
