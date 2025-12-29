//
//  UIMessageBarView.swift
//  WallApp
//

import WallApp
import UIKit

class UIMessageBarView: UIView {
    
    private var render: RenderIos
    private var theme: Theme
    @objc dynamic var viewState: MessageBarViewState?
    
    var title: StyledLabel?
    var summary: StyledLabel?
    var spacer: UIView?
    var progressView: UIProgressView?
    
    var isInitilized = false
    
    private let horizontalStackView: UIStackView = {
        let stackView = UIStackView()
        stackView.axis = .horizontal
        stackView.distribution = .fillEqually
        return stackView
    }()
    
    private let verticleStackView: UIStackView = {
        let stackView = UIStackView()
        stackView.axis = .vertical
        stackView.spacing = 5
        return stackView
    }()

    init(render: RenderIos, theme: Theme, viewState: MessageBarViewState?) {
        self.render = render
        self.theme = theme
        self.viewState = viewState
        super.init(frame: .zero)
        updateView()
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    private func updateView() {
        if let messageBarViewState = viewState {
            self.verticleStackView.alpha = 1.0
            guard let state = messageBarViewState.content.viewState as? DownloadStatusViewState else { return }

            let title = state.title
            let summary = state.summary
            let progress = state.progress
            
            if !isInitilized {
                self.title = StyledLabel(text: title, theme: theme)
                self.summary = StyledLabel(text: summary, theme: theme)
                self.spacer = UIView()
                spacer?.setContentHuggingPriority(.defaultLow, for: .horizontal)
                self.title?.textAlignment = .left
                self.summary!.textAlignment = .right
                horizontalStackView.addArrangedSubview(self.title!)
                horizontalStackView.addArrangedSubview(spacer!)
                horizontalStackView.addArrangedSubview(self.summary!)
                verticleStackView.addArrangedSubview(horizontalStackView)
                
                self.progressView = UIProgressView(frame: .zero)
                self.progressView?.progress = progress
                self.progressView?.trackTintColor = .gray
                self.progressView?.tintColor = .red
                verticleStackView.addArrangedSubview(self.progressView!)
                
                verticleStackView.translatesAutoresizingMaskIntoConstraints = false
                self.addSubview(verticleStackView)
                NSLayoutConstraint.activate([
                    verticleStackView.leadingAnchor.constraint(equalTo: self.leadingAnchor),
                    verticleStackView.bottomAnchor.constraint(equalTo: self.bottomAnchor),
                    verticleStackView.trailingAnchor.constraint(equalTo: self.trailingAnchor),
                    verticleStackView.topAnchor.constraint(equalTo: self.topAnchor),
                ])
                
                isInitilized = true
            } else {
                self.title?.updateWith(text: title, theme: theme)
                self.summary?.updateWith(text: summary, theme: theme)
                self.progressView?.progress = progress
            }
            UIView.animate(withDuration: 0.2, delay: 0.0, options: [.transitionCrossDissolve], animations: {
                self.horizontalStackView.alpha = 1.0
                self.progressView?.alpha = 1.0
            }, completion: nil)
        } else {
            
            UIView.animate(withDuration: 0.2, delay: 0.0, options: [.transitionCrossDissolve], animations: {
                self.horizontalStackView.alpha = 0.0
                self.progressView?.alpha = 0.0
            }, completion: nil)

        }
        
        
    }
    
    func updateWith(render: RenderIos? = nil, theme: Theme? = nil, viewState: MessageBarViewState?) {
        if let render = render {
            self.render = render
        }
        if let theme = theme {
            self.theme = theme
        }
        
        self.viewState = viewState
        self.updateView()
    }
}
