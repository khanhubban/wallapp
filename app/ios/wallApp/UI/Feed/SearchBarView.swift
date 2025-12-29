//
//  SearchBarTextView.swift
//  WallApp
//

import WallApp
import SwiftUI

struct SearchBarView: View {
    let render: RenderIos
    let theme: Theme
    let viewState: SearchBarViewState
    let height: CGFloat
    let paddingHorizontal: CGFloat
    let visibilityProgress: Float?
    let canFocus: Bool

    var body: some View {
        let eventSink = viewState.eventSink
        let searchHint = viewState.searchHint
        let searchIcon = viewState.searchIcon
        let query = viewState.query
        let showClearIcon = viewState.showClearIcon
        let clearIcon = viewState.clearIcon

        let recipeBinState = viewState.recipeBinViewState

        ZStack {
            if let recipeBinState = recipeBinState {
                let containerColor = viewState.containerColor.toColor(themeColors: theme.themeColors)
                let foregroundColor = theme.themeColors.onSurface?.color.toColor()
                SearchRecipeBinBarView(
                    render: render,
                    theme: theme,
                    viewState: recipeBinState,
                    containerColor: containerColor,
                    foregroundColor: foregroundColor,
                    visibilityProgress: visibilityProgress
                )
                .padding(.horizontal, paddingHorizontal)
            } else {
                SearchBarTextView(
                    render: render,
                    theme: theme,
                    query: Binding(
                        get: { query ?? "" },
                        set: { eventSink(SearchViewEvent.QueryChange(query: $0)) }
                    ),
                    searchHint: searchHint,
                    eventSink: eventSink,
                    searchIcon: searchIcon,
                    showClearIcon: showClearIcon,
                    clearIcon: clearIcon,
                    shape: viewState.searchInputViewShape,
                    searchFieldTextStyle: viewState.searchFieldTextStyle,
                    height: height,
                    paddingHorizontal: paddingHorizontal,
                    backgroundColor: viewState.containerColor.toColor(themeColors: theme.themeColors),
                    canFocus: canFocus
                )
            }
        }
        .frame(height: height)
    }
}

struct SearchBarTextView: View {
    let render: RenderIos
    let theme: Theme
    @Binding var query: String?
    let searchHint: StyledText
    let eventSink: (SearchViewEvent) -> Void
    let searchIcon: MenuItem
    let showClearIcon: Bool
    let clearIcon: MenuItem
    let shape: ShapeSpec
    let searchFieldTextStyle: TextStyle
    let height: CGFloat
    let paddingHorizontal: CGFloat
    let backgroundColor: Color?
    let canFocus: Bool
    
    @FocusState private var isFocused: Bool
    @State private var text: String = ""
    @State private var isKeyboardOpen: Bool = false
    
    var body: some View {
        let paddingDefault = render.defaultViewSpec.paddingDefault.toCGFloat()
        let paddingLarge = render.defaultViewSpec.paddingLarge.toCGFloat()
        let textFieldWidth = render.windowFrame.deviceWidth.toCGFloat() - (paddingHorizontal * 2)
        
        let foregroundColor = theme.themeColors.onBackground?.color.toColor() ?? Color.black
        
        let modifiedClearIcon: MenuItem = modifiedClearIcon(clearIcon)
        
        HStack {
            ZStack(alignment: .leading) {
                if text.isEmpty {
                    StyledTextView(text: searchHint, theme: theme, colorOverride: Colors.shared.Gray.toColor())
                }
                TextField(
                    " ",
                    text: $text,
                    onEditingChanged: { focused in
                        if canFocus {
                            isFocused = focused
                            eventSink(SearchViewEvent.QueryFocused(focused: focused))
                        }
                    },
                    onCommit: {
                        eventSink(SearchViewEvent.QuerySubmit(query: text))
                    }
                )
                .setTextStyle(searchFieldTextStyle)
                .accentColor(foregroundColor)
                .foregroundColor(foregroundColor)
                .focused($isFocused)
            }
            .frame(height: height - paddingDefault * 2)
            .onChange(of: text) { newValue in
                eventSink(SearchViewEvent.QueryChange(query: newValue))
            }
            
            if showClearIcon {
                MenuItemUI(
                    render: render,
                    theme: theme,
                    menuItem: modifiedClearIcon
                )
            } else {
                let iconSize = ((searchIcon as? MenuItem.MenuItemIcon)?.width?.toCGFloat() ?? 0) * 0.8
                Image(systemName: "magnifyingglass")
                    .foregroundColor(foregroundColor)
                    .font(.system(size: iconSize, weight: .semibold))
            }
        }
        .padding(.horizontal, paddingLarge)
        .padding(.vertical, paddingDefault)
        .background(backgroundColor)
        .applyClipShapes(shape)
        .frame(width: textFieldWidth, height: height)
        .onAppear {
            // focus the search field when opened with a query
            if canFocus && query != nil && !query!.isEmpty {
                isFocused = true
            }
            text = query ?? ""
        }
        .onChange(of: query) { newQuery in
            if let newQuery, newQuery != text {
                text = newQuery
            }
        }
    }
    
    func modifiedClearIcon(_ clearIcon: MenuItem) -> MenuItem {
        if let clearIcon = clearIcon as? MenuItem.MenuItemButton {
            return MenuItem.MenuItemButton(
                button: ButtonViewState(
                    menuItem: clearIcon.button.menuItem,
                    animatedViewSpec: clearIcon.button.animatedViewSpec,
                    shapeSpec: clearIcon.button.shapeSpec,
                    buttonAppearance: clearIcon.button.buttonAppearance,
                    containerColorToken: clearIcon.button.containerColorToken,
                    eventHandler: ViewEventHandlerCompanion().createOnClick {
                        text = ""
                        isFocused = false
                        eventSink(SearchViewEvent.CloseSearch())
                    } as ViewEventHandler
                ),
                width: clearIcon.width,
                height: clearIcon.height
            )
        }
        return clearIcon
    }
}
