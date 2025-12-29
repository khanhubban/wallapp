//
//  ExploreHighlightsHeaderView.swift
//  WallApp
//

import WallApp
import UIKit

class ExploreHighlightsHeaderView: UICollectionReusableView {
    
    static let identifier = "ExploreHighlightsHeaderView"
    
    private var isInitialized = false
    
    var theme: Theme!
    var render: RenderIos!
    var viewState: HighlightCarouselViewState!
    
    lazy var photoCollectionView: UICollectionView! = {
        let layout = UICollectionViewFlowLayout()
        layout.scrollDirection = .horizontal
        layout.minimumLineSpacing = 0
        layout.minimumInteritemSpacing = 0
        let collectionView = UICollectionView(frame: CGRect.zero, collectionViewLayout: layout)
        collectionView.isPagingEnabled = true
        collectionView.showsHorizontalScrollIndicator = false
        if let backgroundColor = viewState?.backgroundColor.toColor(themeColors: theme.themeColors)?.uiColor() {
            collectionView.backgroundColor = backgroundColor
        }
        return collectionView
    }()
    
    var dataSource: UICollectionViewDiffableDataSource<Int, CarouselPageViewState>!
    
    var pageControl: UIPageControl = UIPageControl()
    private var pageControlBottomConstraint: NSLayoutConstraint?
    private var shadowBottomConstraint: NSLayoutConstraint?
    private var pageControlBottomPadding: CGFloat {
        HighlightCarouselConstants.excessHeight + render.defaultViewSpec.paddingDefault.toCGFloat()
    }
    private var currentPageManuallyUpdated = false
    
    private var autoScrollTimer: Timer?
    private var isAutoScrolling = false
    private var autoScrollDirection = 1 // 1 for forward, -1 for backward
    
    private var lastContentOffsetY: CGFloat = 0
    private var lastContentAlpha: CGFloat = 1
    
    override init(frame: CGRect) {
        super.init(frame: frame)
    }
    
    func configureCell(render: RenderIos, theme: Theme, highlightCarouselViewState: HighlightCarouselViewState?) {
        guard let highlightCarouselViewState = highlightCarouselViewState else {
            return
        }
        
        self.render = render
        self.theme = theme
        self.viewState = highlightCarouselViewState
        
        if !isInitialized {
            photoCollectionView?.register(HighlightCarouselPageViewCell.self, forCellWithReuseIdentifier: HighlightCarouselPageViewCell.identifier)
            photoCollectionView.delegate = self
            configureDataSource()
            isInitialized = true
        }
        
        // Reset the timer when configuring the cell
        autoScrollTimer?.invalidate()
        autoScrollTimer = nil
        
        setupAndUpdateCarousel(highlightCarouselViewState)
    }
    
    func updateContentBasedOnOffsetY(_ offsetY: CGFloat) {
        var alpha: CGFloat = 1
        let contentHeight = self.viewState.carouselViewSpec.height.toCGFloat()
        let alphaThreshold = contentHeight / 3
        if offsetY > alphaThreshold {
            let progress = (offsetY - alphaThreshold) / alphaThreshold
            alpha = 1 - progress
        }
        
        let offsetYScaled = offsetY * HighlightCarouselConstants.scrollScaler
        
        for cell in photoCollectionView.visibleCells {
            if let cell = cell as? HighlightCarouselPageViewCell {
                cell.updateContentOffsetY(offsetYScaled)
                cell.updateContentAlpha(alpha)
            }
        }
        
        pageControlBottomConstraint?.constant = -pageControlBottomPadding - offsetYScaled
        pageControl.alpha = alpha
        
        lastContentOffsetY = offsetYScaled
        lastContentAlpha = alpha
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    private func setupAndUpdateCarousel(_ data: HighlightCarouselViewState) {
        let pages = data.carouselViewState.pages
        
        let initialPreviewIndex = Int(data.carouselViewState.initialPage)
        
        self.applySnapShot(data.carouselViewState)
        
        if !self.subviews.contains(photoCollectionView) {
            self.addSubview(photoCollectionView)
            photoCollectionView.fillSuperview()
            pageControl.currentPage = initialPreviewIndex
            pageControl.isUserInteractionEnabled = false
        }
        
        if !self.subviews.contains(pageControl) {
            pageControl.pageIndicatorTintColor = .white.withAlphaComponent(0.4)
            pageControl.currentPageIndicatorTintColor = .white
            self.insertSubview(pageControl, aboveSubview: self.photoCollectionView)
            pageControl.translatesAutoresizingMaskIntoConstraints = false
            NSLayoutConstraint.activate([
                self.pageControl.centerXAnchor.constraint(equalTo: self.photoCollectionView.centerXAnchor)
            ])
            pageControlBottomConstraint = pageControl.bottomAnchor.constraint(
                equalTo: self.photoCollectionView.bottomAnchor,
                constant: -pageControlBottomPadding
            )
            pageControlBottomConstraint?.isActive = true
        }
        
        pageControl.numberOfPages = pages.count
        if !currentPageManuallyUpdated {
            pageControl.currentPage = initialPreviewIndex
            
            // Scroll to initial index. Async is needed to ensure the collection view has been laid out and scrolling is done immediately after that.
            DispatchQueue.main.async {
                self.photoCollectionView.scrollToItem(at: IndexPath(item: initialPreviewIndex, section: 0), at: .centeredHorizontally, animated: false)
                self.layoutIfNeeded()
            }
        }
        
        startAutoScrollTimer()
    }
    
    private func startAutoScrollTimer() {
        autoScrollTimer?.invalidate()

        // Only start timer if there is more than one page and the user hasn't manually updated the page
        guard viewState.carouselViewState.pages.count > 1, !currentPageManuallyUpdated else { return }

        let firstDelay = Double(viewState.autoScrollFirstDelayInSeconds)
        let subsequentDelay = Double(viewState.autoScrollDelayInSeconds)

        // Start the timer with the first delay and update it with subsequent intervals after the first scroll
        autoScrollTimer = Timer.scheduledTimer(
            timeInterval: firstDelay,
            target: self,
            selector: #selector(autoScrollCarouselFirstDelay),
            userInfo: ["subsequentDelay": subsequentDelay],
            repeats: false
        )
    }

    @objc private func autoScrollCarouselFirstDelay(timer: Timer) {
        // Scroll the first time, then update the timer to use the subsequent delay
        autoScrollCarousel()

        if let subsequentDelay = timer.userInfo as? [String: Double] {
            autoScrollTimer = Timer.scheduledTimer(
                timeInterval: subsequentDelay["subsequentDelay"] ?? 4.0,
                target: self,
                selector: #selector(autoScrollCarousel),
                userInfo: nil,
                repeats: true
            )
        }
    }
    
    @objc private func autoScrollCarousel() {
        guard let currentIndexPath = photoCollectionView.indexPathsForVisibleItems.first else {
            return
        }
        let currentItem = currentIndexPath.item
        let numberOfItems = viewState.carouselViewState.pages.count
        
        // Calculate the next item based on the current direction
        var nextItem = currentItem + autoScrollDirection
        
        // Reverse direction if we've reached the end or the beginning
        if nextItem >= numberOfItems {
            autoScrollDirection = -1
            nextItem = currentItem + autoScrollDirection
        } else if nextItem < 0 {
            autoScrollDirection = 1
            nextItem = currentItem + autoScrollDirection
        }
        
        let nextIndexPath = IndexPath(item: nextItem, section: currentIndexPath.section)
        
        isAutoScrolling = true
        photoCollectionView.scrollToItem(at: nextIndexPath, at: .centeredHorizontally, animated: true)
        pageControl.currentPage = nextItem
    }
    
    func configureDataSource() {
        dataSource = UICollectionViewDiffableDataSource<Int, CarouselPageViewState>(collectionView: photoCollectionView) { [unowned self] (collectionView, indexPath, item) -> UICollectionViewCell? in
            let cell = collectionView.dequeueReusableCell(withReuseIdentifier: HighlightCarouselPageViewCell.identifier, for: indexPath) as? HighlightCarouselPageViewCell
            cell?.configureCell(render: self.render, theme: self.theme, viewState: item, contentOffsetY: lastContentOffsetY, contentAlpha: lastContentAlpha)
            return cell
        }
    }
    
    func applySnapShot(_ carouselViewState: CarouselViewState) {
        let pages = carouselViewState.pages
        var snapshot = NSDiffableDataSourceSnapshot<Int, CarouselPageViewState>()
        snapshot.appendSections([0])
        snapshot.appendItems(pages, toSection: 0)
        dataSource.apply(snapshot, animatingDifferences: false)
    }
}

extension ExploreHighlightsHeaderView: UICollectionViewDelegateFlowLayout, UIScrollViewDelegate {
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
        return CGSize(width: render.windowFrame.deviceWidth.toCGFloat(), height: photoCollectionView.frame.height)
    }
    
    func scrollViewDidEndScrolling(_ scrollView: UIScrollView) {
        let centerPoint = CGPoint(x: UIScreen.main.bounds.midX, y: photoCollectionView.frame.midY)
        let collectionViewCenterPoint = self.convert(centerPoint, to: photoCollectionView)
        
        if let indexPath = photoCollectionView.indexPathForItem(at: collectionViewCenterPoint) {
            let index = indexPath.item
            pageControl.currentPage = index
            currentPageManuallyUpdated = true
        }
        isAutoScrolling = false
    }
    
    func scrollViewDidEndDecelerating(_ scrollView: UIScrollView) {
        self.scrollViewDidEndScrolling(scrollView)
        self.restartAutoScrollTimer()
    }
    
    func scrollViewDidEndDragging(_ scrollView: UIScrollView, willDecelerate decelerate: Bool) {
        if !decelerate {
            self.scrollViewDidEndScrolling(scrollView)
            self.restartAutoScrollTimer()
        }
    }
    
    func scrollViewWillBeginDragging(_ scrollView: UIScrollView) {
        autoScrollTimer?.invalidate()
        autoScrollTimer = nil
        currentPageManuallyUpdated = true
    }
    
    private func restartAutoScrollTimer() {
        autoScrollTimer?.invalidate()
        autoScrollTimer = nil
        DispatchQueue.main.asyncAfter(deadline: .now() + 3.0) {
            self.startAutoScrollTimer()
        }
    }
    
    func collectionView(_ collectionView: UICollectionView, willDisplay cell: UICollectionViewCell, forItemAt indexPath: IndexPath) {
        if let cell = cell as? HighlightCarouselPageViewCell {
            cell.updateContentOffsetY(lastContentOffsetY)
            cell.updateContentAlpha(lastContentAlpha)
        }
    }
}

extension CarouselPageViewState {
    var exhibitViewState: ExhibitViewState {
        return view.viewState as! ExhibitViewState
    }
}
