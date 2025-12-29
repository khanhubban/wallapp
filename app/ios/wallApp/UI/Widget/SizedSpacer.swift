//
//  SizedSpacer.swift
//  WallApp
//

import WallApp
import SwiftUI

struct SizedSpacer: View {
    let viewState: SpacerViewState
    
    var body: some View {
        if let width = viewState.width?.dp, let height = viewState.height?.dp {
            Spacer()
                .frame(width: width.toCGFloat(), height: height.toCGFloat())
        } else if let width = viewState.width?.dp {
            Spacer()
                .frame(width: width.toCGFloat())
        } else if let height = viewState.height?.dp {
            Spacer()
                .frame(height: height.toCGFloat())
        }
    }
}
