//
//  FeedCollectionView.swift
//  WallApp
//

import WallApp
import Kingfisher
import UIKit

class FeedCollectionView: UICollectionViewController {
    var render: RenderIos!
    var theme: Theme! {
        didSet {
            updateThemeChanges()
        }
    }
    var feedViews: [CommonView] = []
    var prevFeedViews: [CommonView] = []
    var pagingViewEventSink: ((PagingViewEvent) -> Void)?
    var feedScrollPositionUpdate: ((FeedScrollPosition) -> Void)?
    var dataSource: UICollectionViewDiffableDataSource<Section, CommonView>!
    var scrollOffsetControllers: [ScrollOffsetController]!
    var viewsVisibleListener: ViewsVisibleListener?
    var highlightCarouselViewState: HighlightCarouselViewState?
    
    private var sectionHeaderRegistered = false
    
    private var previousScrollToTopCounter = 0
    
    var nativeAdsCacheManager: NativeAdsCacheManager!
    
    private var paddingHeaderViewState: PaddingHeaderViewState?
    
    init(
        collectionViewLayout layout: UICollectionViewLayout,
        feedViews: [CommonView],
        render: RenderIos,
        theme: Theme,
        pagingViewEventSink: ((PagingViewEvent) -> Void)? = nil,
        feedScrollPositionUpdate: ((FeedScrollPosition) -> Void)? = nil,
        scrollOffsetControllers: [ScrollOffsetController] = [],
        viewsVisibleListener: ViewsVisibleListener? = nil,
        highlightCarouselViewState: HighlightCarouselViewState? = nil,
        paddingHeaderViewState: PaddingHeaderViewState? = nil
    ) {
        super.init(collectionViewLayout: layout)
        self.feedViews = feedViews
        self.render = render
        self.theme = theme
        self.pagingViewEventSink = pagingViewEventSink
        self.feedScrollPositionUpdate = feedScrollPositionUpdate
        self.scrollOffsetControllers = scrollOffsetControllers
        self.viewsVisibleListener = viewsVisibleListener
        self.nativeAdsCacheManager = NativeAdsCacheManager()
        self.highlightCarouselViewState = highlightCarouselViewState
        self.paddingHeaderViewState = paddingHeaderViewState
        if let layout = collectionViewLayout as? StaggeredGridLayout {
            layout.theme = theme
        }
    }
    
    required init?(coder: NSCoder) {
        super.init(coder: coder)
    }
    
    enum Section {
        case main
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        if let layout = collectionViewLayout as? StaggeredGridLayout {
            layout.delegate = self
            if let highlightCarouselViewState {
                layout.headerHeight = highlightCarouselViewState.carouselViewSpec.height.toCGFloat()
                layout.headerType = .highlights
            } else if let paddingHeaderViewState {
                layout.headerHeight = paddingHeaderViewState.height
                layout.headerType = .padding
            }
        }
        
        // Register cell class
        collectionView?.register(FeedPreviewPreviewCell.self, forCellWithReuseIdentifier: "FeedPreviewPreviewCell")
        collectionView?.register(WallpaperFeedPreviewCell.self, forCellWithReuseIdentifier: "WallpaperFeedPreviewCell")
        collectionView?.register(CollectionPreviewCell.self, forCellWithReuseIdentifier: "CollectionPreviewCell")
        collectionView?.register(CollectionWallpaperFeedPreviewCell.self, forCellWithReuseIdentifier: "CollectionWallpaperFeedPreviewCell")
        collectionView?.register(SpacerViewCell.self, forCellWithReuseIdentifier: "SpacerViewCell")
        collectionView?.register(CarouselViewCell.self, forCellWithReuseIdentifier: "CarouselViewCell")
        collectionView?.register(NativeAdCollectionViewCell.self, forCellWithReuseIdentifier: "NativeAdCollectionViewCell")
        collectionView?.register(PresetAdViewCell.self, forCellWithReuseIdentifier: "PresetAdViewCell")
        collectionView?.register(ArtistPreviewCell.self, forCellWithReuseIdentifier: "ArtistPreviewCell")
        collectionView?.register(ProfileCuratorCell.self, forCellWithReuseIdentifier: "ProfileCuratorCell")
        
        if let paddingHeaderViewState {
            collectionView?.register(PaddingHeaderView.self, forSupplementaryViewOfKind: UICollectionView.elementKindSectionHeader, withReuseIdentifier: PaddingHeaderView.identifier)
        } else {
            collectionView?.register(ExploreHighlightsHeaderView.self, forSupplementaryViewOfKind: UICollectionView.elementKindSectionHeader, withReuseIdentifier: ExploreHighlightsHeaderView.identifier)
        }
        
        if let layout = collectionViewLayout as? StaggeredGridLayout {
            layout.register(BackgroundDecorationView.self, forDecorationViewOfKind: BackgroundDecorationView.identifier)
        }
        
        collectionView?.backgroundColor = .clear
        collectionView?.delegate = self
        
        collectionView?.showsHorizontalScrollIndicator = false
        collectionView?.showsVerticalScrollIndicator = false
        collectionView?.prefetchDataSource = self
        collectionView.contentInsetAdjustmentBehavior = .never
        
        configureDataSource()
        
        nativeAdsCacheManager.prefetchAds(for: feedViews)
    }
    
    func updateContentEdgeInset(top: CGFloat = 0, bottom: CGFloat = 0) {
        collectionView.contentInset = UIEdgeInsets(top: top, left: 0, bottom: bottom, right: 0)
    }
    
    var previousBounds = CGRect.zero

    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        
        if collectionView.bounds != previousBounds && collectionView.bounds.width > 0 && collectionView.bounds.height > 0 {
            collectionView.collectionViewLayout.invalidateLayout()
            previousBounds = collectionView.bounds
        }
        
        updateFeedScrollPosition(collectionView)
    }
    
    override func collectionView(_ collectionView: UICollectionView,
                        willDisplay cell: UICollectionViewCell,
                        forItemAt indexPath: IndexPath) {
        guard let pagingViewEventSink = pagingViewEventSink else { return }
        if indexPath.row == feedViews.count - 10 {
            pagingViewEventSink(PagingViewEvent.LoadMore())
        }
    }
    
    func configureDataSource() {
        dataSource = UICollectionViewDiffableDataSource<Section, CommonView>(collectionView: collectionView) { (collectionView, indexPath, view) -> UICollectionViewCell? in
            
            let model = view
            let viewState = model.viewState
            let viewSpec = model.viewSpec
            
            if let viewState = viewState as? WallpaperPreviewViewState, viewState.isSingle == true {
                let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "WallpaperFeedPreviewCell", for: indexPath) as? WallpaperFeedPreviewCell
                cell?.configureCell(wallpaperPreview: viewState, theme: self.theme, render: self.render)
                return cell
            }
            
            if let viewState = viewState as? WallpaperPreviewViewState, viewState.isSingle == false {
                let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "CollectionWallpaperFeedPreviewCell", for: indexPath) as? CollectionWallpaperFeedPreviewCell
                cell?.configureCell(wallpaperPreview: viewState, viewSpec: viewSpec! as! FeedContentPreviewViewSpec, theme: self.theme, render: self.render)
                return cell
            }

            if let state = viewState as? CollectionPreviewViewState, let viewSpec = viewSpec as? CollectionPreviewViewSpec {
                let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "CollectionPreviewCell", for: indexPath) as? CollectionPreviewCell
                cell?.configureCell(collectionPreview: state, viewSpec: viewSpec, theme: self.theme, render: self.render)
                return cell
            }
            
            if let spacerViewState = viewState as? SpacerViewState {
                let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "SpacerViewCell", for: indexPath) as? SpacerViewCell
                cell?.configureCell(viewState: spacerViewState)
                return cell
            }
            
            if let state = viewState as? CarouselViewState, let viewSpec = viewSpec as? CarouselViewSpec {
                let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "CarouselViewCell", for: indexPath) as? CarouselViewCell
                cell?.configureCell(render: self.render, theme: self.theme, viewState: state, viewSpec: viewSpec)
                return cell
            }
            
            if let state = viewState as? FeedAdViewState {
                let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "NativeAdCollectionViewCell", for: indexPath) as? NativeAdCollectionViewCell
                cell?.configureCell(render: self.render, theme: self.theme, fallbackAdViewState: state.fallbackAdViewState, nativeAdsCacheManager: self.nativeAdsCacheManager, index: indexPath.item)
                return cell
            }
            
            if let state = viewState as? AdViewState {
                let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "PresetAdViewCell", for: indexPath) as? PresetAdViewCell
                cell?.configureCell(render: self.render, theme: self.theme, adViewState: state)
                return cell
            }
            
            if let viewState = viewState as? ArtistPreviewViewState, let viewSpec = viewSpec as? ArtistPreviewViewSpecOnboarding {
                let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "ArtistPreviewCell", for: indexPath) as? ArtistPreviewCell
                let prevViewState = self.prevFeedViews.filter { $0.viewState is ArtistPreviewViewState }.first { ($0.viewState as! ArtistPreviewViewState).name.string == viewState.name.string }?.viewState as? ArtistPreviewViewState
                let updateFollowIndicator = viewState.followIndicator?.visible != prevViewState?.followIndicator?.visible
                cell?.configureCell(render: self.render, theme: self.theme, viewState: viewState, viewSpec: viewSpec, updateFollowIndicator: updateFollowIndicator)
                return cell
            }
            
            if let viewState = viewState as? ProfileCuratorViewState {
                let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "ProfileCuratorCell", for: indexPath) as? ProfileCuratorCell
                cell?.configureCell(render: self.render, theme: self.theme, viewState: viewState)
                return cell
            }

            let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "FeedPreviewPreviewCell", for: indexPath) as? FeedPreviewPreviewCell
            cell?.render = self.render
            cell?.theme = self.theme
            cell?.view = view
            return cell
        }
        
        if let paddingHeaderViewState {
            dataSource.supplementaryViewProvider = { collectionView, kind, indexPath in
                let header = collectionView.dequeueReusableSupplementaryView(ofKind: kind, withReuseIdentifier: PaddingHeaderView.identifier, for: indexPath) as? PaddingHeaderView
                header?.configureCell(theme: self.theme)
                return header
            }
        } else {
            dataSource.supplementaryViewProvider = { collectionView, kind, indexPath in
                let header = collectionView.dequeueReusableSupplementaryView(ofKind: kind, withReuseIdentifier: ExploreHighlightsHeaderView.identifier, for: indexPath) as? ExploreHighlightsHeaderView
                header?.configureCell(render: self.render, theme: self.theme, highlightCarouselViewState: self.highlightCarouselViewState)
                return header
            }
        }
        
        var snapshot = NSDiffableDataSourceSnapshot<Section, CommonView>()
        snapshot.appendSections([.main])
        snapshot.appendItems(feedViews, toSection: .main)
        dataSource.apply(snapshot, animatingDifferences: false)
    }
    
    func updateFeedViewList(with feedViews: [CommonView], and highlightCarouselViewState: HighlightCarouselViewState? = nil) {
        self.prevFeedViews = self.feedViews
        self.feedViews = feedViews
        self.highlightCarouselViewState = highlightCarouselViewState
        
        (collectionViewLayout as? StaggeredGridLayout)?.clearCache()
        
        if let highlightCarouselViewState, let layout = collectionViewLayout as? StaggeredGridLayout {
            getHeaderView()?.configureCell(render: render, theme: theme, highlightCarouselViewState: highlightCarouselViewState)
            layout.headerHeight = highlightCarouselViewState.carouselViewSpec.height.toCGFloat()
        }
        
        // Create a new snapshot
        var snapshot = NSDiffableDataSourceSnapshot<Section, CommonView>()
        snapshot.appendSections([.main])
        snapshot.appendItems(self.feedViews, toSection: .main)
        
        // Apply the snapshot to the data source to update the UI
        dataSource.apply(snapshot, animatingDifferences: false)
        
        nativeAdsCacheManager.prefetchAds(for: feedViews)
    }
    
    private func updateThemeChanges() {
        guard let layout = collectionViewLayout as? StaggeredGridLayout else { return }
        layout.theme = theme
        
        var snapshot = dataSource.snapshot()
        snapshot.reloadItems(feedViews.filter { $0.viewState is NoDataViewState || $0.viewState is MenuItemViewState })
        dataSource.apply(snapshot, animatingDifferences: false)
    }
    
    /// Mark: - Scroll events
    
    override func scrollViewDidScroll(_ scrollView: UIScrollView) {
        let contentOffsetY = scrollView.contentOffset.y
        for scrollOffsetController in scrollOffsetControllers {
            scrollOffsetController.onScroll(offset: contentOffsetY, upperBound: scrollView.maxContentOffset.y)
        }
        // pass contentOffsetY to headerView
        if let headerView = getHeaderView() {
            headerView.updateContentBasedOnOffsetY(contentOffsetY)
        }
        
        guard let viewsVisibleListener = viewsVisibleListener else { return }
        if (scrollView.panGestureRecognizer.translation(in: scrollView.superview).y > 0) {
            viewsVisibleListener.onScrollDirectionChanged(scrollDirection: .ascending)
        } else {
            viewsVisibleListener.onScrollDirectionChanged(scrollDirection: .descending)
        }
        let visibleStates = collectionView?.indexPathsForVisibleItems.map { indexPath in
            ViewVisibleState(visibleIndex: Int32(indexPath.item), viewId: feedViews[indexPath.item].viewState.viewId)
        }
        if let visibleStates {
            viewsVisibleListener.onVisibleViewsChanged(visibleViews: visibleStates)
        }
        
        scrollView.contentOffset = CGPoint(x: scrollView.contentOffset.x, y: max(scrollView.contentOffset.y, -100))
    }
    
    override func scrollViewDidEndDragging(_ scrollView: UIScrollView, willDecelerate decelerate: Bool) {
        if !decelerate {
            performScrollEndActions(scrollView)
        }
    }
    
    override func scrollViewDidEndDecelerating(_ scrollView: UIScrollView) {
        performScrollEndActions(scrollView)
    }
    
    override func scrollViewDidEndScrollingAnimation(_ scrollView: UIScrollView) {
        performScrollEndActions(scrollView)
    }
    
    func scrollToTop() {
        collectionView.setContentOffset(.zero, animated: true)
    }
    
    private func performScrollEndActions(_ scrollView: UIScrollView) {
        updateFeedScrollPosition(scrollView)
        updateScrollOffsetControllersOnDragEnd()
        updateVisibleViewsSettled()
    }
    
    private func updateVisibleViewsSettled() {
        let visibleStates = collectionView?.indexPathsForVisibleItems.map { indexPath in
            ViewVisibleState(visibleIndex: Int32(indexPath.item), viewId: feedViews[indexPath.item].viewState.viewId)
        }
        if let visibleStates {
            viewsVisibleListener?.onVisibleViewsSettled(visibleViews: visibleStates)
        }
    }
    
    private func updateScrollOffsetControllersOnDragEnd() {
        for scrollOffsetController in scrollOffsetControllers {
            scrollOffsetController.onScrollEnd()
        }
    }
    
    private func updateFeedScrollPosition(_ scrollView: UIScrollView) {
        if let feedScrollPositionUpdate {
            let contentOffset = scrollView.contentOffset
            if contentOffset.y <= 0 {
                feedScrollPositionUpdate(.top)
            } else if contentOffset.y > scrollView.contentSize.height - scrollView.bounds.height {
                feedScrollPositionUpdate(.bottom)
            } else {
                feedScrollPositionUpdate(.middle)
            }
        }
    }
    
    func scrollToTopIfNeeded(scrollToTopCounter: Int) {
        if scrollToTopCounter > previousScrollToTopCounter {
            scrollToTop()
            previousScrollToTopCounter = scrollToTopCounter
        }
    }
    
    func updatePaddingHeaderViewState(_ paddingHeaderViewState: PaddingHeaderViewState) {
        self.paddingHeaderViewState = paddingHeaderViewState
        if let layout = collectionViewLayout as? StaggeredGridLayout {
            layout.headerHeight = paddingHeaderViewState.height
        }
    }
    
    private func getHeaderView() -> ExploreHighlightsHeaderView? {
        return collectionView.supplementaryView(forElementKind: UICollectionView.elementKindSectionHeader, at: IndexPath(item: 0, section: 0)) as? ExploreHighlightsHeaderView
    }
}

extension FeedCollectionView : StaggerGridLayoutDelegate {
    
    func collectionView(_ collectionView: UICollectionView, sizeForViewAtIndexPath indexPath: IndexPath) -> CGSize {
        let view = feedViews[indexPath.item]
        var height: CGFloat = 0
        var width: CGFloat = 0
        if let viewSpec = view.viewSpec as? CarouselViewSpec {
            height = viewSpec.height.toCGFloat()
        } else if let viewSpec = view.viewSpec as? CollectionPreviewViewSpec {
            height = viewSpec.height.toCGFloat()
            width = viewSpec.width.toCGFloat()
        } else if let viewSpec = view.viewSpec as? FeedContentPreviewViewSpec {
            height = viewSpec.height.toCGFloat()
            width = viewSpec.width.toCGFloat()
        } else if let viewState = view.viewState as? SpacerViewState {
            height = viewState.height?.toCGFloat() ?? 100
            width = viewState.width?.toCGFloat() ?? 0
        } else if let viewSpec = view.viewSpec as? AdViewSpec {
            height = viewSpec.height.toCGFloat()
        } else if view.viewState is FeedAdViewState {
            height = FeedAdViewState.nativeAdHeight
        } else if let viewState = view.viewState as? ProfileCuratorViewState {
            let viewSpec = viewState.viewSpec
            width = viewSpec.width.toCGFloat()
            height = viewSpec.height.toCGFloat()
        } else if let viewSpec = view.viewSpec as? ArtistPreviewViewSpecOnboarding {
            height = viewSpec.height.toCGFloat()
            width = viewSpec.width.toCGFloat()
        } else if view.viewState is NoDataViewState {
            height = 240
        }
        return CGSize(width: width, height: height)
    }
    
    func collectionView(_ collectionView: UICollectionView, spanForViewAtIndexPath indexPath: IndexPath) -> StaggeredGridSpan {
        let view = feedViews[indexPath.item]
        if let viewSpec = view.viewSpec as? FeedViewViewSpec {
            if viewSpec.useMaxItemSpan == true {
                if viewSpec.useZeroFeedPadding == true {
                    return .maxNoPadding
                } else {
                    return .max
                }
            } else {
                return .single
            }
        }
        return .single
    }
    
    override func collectionView(_ collectionView: UICollectionView, didEndDisplaying cell: UICollectionViewCell, forItemAt indexPath: IndexPath) {
        guard let cell = cell as? FeedPreviewPreviewCell else { return }
        cell.hostConfiguration = nil
        cell.view = nil
    }
    
    override func collectionView(_ collectionView: UICollectionView, viewForSupplementaryElementOfKind kind: String, at indexPath: IndexPath) -> UICollectionReusableView {
        let header = collectionView.dequeueReusableSupplementaryView(ofKind: kind, withReuseIdentifier: ExploreHighlightsHeaderView.identifier, for: indexPath)
        return header
    }
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, referenceSizeForHeaderInSection section: Int) -> CGSize {
        return .init(width: view.frame.width, height: 340)
    }
    
}

extension FeedCollectionView: UICollectionViewDataSourcePrefetching {
    
    func collectionView(_ collectionView: UICollectionView, prefetchItemsAt indexPaths: [IndexPath]) {
        indexPaths.forEach { indexPath in
            let view = feedViews[indexPath.item]
            let index = indexPath.item
            if view.viewState is FeedAdViewState {
                Log.d("[NativeAds] collectionView(): Loading ad at index: \(index)")
                nativeAdsCacheManager.prefetchAd(for: index)
            }
        }
    }
    
}
