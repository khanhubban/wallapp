//
//  CollectionScreenView.swift
//  WallApp
//

import WallApp
import SwiftUI

struct CollectionScreenView: View {
    let viewState: CollectionViewState
    let theme: Theme
    let render: RenderIos
    
    var body: some View {
        ZStack {
            switch(onEnum(of: viewState)) {
            case .loading(_):
                LoadingView(theme: theme, render: render)
            case .success(let data):
                CollectionSuccessView(viewState: data, render: render, theme: theme)
            }
        }
        .animation(.easeInOut, value: viewState)
    }
}


struct CollectionSuccessView: View {
    let viewState: CollectionViewState.Success
    let render: RenderIos
    let theme: Theme
    
    @State private var expandedProgress: CGFloat = 1
    
    private var toolbarMaxHeight: CGFloat {
        switch viewState.toolbarViewState {
        case is CollectionToolbarViewState.Locked:
            return (viewState.toolbarViewState as! CollectionToolbarViewState.Locked).viewSpec.maxToolbarHeight.toCGFloat()
        case is CollectionToolbarViewState.Unlocked:
            return (viewState.toolbarViewState as! CollectionToolbarViewState.Unlocked).viewSpec.maxToolbarHeight.toCGFloat()
        default:
            return 0
        }
    }
    
    private var toolbarMinHeight: CGFloat {
        switch viewState.toolbarViewState {
        case is CollectionToolbarViewState.Locked:
            return (viewState.toolbarViewState as! CollectionToolbarViewState.Locked).viewSpec.minToolbarHeight.toCGFloat()
        case is CollectionToolbarViewState.Unlocked:
            return (viewState.toolbarViewState as! CollectionToolbarViewState.Unlocked).viewSpec.minToolbarHeight.toCGFloat()
        default:
            return 0
        }
    }
    
    private var paddingDefault: CGFloat {
        return render.defaultViewSpec.paddingDefault.toCGFloat()
    }
    
    var body: some View {
        
        ZStack(alignment: .top) {
            if let lockedToolbarViewState = viewState.toolbarViewState as? CollectionToolbarViewState.Locked {
                CollectionToolbarLockedView(
                    viewState: lockedToolbarViewState,
                    render: render,
                    theme: theme,
                    expandedProgress: $expandedProgress
                )
                .frame(alignment: .topLeading)
                .zIndex(1)
            } else if let unlockedToolbarViewState = viewState.toolbarViewState as? CollectionToolbarViewState.Unlocked {
                CollectionToolbarUnlockedView(
                    viewState: unlockedToolbarViewState,
                    render: render,
                    theme: theme,
                    expandedProgress: $expandedProgress
                )
                .frame(alignment: .topLeading)
                .zIndex(1)
            }
            
            let heightDifferenceDueToExpansion = (1-expandedProgress) * (toolbarMaxHeight - toolbarMinHeight)
            let paddingHeaderHeight = toolbarMaxHeight + render.windowFrame.statusBarHeight.toCGFloat() - heightDifferenceDueToExpansion
            FeedGrid(
                render: render,
                theme: theme,
                feedViewState: viewState.feedViewState,
                paddingHeaderViewState: PaddingHeaderViewState(height: paddingHeaderHeight)
            )
            .frame(maxHeight: .infinity)
            .ignoresSafeArea(.all, edges: .all)
        }
        .overlay {
            BottomScrimOverlayView(render: render, theme: theme)
        }
        .transition(.fade)
    }
}
