//
//  SelectionGroupView.swift
//  WallApp
//

import WallApp
import SwiftUI

struct SelectionGroupView: View {
    let render: RenderIos
    let theme: Theme
    let viewState: SelectionGroupViewState

    var body: some View {
        switch onEnum(of: viewState.viewSpec) {
        case .row(_):
            SelectionGroupRowView(render: render, theme: theme, viewState: viewState)
        case .grid(_):
            SelectionGroupGridView(render: render, theme: theme, viewState: viewState)
        }
    }
}

struct SelectionGroupRowView: View {
    let render: RenderIos
    let theme: Theme
    let viewState: SelectionGroupViewState

    var body: some View {
        HStack {
            ForEach(viewState.selections, id: \.self) { selection in
                SelectionView(render: render, theme: theme, viewState: selection)
            }
        }
    }
}

struct SelectionGroupGridView: View {
    let render: RenderIos
    let theme: Theme
    let viewState: SelectionGroupViewState

    var body: some View {
        let gridType = viewState.viewSpec as! SelectionGroupViewSpecGrid
        let itemWidth = viewState.viewSpec.itemWidth.toCGFloat()
        let itemHeight = viewState.viewSpec.itemHeight.toCGFloat()
        let horizontalItemSpacing = gridType.horizontalItemSpacing.toCGFloat()
        let verticalItemSpacing = gridType.verticalItemSpacing.toCGFloat()
        
        // This used to be a LazyVGrid + ForEach but it doesn't work with the search sheet transition. The offset applied to the parent sheet
        // does not get passed down to the LazyVGrid. The issue was with ForEach. This is an ugly workaround that uses a static Grid without ForEach.
        // It only supports 12 items.
        Grid(alignment: .center, horizontalSpacing: horizontalItemSpacing, verticalSpacing: verticalItemSpacing) {
            let selections = viewState.selections
            
            if selections.count > 0 {
                GridRow {
                    buildSelectionView(viewState: selections[0], itemWidth: itemWidth, itemHeight: itemHeight)
                    if selections.count > 1 {
                        buildSelectionView(viewState: selections[1], itemWidth: itemWidth, itemHeight: itemHeight)
                    }
                }
            }
            if selections.count > 2 {
                GridRow {
                    buildSelectionView(viewState: selections[2], itemWidth: itemWidth, itemHeight: itemHeight)
                    if selections.count > 3 {
                        buildSelectionView(viewState: selections[3], itemWidth: itemWidth, itemHeight: itemHeight)
                    }
                }
            }
            if selections.count > 4 {
                GridRow {
                    buildSelectionView(viewState: selections[4], itemWidth: itemWidth, itemHeight: itemHeight)
                    if selections.count > 5 {
                        buildSelectionView(viewState: selections[5], itemWidth: itemWidth, itemHeight: itemHeight)
                    }
                }
            }
            if selections.count > 6 {
                GridRow {
                    buildSelectionView(viewState: selections[6], itemWidth: itemWidth, itemHeight: itemHeight)
                    if selections.count > 7 {
                        buildSelectionView(viewState: selections[7], itemWidth: itemWidth, itemHeight: itemHeight)
                    }
                }
            }
            if selections.count > 8 {
                GridRow {
                    buildSelectionView(viewState: selections[8], itemWidth: itemWidth, itemHeight: itemHeight)
                    if selections.count > 9 {
                        buildSelectionView(viewState: selections[9], itemWidth: itemWidth, itemHeight: itemHeight)
                    }
                }
            }
            if selections.count > 10 {
                GridRow {
                    buildSelectionView(viewState: selections[10], itemWidth: itemWidth, itemHeight: itemHeight)
                    if selections.count > 11 {
                        buildSelectionView(viewState: selections[11], itemWidth: itemWidth, itemHeight: itemHeight)
                    }
                }
            }
        }
    }
    
    @ViewBuilder
    func buildSelectionView(viewState: SelectionViewState, itemWidth: CGFloat, itemHeight: CGFloat) -> some View {
        SelectionView(
            render: render,
            theme: theme,
            viewState: viewState
        )
        .frame(minWidth: itemWidth)
        .frame(height: itemHeight, alignment: .leading)
    }
}

