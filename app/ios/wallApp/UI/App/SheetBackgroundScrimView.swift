//
//  SheetBackgroundScrimView.swift
//  WallApp
//

import SwiftUI

struct SheetBackgroundScrimView: View {
    var sheetArgument: ScreenArgument?
    
    var body: some View {
        Rectangle()
            .fill(Color.black)
            .opacity(sheetArgument != nil ? 0.5 : 0)
            .ignoresSafeArea()
            .animation(.easeInOut(duration: 0.2), value: sheetArgument)
    }
}

