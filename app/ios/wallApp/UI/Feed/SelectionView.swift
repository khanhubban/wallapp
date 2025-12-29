//
//  SelectionView.swift
//  WallApp
//

import WallApp
import SwiftUI

struct SelectionView: View {
    let render: RenderIos
    let theme: Theme
    let viewState: SelectionViewState

    var body: some View {
        let menuItem = viewState.menuItem
        let selected = viewState.selected

        let onCheckedChange: (Bool) -> Void = { checked in
            viewState.eventSink(SelectionViewEvent(key: viewState.key, selected: checked))
        }
        
        if viewState.selectionViewStyle == .radio {
            SelectionRadioView(
                render: render,
                theme: theme,
                menuItem: menuItem,
                selected: selected,
                onCheckedChange: onCheckedChange,
                shape: viewState.shape,
                centerHorizontally: viewState.centerHorizontally
            )
        } else if viewState.selectionViewStyle == .button {
            SelectionButtonView(
                render: render,
                theme: theme,
                menuItem: menuItem,
                selected: selected,
                onCheckedChange: onCheckedChange,
                shape: viewState.shape
            )
        }
    }
}

struct SelectionRadioView: View {
    let render: RenderIos
    let theme: Theme
    let menuItem: MenuItem
    let selected: Bool
    let onCheckedChange: (Bool) -> Void
    let shape: ShapeSpec
    let centerHorizontally: Bool

    var body: some View {
        let paddingDefault = (render.defaultViewSpec.paddingDefault / 2).toCGFloat()

        HStack(alignment: .center, spacing: 0) {
            if centerHorizontally {
                Spacer()
            }
            
            BasicCheckboxView(
                render: render,
                theme: theme,
                checked: selected,
                shape: shape,
                onCheckedChange: onCheckedChange
            )
            
            Spacer().frame(width: paddingDefault)
            
            MenuItemUI(
                render: render,
                theme: theme,
                menuItem: menuItem
            )
            
            if centerHorizontally {
                Spacer()
            }
        }
        .onTapGesture {
            onCheckedChange(!selected)
        }
    }
}

struct SelectionButtonView: View {
    let render: RenderIos
    let theme: Theme
    let menuItem: MenuItem
    let selected: Bool
    let onCheckedChange: (Bool) -> Void
    let shape: ShapeSpec

    var body: some View {
        let paddingSmall = render.defaultViewSpec.paddingSmall.toCGFloat()
        let padding = paddingSmall / 2
        
        let selectedBackgroundColor = theme.themeColors.primary.toColor()
        let selectedForegroundColor = theme.themeColors.onPrimary?.toColor()
        let unselectedBackgroundColor = theme.themeColors.background.toColor()
        let unselectedForegroundColor = theme.themeColors.onBackground?.toColor()

        ZStack {
            MenuItemUI(
                render: render,
                theme: theme,
                menuItem: menuItem,
                overrideColor: selected ? selectedForegroundColor : unselectedForegroundColor
            )
        }
        .padding(.all, padding)
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(selected ? selectedBackgroundColor : unselectedBackgroundColor)
        .foregroundColor(selected ? selectedForegroundColor : unselectedForegroundColor)
        .applyClipShapes(shape)
        .onTapGesture {
            onCheckedChange(!selected)
        }
        
    }
}
