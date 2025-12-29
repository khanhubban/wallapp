//
//  AccountOverviewView.swift
//  WallApp
//

import SwiftUI
import WallApp

struct AccountOverviewView: View {
    let render: RenderIos
    let theme: Theme
    let viewState: AccountOverviewViewState
    
    @ViewBuilder
    var body: some View {
        ZStack {
            switch onEnum(of: viewState) {
            case .signedOut(let viewState):
                AccountOverviewSignedOutView(render: render, theme: theme, viewState: viewState)
            case .loading(_):
                EmptyView() // ADD LOADING VIEW IF NEEDED
            case .signedIn(let viewState):
                AccountOverviewSignedInView(render: render, theme: theme, viewState: viewState)
            }
        }
    }
}

struct AccountOverviewSignedOutView: View {
    let render: RenderIos
    let theme: Theme
    let viewState: AccountOverviewViewState.SignedOut
    
    var body: some View {
        let signInButtons = viewState.signInButtons
        let paddingDefault = render.defaultViewSpec.paddingDefault.toCGFloat()
        ZStack {
            VStack(spacing: render.defaultViewSpec.paddingDefault.toCGFloat()) {
                ForEach(signInButtons.indices, id: \.self) { index in
                    switch signInButtons[index] {
                    case is SignInButtonViewStateApple:
                        let details = signInButtons[index]
                        let apple = details as! SignInButtonViewStateApple
                        AccountButton(theme: theme, image: theme.isLight ? apple.imageDark : apple.imageLight, lable: apple.label, eventHandler: apple.viewEventHandler, viewSpec: apple.viewSpec, shape: viewState.viewSpec.backgroundShapeSpec)
                    case is SignInButtonViewStateGoogle:
                        let details = signInButtons[index]
                        let apple = details as! SignInButtonViewStateGoogle
                        AccountButton(theme: theme, image: apple.image, lable: apple.label, eventHandler: apple.viewEventHandler, viewSpec: apple.viewSpec, shape: viewState.viewSpec.backgroundShapeSpec)
                    default:
                        EmptyView()
                    }
                }
            }
            .padding(viewState.viewSpec.padding.toPadding())
        }
        .frame(maxWidth: .infinity)
        .background(theme.themeColors.surface.toColor())
        .applyClipShapes(viewState.viewSpec.backgroundShapeSpec)
        .padding(.all, paddingDefault)
    }
}

struct AccountOverviewSignedInView: View {
    let render: RenderIos
    let theme: Theme
    let viewState: AccountOverviewViewState.SignedIn

    var body: some View {
        let viewSpec = viewState.viewSpec
        let profileImage = viewState.profileImage
        let displayName = viewState.displayName
        let email = viewState.email
        let padding = viewSpec.padding.toPadding()
        let paddingDefault = viewSpec.paddingDefault.toCGFloat()
        let eventHandler = viewState.onClick
        let backgroundColor = theme.themeColors.surface.toColor()
        let profileImageSize = profileImage.imageViewSpec.size.toCGFloat()
        let height = profileImageSize + (viewSpec.padding.top + viewSpec.padding.bottom).toCGFloat()

        HStack(spacing: 0) {
            ProfileImageView(
                render: render,
                theme: theme,
                profileImage: profileImage,
                imageSize: profileImageSize,
                eventHandler: eventHandler
            )

            Spacer().frame(width: paddingDefault)

            VStack(alignment: .leading) {
                if let displayName {
                    StyledTextView(text: displayName, theme: theme)
                }
                if let email = email {
                    StyledTextView(text: email, theme: theme, colorOverride: theme.themeColors.onSurfaceVariant.toColor())
                }
            }
            
            Spacer()
        }
        .padding(padding)
        .frame(height: height)
        .frame(maxWidth: .infinity)
        .background(backgroundColor)
        .applyClipShapes(viewSpec.backgroundShapeSpec)
        .padding(.all, paddingDefault)
        .onTapGesture {
            eventHandler.invoke()
        }
    }
}
            
struct AccountButton: View {
    let theme: Theme
    let image: ImageUI
    let lable: StyledText
    let eventHandler: ViewEventHandler
    let viewSpec: SignInButtonViewSpec
    let shape: ShapeSpec
    
    var body: some View {
        let foregroundColor: Color = theme.isLight ? Color.black : Color.white
        HStack(alignment: .center) {
            Spacer()
            SwiftUIImage(theme: theme, image: image).frame(width: 22, height: 22)
            StyledTextView(text: lable, theme: theme, colorOverride: foregroundColor)
                .padding(.horizontal, 12.0)
            Spacer()
        }.frame(width: viewSpec.width.toCGFloat(), height: viewSpec.height.toCGFloat())
            .applyClipShapes(shape, borderWidth: 2.0, borderColor: foregroundColor)
            .onTapGesture { eventHandler.invoke()}
   }
}
