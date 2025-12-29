//
//  SearchBar.swift
//  WallApp
//

import WallApp
import SwiftUI

struct SearchInputView: View {
    
    let render: RenderIos
    let theme: Theme
    let viewState: SearchInputViewState.Data
    @Binding var isShowSheet: Float
    let onSheetToggled: (Bool) -> Void
    let sheetVisibilityProgress: (Float) -> Void
    
    private var sheetSizeMinimized: CGFloat {
        (viewState.viewSpec.searchBarHeight + render.windowFrame.statusBarHeight + render.defaultViewSpec.paddingDefault).toCGFloat()
    }
    @State var sheetSizeExpanded : CGFloat = 0
    @State var visibilityProgress: Float = 0
    @State private var draggedOffset: CGSize = .zero
    @State private var showOverlappingSearchBar: Bool = false
    
    var body: some View {
        let transitionEnabled = viewState.searchBar.transitionEnabled
        
        ZStack(alignment: .top) {
            Color.black.opacity(isShowSheet == 1 ? 0.3 : 0)
                .onTapGesture {
                    dismissSearchBar()
                }
            
            let paddingDefault = render.defaultViewSpec.paddingDefault.toCGFloat()
            let searchBarViewState = viewState.searchBar
            let viewSpec = viewState.viewSpec
            let searchFiltersTopPadding = viewSpec.searchFiltersTopPadding.toCGFloat()
            let searchBarHeight = viewSpec.searchBarHeight.toCGFloat()
            let topPadding = viewSpec.topPadding.toCGFloat()
            let deviceWidth = render.windowFrame.deviceWidth.toCGFloat()
            let searchBarHorizontalPadding = viewSpec.searchBarHorizontalPadding.toCGFloat()
            let statusBarHeight = render.windowFrame.statusBarHeight.toCGFloat()
            let sheetExpanded = viewState.searchSheetExpanded
            
            if isShowSheet > 0 {
                VStack {
                    ZStack(alignment: .top) {
                        let dropShadowHeight = viewState.sheetDropShadow.viewSpec.height.toCGFloat()
                        let searchFiltersTopPadding = viewState.viewSpec.searchFiltersTopPadding.toCGFloat()
                        let spacerHeight = sheetExpanded && sheetSizeExpanded > 0 ? (sheetSizeExpanded - dropShadowHeight/3) : (searchFiltersTopPadding - dropShadowHeight/3)
                        VStack(spacing: 0) {
                            Spacer()
                                .frame(height: spacerHeight)
                            SwiftUIImage(render: render, theme: theme, imageViewState: viewState.sheetDropShadow, scaledToFill: true)
                                .onTapGesture {
                                    dismissSearchBar()
                                }
                        }
                        .animation(.easeInOut, value: sheetExpanded)
                        SearchInputContentView(
                            render: render,
                            theme: theme,
                            viewState: viewState,
                            showSearchBar: !transitionEnabled
                        )
                        .background {
                            GeometryReader { proxy in
                                Color.clear
                                    .onAppear {
                                        sheetSizeExpanded = proxy.size.height
                                    }
                            }
                        }
                    }
                    .offset(
                        CGSize(width: 0, height: draggedOffset.height)
                    )
                    .gesture(
                        DragGesture(coordinateSpace: .global)
                            .onChanged { value in
                                if value.translation.height <= 0 {
                                    self.draggedOffset = value.translation
                                }
                            }
                            .onEnded { value in
                                // only call action if the drag was majority in the y axis to
                                // avoid accidental dismissals when scrolling recipe bin
                                let yDisplacement = value.location.y - value.startLocation.y
                                let xDisplacement = value.location.x - value.startLocation.x
                                let isMajorityY = abs(yDisplacement) > abs(xDisplacement)
                                if isMajorityY && value.predictedEndLocation.y < sheetSizeExpanded/3 {
                                    // calculate the time it would take to dismiss the sheet
                                    let endPoint = min(0, value.predictedEndLocation.y) // dismissed means it should be either at the top (0) or above that (negative)
                                    let distanceToTravel = value.location.y - endPoint // distance to travel is the distance from the current position to the top
                                    let timeToDismiss = distanceToTravel / abs(value.velocity.height)
                                    let timeToDismissConstrained = max(0.1, min(0.3, timeToDismiss))
                                    dismissSearchBar(animation: .easeOut(duration: timeToDismissConstrained), duration: timeToDismissConstrained)
                                } else {
                                    withAnimation {
                                        draggedOffset = .zero
                                    }
                                }
                            }
                    )
                    Spacer()
                }
                .transition(.move(edge: .top))
                .animation(.easeIn, value: isShowSheet == 1)
                
                TransparentBlurView()
                    .frame(height: searchFiltersTopPadding)
                    .blur(radius: 8, opaque: false)
                    .background(.clear)
                    .zIndex(1.0)
                    .offset(y: -statusBarHeight)
                    .opacity(transitionEnabled ? 1 : 0)
            }
            
            // Note on how this cheat works: The overlapping search bar is the one that does not animate with the entire sheet.
            // It only shows once the sheet is fully expanded. And disappears as soon as the sheet starts minimising whether via
            // drag or tapping on the background. The other instance where it shows is when viewState says that transition is enabled
            // and the sheet is showing even slightly because during transition the overlapping search bar serves as the sticky bar.
            if showOverlappingSearchBar || (transitionEnabled && visibilityProgress > 0) {
                SearchBarContainerView(
                    render: render,
                    theme: theme,
                    searchBarViewState: searchBarViewState,
                    searchBarHeight: searchBarHeight,
                    paddingDefault: paddingDefault,
                    searchBarTopPadding: topPadding,
                    searchBarPaddingHorizontal: searchBarHorizontalPadding,
                    visibilityProgress: visibilityProgress,
                    canFocus: true
                )
                .frame(width: deviceWidth, height: searchFiltersTopPadding)
                .padding(.top, topPadding)
                .zIndex(3.0)
            }
        }
        .allowsHitTesting(isShowSheet == 1)
        .onChange(of: isShowSheet) { newValue in
            if newValue == 1 {
                draggedOffset = .zero
            }
            onSheetToggled(newValue == 1)
        }
        .animationObserver(for: isShowSheet) { progress in
            if draggedOffset.height != 0 {
                let progress = CGFloat(progress)
                visibilityProgress = Float((progress * (sheetSizeExpanded + draggedOffset.height)) / sheetSizeExpanded)
            } else {
                visibilityProgress = progress
            }
        }
        .animationObserver(for: draggedOffset.height) { offset in
            let progress = 1 + (offset / sheetSizeExpanded)
            visibilityProgress = Float(progress)
        }
        .onChange(of: visibilityProgress) { progress in
            sheetVisibilityProgress(progress)
            showOverlappingSearchBar = visibilityProgress == 1
        }
        .onAppear {
            sheetVisibilityProgress(0)
        }
    }
    
    private func dismissSearchBar(animation: Animation = .easeInOut(duration: 0.3), duration: Double = 0.3) {
        withAnimation(animation) {
            isShowSheet = 0
        }
    }
}

struct SearchInputContentView: View {
    let render: RenderIos
    let theme: Theme
    let viewState: SearchInputViewState.Data
    let showSearchBar: Bool
    var visibilityProgress: Float? = nil

    var body: some View {
        let paddingDefault = render.defaultViewSpec.paddingDefault.toCGFloat()
        let paddingSmall = render.defaultViewSpec.paddingSmall.toCGFloat()
        let deviceWidth = render.windowFrame.deviceWidth.toCGFloat()
        let searchBarViewState = viewState.searchBar
        let viewSpec = viewState.viewSpec
        let searchFiltersTopPadding = viewSpec.searchFiltersTopPadding.toCGFloat()
        let searchBarHeight = viewSpec.searchBarHeight.toCGFloat()
        let footerContainerColor = theme.themeColors.background.toColor()
        let sheetExpanded = viewState.searchSheetExpanded
        let backgroundColor = theme.themeColors.surface.toColor()
        let shape = ShapeSpec(shapeStyle: .roundedCornersBottom, shapeSize: .large)
        let topPadding = viewSpec.topPadding.toCGFloat()
        let searchBarHorizontalPadding = viewSpec.searchBarHorizontalPadding.toCGFloat()

        VStack(spacing: 0) {
            Spacer()
                .frame(height: topPadding)
            
            SearchBarContainerView(
                render: render,
                theme: theme,
                searchBarViewState: searchBarViewState,
                searchBarHeight: searchBarHeight,
                paddingDefault: paddingDefault,
                searchBarTopPadding: topPadding,
                searchBarPaddingHorizontal: searchBarHorizontalPadding,
                visibilityProgress: visibilityProgress,
                canFocus: false
            )
            .frame(width: deviceWidth, height: searchFiltersTopPadding)
            .opacity(showSearchBar ? 1 : 0)
            
            Spacer()
                .frame(height: paddingSmall)

            if sheetExpanded {
                SearchFiltersView(
                    render: render,
                    theme: theme,
                    viewState: viewState,
                    footerContainerColor: footerContainerColor
                )
                .padding(.horizontal, paddingDefault)
                .frame(width: deviceWidth)
                
                SearchBottomSwipeIndicator(render: render, footerContainerColor: footerContainerColor)
                    .background(Color.blue.opacity(0.4))
            }
        }
        .dynamicTypeSize(...DynamicTypeSize.large)
        .background(backgroundColor)
        .applyClipShapes(shape)
        .animation(.easeInOut, value: sheetExpanded)
    }
}

struct SearchBarContainerView: View {
    let render: RenderIos
    let theme: Theme
    let searchBarViewState: SearchBarViewState
    let searchBarHeight: CGFloat
    let paddingDefault: CGFloat
    let searchBarTopPadding: CGFloat
    let searchBarPaddingHorizontal: CGFloat
    let visibilityProgress: Float?
    let canFocus: Bool

    var body: some View {
        VStack(spacing: 0) {
            Spacer().frame(height: render.windowFrame.statusBarHeight.toCGFloat())

            SearchBarView(
                render: render,
                theme: theme,
                viewState: searchBarViewState,
                height: searchBarHeight,
                paddingHorizontal: searchBarPaddingHorizontal,
                visibilityProgress: visibilityProgress,
                canFocus: canFocus
            )
        }
        
    }
}

struct SearchBottomSwipeIndicator: View {
    let render: RenderIos
    let footerContainerColor: Color

    var body: some View {
        ZStack {
            footerContainerColor
                .frame(height: 24)

            RoundedRectangle(cornerRadius: 4)
                .fill(Color.gray.opacity(0.4))
                .frame(width: 120, height: 4)
        }
        .frame(width: render.windowFrame.deviceWidth.toCGFloat())
    }
}
