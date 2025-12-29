//
//  PaywallScreenView.swift
//  WallApp
//

import WallApp
import SwiftUI

struct PaywallScreenView: View {
    let viewState: PaywallViewState
    let theme: Theme
    let render: RenderIos
    
    @EnvironmentObject var router: Router
    
    var body: some View {
        ZStack {
            switch(onEnum(of: viewState)) {
            case .loading(_):
                LoadingView(theme: theme, render: render)
            case .error(_):
                EmptyView()
            case .native(let viewState):
                PaywallNativeView(viewState: viewState, theme: theme, render: render)
            case .nativeTabs(let viewState):
                PaywallNativeTabsView(viewState: viewState, theme: theme, render: render)
            case .remote(_):
                // This is handled via PaywallCoordinator.swift so ignore here
                EmptyView()
            }
        }
        .animation(.easeInOut, value: viewState)
        .onDisappear {
            router.paywallDismissed()
        }
    }
}


struct PaywallNativeView: View {
    let viewState: PaywallViewState.Native
    let theme: Theme
    let render: RenderIos
    
    var body: some View {
        let paddingDefault = render.defaultViewSpec.paddingDefault.toCGFloat()
        let paddingSmall = render.defaultViewSpec.paddingSmall.toCGFloat()
        let deviceHeight = render.windowFrame.deviceHeight.toCGFloat()
        
        let featureImage = viewState.featureImageViewState
        let subscriptionTitle = viewState.subscriptionTitle
        let paywallPlanView = viewState.paywallPlanView
        let actionButton = viewState.actionButton
        let footerItem = viewState.footerItem
        ZStack(alignment: .bottom) {
            VStack(spacing: 0) {
                SwiftUIImage(render: render, theme: theme, imageViewState: featureImage)
                
                Spacer()
                    .frame(minHeight: 0, maxHeight: paddingSmall)
                
                MenuItemUI(render: render, theme: theme, menuItem: subscriptionTitle)
                
                Spacer()
                    .frame(minHeight: 0, maxHeight: paddingDefault)
                
                paywallPlanView.render(render: render, theme: theme)
                
                Spacer().frame(minHeight: paddingDefault)
                
                MenuItemUI(render: render, theme: theme, menuItem: actionButton)
                    .padding(.horizontal, paddingDefault)
                
                Spacer().frame(minHeight: paddingSmall, maxHeight: paddingDefault)
                
                MenuItemUI(render: render, theme: theme, menuItem: footerItem)
                    .padding(.horizontal, paddingDefault)
                
                Spacer().frame(height: paddingDefault + render.windowFrame.navigationBarHeight.toCGFloat())
            }
            .frame(height: deviceHeight)
            .overlay(alignment: .topLeading) {
                VStack {
                    Spacer().frame(height: render.windowFrame.statusBarHeight.toCGFloat())
                    MenuItemUI(render: render, theme: theme, menuItem: viewState.close)
                    Spacer()
                }
            }
        }
    }
}

struct PaywallNativeTabsView: View {
    let viewState: PaywallViewState.NativeTabs
    let theme: Theme
    let render: RenderIos
    @State var selectedTab: Int
    
    init(viewState: PaywallViewState.NativeTabs, theme: Theme, render: RenderIos) {
        self.viewState = viewState
        self.theme = theme
        self.render = render
        self._selectedTab = State(initialValue: Int(viewState.initialTabPage))
    }
    
    var body: some View {
        let paddingDefault = render.defaultViewSpec.paddingDefault.toCGFloat()
        let paddingSmall = render.defaultViewSpec.paddingSmall.toCGFloat()
        let deviceHeight = render.windowFrame.deviceHeight.toCGFloat()
        
        let featureImage = viewState.featureImageViewState
        let subscriptionTitle = viewState.subscriptionTitle
        let planSelectionTabs = viewState.planSelectionTabs
        let actionButton = viewState.actionButton
        let footerItem = viewState.footerItem
        
        ZStack {
            VStack(spacing: 0) {
                SwiftUIImage(render: render, theme: theme, imageViewState: featureImage)
                    .transaction { transaction in
                        transaction.animation = nil 
                    }
                
                Spacer()
                    .frame(height: paddingSmall)
                
                MenuItemUI(render: render, theme: theme, menuItem: subscriptionTitle)

                Spacer()
                    .frame(height: paddingSmall)
                
                if let tabsViewState = planSelectionTabs as? TabsViewState.Pill {
                    CapsuleTabs(
                        render: render,
                        theme: theme,
                        selectedTab: $selectedTab,
                        tabs: tabsViewState.tabs,
                        containerWidth: tabsViewState.viewSpec.tabWidth?.toCGFloat() ?? render.windowFrame.deviceWidth.toCGFloat(),
                        containerHeight: tabsViewState.viewSpec.tabHeight.toCGFloat(),
                        containerColor: tabsViewState.containerColorToken?.toColor(themeColors: theme.themeColors) ?? .white,
                        indicatorColor: tabsViewState.indicatorColorToken?.toColor(themeColors: theme.themeColors) ?? .gray
                    )
                    Spacer().frame(minHeight: 0, maxHeight: paddingSmall)
                    let tabs = tabsViewState.tabs
                    CustomPagerView(selectedTab: $selectedTab, pageCount: tabs.count) {
                        ForEach(tabs.indices, id: \.self) { index in
                            tabs[index].view?.render(render: render, theme: theme)
                        }
                    }
                    .onChange(of: selectedTab) { newValue in
                        if let viewEvent = tabs[safe: newValue]?.viewEvent as? PaywallViewEvent {
                            self.viewState.paywallViewEventSink(viewEvent)
                        }
                    }
                }
                
//                Spacer()
                
                MenuItemUI(render: render, theme: theme, menuItem: actionButton)
                    .padding(.horizontal, paddingDefault)
                
                Spacer().frame(height: paddingSmall)
                
                MenuItemUI(render: render, theme: theme, menuItem: footerItem)
                    .padding(.horizontal, paddingDefault)
                
                Spacer().frame(height: render.windowFrame.navigationBarHeight.toCGFloat())
            }
            .frame(height: deviceHeight)
            .overlay(alignment: .topLeading) {
                VStack {
                    Spacer().frame(height: render.windowFrame.statusBarHeight.toCGFloat())
                    MenuItemUI(render: render, theme: theme, menuItem: viewState.close)
                    Spacer()
                }
            }
        }
        
    }
}

struct PaywallPlanView: View {
    let render: RenderIos
    let theme: Theme
    let viewState: PaywallPlanViewState
    
    var body: some View {
        let paddingDefault = render.defaultViewSpec.paddingDefault.toCGFloat()
        let paddingSmall = render.defaultViewSpec.paddingSmall.toCGFloat()
        
        let subscriptionFeatures = viewState.subscriptionFeatures
        let plusPlans = viewState.plusPlans
        
        VStack(spacing: 0) {
            VStack(alignment: .leading, spacing: 0) {
                ForEach(subscriptionFeatures.indices, id: \.self) { index in
                    let feature = subscriptionFeatures[index]
                    PaywallFeatureView(render: render, theme: theme, viewState: feature)
                }
            }
            .frame(maxWidth: .infinity)
            .padding(.horizontal, paddingDefault)
            
            Spacer()
            
            PlusPlanView(plans: plusPlans, theme: theme, render: render)
                .padding(.horizontal, paddingDefault)
            
            Spacer().frame(height: paddingSmall)
        }
    }
}

struct PaywallFeatureView: View {
    let render: RenderIos
    let theme: Theme
    let viewState: PaywallFeatureViewState
    
    var body: some View {
        let icon = viewState.icon
        let label = viewState.label
        let paddingDefault = render.defaultViewSpec.paddingDefault.toCGFloat()
        
        let iconHeight = icon.itemHeight()?.toCGFloat() ?? 32
        
        HStack(spacing: 0) {
            MenuItemUI(render: render, theme: theme, menuItem: icon)
            Spacer().frame(width: paddingDefault)
            StyledTextView(text: label, theme: theme)
                .frame(maxWidth: .infinity, alignment: .leading)
            Spacer()
        }
        .frame(height: iconHeight + paddingDefault)
    }
}


struct PlusPlanView: View {
    let plans: [SubscriptionPlanViewState]
    let theme: Theme
    let render: RenderIos
    
    var body: some View {
        
        VStack(spacing: render.defaultViewSpec.paddingDefault.toCGFloat()) {
            ForEach(plans.indices, id: \.self) { index in
                let plan = plans[index]
                PurchasePlanOutline(viewState: plan, theme: theme, render: render)
            }
        }
    }
}

struct PurchasePlanOutline: View {
    let viewState: SubscriptionPlanViewState
    let theme: Theme
    let render: RenderIos
    
    var body: some View {
        let paddingDefault = render.defaultViewSpec.paddingDefault.toCGFloat()
        let highlight = viewState.highlight
        let price = viewState.price
        let isSelected = viewState.isSelected
        let onClick = viewState.eventSink
        let shape = viewState.shapeSpec
        let backGroundColor = isSelected ? theme.themeColors.secondary.toColor() : theme.themeColors.onPrimary?.toColor()
        let contentColor = (isSelected ? theme.themeColors.onSurface?.toColor() : theme.themeColors.onPrimary?.toColor()) ?? nil
        
        ZStack(alignment: .topTrailing) {
            HStack {
                VStack(alignment: .leading) {
                    MenuItemUI(render: render, theme: theme, menuItem: viewState.title)
                    
                    Spacer()
                        .frame(height: paddingDefault / 4.0)
                    
                    StyledTextView(text: price, theme: theme)
                }
                Spacer()
            }.frame(maxWidth: .infinity)
            
            
            if let highlight {
                PurchaseButtonHighlightFill(theme: theme, render: render, text: highlight, isSelected: isSelected, shape: shape, contentColor: contentColor, backGroundColor:  backGroundColor ?? .clear)
                    .frame(height: 24.0)
                    .applyClipShapes(shape)
            }
        }.padding(.all, paddingDefault)
            .frame(maxWidth: .infinity)
            .frame(height: 84.0)
            .background(contentColor)
            .applyClipShapes(shape, borderWidth: 2.0, borderColor: isSelected ? .clear : theme.themeColors.outline.toColor())
            .onTapGesture {
                onClick(viewState.event)
            }
    }
}

struct PurchaseButtonHighlightFill: View {
    let theme: Theme
    let render: RenderIos
    let text: StyledText
    let isSelected: Bool
    let shape: ShapeSpec
    let contentColor: Color?
    let backGroundColor: Color
    
    var body: some View {
        let paddingDefault = render.defaultViewSpec.paddingDefault.toCGFloat()
        HStack(alignment: .center) {
            StyledTextView(text: text, theme: theme)
        }.padding(.all, paddingDefault)
        .background(backGroundColor)
    }
}
