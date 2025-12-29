//
//  CollectionToolbarView.swift
//  WallApp
//

import WallApp
import SwiftUI

struct CollectionToolbarLockedView: View {
    let viewState: CollectionToolbarViewState.Locked
    let render: RenderIos
    let theme: Theme
    @Binding var expandedProgress: CGFloat
    
    @State var headerHeight: CGFloat = 0
    
    private var paddingDefault: CGFloat {
        return render.defaultViewSpec.paddingDefault.toCGFloat()
    }
    
    var body: some View {
        let viewSpec = viewState.viewSpec
        let containerColor = viewState.containerColorOverride.toColor(themeColors: theme.themeColors)
        let profileImage = viewState.artistProfileImage
        let profileAnimatedViewSpec = viewSpec.profileAnimatedViewSpec
        let btnBackTopDefaultPadding = render.windowFrame.statusBarHeight.toCGFloat() + paddingDefault
        let btnBackTopPadding = viewState.messageBarViewState == nil ? btnBackTopDefaultPadding : (btnBackTopDefaultPadding + (viewState.messageBarViewState?.viewSpec.maxHeight.toCGFloat() ?? 0))
        let collectionNamePaddingTop = viewSpec.collectionNamePaddingTop.toCGFloat()
        let adFreeCollectionLockedInfo = viewState.adFreeCollectionLockedInfo
        
        CollectionToolbarContentView(
            render: render,
            theme: theme,
            profileImage: profileImage,
            containerColor: containerColor,
            profileAnimatedViewSpec: profileAnimatedViewSpec,
            collectionNamePaddingTop: collectionNamePaddingTop,
            navigationIcon: viewState.toolbar.navigationIcon,
            collectionTitle: viewState.title,
            collectionActionButtonViewState: viewState.collectionActionButtonViewState,
            btnBackTopPadding: btnBackTopPadding,
            minToolbarHeight: viewSpec.minToolbarHeight.toCGFloat(),
            maxToolbarHeight: viewSpec.maxToolbarHeight.toCGFloat(),
            isExpanded: viewState.isExpanded,
            expandedProgress: $expandedProgress,
            messageBarViewState: viewState.messageBarViewState,
            adFreeCollectionLockedInfo: adFreeCollectionLockedInfo,
            adFreeCollectionLockedInfoOffsetTop: viewSpec.adFreeCollectionLockedInfoTop.toCGFloat()
        )
    }
}

struct CollectionToolbarUnlockedView: View {
    let viewState: CollectionToolbarViewState.Unlocked
    let render: RenderIos
    let theme: Theme
    
    @Binding var expandedProgress: CGFloat
    
    var body: some View {
        let paddingDefault = render.defaultViewSpec.paddingDefault.toCGFloat()
        let viewSpec = viewState.viewSpec
        let containerColor = viewState.containerColorOverride.toColor(themeColors: theme.themeColors)
        let profileImage = viewState.artistProfileImage
        let profileAnimatedViewSpec = viewSpec.profileAnimatedViewSpec
        let collectionNamePaddingTop = viewSpec.collectionNamePaddingTop.toCGFloat()
        let btnBackTopPadding = render.windowFrame.statusBarHeight.toCGFloat() + paddingDefault
        
        CollectionToolbarContentView(
            render: render,
            theme: theme,
            profileImage: profileImage,
            containerColor: containerColor,
            profileAnimatedViewSpec: profileAnimatedViewSpec,
            collectionNamePaddingTop: collectionNamePaddingTop,
            navigationIcon: viewState.toolbar.navigationIcon,
            collectionTitle: viewState.title,
            collectionActionButtonViewState: viewState.collectionActionButtonViewState,
            btnBackTopPadding: btnBackTopPadding,
            minToolbarHeight: viewSpec.minToolbarHeight.toCGFloat(),
            maxToolbarHeight: viewSpec.maxToolbarHeight.toCGFloat(),
            isExpanded: viewState.isExpanded,
            expandedProgress: $expandedProgress
        )
    }
}

struct CollectionToolbarContentView: View {
    let render: RenderIos
    let theme: Theme
    let profileImage: ProfileImageViewState
    let containerColor: Color?
    let profileAnimatedViewSpec: AnimatedViewSpec
    let collectionNamePaddingTop: CGFloat
    let navigationIcon: MenuItem?
    let collectionTitle: StyledText
    let collectionActionButtonViewState: CollectionActionButtonViewState
    let btnBackTopPadding: CGFloat
    let minToolbarHeight: CGFloat
    let maxToolbarHeight: CGFloat
    let isExpanded: Bool
    @Binding var expandedProgress: CGFloat
    var messageBarViewState: MessageBarViewState? = nil
    var adFreeCollectionLockedInfo: MenuItem? = nil
    var adFreeCollectionLockedInfoOffsetTop: CGFloat = 0
    
    @State private var isExpandedValue: CGFloat = 1
    
    @State private var messageBarHeight: CGFloat = 0
    @State private var headerHeight: CGFloat = 0
    
    var body: some View {
        let paddingSmall = render.defaultViewSpec.paddingSmall.toCGFloat()
        let profileImageSizeDefault = profileImage.imageViewSpec.size.toCGFloat()
        let profileImageSizeMin = profileAnimatedViewSpec.widthMin?.toCGFloat() ?? profileImageSizeDefault
        let profileImageSizeMax = profileAnimatedViewSpec.widthMax?.toCGFloat() ?? profileImageSizeDefault
        let profileImageSize = lerp(profileImageSizeMin, profileImageSizeMax, expandedProgress)
        let statusBarHeight = render.windowFrame.statusBarHeight.toCGFloat()
        let profileImageStartPadding = profileAnimatedViewSpec.paddingMin?.start.toCGFloat()
        let profileImagePaddingTop = statusBarHeight + paddingSmall + messageBarHeight
        let alpha = arbitrateToolbarContentAnimatedAlpha(animationProgress: Float(expandedProgress)).toCGFloat()
        let collectionNamePaddingTop = collectionNamePaddingTop + statusBarHeight + paddingSmall + messageBarHeight
        let adFreeCollectionLockedInfoOffsetTop = adFreeCollectionLockedInfoOffsetTop + statusBarHeight + paddingSmall + messageBarHeight
        
        let headerHeight = self.headerHeight == 0 ? calculateHeaderHeight(progress: expandedProgress, messageBarHeight: messageBarHeight) : self.headerHeight
        
        ZStack(alignment: .top) {
            Rectangle()
                .fill(containerColor ?? .black)
                .frame(height: headerHeight)

            if let navigationIcon {
                BackButtonView(
                    render: render,
                    theme: theme,
                    menuItem: navigationIcon,
                    topPadding: btnBackTopPadding
                )
            }
            
            StatusBarView(render: render, theme: theme, color: Color.clear)
            
            if messageBarHeight > 0, let messageBarViewState {
                MessageBarView(render: render, theme: theme, viewState: messageBarViewState)
                    .padding(.top, statusBarHeight)
            }
            
            HStack(spacing: 0) {
                if isExpandedValue == 1 {
                    Spacer()
                } else {
                    Spacer()
                        .frame(width: profileImageStartPadding)
                }
                ProfileImageView(
                    render: render,
                    theme: theme,
                    profileImage: profileImage,
                    imageSize: profileImageSize,
                    eventHandler: profileImage.eventHandler
                )
                Spacer()
            }
            .padding(.top, profileImagePaddingTop)
            ZStack {
                StyledTextView(text: collectionTitle, theme: theme)
            }
            .opacity(alpha)
            .padding(.top, collectionNamePaddingTop)
            
            // Entitlement buttons
            if !(collectionActionButtonViewState.buttonViewState.menuItem is MenuItem.MenuItemProgressButton) {
                EntitlementButtonContainerView(
                    render: render,
                    theme: theme,
                    viewState: collectionActionButtonViewState,
                    progress: expandedProgress
                )
                .padding(.top, messageBarHeight)
            }
            
            if let adFreeCollectionLockedInfo {
                MenuItemUI(render: render, theme: theme, menuItem: adFreeCollectionLockedInfo)
                    .opacity(alpha)
                    .offset(y: adFreeCollectionLockedInfoOffsetTop)
            }
        }
        .onChange(of: isExpanded) { newValue in
            withAnimation(.easeOut(duration: 0.2)) {
                isExpandedValue = newValue ? 1 : 0
            }
        }
        .animationObserver(for: isExpandedValue) { progress in
            expandedProgress = progress
            let messageBarHeight = messageBarViewState?.viewSpec.maxHeight.toCGFloat() ?? 0
            self.headerHeight = calculateHeaderHeight(progress: progress, messageBarHeight: messageBarHeight)
        }
        .onChange(of: messageBarViewState) { messageBarViewState in
            withAnimation {
                self.messageBarHeight = messageBarViewState?.viewSpec.maxHeight.toCGFloat() ?? 0
            }
        }
        .animationObserver(for: messageBarHeight) { newHeight in
            self.headerHeight = calculateHeaderHeight(progress: expandedProgress, messageBarHeight: newHeight)
        }
        .onAppear {
            self.messageBarHeight = messageBarViewState?.viewSpec.maxHeight.toCGFloat() ?? 0
            self.headerHeight = calculateHeaderHeight(progress: expandedProgress, messageBarHeight: messageBarHeight)
        }
    }
    
    private func calculateHeaderHeight(progress: CGFloat, messageBarHeight: CGFloat) -> CGFloat {
        let height = lerp(minToolbarHeight, maxToolbarHeight, progress)
        return height + render.windowFrame.statusBarHeight.toCGFloat() + messageBarHeight
    }
}
