//
//  CollectionPreview.swift
//  WallApp
//

import Foundation
import SwiftUI
import WallApp

struct CollectionPreview: View {
    let collectionPreview: CollectionPreviewViewState
    let theme: Theme
    let render: RenderIos
    
    var body: some View {
        CollectionPreviewStack(collectionPreview: collectionPreview, render: render, theme: theme)
    }
}

struct CollectionPreviewStack: View {
    var collectionPreview: CollectionPreviewViewState
    let render: RenderIos
    let theme: Theme
    
    var body: some View {
        let heroImages = collectionPreview.layers.mapToStackItems(viewState: collectionPreview)
        
        if let heroImages {
            CollectionPreviewImageStack(
                theme: theme,
                heroImages: heroImages,
                render: render
            )
        }
    }
}

struct CollectionPreviewImageStack: View {
    let theme: Theme
    let heroImages: [CollectionStackItem]
    let render: RenderIos
    
    var body: some View {
        VStack(alignment: .center, spacing: 0) {
            // Commented out with data change. If SwiftUI impl ends up being used again, this will need to be re-implemented.
//            ForEach(heroImages, id: \.horizontalInset) { heroImage in
//                CollectionPreviewStackItemView(theme: theme, item: heroImage, render: render)
//            }
        }
//        .onTapGesture {
//            eventHandler.invoke()
//        }
    }
}

struct CollectionPreviewStackItemView: View {
    let theme: Theme
    let item: CollectionStackItem
    let render: RenderIos
    
    var body: some View {
        let shadowImage = item.shadowImage
        let plusIndicator = item.plusIndicator
        let width = item.width
        let height = item.height
        
        ZStack {
//            SwiftUIImage(theme: theme, image: item.image, width: width, height: height, imageHashDecoder: render.imageHashDecoder)
            
            if let shadowImage {
                SwiftUIImage(theme: theme, image: shadowImage, width: width, height: height)
                    .scaledToFill()
            }
            
            if let plusIndicator {
                CollectionPlusIndicatorView(theme: theme, plusIndicator: plusIndicator, render: render)
            }
        }
        .frame(width: item.width, height: item.height)
        .applyClipShapes(ShapeMapper.map(shapeSpec: item.shapeSpec))
    }
}

struct CollectionPlusIndicatorView: View {
    let theme: Theme
    let plusIndicator: PlusIndicatorViewState
    let render: RenderIos
    
    var body: some View {
        VStack {
            Spacer()
            HStack {
                Spacer()
                PlusIndicatorView(theme: theme, viewState: plusIndicator, render: render)
            }
        }
        .padding(render.defaultViewSpec.paddingSmall.toCGFloat())
    }
}

struct CollectionStackItem {
    let imageViewState: ImageViewState
    var shapeSpec: ShapeSpec
    var shadowImage: ImageUI? = nil
    let width: CGFloat
    var height: CGFloat
    let eventHandler: ViewEventHandler
    var plusIndicator: PlusIndicatorViewState? = nil
    var footer: CollectionPreviewFooterViewState? = nil
}


///ImageUI Extension to create CollectionStackItem
extension Array where Element == CollectionPreviewLayerViewState {
    func mapToStackItems(viewState: CollectionPreviewViewState) -> [CollectionStackItem]? {
        var layers: [CollectionPreviewLayerViewState]?
        
        if isEmpty {
            layers = nil
        } else if count > 3 {
            layers = Array(self.prefix(3))
        } else {
            layers = self
        }
        
        return layers?.enumerated().map { index, layer in
            let imageViewState = layer.imageViewState
            let imageViewSpec = imageViewState.viewSpec
        
            let (plusIndicator, footer) = index == 0 ? (viewState.plusIndicator, viewState.footer) : (nil, nil)
            
            return CollectionStackItem(
                imageViewState: imageViewState,
                shapeSpec: imageViewSpec.shapeSpec!,
                shadowImage: layer.shadow,
                width: imageViewSpec.width.toCGFloat(),
                height: imageViewSpec.height.toCGFloat(),
                eventHandler: layer.eventHandler,
                plusIndicator: plusIndicator,
                footer: footer
            )
        }
    }
}
