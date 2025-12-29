//
//  UIExploreHeaderView.swift
//  WallApp
//

import WallApp
import Combine
import UIKit

class UIExploreHeaderView: UIView {
    var render: RenderIos
    var theme: Theme
    var viewState: ExploreHeaderViewState
    var viewSpec: ExploreHeaderViewSpec
    var scrollOffsetController: ScrollOffsetController?
    
    private var headerHeight: CGFloat = 0
    private var statusBarHeight: CGFloat {
        render.windowFrame.statusBarHeight.toCGFloat()
    }
    
    private var heightConstraint: NSLayoutConstraint?
    
    private var searchImageView: UIImageView!
    private var searchLabelView: StyledLabel!
    
    private var currentHeaderOffset: CGFloat = 0
    
    var cancellable: AnyCancellable?
    
    init(render: RenderIos, theme: Theme, viewState: ExploreHeaderViewState, viewSpec: ExploreHeaderViewSpec, scrollOffsetController: ScrollOffsetController?) {
        self.render = render
        self.theme = theme
        self.viewState = viewState
        self.viewSpec = viewSpec
        self.scrollOffsetController = scrollOffsetController
        super.init(frame: .zero)
        setupViews()
    }
    
    deinit {
        cancellable?.cancel()
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func draw(_ rect: CGRect) {
        super.draw(rect)
        self.updateFrame()
        self.applyShape(shapeSpec: ShapeSpec(shapeStyle: .roundedCorners, shapeSize: .medium))
    }
    
    func updateFrame() {
        if cancellable != nil { return }
        cancellable = scrollOffsetController?.$offset.sink(receiveValue: { [weak self] value in
            guard let self = self else { return }
            self.currentHeaderOffset = value
            self.transform = CGAffineTransform(translationX: 0, y: value)
            self.layoutIfNeeded()
        })
    }
    
    private func setupViews() {
        let horizontalStackView: UIStackView = {
            let stackView = UIStackView()
            stackView.axis = .horizontal
            stackView.alignment = .center
            stackView.distribution = .fill
            return stackView
        }()
 
        let paddingSmall = render.defaultViewSpec.paddingSmall.toCGFloat()
        horizontalStackView.spacing = paddingSmall
        
        horizontalStackView.translatesAutoresizingMaskIntoConstraints = false
    
        searchImageView = UIImageView(image: UIImage(systemName: "magnifyingglass"))
        searchImageView.tintColor = theme.themeColors.onBackground?.color.uiColor
        horizontalStackView.addArrangedSubview(searchImageView)
        
        guard let label = viewState.searchLabel as? MenuItem.MenuItemLabel else { return }
        searchLabelView = StyledLabel(text: label.text, theme: theme)
        horizontalStackView.addArrangedSubview(searchLabelView)
        
        horizontalStackView.heightAnchor.constraint(equalToConstant: viewState.viewSpec.height.toCGFloat()).isActive = true
        
        self.addSubview(horizontalStackView)
        NSLayoutConstraint.activate([
            horizontalStackView.topAnchor.constraint(equalTo: self.topAnchor),
            horizontalStackView.centerXAnchor.constraint(equalTo: self.centerXAnchor)
        ])
        
        self.addTapGesture {
            self.viewState.searchOnClick()
        }
        
        self.backgroundColor = viewState.containerColor.uiColor
        
        let headerHeight = viewState.viewSpec.height.toCGFloat()
        heightConstraint = heightAnchor.constraint(equalToConstant: headerHeight)
        heightConstraint?.isActive = true
        scrollOffsetController?.update(maxOffset: headerHeight + statusBarHeight)
        
        self.layoutIfNeeded()
    }
    
    func updateWith(render: RenderIos? = nil, theme: Theme? = nil, viewState: ViewState? = nil) {
        if let render {
            self.render = render
        }
        if let theme {
            self.theme = theme
            searchImageView.tintColor = theme.themeColors.onBackground?.color.uiColor
            searchLabelView.updateWith(theme: theme)
        }
        if let viewState = viewState as? ExploreHeaderViewState {
            self.viewState = viewState
            self.backgroundColor = viewState.containerColor.uiColor
        }
    }
}
