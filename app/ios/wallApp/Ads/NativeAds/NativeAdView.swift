import WallApp
import GoogleMobileAds
import SwiftUI


struct NativeAdContentView: View {
    let render: RenderIos
    let theme: Theme
    let maxWidth: Bool
    let fallbackAdViewState: AdViewState
    
    @StateObject private var nativeViewModel = NativeAdViewModel()
    
    init(render: RenderIos, theme: Theme, maxWidth: Bool = false, fallback adViewState: AdViewState) {
        self.render = render
        self.theme = theme
        self.maxWidth = maxWidth
        self.fallbackAdViewState = adViewState
    }
    
    var body: some View {
        VStack {
            switch nativeViewModel.state {
            case .loading:
                NativeAdLoadingView(render: render)
                    .frame(height: FeedAdViewState.nativeAdHeight)
            case .data:
                NativeAdView(nativeViewModel: nativeViewModel, fileName: maxWidth ? "NativeAdViewWide" : "NativeAdViewNarrow")
                    .frame(height: FeedAdViewState.nativeAdHeight)
            case .fallback:
                PresetAdView(render: render, theme: theme, adViewState: fallbackAdViewState)
            }
        }
        .onAppear {
            Log.d("[NativeAds] NativeAdContentView.onAppear()")
            refreshAd()
        }
        
    }
    
    private func refreshAd() {
        nativeViewModel.refreshAd()
    }
}

private struct NativeAdView: UIViewRepresentable {
    typealias UIViewType = GADNativeAdView
    
    @ObservedObject var nativeViewModel: NativeAdViewModel
    let fileName: String
    
    func makeUIView(context: Context) -> GADNativeAdView {
        return Bundle.main.loadNibNamed(
            fileName,
            owner: nil,
            options: nil)?.first as! GADNativeAdView
    }
    
    func updateUIView(_ nativeAdView: GADNativeAdView, context: Context) {
        guard let nativeAd = nativeViewModel.nativeAd else { return }
        
        (nativeAdView.headlineView as? UILabel)?.text = nativeAd.headline
        
        nativeAdView.mediaView?.mediaContent = nativeAd.mediaContent
        
        (nativeAdView.bodyView as? UILabel)?.text = nativeAd.body
        
        (nativeAdView.iconView as? UIImageView)?.image = nativeAd.icon?.image
        
        (nativeAdView.starRatingView as? UIImageView)?.image = imageOfStars(from: nativeAd.starRating)
        
        (nativeAdView.storeView as? UILabel)?.text = nativeAd.store
        
        (nativeAdView.priceView as? UILabel)?.text = nativeAd.price
        
        (nativeAdView.advertiserView as? UILabel)?.text = nativeAd.advertiser
        
        (nativeAdView.callToActionView as? UIButton)?.setTitle(nativeAd.callToAction, for: .normal)
        
        // In order for the SDK to process touch events properly, user interaction should be disabled.
        nativeAdView.callToActionView?.isUserInteractionEnabled = false
        
        // Associate the native ad view with the native ad object. This is required to make the ad clickable.
        // Note: this should always be done after populating the ad views.
        nativeAdView.nativeAd = nativeAd
    }
    
    private func imageOfStars(from starRating: NSDecimalNumber?) -> UIImage? {
        guard let rating = starRating?.doubleValue else {
            return nil
        }
        if rating >= 5 {
            return UIImage(named: "stars_5")
        } else if rating >= 4.5 {
            return UIImage(named: "stars_4_5")
        } else if rating >= 4 {
            return UIImage(named: "stars_4")
        } else if rating >= 3.5 {
            return UIImage(named: "stars_3_5")
        } else {
            return nil
        }
    }
}

private class NativeAdViewModel: NSObject, ObservableObject, GADNativeAdLoaderDelegate {
    @Published var state: NativeAdState = .loading
    @Published var nativeAd: GADNativeAd?
    private var adLoader: GADAdLoader!
    
    func refreshAd() {
        if nativeAd != nil {
            return
        }
        adLoader = GADAdLoader(
            adUnitID: InteropModulesIos.shared.adUnitIds.feedVideoAdUnitId?.id ?? "",
//                "ca-app-pub-3940256099942544/3986624511",// native ad unit id
//                "ca-app-pub-3940256099942544/2521693316",// native video ad unit id
            rootViewController: nil,
            adTypes: [.native], options: nil)
        adLoader.delegate = self
        adLoader.load(GADRequest())
    }
    
    func adLoader(_ adLoader: GADAdLoader, didReceive nativeAd: GADNativeAd) {
        Log.d("[NativeAds] \(#function) called")
        self.nativeAd = nativeAd
        nativeAd.delegate = self
//      state = .fallback // uncomment to test fallback
        state = .data
    }
    
    func adLoader(_ adLoader: GADAdLoader, didFailToReceiveAdWithError error: Error) {
        Log.d("[NativeAds] \(#function) called")
        Log.d("\(adLoader) failed with error: \(error.localizedDescription)")
        state = .fallback
    }
}

// MARK: - GADNativeAdDelegate implementation
extension NativeAdViewModel: GADNativeAdDelegate {
    func nativeAdDidRecordClick(_ nativeAd: GADNativeAd) {
        Log.d("[NativeAds] \(#function) called")
    }
    
    func nativeAdDidRecordImpression(_ nativeAd: GADNativeAd) {
        Log.d("[NativeAds] \(#function) called")
    }
    
    func nativeAdWillPresentScreen(_ nativeAd: GADNativeAd) {
        Log.d("[NativeAds] \(#function) called")
    }
    
    func nativeAdWillDismissScreen(_ nativeAd: GADNativeAd) {
        Log.d("[NativeAds] \(#function) called")
    }
    
    func nativeAdDidDismissScreen(_ nativeAd: GADNativeAd) {
        Log.d("[NativeAds] \(#function) called")
    }
}

enum NativeAdState {
    case loading
    case data
    case fallback
}

struct NativeAdLoadingView: View {
    let render: RenderIos
    
    var body: some View {
        let paddingDefault = render.defaultViewSpec.paddingDefault.toCGFloat()
        VStack(alignment: .center) {
            let titleHeight: CGFloat = 60
            ShimmerView(
                size: CGSize(
                    width: render.windowFrame.deviceWidth.toCGFloat() - paddingDefault * 4,
                    height: titleHeight
                )
            )
            .padding(.horizontal, paddingDefault)
            .padding(.top, paddingDefault)
            .padding(.bottom, paddingDefault / 2)
            
            ShimmerView(
                size: CGSize(
                    width: render.windowFrame.deviceWidth.toCGFloat() - paddingDefault * 4,
                    height: FeedAdViewState.nativeAdHeight - paddingDefault * 3 - titleHeight
                )
            )
            .padding(.horizontal, paddingDefault)
            .padding(.bottom, paddingDefault)
            .padding(.top, paddingDefault / 2)
        }
        .frame(width: render.windowFrame.deviceWidth.toCGFloat() - paddingDefault * 2)
        .background(Color.gray.opacity(0.5))
    }
}
