import WallApp
import UIKit

protocol StaggerGridLayoutDelegate: AnyObject {
    func collectionView(_ collectionView: UICollectionView, sizeForViewAtIndexPath indexPath: IndexPath) -> CGSize
    func collectionView(_ collectionView: UICollectionView, spanForViewAtIndexPath indexPath: IndexPath) -> StaggeredGridSpan
}

class StaggeredGridLayout: UICollectionViewLayout {
    let spacing: CGFloat
    let paddingStart: CGFloat
    
    var theme: Theme? = nil
    var headerHeight: CGFloat? = nil
    var headerType: HeaderType = .none
    
    private var headerExcessHeight: CGFloat = 100
    
    init(spacing: CGFloat = 6, paddingStart: CGFloat = 16) {
        self.spacing = spacing
        self.paddingStart = paddingStart
        super.init()
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    weak var delegate: StaggerGridLayoutDelegate?
        private let numberOfColumns = 2
    
    private var cache: [UICollectionViewLayoutAttributes] = []
    
    private var contentHeight: CGFloat = 0
    
    private var contentWidth: CGFloat {
        guard let collectionView = collectionView else {
            return 0
        }
        let insets = collectionView.contentInset
        return collectionView.bounds.width - (insets.left + insets.right)
    }
    
    override var collectionViewContentSize: CGSize {
        return CGSize(width: contentWidth, height: contentHeight)
    }
    
    override func prepare() {
        guard cache.isEmpty == true, let collectionView = collectionView, contentWidth > 0 else {
            return
        }
        contentHeight = 0
        
        let yOffsetStart = headerHeight == nil ? 0 : headerHeight!
        var xOffset: [CGFloat] = []
        var yOffset: [CGFloat] = .init(repeating: yOffsetStart, count: numberOfColumns)

        for item in 0..<collectionView.numberOfItems(inSection: 0) {
            let indexPath = IndexPath(item: item, section: 0)
            let span = delegate?.collectionView(collectionView, spanForViewAtIndexPath: indexPath) ?? .single
            let size = delegate?.collectionView(collectionView, sizeForViewAtIndexPath: indexPath)
            let columnWidth = contentWidth / CGFloat(numberOfColumns)
            
            let viewWidth = size?.width
            let correctedSpacing: CGFloat
            let width: CGFloat
            
            // Find the column with the minimum yOffset
            let minYOffset = yOffset.min()!
            var minColumn = -1
            
            // For single span, use the column with minimum yOffset
            // For max spans, place item in all columns
            if span == .single {
                minColumn = yOffset.firstIndex(of: minYOffset)!
            } else if span == .max || span == .maxNoPadding {
                minColumn = 0
            }
            
            // This should not happen unless there is a new span added and
            // its handling has not been implemented
            if minColumn == -1 {
                Log.e("[UIKitFeed] minColumn is -1")
                continue
            }
            
            switch span {
            case .maxNoPadding:
                width = contentWidth
                correctedSpacing = 0
            case .max:
                width = contentWidth - spacing * 2
                correctedSpacing = spacing
            case .single:
                width = (viewWidth == nil || viewWidth == 0 ? columnWidth : viewWidth!)
                correctedSpacing = (minColumn == 0 ? paddingStart : minColumn.toCGFloat() * spacing * 2)
            }
            let height = size?.height ?? 0
            
            // Adjust xOffset based on span
            if xOffset.isEmpty && span == .single {
                for column in 0..<numberOfColumns {
                    xOffset.append(CGFloat(column) * width)
                }
            } else if span == .max || span == .maxNoPadding {
                xOffset = .init(repeating: 0, count: numberOfColumns)
                yOffset = .init(repeating: yOffset.max()!, count: numberOfColumns)
            }
            
            let x = xOffset[minColumn] + correctedSpacing
            let y = yOffset[minColumn]
            
//            Log.d("[UIKitFeed] item: \(item) x: \(x), y: \(y), width: \(width), height: \(height), spacing: \(spacing), minColumn: \(minColumn), correctedSpacing: \(correctedSpacing), span: \(span)")
            let frame = CGRect(x: x,
                               y: y,
                               width: width,
                               height: height)
            
            let attributes = UICollectionViewLayoutAttributes(forCellWith: indexPath)
            attributes.frame = frame
            cache.append(attributes)
            
            contentHeight = max(contentHeight, frame.maxY)
            
            if span == .single {
                yOffset[minColumn] += height + spacing
            } else {
                yOffset = yOffset.map { _ in yOffset[minColumn] + height + spacing }
                xOffset.removeAll()
            }
        }
    }
    
    override func layoutAttributesForDecorationView(ofKind elementKind: String, at indexPath: IndexPath) -> UICollectionViewLayoutAttributes? {
        guard let headerHeight, elementKind == BackgroundDecorationView.identifier else {
            return nil
        }
        let decorationAttributes = BackgroundDecorationViewLayoutAttributes(
            forDecorationViewOfKind: elementKind,
            with: indexPath
        )
        decorationAttributes.frame = CGRect(x: 0, y: headerHeight, width: contentWidth, height: headerHeight * 3)
        decorationAttributes.zIndex = -1
        decorationAttributes.backgroundColor = theme?.themeColors.background.uiColor ?? .white
        return decorationAttributes
    }
    
    override func layoutAttributesForElements(in rect: CGRect) -> [UICollectionViewLayoutAttributes]? {
        var visibleLayoutAttributes: [UICollectionViewLayoutAttributes] = []
        
        if let headerHeight {
            if headerType == .highlights {
                let headerLayoutAttributes = UICollectionViewLayoutAttributes(forSupplementaryViewOfKind: UICollectionView.elementKindSectionHeader, with: IndexPath(item: 0, section: 0))
                let totalHeaderHeight = headerHeight + (HighlightCarouselConstants.excessHeight * 2)
                headerLayoutAttributes.frame = CGRect(x: 0, y: (collectionView!.contentOffset.y * HighlightCarouselConstants.scrollScaler) - headerExcessHeight, width: contentWidth, height: totalHeaderHeight)
                headerLayoutAttributes.zIndex = -2
                visibleLayoutAttributes.append(headerLayoutAttributes)
                
                // Add decoration view for the whole section
                let decorationAttributes = self.layoutAttributesForDecorationView(
                    ofKind: BackgroundDecorationView.identifier,
                    at: IndexPath(item: 0, section: 0)
                )!
                visibleLayoutAttributes.append(decorationAttributes)
            } else {
                let headerLayoutAttributes = UICollectionViewLayoutAttributes(forSupplementaryViewOfKind: UICollectionView.elementKindSectionHeader, with: IndexPath(item: 0, section: 0))
                headerLayoutAttributes.frame = CGRect(x: 0, y: collectionView!.contentOffset.y, width: contentWidth, height: headerHeight)
                headerLayoutAttributes.zIndex = -2
                visibleLayoutAttributes.append(headerLayoutAttributes)
            }
        }
        
        // Loop through the cache and look for items in the rect
        for attributes in cache {
            if attributes.frame.intersects(rect) {
                visibleLayoutAttributes.append(attributes)
            }
        }
        return visibleLayoutAttributes
    }
    
    override func layoutAttributesForItem(at indexPath: IndexPath) -> UICollectionViewLayoutAttributes? {
        return cache[indexPath.item]
    }
    
    func clearCache() {
        cache.removeAll()
    }
}

enum HeaderType {
    case none
    case padding
    case highlights
}
