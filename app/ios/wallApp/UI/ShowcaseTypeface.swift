//
//  ShowcaseTypeface.swift
//  WallApp
//
import WallApp
import SwiftUI

struct ShowcaseTypeface: View {
    
    @State var theme: Theme = InteropModulesIos.shared.themeManager.theme.value
    
    private let message = "A quick brown Fox"
    
    private let items = [
        TextWithStyle.create(textStyle: TextStyle.caption, label: "Caption"),
        TextWithStyle.create(textStyle: TextStyle.body, label: "Body"),
        TextWithStyle.create(textStyle: TextStyle.subheading, label: "Subhead"),
        TextWithStyle.create(textStyle: TextStyle.subheadingActive, label: "Subhead Active"),
        TextWithStyle.create(textStyle: TextStyle.callToAction, label: "CallToAction"),
        TextWithStyle.create(textStyle: TextStyle.headline, label: "Headline"),
        TextWithStyle.create(textStyle: TextStyle.display, label: "Display"),
    ]
    
    var body: some View {
        List(items, id: \.text.string) { item in
            TextPreview(message: message, previewTextWithStyle: item, theme: theme)
                .listRowSeparator(.hidden)
                .padding(.vertical, -10)
        }.listStyle(.plain)
    }
    
    private struct TextPreview: View {
        let message: String
        let previewTextWithStyle: TextWithStyle
        let theme: Theme
        
        var body: some View {
            let messageText = mapTextStyle(textStyle: previewTextWithStyle.style,
                                           string: message,
                                           textAlign: TextAlign.Start(),
                                           colorToken: nil,
                                           maxLines: nil, ignoreLargeSystemFontScaling: false,
                                           useMarquee: false,
                                           animateMarquee: true)
            VStack(alignment: .leading, spacing: 2) {
                StyledTextView(text: previewTextWithStyle.text, theme: theme).padding(2)
                StyledTextView(text: messageText, theme: theme).padding(2)
                Spacer().frame(height: 8)
            }
        }
    }
}


struct TextWithStyle {
    let text: StyledText
    let style: TextStyle
}

extension TextWithStyle {
    static func create(textStyle: TextStyle, label: String) -> TextWithStyle {
        let text = TextStyleBody(string: label, textAlign: TextAlign.Start(), colorToken: nil, maxLines: nil, ignoreLargeSystemFontScaling: false)
        return TextWithStyle(text: text, style: textStyle)
    }
}
