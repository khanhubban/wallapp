//
//  UIExploreStatusBarView.swift
//  WallApp
//

import WallApp
import Combine
import UIKit

class UIExploreStatusBarView: UIView {
    var render: RenderIos
    var theme: Theme
    var viewState: ExploreHeaderViewState
    var viewSpec: ExploreHeaderViewSpec
    var scrollOffsetController: ScrollOffsetController?
    
    private var statusBarHeight: CGFloat {
        render.windowFrame.statusBarHeight.toCGFloat()
    }
    
    private var stickyStatusBarView: UIStatusBarView!
    
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
        updateFrame()
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
    }
    
    func updateFrame() {
        if cancellable != nil { return }
        cancellable = scrollOffsetController?.$offset.sink(receiveValue: { [weak self] value in
            guard let self = self else { return }
            self.currentHeaderOffset = value
            let statusBarAlpha = calculateStatusBarAlpha(offset: value)
            self.stickyStatusBarView.updateWith(color: viewState.containerColor.uiColor.withAlphaComponent(statusBarAlpha))
        })
    }
    
    private func calculateStatusBarAlpha(offset: CGFloat) -> CGFloat {
        var alpha: CGFloat = 1
        let carouselHeight = viewSpec.offsetDueToHighlightCarousel.toCGFloat()
        let threshold = statusBarHeight + ((carouselHeight - statusBarHeight) * 0.2)
        if offset > threshold {
            alpha = 0
        } else {
            alpha = (threshold - offset) / (threshold - statusBarHeight)
        }
//        Log.d("[StatusBarAlpha] offset: \(offset), carouselHeight: \(carouselHeight), statusBarHeight: \(statusBarHeight), threshold: \(threshold), alpha: \(alpha)")
        return alpha
    }
    
    private func setupViews() {
        backgroundColor = .clear
        stickyStatusBarView = UIStatusBarView(
            render: render,
            theme: theme,
            color: viewState.containerColor.uiColor.withAlphaComponent(calculateStatusBarAlpha(offset: currentHeaderOffset))
        )
        addSubview(stickyStatusBarView)
        
        stickyStatusBarView.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint.activate([
            stickyStatusBarView.topAnchor.constraint(equalTo: self.topAnchor),
            stickyStatusBarView.leadingAnchor.constraint(equalTo: self.leadingAnchor),
            stickyStatusBarView.trailingAnchor.constraint(equalTo: self.trailingAnchor)
        ])
    }
    
    func updateWith(render: RenderIos? = nil, theme: Theme? = nil, viewState: ViewState? = nil) {
        if let render {
            self.render = render
        }
        if let theme {
            self.theme = theme
            stickyStatusBarView.updateWith(theme: theme)
        }
        if let viewState = viewState as? ExploreHeaderViewState {
            self.viewState = viewState
            stickyStatusBarView.updateWith(
                render: render,
                theme: theme,
                color: viewState.containerColor.uiColor.withAlphaComponent(calculateStatusBarAlpha(offset: currentHeaderOffset))
            )
        }
    }
}
