//
//  SearchRecipeBinBarView.swift
//  WallApp
//

import WallApp
import SwiftUI

struct SearchRecipeBinBarView: View {
    let render: RenderIos
    let theme: Theme
    let viewState: SearchRecipeBinViewState
    let containerColor: Color?
    let foregroundColor: Color?
    var visibilityProgress: Float? = nil
    
    @State private var scrollToStart = false
    @State private var checkIconClickTrigger = 0
    
    var viewSpec: SearchRecipeBinViewSpec {
        viewState.viewSpec
    }
    
    var body: some View {
        ZStack {
            HStack(spacing: 0) {
                ScrollViewReader { scrollView in
                    ScrollView(.horizontal, showsIndicators: false) {
                        HStack(spacing: 0) {
                            ForEach(viewState.viewStates.indices, id: \.self) { index in
                                let childState = viewState.viewStates[index]
                                if let childState = childState as? SearchColorsViewState {
                                    SearchColorsView(
                                        render: render,
                                        theme: theme,
                                        viewState: childState
                                    )
                                } else if let childState = childState as? SearchRecipeBinText {
                                    SearchRecipeBinTextView(
                                        render: render,
                                        theme: theme,
                                        tags: childState.tags,
                                        textOpacity: viewSpec.recipeTextOpacity.toCGFloat()
                                    )
                                } else if let childState = childState as? SpacerViewState {
                                    SizedSpacer(viewState: childState)
                                        .background(.blue)
                                }
                            }
                        }
                    }
                    .onChange(of: visibilityProgress) { visibilityProgress in
                        if let visibilityProgress, visibilityProgress < 1 {
                            scrollToStart = true
                        } else {
                            scrollToStart = false
                        }
                    }
                    .onChange(of: scrollToStart) { scrollToStart in
                        if scrollToStart {
                            scrollView.scrollTo(0)
                        }
                    }
                }
                .frame(maxHeight: .infinity)
                .padding(.trailing, viewSpec.recipeEndPadding.toCGFloat())
            }
            
            let edgeFade = viewState.edgeFade
            EdgeFadeView(
                edgeFade: edgeFade,
                theme: theme,
                render: render
            )
            
            let checkIcon = viewState.checkIcon
            let actionIconSpacing = viewSpec.actionIconSpacing
            let offsetX = viewState.endOffsetX.toCGFloat()
            HStack(spacing: 0) {
                Spacer()
                MenuItemUI(
                    render: render,
                    theme: theme,
                    menuItem: viewState.clearIcon
                )
                .zIndex(1)
                .offset(x: offsetX)
                
                if let checkIcon {
                    Spacer()
                        .frame(width: actionIconSpacing.toCGFloat())
                    if #available(iOS 17.0, *), let checkIcon = checkIcon as? MenuItem.MenuItemButton {
                        // Need this ugly copy code because we don't have the Kotlin copy function
                        let checkIconWithAnimation = MenuItem.MenuItemButton(
                            button: ButtonViewState(
                                menuItem: checkIcon.button.menuItem,
                                animatedViewSpec: checkIcon.button.animatedViewSpec,
                                shapeSpec: checkIcon.button.shapeSpec,
                                buttonAppearance: checkIcon.button.buttonAppearance,
                                containerColorToken: checkIcon.button.containerColorToken,
                                eventHandler: ViewEventHandlerCompanion().createOnClick(onClick: {
                                    checkIconClickTrigger += 1
                                    checkIcon.button.eventHandler?.invoke()
                                })
                            ),
                            width: checkIcon.width,
                            height: checkIcon.height
                        )
                        MenuItemUI(
                            render: render,
                            theme: theme,
                            menuItem: checkIconWithAnimation
                        )
                        // this is the tap bounce animation
                        .phaseAnimator([false, true], trigger: checkIconClickTrigger) { content, phase in
                            content.scaleEffect(phase ? 0.5 : 1)
                        } animation: { phase in
                                .easeOut(duration: 0.1)
                        }
                    } else {
                        MenuItemUI(
                            render: render,
                            theme: theme,
                            menuItem: checkIcon
                        )
                    }
                }
            }
        }
        .padding(.leading, viewSpec.startPadding.toCGFloat())
        .padding(.trailing, viewSpec.endPadding.toCGFloat())
        .background(containerColor)
        .applyClipShapes(ShapeMapper.map(shapeSpec: viewState.shape))
    }
}

struct SearchRecipeBinTextView: View {
    let render: RenderIos
    let theme: Theme
    let tags: [StyledText]
    let textOpacity: CGFloat
    
    var body: some View {
        HStack(spacing: 0) {
            ForEach(tags.indices, id: \.self) { index in
                StyledTextView(
                    text: tags[index],
                    theme: theme
                )
            }
        }
        .opacity(textOpacity)
    }
}
