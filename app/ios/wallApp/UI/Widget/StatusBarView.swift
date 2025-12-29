//
//  StatusBarView.swift
//  WallApp
//

import WallApp
import SwiftUI

struct StatusBarView: View {
    let render: RenderIos
    let theme: Theme
    var color: Color? = nil
    
    var body: some View {
        VStack {
            Spacer()
                .frame(height: render.windowFrame.statusBarHeight.toCGFloat())
                .frame(maxWidth: .infinity)
                .background(color ?? theme.themeColors.surfaceVariant.toColor())
        }
    }
}
