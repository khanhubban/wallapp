//
//  HomeView.swift
//  WallApp
//

import WallApp
import SwiftUI

struct HomeScreenView: View {
    let viewState: HomeViewState
    let theme: Theme
    let render: RenderIos
    
    var body: some View {
        ZStack {
            switch(onEnum(of: viewState)) {
            case .loading(_):
                LoadingView(theme: theme, render: render)
            case .data(let data):
                HomeTabPagedView(data: data, render: render, theme: theme)
            case .noData(_):
                let _ = 0 // ignore this
            }
        }
        .animation(.easeInOut, value: viewState)
    }
}


struct HomeTabPagedView: View {
    let data: HomeViewState.Data
    let render: RenderIos
    let theme: Theme
    
    @StateObject var toolbarOffsetController = ScrollOffsetController()
    @EnvironmentObject var navbarOffsetController: ScrollOffsetController
    
    @State private var selectedTab = 0
    
    var body: some View {
        let tabs = data.tabs.tabs
        ZStack(alignment: .top) {
            HomeToolbarView(
                homeTopBarState: data.topBar,
                selectedTab: $selectedTab,
                render: render,
                theme: theme,
                scrollOffsetController: toolbarOffsetController
            )
            .zIndex(1)
            
            CustomPagerView(selectedTab: $selectedTab, pageCount: tabs.count) {
                ForEach(tabs.indices, id: \.self) { index in
                    if let view = tabs[index].view, let viewState = view.viewState as? FeedViewState {
                        FeedGrid(
                            render: render,
                            theme: theme,
                            feedViewState: viewState,
                            scrollOffsetControllers: [
                                toolbarOffsetController,
                                navbarOffsetController
                            ]
                        )
                        .frame(maxHeight: .infinity)
                        .ignoresSafeArea(.all, edges: .all)
                    }
                }
            }
            .frame(maxHeight: .infinity)
            .onChange(of: selectedTab) { index in
                navbarOffsetController.setToShow()
                toolbarOffsetController.setToShow()
            }
        }
        .transition(.fade)
    }
}

struct HomeToolbarView: View {
    let homeTopBarState: HomeTopBarViewState
    @Binding var selectedTab: Int
    let render: RenderIos
    let theme: Theme
    let scrollOffsetController: ScrollOffsetController
    
    let systemPhotoPicker = SystemPhotoPickerIos()
    
    @State var messageBar: MessageBarViewState? = nil
    @State var headerHeight: CGFloat = 0
    
    var body: some View {
        let tabs = homeTopBarState.tabs
        let viewSpec = homeTopBarState.viewSpec
        let containerColor = homeTopBarState.containerColor.toColor()
        let profileImage = homeTopBarState.profileImage
        
        ZStack {
            ZStack(alignment: .top) {
                StatusBarView(render: render, theme: theme)
                    .zIndex(1.0)
                VStack(alignment: .center, spacing: 0) {
                    StatusBarView(render: render, theme: theme, color: Color.clear)
                    
                    if let messageBar {
                        MessageBarView(render: render, theme: theme, viewState: messageBar)
                            .scaleEffect(y: messageBar != nil ? 1 : 0)
                            .background(containerColor)
                    }
                    
                    ZStack(alignment: .center) {
                        ProfileImageView(
                            render: render,
                            theme: theme,
                            profileImage: profileImage,
                            imageSize: profileImage.imageViewSpec.size.toCGFloat(),
                            eventHandler: profileImage.eventHandler
                        )
                    }
                    .padding(.vertical, viewSpec.profileVerticalPadding.toCGFloat())
                    .frame(maxWidth: .infinity)
                    .background(containerColor)
                    if let tabsViewState = tabs as? TabsViewState.Indicator {
                        TopTabsView(tabsState: tabsViewState, selectedTab: $selectedTab, render: render, theme: theme)
                    }
                }
                .frame(maxWidth: .infinity)
                .frame(height: headerHeight)
                .onAppear {
                    headerHeight = calculateHeaderHeight(viewState: homeTopBarState)
                    messageBar = homeTopBarState.messageBar
                }
                .onChange(of: homeTopBarState) { viewState in
                    withAnimation {
                        headerHeight = calculateHeaderHeight(viewState: viewState)
                    }
                    withAnimation {
                        messageBar = viewState.messageBar
                    }
                }
                .onChange(of: headerHeight) { newHeight in
                    scrollOffsetController.update(maxOffset: newHeight)
                }
                .offset(y: scrollOffsetController.offset)
                .animation(.spring(duration: 0.2), value: scrollOffsetController.offset)
            }
        }
    }
    
    private func calculateHeaderHeight(viewState: HomeTopBarViewState) -> CGFloat {
        return viewState.viewSpec.height.toCGFloat() + (viewState.messageBar?.viewSpec.maxHeight.toCGFloat() ?? 0) + render.windowFrame.statusBarHeight.toCGFloat()
    }
}
