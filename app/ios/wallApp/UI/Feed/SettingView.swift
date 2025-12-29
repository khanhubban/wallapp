//
//  SettingView.swift
//  WallApp
//

import WallApp
import SwiftUI



struct SettingGroupView: View {
    let render: RenderIos
    let theme: Theme
    let settingGroupViewState: SettingGroupViewState
    
    var body: some View {
        VStack(spacing: 0) {
            ForEach(settingGroupViewState.settingViewStates.indices, id: \.self) { index in
                SettingView(render: render, theme: theme, settingViewState: settingGroupViewState.settingViewStates[index])
            }
        }.listRowBackground(Color.clear)
    }
}

struct SettingView: View {
    
    let render: RenderIos
    let theme: Theme
    let settingViewState: SettingViewState
    
    let edgeInsets = EdgeInsets(top: 8, leading: 32, bottom: 8, trailing: 32)
    
    var body: some View {
        switch onEnum(of: settingViewState) {
        case .switch(let settingSwitch):
            SettingSwitchView(render: render, theme: theme, viewState: settingSwitch, padding: edgeInsets)
        case .detail(let detail):
            SettingDetailView(render: render, theme: theme, viewState: detail, padding: edgeInsets)
        case .divider(let divider):
            SettingDividerView(render: render, theme: theme, viewState: divider)
        case .footer(let footer):
            SettingFooterView(render: render, theme: theme, viewState: footer)
        case .heading(let heading):
            SettingHeadingView(render: render, theme: theme, viewState: heading, padding: edgeInsets)
        case .itemPreviewRowViewState(let itemPreviewRowViewState):
            SettingItemPreviewRowView(render: render, theme: theme, viewState: itemPreviewRowViewState, padding: edgeInsets)
        case .spacer(let spacer):
            SettingSpacerView(render: render, theme: theme, spacer: spacer)
        case .switchMutable(_):
            // Ignore this for now
            EmptyView()
        case .viewStateWrapper(let viewState):
            RenderedView(render: render, theme: theme, view: CommonView(viewState: viewState))
        }
    }
    
    @ViewBuilder
    func dummyView() -> some View {
        Rectangle()
            .fill(Color.gray)
            .frame(height: 40)
    }
}

struct SettingDetailView: View {
    let render: RenderIos
    let theme: Theme
    let viewState: SettingViewState.Detail
    let padding: EdgeInsets
    
    var body: some View {
        let title = viewState.title
        let summary = viewState.summary
        let onClick = viewState.onClick
        let minHeight = summary != nil ? 64 : 56
        
        HStack(alignment: .center, spacing: 0) {
            VStack(alignment: .leading, spacing: 0) {
                StyledTextView(text: title, theme: self.theme)
                if let summary {
                    Spacer().frame(height: 4.0)
                    StyledTextView(text: summary, theme: self.theme)
                }
            }
            Spacer()
        }
        .padding(padding)
        .frame(minHeight: minHeight.toCGFloat())
        .frame(maxWidth: .infinity)
        .contentShape(Rectangle()) // this is needed to extend the tap area for the HStack - https://stackoverflow.com/a/58138763/3708321
        .if (onClick != nil) { $0.onTapGesture { onClick!.invoke() } }
    }
}

struct SettingDividerView: View {
    let render: RenderIos
    let theme: Theme
    let viewState: SettingViewState.Divider
    
    var body: some View {
        Divider()
            .padding(.all, viewState.inset.toCGFloat())
    }
}

struct SettingItemPreviewRowView: View {
    let render: RenderIos
    let theme: Theme
    let viewState: SettingViewState.ItemPreviewRowViewState
    let padding: EdgeInsets
    
    var paddingDefault: CGFloat {
        render.defaultViewSpec.paddingDefault.toCGFloat()
    }
    
    var scrolling: Bool {
        viewState.scrolling
    }
    
    @ViewBuilder
    var body: some View {
        let title = viewState.title
        let viewSpec = viewState.viewSpec
        let paddingHorizontal = viewSpec?.horizontalPadding.toCGFloat() ?? 0
        let paddingVertical = viewSpec?.verticalPadding.toCGFloat() ?? 0
        let contentPaddingHorizontal = paddingHorizontal / 2
        
        VStack(alignment: .leading, spacing: 0) {
            if let title {
                StyledTextView(text: title, theme: self.theme)
                    .padding(.horizontal, paddingHorizontal)
                Spacer().frame(height: paddingDefault)
            }
            ZStack {
                ZStack {
                    if scrolling {
                        ScrollView(.horizontal, showsIndicators: false) {
                            childViews(paddingHorizontal: contentPaddingHorizontal)
                        }
                    } else {
                        childViews(paddingHorizontal: contentPaddingHorizontal)
                            .frame(maxWidth: .infinity)
                    }
                }
                .padding(.horizontal, contentPaddingHorizontal)
                if let edgeFade = viewState.edgeFade {
                    EdgeFadeView(edgeFade: edgeFade, theme: theme, render: render)
                }
            }
        }
        .padding(.vertical, paddingVertical)
    }
    
    @ViewBuilder
    func childViews(paddingHorizontal: CGFloat = 0) -> some View {
        //FIXME: Optimise code when we have itempreviewstate access, create seprate class for the same.
        /// As we do not have access of itempreviewstate we have to create buttons here
        
        let itemPreviews = viewState.itemPreviews
        let items = itemPreviews.enumerated().map { return ($0.offset, $0.element) }
        let itemSpacing = viewState.viewSpec?.itemSpacing?.toCGFloat() ?? paddingDefault
        HStack(spacing: 0) {
            if paddingHorizontal > 0 {
                Spacer().frame(width: paddingHorizontal)
            }
            ForEach(items, id: \.0) { item in
                let index = item.0
                let content = itemPreviews[index]
                let paddingDefault = render.defaultViewSpec.paddingDefault.toCGFloat()
                let buttonPadding = EdgeInsets(
                    top: paddingDefault,
                    leading: paddingDefault / 2,
                    bottom: paddingDefault,
                    trailing: paddingDefault / 2
                )
                
                let color = self.theme.themeColors.background.toColor()
                let onColor = self.theme.themeColors.onBackground?.toColor()
                let isSelected =  index == viewState.currentIndex
                let contentColor = color
                let backGroundColor = onColor
                let contentItem = content.item
                
                let viewSpec = content.viewSpec
                let itemWidth = viewSpec.width.toCGFloat()
                let itemHeight = viewSpec.height.toCGFloat()
                
                /// As we do not have access of itempreviewstate we have to create buttons here
                if content.style == SettingViewState.ItemPreviewStyle.button {
                    ZStack {
                        StyledTextView(
                            text: content.label!,
                            theme: theme,
                            colorOverride: isSelected ? contentColor : onColor
                        )
                        .padding(buttonPadding)
                    }
                    .frame(width: itemWidth, height: itemHeight)
                    .background(isSelected ? backGroundColor : contentColor)
                    .applyClipShapes(content.shapeSpec, borderWidth: 1.0, borderColor: isSelected ? .clear : .gray)
                    .onTapGesture {
                        viewState.onSelectedChanged(KotlinInt(integerLiteral: index))
                    }
                }
                
                /// As we do not have access of itempreviewstate we have to create buttons here
                if content.style == SettingViewState.ItemPreviewStyle.default {
                    VStack(spacing: 8.0) {
                        createView(isSelected: isSelected,contentItem: contentItem, itemWidth: itemWidth, itemHeight: itemHeight)
                            .frame(width: itemWidth, height: itemHeight)
                        // Not available in compose so commenting this, in future if you require lable comment this
                        /*
                         if let lable = content.label {
                         HStack{
                         Spacer()
                         StyledTextView(text: lable, theme: theme)
                         Spacer()
                         }.padding(.all,insetPadding.toCGFloat())
                         .applyClipShapes(content.shapeSpec)
                         }
                         */
                    }
                    .applyClipShapes(content.shapeSpec)
                    .onTapGesture(perform: { viewState.onSelectedChanged(KotlinInt(integerLiteral: index)) })
                }
                if index < items.count - 1 {
                    if scrolling {
                        Spacer().frame(width: itemSpacing)
                    } else {
                        Spacer()
                    }
                }
            }
            if paddingHorizontal > 0 {
                Spacer().frame(width: paddingHorizontal)
            }
        }
    }
    
    func createView(isSelected: Bool, contentItem: AnyObject?, itemWidth: CGFloat, itemHeight: CGFloat) -> some View {
        ZStack {
            if let image = contentItem as? SettingAppIconViewState {
                let borderImage = isSelected ? image.appIconPreviewSelected : image.appIconPreviewUnselected
                let inset = paddingDefault / 4
                let itemWidth = itemWidth - inset
                let itemHeight = itemHeight - inset
                let lockedImage = image.lockedImage
                SwiftUIImage(theme: theme, image: image.wallAppIcon, width: itemWidth, height: itemHeight)
                if let lockedImage {
                    SwiftUIImage(theme: theme, image: lockedImage, width: itemWidth, height: itemHeight)
                }
                if let borderImage {
                    SwiftUIImage(theme: theme, image: borderImage, width: itemWidth, height: itemHeight)
                }
            }
            
            if let image = contentItem as? ImageUI {
                SwiftUIImage(theme: theme, image: image)
            }
            
            if let shape = contentItem as? ClipShapeStyle {
                Rectangle().background(theme.themeColors.onBackground?.toColor())
            }
        }
    }
}


struct SettingFooterView: View {
    let render: RenderIos
    let theme: Theme
    let viewState: SettingViewState.Footer
    
    var body: some View {
        HStack {
            Spacer()
            VStack(alignment: .center) {
                ForEach(viewState.messages, id: \.self) { content in
                    StyledTextView(text: content, theme: self.theme)
                }
            }
            Spacer()
        }
        .padding(.all, render.defaultViewSpec.paddingDefault.toCGFloat())
    }
}

struct SettingHeadingView: View {
    let render: RenderIos
    let theme: Theme
    let viewState: SettingViewState.Heading
    let padding: EdgeInsets
    
    var body: some View {
        HStack {
            StyledTextView(text: viewState.title, theme: self.theme)
            Spacer()
        }
        .padding(padding)
    }
}

struct SettingSwitchView: View {
    let render: RenderIos
    let theme: Theme
    let viewState: SettingViewState.Switch
    let padding: EdgeInsets
    
    var body: some View {
        let checked = viewState.checked
        let onClick = viewState.onClicked
        let title = viewState.title
        let icon = viewState.icon
        let summary = viewState.summary
        SettingSwitch(
            theme: self.theme,
            padding: padding,
            checked: checked,
            onCheckedChange: { newValue in
                viewState.onCheckedChange(KotlinBoolean(bool: newValue))
            },
            onClick: onClick,
            title: title,
            summary: summary,
            icon: icon
        )
        .frame(maxWidth: .infinity)
    }
}

struct SettingSwitch: View {
    let theme: Theme
    var padding: EdgeInsets = EdgeInsets()
    var checked: Bool = true
    var showShimmer: Bool = false
    var onCheckedChange: ((Bool) -> Void)? = nil
    let onClick: () -> Void
    let title: StyledText
    let summary: StyledText?
    let icon: ImageUI?

    var body: some View {
        ZStack {
            HStack(spacing: 0) {
                VStack(alignment: .leading, spacing: 0) {
                    StyledTextView(text: title, theme: theme)
                        .multilineTextAlignment(.leading)
                    if let summary = summary {
                        Spacer().frame(height: 4)
                        StyledTextView(text: summary, theme: theme)
                    }
                }
                .layoutPriority(1)
                Spacer().frame(minWidth: 16)
                Toggle(isOn: Binding(
                    get: { self.checked },
                    set: { newValue in
                        self.onCheckedChange?(newValue)
                    }
                )) {
                    EmptyView()
                }
                .labelsHidden()
            }
            .frame(maxWidth: .infinity)
        }
        .frame(maxWidth: .infinity)
        .padding(padding)
        .contentShape(Rectangle())
        .onTapGesture {
            self.onClick()
        }
    }
}

struct SettingSpacerView: View {
    let render: RenderIos
    let theme: Theme
    let spacer: SettingViewState.Spacer
    
    var body: some View {
        Spacer()
            .frame(height: spacer.height.toCGFloat())
    }
}

struct SettingItemPreview: View {
    let render: RenderIos
    let theme: Theme
    let viewState: SettingViewState.ItemPreviewStyle
    
    var body: some View {
        
        ZStack {
            if viewState == SettingViewState.ItemPreviewStyle.default {
                
            }
            
            if viewState == SettingViewState.ItemPreviewStyle.button {
                EmptyView()
            }
        }
        
    }
}

struct SettingItemPreviewDefault: View {
    let render: RenderIos
    let theme: Theme
    
    
    var body: some View {
        HStack {
            
        }
    }
}

struct SettingItemImage: View {
    let render: RenderIos
    let shape: ShapeSpec
    let theme: Theme
    let viewState: ButtonViewState
    
    var body: some View {
        ButtonView(render: render, theme: theme, viewState: viewState)
    }
}

struct SettingItemShape: View {
    let render: RenderIos
    let shape: ShapeSpec
    let theme: Theme
    
    var body: some View {
        ZStack {
            
        }.applyClipShapes(shape)
            .background(theme.themeColors.onBackground?.toColor())
    }
}
