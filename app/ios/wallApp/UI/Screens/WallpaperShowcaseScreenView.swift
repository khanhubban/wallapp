//
//  WallpaperShowcaseView.swift
//  WallApp
//

import WallApp
import SwiftUI

struct WallpaperShowcaseScreenView: View {
    private let viewState: WallpaperShowcaseViewState
    private let theme: Theme
    private let render: RenderIos
    
    private var showSuccessView: Bool
    
    init(viewState: WallpaperShowcaseViewState, theme: Theme, render: RenderIos) {
        self.viewState = viewState
        self.theme = theme
        self.render = render
        showSuccessView = viewState is WallpaperShowcaseViewState.Success
    }
    
    var body: some View {
        ZStack {
            if showSuccessView && viewState is WallpaperShowcaseViewState.Success {
                WallpaperShowcaseSuccessView(
                    viewState: viewState as! WallpaperShowcaseViewState.Success,
                    theme: theme,
                    render: render
                )
                .transition(.fade)
            } else if viewState is WallpaperShowcaseViewState.Loading {
                LoadingView(theme: theme, render: render)
                    .transition(.fade)
            }
        }
        .ignoresSafeArea(.all, edges: .all)
        .animation(.easeInOut, value: showSuccessView)
    }
}

struct WallpaperShowcasePreviewView: UIViewControllerRepresentable {
    let viewState: WallpaperShowcaseViewState.Success
    let theme: Theme
    let render: RenderIos
    
    func makeUIViewController(context: Context) -> WallpaperShowcaseViewController {
        return WallpaperShowcaseViewController(
            viewState: viewState,
            theme: theme,
            render: render
        )
    }
    
    func updateUIViewController(_ uiViewController: WallpaperShowcaseViewController, context: Context) {
        uiViewController.viewState = viewState
    }
}

struct WallpaperShowcaseSuccessView: View {
    let viewState: WallpaperShowcaseViewState.Success
    let theme: Theme
    let render: RenderIos
    
    var body: some View {
        let viewSpec = viewState.viewSpec
        let horizontalPadding = viewSpec.horizontalPadding.toCGFloat()
        let bottomPadding = viewSpec.bottomPadding.toCGFloat()
        let title = viewState.title
        let actionButton = viewState.actionButton
        let wallpaperArtist = viewState.wallpaperArtist
        let previewHeight = viewState.wallpaperPreviews.first?.viewSpec.height.toCGFloat()
        let previewActionButton = viewState.previewActionButton
        let titleSpacing = viewSpec.maxTitleSpacing.toCGFloat()
        let maxArtistTopSpacing = viewSpec.maxArtistTopSpacing.toCGFloat()
        let minArtistTopSpacing = viewSpec.minArtistTopSpacing.toCGFloat()
        let maxArtistBottomSpacing = viewSpec.maxArtistBottomSpacing.toCGFloat()
        let minArtistBottomSpacing = viewSpec.minArtistBottomSpacing.toCGFloat()
        VStack(spacing: 0) {
            WallpaperShowcasePreviewView(viewState: viewState, theme: theme, render: render)
            .frame(height: previewHeight ?? 0)
            .applyClipShapes(viewState.viewSpec.wallpaperPreviewShapeSpec)
            .overlay {
                PreviewActionButtonView(
                    render: render,
                    theme: theme,
                    menuItem: previewActionButton
                )
            }
            
            Spacer()
                .frame(minHeight: 0, maxHeight: titleSpacing)
            
            // Title
            MenuItemUI(render: render, theme: theme, menuItem: title)
            
            Spacer()
                .frame(minHeight: 0, maxHeight: titleSpacing)
            
            // Action Button
            ZStack {
                MenuItemUI(render: render, theme: theme, menuItem: actionButton)
            }
            .padding(.horizontal, horizontalPadding)
            
            Spacer()
                .frame(minHeight: minArtistTopSpacing, maxHeight: maxArtistTopSpacing)
            
            WallpaperArtistView(
                viewState: wallpaperArtist,
                theme: theme,
                render: render
            )
            .padding(.leading, horizontalPadding)
            .padding(.trailing, horizontalPadding - 12)
            
            Spacer()
                .frame(minHeight: minArtistBottomSpacing, maxHeight: maxArtistBottomSpacing)
            
            WallpaperDetailsView(
                viewState: viewState.detail,
                theme: theme,
                render: render
            )
            .padding(.horizontal, horizontalPadding)
            
            Spacer(minLength: 0)
            
            Spacer()
                .frame(height: bottomPadding)
        }
        .overlay {
            let closeButton = viewState.close
            let actionItems = viewState.actionItems
            VStack(alignment: .center, spacing: 0) {
                Spacer()
                    .frame(height: viewSpec.topControlButtonsVerticalOffset.toCGFloat())
                HStack(alignment: .center, spacing: 0) {
                    MenuItemUI(
                        render: render,
                        theme: theme,
                        menuItem: closeButton
                    )
                    Spacer()
                    MenuItemUI(
                        render: render,
                        theme: theme,
                        menuItem: actionItems
                    )
                }
                Spacer()
            }
        }
    }
}

struct WallpaperShowCaseCarouselView: View {

    let render: RenderIos
    let theme: Theme
    let viewState: WallpaperShowcaseViewState.Success
    @State var startIndex: Int = 0

    var body: some View {
        TabView(selection: $startIndex) {
            ForEach(viewState.wallpaperPreviews.indices, id: \.self) { index in
                let item = viewState.wallpaperPreviews[index]
                let viewSpec = item.viewSpec
                SwiftUIImage(
                    render: render,
                    theme: self.theme,
                    imageViewState: item.imageViewState
                )
                .onTapGesture {
                    item.onClick.invoke()
                }
                .tag(index)
            }
        }
        .tabViewStyle(.page)
        .onChange(of: startIndex) { newValue in
            viewState.onPageChangedWallpaperPreview(KotlinInt(int: Int32(startIndex)))
        }
    }

}

struct WallpaperArtistView: View {
    let viewState: WallpaperArtistViewState
    let theme: Theme
    let render: RenderIos
    
    var body: some View {
        switch onEnum(of: viewState) {
        case .loading(let loadingState):
            Spacer()
                .frame(height: loadingState.height.toCGFloat())
        case .data(let artistState):
            WallpaperArtistSuccessView(
                viewState: artistState,
                theme: theme,
                render: render
            )
        }
    }
}

struct WallpaperArtistSuccessView: View {
    let viewState: WallpaperArtistViewState.Data
    let theme: Theme
    let render: RenderIos
    
    var body: some View {
        let paddingSmall = render.defaultViewSpec.paddingSmall.toCGFloat()
        HStack(alignment: .center, spacing: 0) {
            let profileSize = viewState.profileImage.imageViewSpec.size
            ProfileImageView(render: self.render, theme: self.theme, profileImage: viewState.profileImage, imageSize: profileSize.toCGFloat(), eventHandler: viewState.profileImage.eventHandler)
            .frame(width: profileSize.toCGFloat(), height: profileSize.toCGFloat())
            
            StyledTextView(
                text: viewState.name,
                theme: theme
            )
            .frame(maxWidth: .infinity, alignment: .leading)
            .padding(.horizontal, paddingSmall)
            .onTapGesture {
                viewState.nameEventHandler.invoke()
            }
        }
    }
}

struct WallpaperDetailsView: View {
    let viewState: WallpaperDetailViewState
    let theme: Theme
    let render: RenderIos
    
    var body: some View {
        let viewSpec = viewState.viewSpec
        let wallpaperDetails = viewState.detailItems ?? []
        let spacerHeight = viewSpec.itemVerticalSpacerHeight.toCGFloat()
        let height = viewSpec.height.toCGFloat()
        VStack(alignment: .leading, spacing: 0) {
            ForEach(wallpaperDetails.indices, id: \.self) { index in
                WallpaperDetailItemView(
                    viewState: wallpaperDetails[index],
                    viewSpec: viewSpec,
                    theme: theme,
                    render: render
                )
                if index < wallpaperDetails.count - 1 {
                    Spacer()
                        .frame(maxHeight: spacerHeight)
                }
            }
        }
        .frame(height: height)
        .frame(maxWidth: .infinity, alignment: .leading)
    }
}

struct WallpaperDetailItemView: View {
    let viewState: WallpaperDetailItem
    let viewSpec: WallpaperDetailViewSpec
    let theme: Theme
    let render: RenderIos
    
    var body: some View {
        let itemHeight = viewSpec.itemHeight.toCGFloat()
        let itemImageSize = viewSpec.itemImageSize.toCGFloat()
        HStack(spacing: 8) {
            SwiftUIImage(
                theme: theme,
                image: viewState.image,
                width: itemImageSize,
                height: itemImageSize
            )
            StyledTextView(text: viewState.label, theme: theme)
        }
        .frame(height: itemHeight)
    }
}

struct PreviewActionButtonView: View {
    let render: RenderIos
    let theme: Theme
    let menuItem: MenuItem?
    
    var body: some View {
        if let menuItem {
            let paddingDefault = render.defaultViewSpec.paddingDefault.toCGFloat()
            VStack {
                Spacer()
                HStack {
                    Spacer()
                    MenuItemUI(
                        render: render,
                        theme: theme,
                        menuItem: menuItem
                    )
                }
                .padding(.all, paddingDefault)
            }
        }
    }
}
