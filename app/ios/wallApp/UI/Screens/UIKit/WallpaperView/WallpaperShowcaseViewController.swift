//
//  WallpaperViewController.swift
//  WallApp
//

import Foundation
import WallApp
import UIKit

class WallpaperShowcaseViewController: UIViewController {
    
    var theme: Theme
    var render: RenderIos
    var viewState: WallpaperShowcaseViewState.Success {
        didSet {
            updateViewState()
        }
    }
    
    lazy var photoCollectionView: UICollectionView! = {
        let layout = UICollectionViewFlowLayout()
        layout.scrollDirection = .horizontal
        layout.minimumLineSpacing = 0
        layout.minimumInteritemSpacing = 0
        let collectionView = UICollectionView(frame: CGRect.zero, collectionViewLayout: layout)
        collectionView.isPagingEnabled = true
        collectionView.showsHorizontalScrollIndicator = false
        return collectionView
    }()
    
    var dataSource: UICollectionViewDiffableDataSource<Int, WallpaperPreviewViewState>!
    
    var pageControl: UIPageControl = UIPageControl()
    var pageControlBackgroundView: UIView = UIView()
    
    init(viewState: WallpaperShowcaseViewState.Success, theme: Theme, render: RenderIos) {
        self.viewState = viewState
        self.theme = theme
        self.render = render
        super.init(nibName: nil, bundle: nil)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func viewDidLoad() {
        Log.d("[WallpaperShowcaseViewModel] [Lifecycle] viewDidLoad()")
        super.viewDidLoad()
        self.navigationItem.hidesBackButton = true
        
        photoCollectionView?.register(WallpaperPhotoViewCell.self, forCellWithReuseIdentifier: "WallpaperPhotoViewCell")
        photoCollectionView.delegate = self
        configureDataSource()
    }
    
    // Handling ViewState Changes
    private func updateViewState() {
        switch onEnum(of: viewState) {
        case .success(let data):
            setupToolbarAndDisplayTabs(data: data)
        default:
            let _ = 0
        }
    }
    
    private func setupToolbarAndDisplayTabs(data: WallpaperShowcaseViewState.Success) {
        
        let paddingDefault = self.render.defaultViewSpec.paddingDefault.toCGFloat()
        let paddingSmall = self.render.defaultViewSpec.paddingSmall.toCGFloat()
        
        let initialPreviewIndex = (data.currentPreviewIndex as? Int) ?? 0
        
        let photoCollectionViewHeight = data.wallpaperPreviews.first!.viewSpec.height.toCGFloat()
        
        self.applySnapShot(images: data.wallpaperPreviews, initialPreviewIndex: initialPreviewIndex)
        
        /// PhotoCollectionView
        if !self.view.subviews.contains(photoCollectionView) {
            self.view.addSubview(photoCollectionView)
            photoCollectionView.translatesAutoresizingMaskIntoConstraints = false
            NSLayoutConstraint.activate([
                self.photoCollectionView.topAnchor.constraint(equalTo: self.view.topAnchor),
                self.photoCollectionView.leadingAnchor.constraint(equalTo: self.view.leadingAnchor),
                self.photoCollectionView.trailingAnchor.constraint(equalTo: self.view.trailingAnchor),
                self.photoCollectionView.heightAnchor.constraint(equalToConstant: photoCollectionViewHeight),
            ])
            photoCollectionView.scrollToItem(at: IndexPath(item: initialPreviewIndex, section: 0), at: .centeredHorizontally, animated: false)
            pageControl.currentPage = initialPreviewIndex
            pageControl.isUserInteractionEnabled = false
        }
        
        let indicatorColor = viewState.indicatorColor.uiColor
        if data.wallpaperPreviews.count > 1 && !self.view.subviews.contains(pageControl) {
            pageControl.numberOfPages = data.wallpaperPreviews.count
            pageControl.pageIndicatorTintColor = indicatorColor.withAlphaComponent(0.4)
            pageControl.currentPageIndicatorTintColor = indicatorColor
            self.view.insertSubview(pageControl, aboveSubview: self.photoCollectionView)
            pageControl.translatesAutoresizingMaskIntoConstraints = false
            NSLayoutConstraint.activate([
                self.pageControl.bottomAnchor.constraint(equalTo: self.photoCollectionView.bottomAnchor, constant: -paddingDefault),
                self.pageControl.centerXAnchor.constraint(equalTo: self.photoCollectionView.centerXAnchor),
            ])
            
            let pillBackgroundColor = viewState.indicatorPillBackgroundColor.uiColor
            pageControlBackgroundView.backgroundColor = pillBackgroundColor
            pageControlBackgroundView.translatesAutoresizingMaskIntoConstraints = false
            
            let pageControlBackgroundConstant: CGFloat
            if #available(iOS 18.0, *) {
                pageControlBackgroundConstant = 3
            } else {
                pageControlBackgroundConstant = 22
            }
            
            view.insertSubview(pageControlBackgroundView, belowSubview: pageControl)
            NSLayoutConstraint.activate([
                pageControlBackgroundView.leadingAnchor.constraint(equalTo: pageControl.leadingAnchor, constant: pageControlBackgroundConstant),
                pageControlBackgroundView.trailingAnchor.constraint(equalTo: pageControl.trailingAnchor, constant: -pageControlBackgroundConstant),
                pageControlBackgroundView.topAnchor.constraint(equalTo: pageControl.topAnchor, constant: 3),
                pageControlBackgroundView.bottomAnchor.constraint(equalTo: pageControl.bottomAnchor, constant: -3)
            ])
        }
        
        pageControl.numberOfPages = data.wallpaperPreviews.count
        pageControl.currentPage = initialPreviewIndex
        
        updateView(data: data)
    }
    
    func updateView(data: WallpaperShowcaseViewState.Success) {
        photoCollectionView.applyShape(shapeSpec: data.viewSpec.wallpaperPreviewShapeSpec)
        self.view.layoutIfNeeded()
    }
    
    func configureDataSource() {
        dataSource = UICollectionViewDiffableDataSource<Int, WallpaperPreviewViewState>(collectionView: photoCollectionView) { [unowned self] (collectionView, indexPath, item) -> UICollectionViewCell? in
            let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "WallpaperPhotoViewCell", for: indexPath) as? WallpaperPhotoViewCell
            cell?.configureCell(render: self.render, theme: self.theme, imageViewState: item.imageViewState)
            cell?.customImageView.addTapGesture { [unowned self] in
                item.onClick.invoke()
            }
            return cell
        }
        
    }
    
    private var isFirstApplySnapShot = true
    private var pendingScrollToIndex: Int? = nil
    func applySnapShot(images: [WallpaperPreviewViewState], initialPreviewIndex: Int = 0) {
        var snapshot = NSDiffableDataSourceSnapshot<Int, WallpaperPreviewViewState>()
        snapshot.appendSections([0])
        snapshot.appendItems(images, toSection: 0)
        dataSource.apply(snapshot, animatingDifferences: false)
        if isFirstApplySnapShot && initialPreviewIndex != 0 {
            pendingScrollToIndex = initialPreviewIndex
            isFirstApplySnapShot = false
        }
    }
    
    override func viewWillLayoutSubviews() {
        pageControlBackgroundView.layer.cornerRadius = pageControlBackgroundView.frame.height / 2
    }
    
    override func viewDidLayoutSubviews() {
        Log.d("[WallpaperShowcaseViewController] [Lifecycle] viewDidLayoutSubviews()")
    }
    
    override func viewDidAppear(_ animated: Bool) {
        Log.d("[WallpaperShowcaseViewController] [Lifecycle] viewDidAppear()")
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        self.navigationController?.isNavigationBarHidden = true
        self.navigationItem.hidesBackButton = true
        
        Log.d("[WallpaperShowcaseViewController] [Lifecycle] viewWillAppear()")
        if let index = pendingScrollToIndex {
            photoCollectionView.scrollToItem(at: IndexPath(item: index, section: 0), at: .centeredHorizontally, animated: false)
            pageControl.currentPage = index
            pendingScrollToIndex = nil
        }
    }
    
    override func traitCollectionDidChange(_ previousTraitCollection: UITraitCollection?) {
        super.traitCollectionDidChange(previousTraitCollection)
        updateUIUserInterfaceStyle(traitCollection.userInterfaceStyle, "traitCollectionDidChange()")
    }
    
    override func viewDidDisappear(_ animated: Bool) {
        super.viewDidDisappear(animated)
        Log.d("[WallpaperShowcaseViewModel] [Lifecycle] viewDidDisappear()")
    }
}

extension WallpaperShowcaseViewController: UICollectionViewDelegateFlowLayout, UIScrollViewDelegate {
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
        return CGSize(width: render.windowFrame.deviceWidth.toCGFloat(), height: photoCollectionView.frame.height)
    }
    
    func scrollViewDidEndScrolling(_ scrollView: UIScrollView) {
        let centerPoint = CGPoint(x: UIScreen.main.bounds.midX, y: photoCollectionView.frame.midY)
        let collectionViewCenterPoint = self.view.convert(centerPoint, to: photoCollectionView)
        
        if let indexPath = photoCollectionView.indexPathForItem(at: collectionViewCenterPoint) {
            let index = indexPath.item
            viewState.onPageChangedWallpaperPreview(KotlinInt(int: Int32(index)))
            pageControl.currentPage = index
        }
    }
    
    func scrollViewDidEndDecelerating(_ scrollView: UIScrollView) {
        self.scrollViewDidEndScrolling(scrollView)
    }
    
    func scrollViewDidEndDragging(_ scrollView: UIScrollView, willDecelerate decelerate: Bool) {
        if !decelerate {
            self.scrollViewDidEndScrolling(scrollView)
        }
    }
    
    func scrollViewDidScroll(_ scrollView: UIScrollView) {
        self.scrollViewDidEndScrolling(scrollView)
    }
}
