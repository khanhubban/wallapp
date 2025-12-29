//
//  SearchHeaderView.swift
//  WallApp
//

import WallApp
import SwiftUI

struct SearchResultsHeaderView: View {
    let render: RenderIos
    let theme: Theme
    let viewState: SearchResultsHeaderViewState
    
    private var viewSpec: SearchResultsHeaderViewSpec {
        viewState.viewSpec
    }
    
    var body: some View {
        let containerColor = viewState.searchContainerColor.toColor(themeColors: theme.themeColors)
        let foregroundColor = theme.themeColors.onSurface?.color.toColor()
        let paddingSmall = render.defaultViewSpec.paddingSmall.toCGFloat()
        let horizontalPadding = viewSpec.searchBarInternalHorizontalPadding.toCGFloat()
        let height = viewSpec.searchBarHeight.toCGFloat()
        let barTopPadding = viewSpec.barTopPadding.toCGFloat()
        ZStack(alignment: .center) {
            if (viewState.searchRecipeBinViewState != nil) {
                SearchRecipeBinBarView(
                    render: render,
                    theme: theme,
                    viewState: viewState.searchRecipeBinViewState!,
                    containerColor: containerColor,
                    foregroundColor: foregroundColor
                )
            } else {
                SearchResultsBarView(
                    render: render,
                    theme: theme,
                    searchText: viewState.searchText,
                    clearIcon: viewState.clearIcon,
                    height: height,
                    horizontalPadding: horizontalPadding,
                    foregroundColor: foregroundColor,
                    containerColor: containerColor,
                    shapeSpec: viewSpec.searchBarShapeSpec
                )
            }
        }
        .frame(height: height)
        .padding(.horizontal, viewSpec.searchBarExternalHorizontalPadding.toCGFloat())
        .padding(.top, render.windowFrame.statusBarHeight.toCGFloat() + barTopPadding * 2)
        .onTapGesture {
            viewState.clickEventHandler.invoke()
        }
    }
}

struct SearchResultsBarView: View {
    let render: RenderIos
    let theme: Theme
    let searchText: StyledText
    let clearIcon: MenuItem
    let height: CGFloat
    let horizontalPadding: CGFloat
    let foregroundColor: Color?
    let containerColor: Color?
    let shapeSpec: ShapeSpec
    
    var body: some View {
        HStack {
            StyledTextView(
                text: searchText,
                theme: theme,
                colorOverride: foregroundColor
            )
            Spacer()
            MenuItemUI(
                render: render,
                theme: theme,
                menuItem: clearIcon
            )
        }
        .frame(height: height)
        .padding(.horizontal, horizontalPadding)
        .background(containerColor)
        .applyClipShapes(ShapeMapper.map(shapeSpec: shapeSpec))
    }
}
