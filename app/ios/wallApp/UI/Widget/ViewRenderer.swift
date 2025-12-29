//
//  ViewRenderer.swift
//  WallApp
//

import WallApp
import SwiftUI

extension CommonView {
    
    @ViewBuilder
    func render(render: RenderIos, theme: Theme) -> some View {
        if let viewState = self.viewState as? FeedViewState {
            FeedGrid(render: render, theme: theme, feedViewState: viewState)
        } else if let spacerViewState = viewState as? SpacerViewState {
            SizedSpacer(viewState: spacerViewState)
        } else if let state = viewState as? WallpaperPreviewViewState, let viewSpec = viewSpec as? FeedContentPreviewViewSpec {
            let shape = ShapeMapper.map(shapeSpec: viewSpec.shapeSpec)
            WallpaperFeedPreview(wallpaperPreview: state, viewSpec: viewSpec, theme: theme, render: render)
                .frame(width: viewSpec.width.toCGFloat(), height: viewSpec.height.toCGFloat())
                .applyClipShapes(shape)
        } else if let adViewState = viewState as? FeedAdViewState {
            NativeAdContentView(render: render, theme: theme, maxWidth: true, fallback: adViewState.fallbackAdViewState)
                .frame(height: FeedAdViewState.nativeAdHeight)
                .frame(maxWidth: .infinity)
        } else if let adViewState = viewState as? AdViewState {
            PresetAdView(render: render, theme: theme, adViewState: adViewState)
                .frame(height: adViewState.viewSpec.height.toCGFloat())
                .frame(maxWidth: .infinity)
        } else if let state = viewState as? CollectionPreviewViewState, let viewSpec = viewSpec as? CollectionPreviewViewSpec {
            CollectionPreview(collectionPreview: state, theme: theme, render: render)
                .frame(height: viewSpec.height.toCGFloat())
                .frame(width: viewSpec.width.toCGFloat())
        } else if let state = viewState as? MenuItemViewState {
            MenuItemsView(render: render, theme: theme, viewState: state)
        } else if let state = viewState as? NoDataViewState {
            NoDataView(render: render, theme: theme, viewState: state)
        } else if let state = viewState as? DownloadStatusViewState {
            DownloadStatusView(render: render, theme: theme, viewState: state)
        } else if let state = viewState as? CarouselViewState, let viewSpec = viewSpec as? CarouselViewSpec {
            CarouselView(render: render, theme: theme, viewState: state, viewSpec: viewSpec)
        } else if let state = viewState as? ExhibitViewState, let viewSpec = viewSpec as? ExhibitViewSpec {
            CarouselPageView(render: render, theme: theme, viewState: state, viewSpec: viewSpec)
        } else if let state = viewState as? SettingViewState {
            SettingView(render: render, theme: theme, settingViewState: state)
        } else if let state = viewState as? SettingGroupViewState {
            SettingGroupView(render: render, theme: theme, settingGroupViewState: state)
        } else if let state = viewState as? AccountOverviewViewState {
            AccountOverviewView(render: render, theme: theme, viewState: state)
        } else if let state = viewState as? FavoriteViewState {
            FavoriteAnimatedView(theme: theme, viewState: state)
        } else if let state = viewState as? PaywallPlanViewState {
            PaywallPlanView(render: render, theme: theme, viewState: state)
        }
    }
}
