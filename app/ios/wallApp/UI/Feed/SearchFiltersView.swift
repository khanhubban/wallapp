//
//  SearchFiltersView.swift
//  WallApp
//

import WallApp
import SwiftUI

struct SearchFiltersView: View {
    let render: RenderIos
    let theme: Theme
    let viewState: SearchInputViewState.Data
    let footerContainerColor: Color

    var body: some View {
        let paddingDefault = render.defaultViewSpec.paddingDefault.toCGFloat()
        let paddingLarge = render.defaultViewSpec.paddingLarge.toCGFloat()
        let selectionPadding = paddingDefault + paddingLarge
        let filterItemPaddingDefault = (render.defaultViewSpec.paddingDefault / 2).toCGFloat()
        let searchColorHorizontalPadding = selectionPadding + filterItemPaddingDefault

        let colorsViewState = viewState.searchColors
        let searchFilterGroup = viewState.searchFilterGroup
        let contentCategoryGroup = viewState.contentCategoryGroup

        VStack(spacing: 0) {
            SearchColorsView(
                render: render,
                theme: theme,
                viewState: colorsViewState
            )
            .padding(.top, paddingDefault)
            .padding(.horizontal, searchColorHorizontalPadding)

            Spacer().frame(height: paddingDefault)

            SelectionGroupView(
                render: render,
                theme: theme,
                viewState: searchFilterGroup
            )
            .padding(.horizontal, selectionPadding)

            Spacer().frame(height: paddingDefault)

            VStack {
                Spacer().frame(height: paddingDefault)

                SelectionGroupView(
                    render: render,
                    theme: theme,
                    viewState: contentCategoryGroup
                )
                .padding(.horizontal, selectionPadding)
            }
            .background(footerContainerColor)
        }
        .frame(width: render.windowFrame.deviceWidth.toCGFloat())
    }
}
