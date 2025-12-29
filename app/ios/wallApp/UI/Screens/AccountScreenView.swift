//
//  AccountScreenView.swift
//  WallApp
//

import WallApp
import SwiftUI

struct AccountScreenView: View {
    let viewState: AccountViewState
    let theme: Theme
    let render: RenderIos
    
    var body: some View {
        let settings = viewState.settings
        let toolbar = viewState.toolbarViewState
        let statusBarHeight = render.windowFrame.statusBarHeight.toCGFloat()
        let toolbarHeight = toolbar.height.toCGFloat()
        let topPadding = statusBarHeight + toolbarHeight
        let profileImage = viewState.profileImage
        let email = viewState.email
        let displayName = viewState.displayName
        let paddingDefault = render.defaultViewSpec.paddingDefault.toCGFloat()
        ZStack(alignment: .topLeading) {
            ToolbarView(
                render: render,
                theme: theme,
                toolbarViewState: toolbar,
                scrollOffsetController: nil,
                renderStatusBar: true
            )
            .zIndex(1.0)
            ScrollView {
                VStack(alignment: .leading, spacing: 0) {
                    Spacer().frame(height: topPadding)
                    if let profileImage {
                        Spacer().frame(height: paddingDefault)
                        HStack {
                            Spacer()
                            AccountHeaderView(
                                render: render,
                                theme: theme,
                                profileImage: profileImage,
                                email: email,
                                displayName: displayName
                            )
                            Spacer()
                        }
                    }
                    ForEach(settings.indices, id: \.self) { index in
                        var setting = settings[index]
                        SettingView(
                            render: render,
                            theme: theme,
                            settingViewState: setting
                        )
                    }
                }
            }
            .scrollIndicators(.hidden)
        }
    }
}

struct AccountHeaderView: View {
    let render: RenderIos
    let theme: Theme
    let profileImage: ProfileImageViewState
    let email: StyledText?
    let displayName: StyledText?
    
    var body: some View {
        VStack(alignment: .center, spacing: 0) {
            ProfileImageView(
                render: render,
                theme: theme,
                profileImage: profileImage,
                imageSize: profileImage.imageViewSpec.size.toCGFloat(),
                eventHandler: profileImage.eventHandler
            )
            if let email {
                let paddingDefault = render.defaultViewSpec.paddingDefault.toCGFloat()
                Spacer().frame(height: paddingDefault)
                StyledTextView(text: email, theme: theme)
            }
        }
    }
}


