//
//  ProfileScreenView.swift
//  WallApp
//

import Foundation

struct ProfileScreenView: View {
    let viewModel: ProfileViewModel
    @State var theme: Theme = InteropModulesIos.shared.themeManager.theme.value
    let render: RenderIos
    let navBarOffsetController: ScrollOffsetController
    var systemTheme: SystemThemeIos { return InteropModulesIos.shared.systemTheme }
    @State var viewState: ProfileViewState? = nil
    @Environment(\.colorScheme) var colorScheme
    
    @EnvironmentObject var router: Router
    
    var body: some View {
        ZStack(alignment: .topLeading) {
            if let viewState {
                AppScreenView(
                    viewState: viewState,
                    theme: theme,
                    render: render
                )
                .frame(maxHeight: .infinity)
            } else {
                EmptyView()
            }
        }
        .ignoresSafeArea(.all, edges: .all)
        .task {
            for await viewState in viewModel.viewState {
                self.viewState = viewState
            }
        }.task {
            let themeManager = InteropModulesIos.shared.themeManager
            for await theme in themeManager.theme {
                self.theme = theme
            }
        }
        .onAppear {
            router.screenAppeared(ScreenArgument.ProfileScreenArgument())
        }
        
    }
}

struct ProfileTabView: View {
    let viewState: ProfileViewState
    let theme: Theme
    let render: RenderIos
    
    var body: some View {
        switch onEnum(of: viewState) {
        case .data(let data):
            SettingsFeedView(
                viewState: data.feedViewState,
                theme: theme,
                render: render
            )
            .frame(maxHeight: .infinity)
        default: EmptyView()
        }
    }
}

struct ProfileHeaderView: View {
    let theme: Theme
    let render: RenderIos
    var viewState: ProfileHeaderViewState
    
    @State var messageBar: MessageBarViewState? = nil
    @State var messageBarScale: CGFloat = 0
    @State var headerHeight: CGFloat = 0
    
    var body: some View {
        let title = viewState.title
        let summary = viewState.summary
        let upgradeButton = viewState.upgradeButton
        let viewSpec = viewState.viewSpec
        
        let statusBarHeight = render.windowFrame.statusBarHeight.toCGFloat()
        let paddingSmall = render.defaultViewSpec.paddingSmall.toCGFloat()
        
        VStack(alignment: .leading, spacing: 0) {
            Spacer()
                .frame(height: statusBarHeight)
                .frame(maxWidth: .infinity)
            
            ZStack {
                if let messageBar {
                    MessageBarView(render: render, theme: theme, viewState: messageBar)
                }
            }
            .scaleEffect(y: messageBarScale)
                
            Spacer().frame(height: paddingSmall)
            HStack(spacing: 0) {
                ProfileHeaderColumn(render: render, theme: theme, viewSpec: viewSpec, title: title, summary: summary)
                Spacer()
                if let upgradeButton = upgradeButton {
                    MenuItemUI(render: render, theme: theme, menuItem: upgradeButton)
                }
            }
            .padding(.horizontal, viewSpec.padding.start.toCGFloat())
            .zIndex(1.0)
        }
        .frame(maxWidth: .infinity)
        .frame(height: headerHeight)
        .onAppear {
            headerHeight = calculateHeaderHeight(viewState: viewState)
            messageBar = viewState.messageBar
            messageBarScale = messageBar != nil ? 1 : 0
        }
        .onChange(of: viewState) { viewState in
            withAnimation {
                headerHeight = calculateHeaderHeight(viewState: viewState)
            }
            withAnimation {
                messageBar = viewState.messageBar
            }
            withAnimation {
                messageBarScale = messageBar != nil ? 1 : 0
            }
        }
    }
    
    private func calculateHeaderHeight(viewState: ProfileHeaderViewState) -> CGFloat {
        return viewState.viewSpec.height.toCGFloat() + (viewState.messageBar?.viewSpec.maxHeight.toCGFloat() ?? 0)
    }
}

struct SettingsFeedView: View {
    let viewState: FeedViewState
    let theme: Theme
    let render: RenderIos
    
    @State var offset: CGFloat = 0
    @State var maxY: CGFloat = 0
    
    @State var messageBarHeight: CGFloat = 0
    
    @EnvironmentObject var navBarOffsetController: ScrollOffsetController
    
    var body: some View {
        let profileViewState = (viewState.toolbar as! ProfileHeaderViewState)
        let messageBar = profileViewState.messageBar
        // note: it's likely `messageBarHeight` will be a dynamic value, that will animate to 0 if `messageBarViewState` becomes `null` in a future task
        let containerColor = profileViewState.containerColor.toColor()
        ZStack(alignment: .top) {
            ProfileHeaderView(theme: theme, render: render, viewState: profileViewState)
                .background(containerColor)
                .zIndex(1.0)
            
            GeometryReader { proxy in
                ScrollView {
                    VStack(spacing: 0) {
                        ForEach(viewState.views.indices, id: \.self) { index in
                            let view = viewState.views[index]
                            FeedPreview(
                                view: view,
                                theme: theme,
                                render: render
                            )
                        }
                    }
                    .modifier(OffsetModifier(offset: $offset, maxY: $maxY))
                    .padding(.top, messageBarHeight)
                }
                .scrollIndicators(.hidden)
                .coordinateSpace(name: "scroll")
                .onChange(of: offset) { offset in
                    navBarOffsetController.onScroll(offset: -offset, upperBound: maxY - proxy.size.height, autoScrollEnd: true)
                }
            }
        }
        .onAppear {
            messageBarHeight = messageBar?.viewSpec.maxHeight.toCGFloat() ?? 0
        }
        .onChange(of: messageBar) { messageBar in
            withAnimation {
                messageBarHeight = messageBar?.viewSpec.maxHeight.toCGFloat() ?? 0
            }
        }
    }
}

struct ProfileHeaderColumn: View {
    let render: RenderIos
    let theme: Theme
    let viewSpec: ProfileHeaderViewSpec
    let title: StyledText
    var summary: StyledText?
    
    var body: some View {
        let titleHeight = viewSpec.titleHeight.toCGFloat()
        let subtitleTopPadding = viewSpec.subtitleTopPadding.toCGFloat()
        let subtitleHeight = viewSpec.subtitleHeight.toCGFloat()
        HStack(alignment: .center, spacing: 0) {
            VStack(alignment: .leading, spacing: 0) {
                StyledTextView(text: title, theme: theme)
                    .frame(height: titleHeight)
                if let summary {
                    Spacer().frame(height: subtitleTopPadding)
                    StyledTextView(text: summary, theme: theme)
                        .frame(height: subtitleHeight)
                }
            }
        }
    }
    
}
