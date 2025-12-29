//
//  MenuItemUIContent.swift
//  WallApp
//

import WallApp
import SwiftUI

struct MenuItemsView: View {
    let render: RenderIos
    let theme: Theme
    let viewState: MenuItemViewState
    let ignoreInherentSize: Bool = false
    
    var body: some View {
        let menuItems = viewState.menuItems
        if menuItems.count == 1 {
            MenuItemUI(render: render, theme: theme, menuItem: menuItems[0], ignoreInherentSize: ignoreInherentSize)
        } else if menuItems.count > 1 {
            VStack(alignment: viewState.centerItems ? .center : .leading) {
                ForEach(menuItems, id: \.self) { menuItem in
                    MenuItemUI(render: render, theme: theme, menuItem: menuItem, ignoreInherentSize: ignoreInherentSize)
                }
            }
        }
    }
}

struct MenuItemUI: View {
    let render: RenderIos
    let theme: Theme
    let menuItem: MenuItem
    var ignoreInherentSize: Bool = false
    var width: CGFloat? = nil
    var height: CGFloat? = nil
    var overrideColor: Color? = nil
    
    var body: some View {
        switch menuItem {
        case let menuItem as MenuItem.MenuItemImage:
            MenuItemImageUI(render: render, theme: theme, menuItemImage: menuItem, width: width, height: height)
        case let menuItem as MenuItem.MenuItemLabel:
            MenuItemLabelUI(theme: theme, menuItemLabel: menuItem, ignoreInherentSize: ignoreInherentSize, overrideColor: overrideColor)
        case let menuItem as MenuItem.MenuItemIcon:
            MenuItemIconUI(render: render, theme: theme, menuItemIcon: menuItem)
        case let menuItem as MenuItem.MenuItemSpacer:
            MenuItemSpacerUI(menuItemSpacer: menuItem)
        case let menuItem as MenuItem.MenuItemGroup:
            MenuItemGroupUI(render: render, theme: theme, menuItemGroup: menuItem, width: width, height: height)
        case let menuItem as MenuItem.MenuItemButton:
            MenuItemButtonUI(render: render, theme: theme, menuItemButton: menuItem, width: width, height: height)
        case let menuItem as MenuItem.MenuItemProgressButton:
            MenuItemProgressButtonUI(render: render, theme: theme, menuItem: menuItem, width: width, height: height)
        case let menuItem as MenuItemMenuItemViewStateWrapper<AnyObject>:
            MenuItemViewStateWrapperUI(render: render, theme: theme, menuItemViewStateWrapper: menuItem)
        default:
            EmptyView()
        }
    }
}

struct MenuItemImageUI: View {
    let render: RenderIos
    let theme: Theme
    let menuItemImage: MenuItem.MenuItemImage
    var width: CGFloat? = nil
    var height: CGFloat? = nil
    
    var body: some View {
        let imageViewState = menuItemImage.imageViewState
        let image = imageViewState
        let onClick = menuItemImage.onClick
        let width = width ?? imageViewState.viewSpec.width.toCGFloat()
        let height = height ?? imageViewState.viewSpec.width.toCGFloat()
        
        SwiftUIImage(      
            theme: theme,
            image: image.image,
            width: width,
            height: height,
            imageHashDecoder: render.imageHashDecoder
        )
        .if (onClick != nil) {
            $0.onTapGesture {
                onClick!.invoke()
            }
        }
    }
}

struct MenuItemLabelUI: View {
    let theme: Theme
    let menuItemLabel: MenuItem.MenuItemLabel
    let ignoreInherentSize: Bool
    let overrideColor: Color?
    
    var body: some View {
        let text = menuItemLabel.text
        let onClick = menuItemLabel.onClick
        let width = menuItemLabel.width as? Width.WidthDp
        let height = menuItemLabel.height as? Height.HeightDp
        let foregroundOverrideColor = overrideColor
        let foregroundDefaultColor = text.colorToken?.toColor(themeColors: theme.themeColors) ?? theme.themeColors.onBackground?.color.toColor()
        
        // TODO: add proper alignment via the MenuItemLabel.textAlignment
        ZStack(alignment: .center) {
            StyledTextView(text: text, theme: theme, colorOverride: foregroundOverrideColor)
            // This is needed for wallpaper showcase as the colorOverride is not working in StyledTextView
                .if (foregroundDefaultColor != nil) { $0.foregroundColor(foregroundDefaultColor!) }
        }
        .if (ignoreInherentSize) { $0.frame(maxWidth: .infinity, maxHeight: .infinity) }
        .if (width != nil && !ignoreInherentSize) { $0.frame(width: width!.dp.toCGFloat()) }
        .if (height != nil && !ignoreInherentSize) { $0.frame(height: height!.dp.toCGFloat()) }
        .if (onClick != nil) { $0.onTapGesture { onClick!.invoke() } }
    }
}

struct MenuItemIconUI: View {
    let render: RenderIos
    let theme: Theme
    let menuItemIcon: MenuItem.MenuItemIcon
    
    var body: some View {
        let icon = menuItemIcon.icon
        let onClick = menuItemIcon.onClick
        let width = menuItemIcon.width?.toCGFloat()
        let height = menuItemIcon.height?.toCGFloat()
        let color = menuItemIcon.tintColor?.toColor(themeColors: theme.themeColors)
        let iconButtonLayerSize = render.defaultViewSpec.iconButtonLayerSize.toCGFloat()
        
        if let onClick {
            Button(action: { onClick.invoke() }) {
                SwiftUIImage(theme: theme, image: icon, width: width, height: height, tintColor: color)
            }
            .frame(width: iconButtonLayerSize, height: iconButtonLayerSize)
        } else {
            SwiftUIImage(theme: theme, image: icon, width: width, height: height, tintColor: color)
        }
    }
}

struct MenuItemSpacerUI: View {
    let menuItemSpacer: MenuItem.MenuItemSpacer
    
    var body: some View {
        Spacer()
            .if (menuItemSpacer.width != nil) { $0.frame(width: menuItemSpacer.width!.toCGFloat()) }
            .if (menuItemSpacer.height != nil) { $0.frame(height: menuItemSpacer.height!.toCGFloat()) }
    }
}

struct MenuItemGroupUI: View {
    let render: RenderIos
    let theme: Theme
    let menuItemGroup: MenuItem.MenuItemGroup
    let width: CGFloat?
    let height: CGFloat?
    
    var body: some View {
        switch menuItemGroup {
        case is MenuItem.MenuItemGroupHorizontal:
            MenuItemGroupHorizontalUI(render: render, theme: theme, menuItemGroupHorizontal: menuItemGroup as! MenuItem.MenuItemGroupHorizontal, width: width, height: height)
        case is MenuItem.MenuItemGroupVertical:
            MenuItemGroupVerticalUI(render: render, theme: theme, menuItemGroupVertical: menuItemGroup as! MenuItem.MenuItemGroupVertical, width: width, height: height)
        default:
            EmptyView()
        }
    }
}

struct MenuItemGroupHorizontalUI: View {
    let render: RenderIos
    let theme: Theme
    let menuItemGroupHorizontal: MenuItem.MenuItemGroupHorizontal
    var width: CGFloat?
    var height: CGFloat?
    
    var body: some View {
        let menuItems = menuItemGroupHorizontal.menuItems
        let height = height ?? menuItemGroupHorizontal.height.toCGFloat()
        let width = width ?? menuItemGroupHorizontal.width?.toCGFloat()
        
        ZStack(alignment: .center) {
            HStack(spacing: 0) {
                ForEach(menuItems.indices, id: \.self) { index in
                    let menuItem = menuItems[index]
                    MenuItemUI(render: render, theme: theme, menuItem: menuItem)
                }
            }
        }
        .frame(width: width, height: height)
    }
}

struct MenuItemGroupVerticalUI: View {
    let render: RenderIos
    let theme: Theme
    let menuItemGroupVertical: MenuItem.MenuItemGroupVertical
    var width: CGFloat?
    var height: CGFloat?
    
    var body: some View {
        let menuItems = menuItemGroupVertical.menuItems
        let width = width ?? menuItemGroupVertical.width?.toCGFloat()
        let height = height ?? menuItemGroupVertical.height?.toCGFloat()
        
        ZStack(alignment: .center) {
            VStack(spacing: 0) {
                ForEach(menuItems.indices, id: \.self) { index in
                    let menuItem = menuItems[index]
                    MenuItemUI(render: render, theme: theme, menuItem: menuItem)
                }
            }
        }
        .frame(width: width, height: height)
    }
}

struct MenuItemButtonUI: View {
    let render: RenderIos
    let theme: Theme
    let menuItemButton: MenuItem.MenuItemButton
    var width: CGFloat? = nil
    var height: CGFloat? = nil
    
    var body: some View {
        let button = menuItemButton.button
        let width = menuItemButton.width?.toCGFloat()
        let height = menuItemButton.height?.toCGFloat()
        
        ButtonView(render: render, theme: theme, viewState: button, width: width, height: height)
            .frame(width: width, height: height)
    }
}

struct MenuItemProgressButtonUI: View {
    let render: RenderIos
    let theme: Theme
    let menuItem: MenuItem.MenuItemProgressButton
    var width: CGFloat? = nil
    var height: CGFloat? = nil
    
    var body: some View {
        let backgroundColor = theme.themeColors.tertiary?.toColor()
        let progressColor = theme.themeColors.secondary.toColor()
        let progress = menuItem.progress.toCGFloat()
        let width = menuItem.width?.toCGFloat()
        let height = menuItem.height?.toCGFloat()
        let overlayText = menuItem.overlayText
        let shapeSpec = menuItem.shapeSpec
        let overlayTextColor = theme.themeColors.onSurface?.color.toColor()
        
        GeometryReader { geometry in
            ZStack(alignment: .leading) {
                Rectangle()
                    .fill(progressColor)
                    .frame(width: geometry.size.width * progress)
                    .frame(maxHeight: .infinity)
                HStack {
                    Spacer()
                    StyledTextView(text: overlayText, theme: theme, colorOverride: overlayTextColor)
                    Spacer()
                }
            }
            
        }
        .frame(width: width, height: height)
        .background(backgroundColor)
        .if (shapeSpec != nil) { $0.applyClipShapes(shapeSpec!) }
    }
}

struct MenuItemViewStateWrapperUI: View {
    let render: RenderIos
    let theme: Theme
    let menuItemViewStateWrapper: MenuItemMenuItemViewStateWrapper<AnyObject>
    
    var body: some View {
        let viewState = menuItemViewStateWrapper.viewState
        let width = menuItemViewStateWrapper.width?.toCGFloat()
        let height = menuItemViewStateWrapper.height?.toCGFloat()
        
        if let viewState = viewState as? ViewState {
            RenderedView(render: render, theme: theme, view: CommonView(viewState: viewState))
                .frame(width: width, height: height)
        }
    }
}
