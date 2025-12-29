//
//  EntitlementButtonView.swift
//  WallApp
//

import WallApp
import SwiftUI

struct EntitlementButtonContainerView: View {
    let render: RenderIos
    let theme: Theme
    let viewState: CollectionActionButtonViewState
    let progress: CGFloat
    
    var body: some View {
        let paddingSmall = render.defaultViewSpec.paddingSmall.toCGFloat()
        let statusBarHeight = render.windowFrame.statusBarHeight.toCGFloat()
        let entitlementButtonTopPaddingMin: CGFloat = paddingSmall
        let entitlementButtonTopPaddingMax = viewState.viewSpec.animatedViewSpec.paddingMax?.top.toCGFloat() ?? 0
        let entitlementButtonTopPadding = lerp(entitlementButtonTopPaddingMin, entitlementButtonTopPaddingMax, progress)
        let entitlementButtonStartPaddingMin: CGFloat = 0
        let entitlementButtonStartPaddingMax = viewState.viewSpec.animatedViewSpec.paddingMax?.start.toCGFloat() ?? 0
        let entitlementButtonStartPadding = lerp(entitlementButtonStartPaddingMin, entitlementButtonStartPaddingMax, progress)
        let entitlementButtonEndPaddingMin = viewState.viewSpec.animatedViewSpec.paddingMin?.end.toCGFloat() ?? 0
        let entitlementButtonEndPaddingMax = viewState.viewSpec.animatedViewSpec.paddingMax?.end.toCGFloat() ?? 0
        let entitlementButtonEndPadding = lerp(entitlementButtonEndPaddingMin, entitlementButtonEndPaddingMax, progress)
        EntitlementButtonView(
            render: render,
            theme: theme,
            entitlementItem: viewState,
            progress: progress
        )
        .padding(.vertical, entitlementButtonTopPadding + statusBarHeight)
        .padding(.leading, entitlementButtonStartPadding)
        .padding(.trailing, entitlementButtonEndPadding)
    }
}

struct EntitlementButtonView: View {
    let render: RenderIos
    let theme: Theme
    let entitlementItem: CollectionActionButtonViewState
    let progress: CGFloat
    
    var body: some View {
        let buttonItems = entitlementItem.getButtonItems()
        let image = buttonItems.first!
        let text = buttonItems.second!
        let textAlt = buttonItems.third!
        let shape = entitlementItem.buttonViewState.shapeSpec
        let eventHandler = entitlementItem.buttonViewState.eventHandler
        
        if let viewSpec = entitlementItem.viewSpec as? CollectionActionButtonViewSpec.BuyCollectionActionButtonViewSpec {
            BuyCollectionButtonView(
                render: render,
                theme: theme,
                viewSpec: viewSpec,
                image: image,
                label: text,
                priceLabel: textAlt,
                shape: shape,
                eventHandler: eventHandler,
                progress: progress
            )
        } else if let viewSpec = entitlementItem.viewSpec as? CollectionActionButtonViewSpec.GetCollectionActionButtonViewSpec {
            GetCollectionButtonView(
                render: render,
                theme: theme,
                viewSpec: viewSpec,
                image: image,
                label: text,
                label2: textAlt,
                shape: shape,
                eventHandler: eventHandler,
                progress: progress
            )
        } else if let viewSpec = entitlementItem.viewSpec as? CollectionActionButtonViewSpec.DownloadProgressButtonViewSpec {
            CollectionDownloadProgressButtonView(
                render: render,
                theme: theme,
                viewSpec: viewSpec,
                progressButton: entitlementItem as! CollectionActionButtonViewState.DownloadProgressButton,
                visibilityProgress: progress,
                shape: shape
            )
        }
    }
}

struct BuyCollectionButtonView: View {
    let render: RenderIos
    let theme: Theme
    let viewSpec: CollectionActionButtonViewSpec.BuyCollectionActionButtonViewSpec
    let image: MenuItem
    let label: StyledText
    let priceLabel: StyledText
    let shape: ShapeSpec?
    let eventHandler: ViewEventHandler?
    let progress: CGFloat
    
    @State private var labelWidth: CGFloat = 0

    var body: some View {
        let alpha = arbitrateToolbarContentAnimatedAlpha(animationProgress: Float(progress))
        let width = lerp(viewSpec.animatedViewSpec.widthMin?.toCGFloat() ?? 0,
                         viewSpec.animatedViewSpec.widthMax?.toCGFloat() ?? 0, progress)
        let backgroundColor = theme.themeColors.primary.toColor()
        let height = viewSpec.animatedViewSpec.heightMax?.toCGFloat()
        let paddingDefault = render.defaultViewSpec.paddingDefault.toCGFloat()

        HStack(spacing: 0) {
            HStack(spacing: 0) {
                Spacer()
                
                MenuItemUI(render: render, theme: theme, menuItem: image)
                
                Spacer()
                    .frame(width: paddingDefault)
                
                StyledTextView(text: label, theme: theme)
                    .opacity(Double(alpha))
                    .if (progress < 1) { $0.frame(width: labelWidth * progress) }
                    .background(
                        GeometryReader { geometry in
                            Color.clear
                                .preference(key: ViewWidthKey.self, value: geometry.size.width)
                        }
                    )
                    .onPreferenceChange(ViewWidthKey.self) { width in
                        if self.labelWidth == 0 {
                            self.labelWidth = width
                        }
                    }
                
                Spacer()
                    .frame(width: paddingDefault * progress)
                
                StyledTextView(text: priceLabel, theme: theme)
                
                Spacer()
            }
            .frame(width: width, height: height)
            .background(backgroundColor)
            .if(shape != nil) { $0.applyClipShapes(shape!) }
            .onTapGesture {
                eventHandler?.invoke()
            }
        }
        .frame(maxWidth: .infinity, alignment: .trailing)
        .frame(height: height)
    }
}
// Preference key to measure the label's width
struct ViewWidthKey: PreferenceKey {
    static var defaultValue: CGFloat = 0
    static func reduce(value: inout CGFloat, nextValue: () -> CGFloat) {
        value = nextValue()
    }
}


struct GetCollectionButtonView: View {
    let render: RenderIos
    let theme: Theme
    let viewSpec: CollectionActionButtonViewSpec.GetCollectionActionButtonViewSpec
    let image: MenuItem
    let label: StyledText
    let label2: StyledText
    let shape: ShapeSpec?
    let eventHandler: ViewEventHandler?
    let progress: CGFloat
    
    var body: some View {
        let alpha = arbitrateToolbarContentAnimatedAlpha(animationProgress: Float(progress)).toCGFloat()
        let imagePaddingStartMin = viewSpec.iconPaddingStartMin.toCGFloat()
        let imagePaddingStartMax = viewSpec.iconPaddingStartMax.toCGFloat()
        let imagePaddingStart = lerp(imagePaddingStartMin, imagePaddingStartMax, progress)
        let labelPaddingStartMin = viewSpec.labelPaddingStartMin.toCGFloat()
        let labelPaddingStartMax = viewSpec.labelPaddingStartMax.toCGFloat()
        let labelPaddingStart = lerp(labelPaddingStartMin, labelPaddingStartMax, progress)
        let widthMin = viewSpec.animatedViewSpec.widthMin?.toCGFloat() ?? 0
        let widthMax = viewSpec.animatedViewSpec.widthMax?.toCGFloat() ?? 0
        let width = lerp(widthMin, widthMax, progress)
        let backgroundColor = theme.themeColors.primary.toColor()
        let height = viewSpec.animatedViewSpec.heightMax?.toCGFloat()
        
        HStack(spacing: 0) {
            Spacer()
            ZStack {
                HStack(spacing: 0) {
                    Spacer()
                        .frame(width: imagePaddingStart)
                    MenuItemUI(render: render, theme: theme, menuItem: image)
                    Spacer()
                }
                HStack(spacing: 0) {
                    Spacer()
                        .frame(width: labelPaddingStart)
                    StyledTextView(text: label, theme: theme)
                    if alpha > 0 {
                        StyledTextView(text: label2, theme: theme)
                            .opacity(alpha)
                    }
                    Spacer()
                }
                .frame(maxWidth: .infinity)
            }
            .frame(width: width, height: height)
            .background(backgroundColor)
            .if (shape != nil) { $0.applyClipShapes(shape!) }
            .onTapGesture {
                eventHandler?.invoke()
            }
        }
        .frame(height: height)
    }
}

struct CollectionDownloadProgressButtonView: View {
    let render: RenderIos
    let theme: Theme
    let viewSpec: CollectionActionButtonViewSpec.DownloadProgressButtonViewSpec
    let progressButton: CollectionActionButtonViewState.DownloadProgressButton
    let visibilityProgress: CGFloat
    let shape: ShapeSpec?
    
    var body: some View {
        let alpha = arbitrateToolbarContentAnimatedAlpha(animationProgress: Float(visibilityProgress)).toCGFloat()
        let minAlpha = 1 - alpha
        let minIconPaddingStart = viewSpec.minIconPaddingStart.toCGFloat()
        let maxLabelPaddingStart = viewSpec.maxLabelPaddingStart.toCGFloat()
        let countLabelPaddingEnd = lerp(
            viewSpec.countLabelPaddingEndMin.toCGFloat(),
            viewSpec.countLabelPaddingEndMax.toCGFloat(),
            visibilityProgress
        )
        let minIcon = progressButton.minIcon
        let maxLabel = progressButton.maxLabel
        let countLabel = progressButton.countLabel
        let backgroundColor = theme.themeColors.tertiary?.toColor() ?? .gray
        let progressColor = theme.themeColors.secondary.toColor()
        
        let widthMin = viewSpec.animatedViewSpec.widthMin?.toCGFloat() ?? 0
        let widthMax = viewSpec.animatedViewSpec.widthMax?.toCGFloat() ?? 0
        let width = lerp(widthMin, widthMax, visibilityProgress)
        let height = viewSpec.animatedViewSpec.heightMax?.toCGFloat()
        
        let downloadProgress = progressButton.progress.toCGFloat()
        
        HStack(spacing: 0) {
            Spacer()
            ZStack {
                Rectangle()
                    .fill(backgroundColor)
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
                
                HStack(spacing: 0) {
                    Rectangle()
                        .fill(progressColor)
                        .frame(maxHeight: .infinity)
                        .frame(width: width * downloadProgress)
                    Spacer()
                }
                .frame(maxWidth: .infinity)
                
                if minAlpha > 0 {
                    HStack(spacing: 0) {
                        Spacer()
                            .frame(width: minIconPaddingStart)
                        MenuItemUI(render: render, theme: theme, menuItem: minIcon)
                        Spacer()
                    }
                    .opacity(minAlpha)
                }
                
                if alpha > 0 {
                    HStack(spacing: 0) {
                        Spacer()
                            .frame(width: maxLabelPaddingStart)
                        StyledTextView(text: maxLabel, theme: theme)
                        Spacer()
                    }
                    .opacity(alpha)
                }
                
                HStack(spacing: 0) {
                    Spacer()
                    StyledTextView(text: countLabel, theme: theme)
                    Spacer()
                        .frame(width: countLabelPaddingEnd)
                }
            }
            .frame(width: width, height: height)
            .if (shape != nil) { $0.applyClipShapes(shape!) }
        }
        .frame(height: height)
    }
}
