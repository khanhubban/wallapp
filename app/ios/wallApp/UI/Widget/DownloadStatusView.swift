//
//  DownloadStatusView.swift
//  WallApp
//

import WallApp
import SwiftUI

struct DownloadStatusView: View {
    let render: RenderIos
    let theme: Theme
    let viewState: DownloadStatusViewState
    
    var body: some View {
        let paddingSmall = render.defaultViewSpec.paddingSmall.toCGFloat()
        let paddingDefault = render.defaultViewSpec.paddingDefault.toCGFloat()
        let title = viewState.title
        let summary = viewState.summary
        let progress = viewState.progress
        let shape = ShapeSpec(shapeStyle: .roundedCorners, shapeSize: .small)
        VStack(alignment: .center, spacing: 0) {
            VStack(spacing: 5) {
                HStack {
                    StyledTextView(text: title, theme: theme)
                    Spacer()
                    StyledTextView(text: summary, theme: theme)
                }
                ProgressView(value: progress)
                    .progressViewStyle(RoundedRectProgressViewStyle())
                    .applyClipShapes(shape)
            }
            .padding(.vertical, paddingSmall)
            .padding(.horizontal, paddingDefault * 1.5)
        }
    }
}

struct RoundedRectProgressViewStyle: ProgressViewStyle {
    func makeBody(configuration: Configuration) -> some View {
        ZStack(alignment: .leading) {
            RoundedRectangle(cornerRadius: 0)
                .frame(height: 5)
                .foregroundColor(.gray)

            RoundedRectangle(cornerRadius: 0)
                .frame(width: CGFloat(configuration.fractionCompleted ?? 0) * 250, height: 5)
                .foregroundColor(.red)
        }
    }
}
