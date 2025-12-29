//
//  ExploreHeaderView.swift
//  WallApp
//

import WallApp
import SwiftUI

// DEPRECATED
struct ExploreHeaderView: View {
    let render: RenderIos
    let theme: Theme
    let viewState: ExploreHeaderViewState
    let scrollOffsetController: ScrollOffsetController?
    
    @State var headerHeight: CGFloat = 0
    
    var body: some View {
        let foregroundColor = theme.themeColors.onBackground?.color.toColor()
        let containerColor = viewState.containerColor.toColor()
        let paddingDefault = render.defaultViewSpec.paddingDefault.toCGFloat()
        if let scrollOffsetController {
            ZStack(alignment: .top) {
                StatusBarView(render: render, theme: theme, color: containerColor)
                    .zIndex(1.0)
                VStack(spacing: 0) {
                    StatusBarView(render: render, theme: theme, color: Color.clear)
//                    MessageBarView(render: render, theme: theme, viewState: viewState.messageBar)
                    HStack(alignment: .center) {
                        Image(systemName: "magnifyingglass")
                            .if (foregroundColor != nil) { $0.foregroundColor(foregroundColor!) }
                        MenuItemUI(render: render, theme: theme, menuItem: viewState.searchLabel)
                    }
                    .padding(.horizontal, paddingDefault * 2)
                    .padding(.vertical, paddingDefault / 2)
                }
                .frame(maxWidth: .infinity)
                .frame(height: headerHeight)
                .background(containerColor)
                .offset(y: scrollOffsetController.offset)
                .animation(.spring(duration: 0.2), value: scrollOffsetController.offset)
                .onAppear {
                    headerHeight = calculateHeaderHeight(viewState: viewState)
                }
                .onChange(of: headerHeight) { newHeight in
                    scrollOffsetController.update(maxOffset: newHeight)
                }
            }
            .onTapGesture {
                viewState.searchOnClick()
            }
        }
    }
    
    private func calculateHeaderHeight(viewState: ExploreHeaderViewState) -> CGFloat {
        return viewState.viewSpec.height.toCGFloat()
    }
}
