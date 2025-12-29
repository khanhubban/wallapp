//
//  FirstRunView.swift
//  WallApp
//

import WallApp
import Lottie
import SwiftUI

struct FirstRunView: View {
    let viewState: FirstRunViewState
    let theme: Theme
    let render: RenderIos
    
    var body: some View {
        ZStack {
            if let viewState = viewState as? FirstRunViewState.Success  {
                ZStack(alignment: .center) {
                    ContentViewCompose(screenArgument: ScreenArgument.FirstRunScreenArgument())
                    if let screenViewState = viewState.screen as? SignUpViewState {
                        
                    }
                }
            } else {
                LoadingView(theme: theme, render: render)
            }
        }
    }
}
