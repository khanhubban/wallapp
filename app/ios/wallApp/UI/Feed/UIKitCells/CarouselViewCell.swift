//
//  CarouselViewCell.swift
//  WallApp
//

import UIKit
import WallApp
import SwiftUI

class CarouselViewCell: UICollectionViewCell {
    
    //MARK: Variables
    @State var carouselLocation = 0
    @State var relativeLoc = 0
    
    var render: RenderIos!
    var theme: Theme!
    var viewState: CarouselViewState!
    var viewSpec: CarouselViewSpec!
    
    private var currentPage: Int {
        carouselLocation % viewState.pages.count
    }
    
    private var pageCount: Int {
        viewState.pages.count * 3
    }
    
    private var pageHeight: CGFloat {
        return viewSpec.height.toCGFloat()
    }
    
    private var pageWidth: CGFloat {
        viewSpec.pageWidth.toCGFloat()
    }
    
    fileprivate var scalingCarousel: ScalingCarouselView!
    
    var arrPages: [CommonView] = []
    
    var isInitialized = false
    
    func configureCell(render: RenderIos!, theme: Theme!, viewState: CarouselViewState!, viewSpec: CarouselViewSpec!) {
        self.carouselLocation = carouselLocation
        self.relativeLoc = relativeLoc
        self.render = render
        self.theme = theme
        self.viewState = viewState
        self.viewSpec = viewSpec
        
        let pages = viewState.pages + viewState.pages + viewState.pages
        pages.enumerated().forEach { pageData in
            let page = pages[pageData.offset]
            let view = CommonView(
                viewState: page.view.viewState,
                viewSpec: ExhibitViewSpec(width: pageWidth, height: pageHeight),
                applyScrollParallax: page.view.applyScrollParallax
            )
            arrPages.append(view)
        }
        
        if !isInitialized {
            self.addCarousel()
            isInitialized = true
        }
        
        self.updateCarousel()
    }
    
    
    func addCarousel() {
        let frame = CGRect(x: 0, y: 0, width: self.bounds.width, height: pageHeight)
        scalingCarousel = ScalingCarouselView(withFrame: frame, andInset: (bounds.width - pageWidth) / 2.0)
        scalingCarousel.scrollDirection = .horizontal
        scalingCarousel.dataSource = self
        scalingCarousel.delegate = self
        scalingCarousel.translatesAutoresizingMaskIntoConstraints = false
        scalingCarousel.backgroundColor = .clear
        
        scalingCarousel.register(CarouselPage.self, forCellWithReuseIdentifier: "cell")
        
        self.addSubview(scalingCarousel)
        
        // Constraints
        scalingCarousel.widthAnchor.constraint(equalTo: self.widthAnchor, multiplier: 1).isActive = true
        scalingCarousel.heightAnchor.constraint(equalToConstant: pageHeight).isActive = true
        scalingCarousel.leadingAnchor.constraint(equalTo: self.leadingAnchor).isActive = true
        scalingCarousel.topAnchor.constraint(equalTo: self.topAnchor, constant: 0).isActive = true
    
    }
    
    func updateCarousel() {
        scalingCarousel.reloadData()
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.1 , execute: {
            let middleIndex = Int(self.viewState.initialPage) + (self.viewState.pages.count * 1000)
            self.scalingCarousel.scrollToItem(at: IndexPath(item: middleIndex, section: 0), at: .centeredHorizontally, animated: false)
        })
    }
    
}

extension CarouselViewCell: UICollectionViewDataSource {
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return pageCount * 10000
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "cell", for: indexPath) as! CarouselPage
        let view = arrPages[indexPath.item % pageCount]
        cell.configureCell(render: self.render, theme: self.theme, viewState: view.viewState as? ExhibitViewState, viewSpec: view.viewSpec as? ExhibitViewSpec)
        return cell
    }
    
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        if let cell = scalingCarousel.cellForItem(at: indexPath), cell == scalingCarousel.currentCenterCell {
            let viewState = viewState.pages[indexPath.item % viewState.pages.count]
            viewState.onClick?.invoke()
        } else {
            UIView.animate(withDuration: 0.15, animations: { [weak self] in
                self?.scalingCarousel.scrollToItem(at: indexPath, at: .centeredHorizontally, animated: false)
                self?.scalingCarousel.layoutIfNeeded()
            })
        }
    }
}

extension CarouselViewCell: UICollectionViewDelegate {
    
    func scrollViewDidScroll(_ scrollView: UIScrollView) {
        scalingCarousel.didScroll()
    }
}


class CarouselPage: ScalingCarouselCell {
    
    var imageView: CustomImageView!
    var titleView: StyledLabel!
    
    var render: RenderIos!
    var theme: Theme!
    var viewState: ExhibitViewState!
    var viewSpec: ExhibitViewSpec!
    var contentColor = Color.white
    
    var isInitialized = false
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
   
    
    func configureCell(render: RenderIos!, theme: Theme!, viewState: ExhibitViewState!, viewSpec: ExhibitViewSpec!, contentColor: SwiftUI.Color = Color.white) {
        self.render = render
        self.theme = theme
        self.viewState = viewState
        self.viewSpec = viewSpec
        self.contentColor = contentColor
        
        if !isInitialized {
            self.initialize()
        }

        imageView.theme = self.theme
        imageView.imageViewState = viewState.imageViewState
        imageView.setup()
        
        titleView.updateWith(text: viewState.label, theme: self.theme, colorOverride: contentColor)
        
        self.imageView.applyShape(shapeSpec: viewState.shapeSpec)
    }
    
    func initialize() {
        self.scaleMinimum = 0.86
        mainView = UIView(frame: contentView.bounds)
        contentView.addSubview(mainView)
        mainView.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint.activate([
            mainView.leadingAnchor.constraint(equalTo: contentView.leadingAnchor),
            mainView.trailingAnchor.constraint(equalTo: contentView.trailingAnchor),
            mainView.topAnchor.constraint(equalTo: contentView.topAnchor),
            mainView.bottomAnchor.constraint(equalTo: contentView.bottomAnchor)
        ])
        
        imageView = CustomImageView(
            theme: theme,
            imageViewState: viewState.imageViewState
        )
        
        mainView.addSubview(imageView)
        imageView.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint.activate([
            imageView.centerXAnchor.constraint(equalTo: mainView.centerXAnchor),
            imageView.topAnchor.constraint(equalTo: mainView.topAnchor),
        ])
        
        mainView.clipsToBounds = true
        
        if let label = viewState.label {
            let scrimColor = theme.themeColors.scrim?.toColor() ?? Color.black.opacity(0.5)
            titleView = StyledLabel(text: label, theme: theme, colorOverride: contentColor)
            imageView.addSubview(titleView)
            titleView.translatesAutoresizingMaskIntoConstraints = false
            NSLayoutConstraint.activate([
                titleView.leadingAnchor.constraint(equalTo: imageView.leadingAnchor),
                titleView.trailingAnchor.constraint(equalTo: imageView.trailingAnchor),
                titleView.topAnchor.constraint(equalTo: imageView.topAnchor),
                titleView.bottomAnchor.constraint(equalTo: imageView.bottomAnchor)
            ])
            
            titleView.lineBreakMode = .byWordWrapping
            titleView.backgroundColor = scrimColor.uiColor()
        }
        
        isInitialized = true
    }
}
