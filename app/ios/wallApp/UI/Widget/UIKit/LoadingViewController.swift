//
//  LoadingViewController.swift
//  WallApp
//

import WallApp
import UIKit

class LoadingViewController: UIViewController {
    let theme: Theme
    let render: RenderIos
    let imageSize: CGFloat = 120

    init(theme: Theme, render: RenderIos) {
        self.theme = theme
        self.render = render
        super.init(nibName: nil, bundle: nil)
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func viewDidLoad() {
        Log.d("[LoadingInvestigate] LoadingViewController: viewDidLoad")
        super.viewDidLoad()
        setupViews()
    }

    private func setupViews() {
        view.backgroundColor = theme.themeColors.background.uiColor
        if let image = render.defaultResources.loading as? ImageUIImageResource, let loadingAnimatedSpec = image.resource as? ResourceAnimatedImage {
            Log.d("[LoadingInvestigate] LoadingViewController: setupViews: loadingAnimatedSpec: \(loadingAnimatedSpec.animatedImageSpec)")
            let imageView = UILottieAnimationView(
                animatedImageSpec: loadingAnimatedSpec.animatedImageSpec,
                width: imageSize,
                height: imageSize
            )
            
            imageView.translatesAutoresizingMaskIntoConstraints = false
            view.addSubview(imageView)
            
            // Center imageView in the middle of the view
            NSLayoutConstraint.activate([
                imageView.centerXAnchor.constraint(equalTo: view.centerXAnchor),
                imageView.centerYAnchor.constraint(equalTo: view.centerYAnchor),
                imageView.widthAnchor.constraint(equalToConstant: imageSize),
                imageView.heightAnchor.constraint(equalToConstant: imageSize)
            ])
        }
    }
}

