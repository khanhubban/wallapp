//
//  NativeAdCollectionViewCell.swift
//  WallApp
//

import UIKit
import SwiftUI
import GoogleMobileAds


class NativeAdCollectionViewCell: UICollectionViewCell {
    var adLoader: GADAdLoader!
    private var nativeAdView: GADNativeAdView?
    private var loadingView: UINativeAdLoadingView?
    private var presetAdView: UIPresetAdView?
    var render: RenderIos!
    var theme: Theme!
    private var fallbackAdViewState: AdViewState?
    private var index: Int!

    override init(frame: CGRect) {
        super.init(frame: frame)
    }
    
    required init?(coder: NSCoder) {
        super.init(coder: coder)
    }
    
    
    override func prepareForReuse() {
        super.prepareForReuse()
        nativeAdView?.removeFromSuperview()
        nativeAdView = nil
        loadingView?.removeFromSuperview()
        loadingView = nil
        presetAdView?.removeFromSuperview()
        presetAdView = nil
    }
    
    func configureCell(
        render: RenderIos,
        theme: Theme,
        fallbackAdViewState: AdViewState,
        nativeAdsCacheManager: NativeAdsCacheManager,
        index: Int
    ) {
        self.render = render
        self.theme = theme
        self.fallbackAdViewState = fallbackAdViewState
        self.index = index
        self.addLoadingView()
        nativeAdsCacheManager.fetchAd(for: index) { [weak self] result in
            guard let self else { return }
            switch result {
            case .success(let nativeAd, let loadedIndex):
                Log.d("[NativeAds] Ad loaded for index: \(loadedIndex), current index: \(self.index ?? -1)")
                guard loadedIndex == self.index else { return }
                self.setupAdView(with: nativeAd)
            case .failure:
                self.setupFallbackView()
            }
        }
    }
    
    private func addLoadingView() {
        self.loadingView = UINativeAdLoadingView(render: self.render)
        guard let loadingView = self.loadingView else { return }
        self.addSubview(loadingView)
        loadingView.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint.activate([
            loadingView.topAnchor.constraint(equalTo: self.topAnchor),
            loadingView.bottomAnchor.constraint(equalTo: self.bottomAnchor),
            loadingView.leadingAnchor.constraint(equalTo: self.leadingAnchor),
            loadingView.trailingAnchor.constraint(equalTo: self.trailingAnchor),
            loadingView.heightAnchor.constraint(equalToConstant: FeedAdViewState.nativeAdHeight)
        ])
    }
    
    private func setupAdView(with nativeAd: GADNativeAd) {
        removeLoadingView()
        
        nativeAdView = Bundle.main.loadNibNamed(
            "NativeAdViewWide",
            owner: nil,
            options: nil)?.first as? GADNativeAdView
        
        guard let nativeAdView = nativeAdView else { return }
        
        // Update the ad view
        (nativeAdView.headlineView as? UILabel)?.text = nativeAd.headline
        nativeAdView.mediaView?.mediaContent = nativeAd.mediaContent
        (nativeAdView.bodyView as? UILabel)?.text = nativeAd.body
        (nativeAdView.iconView as? UIImageView)?.image = nativeAd.icon?.image
        (nativeAdView.starRatingView as? RatingView)?.setUp(rating: Double(truncating: nativeAd.starRating ?? 0), theme: self.theme)
        (nativeAdView.storeView as? UILabel)?.text = nativeAd.store
        (nativeAdView.priceView as? UILabel)?.text = nativeAd.price
        (nativeAdView.advertiserView as? UILabel)?.text = nativeAd.advertiser
        (nativeAdView.callToActionView as? UIButton)?.setTitle(nativeAd.callToAction, for: .normal)
        nativeAdView.callToActionView?.isUserInteractionEnabled = false
        nativeAdView.nativeAd = nativeAd
        
        if let mediaView = nativeAdView.mediaView, nativeAd.mediaContent.aspectRatio > 0 {
            let heightConstraint = NSLayoutConstraint(
              item: mediaView,
              attribute: .height,
              relatedBy: .equal,
              toItem: mediaView,
              attribute: .width,
              multiplier: CGFloat(1 / nativeAd.mediaContent.aspectRatio),
              constant: 0)
            heightConstraint.isActive = true
            heightConstraint.priority = UILayoutPriority(999)
          }
        
        // Add the ad view to the view hierarchy
        self.addSubview(nativeAdView)
        nativeAdView.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint.activate([
            nativeAdView.leadingAnchor.constraint(equalTo: self.leadingAnchor),
            nativeAdView.trailingAnchor.constraint(equalTo: self.trailingAnchor),
            nativeAdView.topAnchor.constraint(equalTo: self.topAnchor),
            nativeAdView.bottomAnchor.constraint(equalTo: self.bottomAnchor)
        ])
        
        //Main View
        nativeAdView.backgroundColor = theme.themeColors.onPrimary?.color.uiColor
        
        //Headline
        (nativeAdView.headlineView as? UILabel)?.textColor = theme.themeColors.onSurface?.color.uiColor
        (nativeAdView.headlineView as? UILabel)?.setTextStyle(.subheadingActive)
        
        //Advertiser
        (nativeAdView.advertiserView as? UILabel)?.textColor = theme.themeColors.onSurface?.color.uiColor
        (nativeAdView.advertiserView as? UILabel)?.setTextStyle(.body)
        (nativeAdView.advertiserView as? UILabel)?.isHidden = nativeAd.advertiser == nil
        
        //Body Lable
        (nativeAdView.bodyView as? UILabel)?.numberOfLines = 0
        (nativeAdView.bodyView as? UILabel)?.textColor = theme.themeColors.onSurface?.color.uiColor
        (nativeAdView.bodyView as? UILabel)?.setTextStyle(.body)
        
        //Button
        nativeAdView.callToActionView?.backgroundColor = theme.themeColors.secondary.uiColor
        (nativeAdView.callToActionView as? UIButton)?.setTitleColor( theme.themeColors.onPrimary?.color.uiColor, for: .normal)
        (nativeAdView.callToActionView as? UIButton)?.titleLabel?.setTextStyle(.subheadingActive)
        DispatchQueue.main.async {
            nativeAdView.callToActionView?.layoutIfNeeded() //require to get correct frame before applying shape
            nativeAdView.callToActionView?.shape(shapeStyle: .rectangle)
        }
        
    }
    
    private func setupFallbackView() {
        guard let fallbackAdViewState = fallbackAdViewState else { return }
        removeLoadingView()
        presetAdView = UIPresetAdView(render: render, theme: theme, adViewState: fallbackAdViewState)
        presetAdView?.translatesAutoresizingMaskIntoConstraints = false
        self.addSubview(presetAdView!)
        NSLayoutConstraint.activate([
            presetAdView!.topAnchor.constraint(equalTo: self.topAnchor),
            presetAdView!.bottomAnchor.constraint(equalTo: self.bottomAnchor),
            presetAdView!.trailingAnchor.constraint(equalTo: self.trailingAnchor),
            presetAdView!.leadingAnchor.constraint(equalTo: self.leadingAnchor)
        ])
    }
    
    private func removeLoadingView() {
        loadingView?.removeFromSuperview()
        loadingView = nil
    }
}
