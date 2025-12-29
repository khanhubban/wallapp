//
//  UIPlusIndicatorView.swift
//  WallApp
//

import Foundation

class UICollectionPlusIndicatorView: UIView {
    
    var theme: Theme
    var plusIndicator: PlusIndicatorViewState
    var render: RenderIos
    var plusIndicatorView: UIPlusIndicatorView!
    
    private let horizontalStackView: UIStackView = {
        let stackView = UIStackView()
        stackView.axis = .horizontal
        return stackView
    }()
    
    private let verticalStackView: UIStackView = {
        let stackView = UIStackView()
        stackView.axis = .vertical
        return stackView
    }()
    
    init(theme: Theme, plusIndicator: PlusIndicatorViewState, render: RenderIos) {
        self.theme = theme
        self.plusIndicator = plusIndicator
        self.render = render
        super.init(frame: .zero)
        self.setUp()
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    func setUp() {
        plusIndicatorView = UIPlusIndicatorView(theme: self.theme, viewState: self.plusIndicator, render: self.render)
        
        let spacer = UIView(frame: .zero)
        spacer.setContentHuggingPriority(UILayoutPriority(249), for: .horizontal)
        
        self.horizontalStackView.addArrangedSubview(spacer)
        self.horizontalStackView.addArrangedSubview(plusIndicatorView)
        
        let verticalSpacer = UIView()
        spacer.setContentHuggingPriority(UILayoutPriority(249), for: .vertical)
        
        verticalStackView.addArrangedSubview(verticalSpacer)
        verticalStackView.addArrangedSubview(horizontalStackView)
        
        let padding = render.defaultViewSpec.paddingSmall.toCGFloat()
        self.addSubview(verticalStackView)
        verticalStackView.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint.activate([
            verticalStackView.topAnchor.constraint(equalTo: self.topAnchor, constant: padding),
            verticalStackView.bottomAnchor.constraint(equalTo: self.bottomAnchor, constant: -padding),
            verticalStackView.leadingAnchor.constraint(equalTo: self.leadingAnchor, constant: padding),
            verticalStackView.trailingAnchor.constraint(equalTo: self.trailingAnchor, constant: -padding),
        ])
    }
    
    func update(theme: Theme, plusIndicator: PlusIndicatorViewState, render: RenderIos) {
        self.theme = theme
        self.plusIndicator = plusIndicator
        self.render = render
        self.plusIndicatorView.update(theme: theme, viewState: plusIndicator, render: render)
    }
}


class UIPlusIndicatorView: UIView {
    
    var theme: Theme
    var viewState: PlusIndicatorViewState
    var render: RenderIos
    var imageView: CustomImageView!
    
    init(theme: Theme, viewState: PlusIndicatorViewState, render: RenderIos) {
        self.theme = theme
        self.viewState = viewState
        self.render = render
        super.init(frame: .zero)
        self.setUp()
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    func setUp() {
        let imageUI = viewState.image
        let height = viewState.height
        let width = viewState.width
        
        self.imageView = CustomImageView(theme: theme, image: imageUI, width: width, height: height)
        self.addSubview(imageView)
        imageView.translatesAutoresizingMaskIntoConstraints = false
        
        NSLayoutConstraint.activate([
            imageView.topAnchor.constraint(equalTo: self.topAnchor),
            imageView.bottomAnchor.constraint(equalTo: self.bottomAnchor),
            imageView.leadingAnchor.constraint(equalTo: self.leadingAnchor),
            imageView.trailingAnchor.constraint(equalTo: self.trailingAnchor)
        ])
        
        self.imageView.setup()

        if let viewEventHandler = viewState.viewEventHandler {
            self.addTapGesture { [weak self] in
                guard let _ = self else { return }
                viewEventHandler.invoke()
            }
        }   
    }
    
    func update(theme: Theme, viewState: PlusIndicatorViewState, render: RenderIos) {
        let imageUI = viewState.image
        let height = viewState.height
        let width = viewState.width
        
        self.imageView.theme = self.theme
        self.imageView.imageUI = imageUI
        self.imageView.width = width
        self.imageView.height = height
        self.imageView.setup()
    }
}
