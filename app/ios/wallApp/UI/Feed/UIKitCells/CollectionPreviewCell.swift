//
//  CollectionPreviewCell.swift
//  WallApp
//

import UIKit
@_exported import WallApp
@_exported import SwiftUI

class CollectionPreviewCell: UICollectionViewCell {
    
    var collectionPreview: CollectionPreviewViewState!
    var viewSpec: CollectionPreviewViewSpec!
    var theme: Theme!
    var render: RenderIos!
    
    var collectionContentPreviewImageStack: CollectionContentPreviewImageStack!
    
    var isInitialized: Bool = false
    
    func configureCell(collectionPreview: CollectionPreviewViewState, viewSpec: CollectionPreviewViewSpec, theme: Theme, render: RenderIos) {
        self.collectionPreview = collectionPreview
        self.theme = theme
        self.render = render
        
        let viewSpec = collectionPreview.viewSpec
        let heroImages = collectionPreview.layers.mapToStackItems(viewState: collectionPreview)
        
        if let heroImages {
            if !isInitialized {
                collectionContentPreviewImageStack = CollectionContentPreviewImageStack(theme: theme, heroImages: heroImages, render: render)
                
                self.collectionContentPreviewImageStack.translatesAutoresizingMaskIntoConstraints = false
                
                self.addSubview(collectionContentPreviewImageStack)
                
                NSLayoutConstraint.activate([
                    collectionContentPreviewImageStack.topAnchor.constraint(equalTo: self.topAnchor),
                    collectionContentPreviewImageStack.bottomAnchor.constraint(equalTo: self.bottomAnchor),
                    collectionContentPreviewImageStack.trailingAnchor.constraint(equalTo: self.trailingAnchor),
                    collectionContentPreviewImageStack.leadingAnchor.constraint(equalTo: self.leadingAnchor),
                    collectionContentPreviewImageStack.heightAnchor.constraint(equalToConstant: CGFloat(viewSpec.height))
                ])
                isInitialized = true
            }
            
            self.setup()
        }
    }
    
    func setup() {
        
        let viewSpec = collectionPreview.viewSpec
        let heroImages = collectionPreview.layers.mapToStackItems(viewState: collectionPreview)
        
        if let heroImages {
            
            collectionContentPreviewImageStack.theme = theme
            collectionContentPreviewImageStack.heroImages = heroImages
            collectionContentPreviewImageStack.render = render
            
            collectionContentPreviewImageStack.clipsToBounds = true
            collectionContentPreviewImageStack.setUp()
        }
        
    }
    
}

class CollectionContentPreviewImageStack: UIView {
    
    var theme: Theme
    var heroImages: [CollectionStackItem]
    var render: RenderIos
    
    private let verticleStackView: UIStackView = {
        let stackView = UIStackView()
        stackView.axis = .vertical
        stackView.alignment = .center
        stackView.distribution = .equalCentering
        return stackView
    }()
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    init(theme: Theme, heroImages: [CollectionStackItem], render: RenderIos) {
        self.theme = theme
        self.heroImages = heroImages
        self.render = render
        super.init(frame: .zero)
        
        self.verticleStackView.translatesAutoresizingMaskIntoConstraints = false
        addSubview(verticleStackView)
        NSLayoutConstraint.activate([
            verticleStackView.bottomAnchor.constraint(equalTo: self.bottomAnchor),
            verticleStackView.trailingAnchor.constraint(equalTo: self.trailingAnchor),
            verticleStackView.leadingAnchor.constraint(equalTo: self.leadingAnchor),
            verticleStackView.topAnchor.constraint(equalTo: self.topAnchor)
        ])
    }
    
    func setUp() {
        for (index, stackItem) in heroImages.enumerated() {
            var itemView: CollectionContentPreviewStackItemView? = nil

            if index < verticleStackView.arrangedSubviews.count {
                // The view already exists, update it
                if let view = verticleStackView.arrangedSubviews[index] as? CollectionContentPreviewStackItemView {
                    view.updateWith(theme: theme, item: stackItem)
                    view.setup()
                    itemView = view
                }
            } else {
                // Need to create a new view
                let view = CollectionContentPreviewStackItemView(theme: theme, item: stackItem, render: render)
                verticleStackView.addArrangedSubview(view)
                itemView = view
            }
            
            itemView?.addTapGesture {
                stackItem.eventHandler.invoke()
            }
        }
        
        // Remove excess views if necessary
        if heroImages.count < verticleStackView.arrangedSubviews.count {
            for _ in heroImages.count..<verticleStackView.arrangedSubviews.count {
                verticleStackView.arrangedSubviews.last?.removeFromSuperview()
            }
        }
    }
}


class CollectionContentPreviewStackItemView: UIView {
    
    var theme: Theme
    var item: CollectionStackItem
    var render: RenderIos
    
    var imgPreview: CustomImageView!
    var imgShadow: CustomImageView?
    var plusIndicator: CustomImageView?
    var indicator: UICollectionPlusIndicatorView?
    var footer: CollectionPreviewFooter?
    var isInitialized: Bool = false
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    init(theme: Theme, item: CollectionStackItem, render: RenderIos) {
        self.theme = theme
        self.item = item
        self.render = render
        super.init(frame: .zero)
        self.initialize()
        self.setup()
    }
    
    override func draw(_ rect: CGRect) {
        self.applyShape(shapeSpec: item.shapeSpec)
    }
    
    private func initialize() {
        let shadowImage = item.shadowImage
        let plusIndicator = item.plusIndicator
        let width = Float(item.width)
        let height = Float(item.height)
        let footer = item.footer
        
        self.imgPreview = CustomImageView(theme: theme, imageViewState: item.imageViewState, imageHashDecoder: render.imageHashDecoder)
        self.imgPreview.translatesAutoresizingMaskIntoConstraints = false
        self.imgPreview.addTapGesture { [unowned self] in
            item.eventHandler.invoke()
        }
        self.addSubview(imgPreview)
        
        NSLayoutConstraint.activate([
            imgPreview.topAnchor.constraint(equalTo: self.topAnchor),
            imgPreview.bottomAnchor.constraint(equalTo: self.bottomAnchor),
            imgPreview.trailingAnchor.constraint(equalTo: self.trailingAnchor),
            imgPreview.leadingAnchor.constraint(equalTo: self.leadingAnchor),
            imgPreview.heightAnchor.constraint(equalToConstant: item.height)
        ])
        self.imgPreview.clipsToBounds = true
        
        if let shadowImage {
            self.imgShadow = CustomImageView(theme: theme, image: shadowImage, width: width, height: height)
            guard let imgShadow = self.imgShadow else { return }
            imgShadow.backgroundColor = .clear
            imgShadow.translatesAutoresizingMaskIntoConstraints = false
            self.insertSubview(imgShadow, aboveSubview: imgPreview)
            
            NSLayoutConstraint.activate([
                imgShadow.topAnchor.constraint(equalTo: self.topAnchor),
                imgShadow.bottomAnchor.constraint(equalTo: self.bottomAnchor),
                imgShadow.trailingAnchor.constraint(equalTo: self.trailingAnchor),
                imgShadow.leadingAnchor.constraint(equalTo: self.leadingAnchor),
                imgShadow.heightAnchor.constraint(equalToConstant: item.height)
            ])
            imgShadow.setup()
            imgShadow.imgPreview.contentMode = .scaleToFill
        }
        
        if let footer {
            self.footer = CollectionPreviewFooter(theme: self.theme, render: self.render, viewState: footer)
            guard let footer = self.footer else { return }
            self.addSubview(footer)
            footer.translatesAutoresizingMaskIntoConstraints = false
            NSLayoutConstraint.activate([
                footer.bottomAnchor.constraint(equalTo: self.bottomAnchor),
                footer.trailingAnchor.constraint(equalTo: self.trailingAnchor),
                footer.leadingAnchor.constraint(equalTo: self.leadingAnchor),
                footer.heightAnchor.constraint(equalToConstant: 50.0)
            ])
            footer.clipsToBounds = true
        }
        
        if let plusIndicator {
            self.indicator = UICollectionPlusIndicatorView(theme: theme, plusIndicator: plusIndicator, render: render)
            guard let indicator = self.indicator else { return }
            self.addSubview(indicator)
            indicator.translatesAutoresizingMaskIntoConstraints = false
            indicator.backgroundColor = .clear
            indicator.setContentHuggingPriority(UILayoutPriority(rawValue: 999), for: .horizontal)
            indicator.translatesAutoresizingMaskIntoConstraints = false
            self.insertSubview(indicator, aboveSubview: imgPreview)
            let bottomAnchor = self.footer == nil ? self.bottomAnchor : self.footer!.topAnchor
            NSLayoutConstraint.activate([
                indicator.bottomAnchor.constraint(equalTo: bottomAnchor),
                indicator.trailingAnchor.constraint(equalTo: self.trailingAnchor)
            ])
            indicator.clipsToBounds = true
        }
    }
    
    func updateWith(theme: Theme, item: CollectionStackItem) {
        self.theme = theme
        self.item = item
    }
    
    func setup() {
        self.imgPreview.theme = theme
        self.imgPreview.imageViewState = item.imageViewState
        self.imgPreview.setup()
        
        self.imgShadow?.theme = theme
        self.imgShadow?.imageViewState = item.imageViewState
        self.imgShadow?.setup()
        self.imgShadow?.imgPreview.contentMode = .scaleToFill // required
        
        if let plusIndicator = item.plusIndicator {
            self.indicator?.update(theme: theme, plusIndicator: plusIndicator, render: render)
        } else {
            self.indicator?.removeFromSuperview()
            self.indicator = nil
        }
        
        if let footer = item.footer {
            self.footer?.update(theme: self.theme, render: self.render, viewState: footer)
        }
    }
}


class CollectionPreviewFooter: UIView {
    var theme: Theme
    var render: RenderIos
    var viewState: CollectionPreviewFooterViewState!
    
    var scrimImageView: CustomImageView!
    var title: StyledLabel!
    
    init(theme: Theme, render: RenderIos, viewState: CollectionPreviewFooterViewState!) {
        self.theme = theme
        self.render = render
        self.viewState = viewState
        super.init(frame: .zero)
        self.setUp()
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func draw(_ rect: CGRect) {
        self.applyShape(shapeSpec: self.viewState.shapeSpec)
    }
    
    func setUp() {
        let title = viewState.title
        let scrimImage = viewState.scrimImage
        let pading = render.defaultViewSpec.paddingSmall.toCGFloat()
        let colortoken = Color.white
        self.backgroundColor = .clear
        self.scrimImageView = CustomImageView(theme: self.theme, image: scrimImage)
        self.addSubview(scrimImageView)
        self.scrimImageView.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint.activate([
            scrimImageView.topAnchor.constraint(equalTo: self.topAnchor),
            scrimImageView.bottomAnchor.constraint(equalTo: self.bottomAnchor),
            scrimImageView.trailingAnchor.constraint(equalTo: self.trailingAnchor),
            scrimImageView.leadingAnchor.constraint(equalTo: self.leadingAnchor),
        ])
        
        self.title = StyledLabel(text: title, theme: self.theme, colorOverride: colortoken)
        self.insertSubview(self.title, aboveSubview: scrimImageView)
        self.title.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint.activate([
            self.title.topAnchor.constraint(equalTo: self.topAnchor,constant: pading),
            self.title.bottomAnchor.constraint(equalTo: self.bottomAnchor, constant: -pading),
            self.title.trailingAnchor.constraint(equalTo: self.trailingAnchor, constant: -pading),
            self.title.leadingAnchor.constraint(equalTo: self.leadingAnchor, constant: pading),
        ])
        
        self.scrimImageView.setup()
    }
    
    func update(theme: Theme, render: RenderIos, viewState: CollectionPreviewFooterViewState!) {
        self.theme = theme
        self.render = render
        self.viewState = viewState
        
        self.scrimImageView.theme = self.theme
        self.scrimImageView.imageUI = self.viewState.scrimImage
        self.scrimImageView.setup()
        
        self.title.updateWith(text: self.viewState.title, theme: self.theme)
    }
}
