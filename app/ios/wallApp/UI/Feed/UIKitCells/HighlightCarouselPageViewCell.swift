//
//  HighlightCarouselPageViewCell.swift
//  WallApp
//

import WallApp
import UIKit

class HighlightCarouselPageViewCell : UICollectionViewCell {
    
    static let identifier = "HighlightCarouselPageViewCell"
    
    var render: RenderIos!
    var theme: Theme!
    
    private var imageView: CustomImageView!
    private var title: StyledLabel!
    private var additionalLabel: StyledLabel?
    private var shadowView: CustomImageView!
    
    var isInitialized = false
    
    private var bottomPaddingTitle: CGFloat {
        HighlightCarouselConstants.excessHeight + 46
    }
    private var bottomPaddingShadow: CGFloat {
        HighlightCarouselConstants.excessHeight - 16
    }
    
    private var frameWidth: Float {
        Float(frame.width)
    }
    private var frameHeight: Float {
        Float(frame.height)
    }
    
    private var titleBottomConstraint: NSLayoutConstraint?
    private var shadowBottomConstraint: NSLayoutConstraint?
    
    private var labelStackView: UIStackView!
    
    override init(frame: CGRect) {
        super.init(frame: frame)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    func configureCell(
        render: RenderIos,
        theme: Theme,
        viewState: CarouselPageViewState,
        contentOffsetY: CGFloat,
        contentAlpha: CGFloat
    ) {
        self.render = render
        self.theme = theme
        let exhibitViewState = viewState.exhibitViewState
        
        if !isInitialized {
            // image
            imageView = CustomImageView(
                theme: theme,
                image: exhibitViewState.imageViewState.image,
                width: frameWidth,
                height: frameHeight,
                contentScale: exhibitViewState.imageViewState.viewSpec.contentScale
            )
            addSubview(imageView)
            imageView.fillSuperview()
            imageView.layer.masksToBounds = true
            
            // shadow
            setupShadow(exhibitViewState.bottomShadowImage)
            
            // title and additionalLabel
            title = StyledLabel(text: exhibitViewState.label, theme: theme, colorOverride: .white)
            title.translatesAutoresizingMaskIntoConstraints = false
            
            if let additionalLabelText = exhibitViewState.additionalLabel {
                additionalLabel = StyledLabel(text: additionalLabelText, theme: theme, colorOverride: .white)
                additionalLabel?.translatesAutoresizingMaskIntoConstraints = false
            }

            labelStackView = UIStackView(arrangedSubviews: [title])
            if let additionalLabel = additionalLabel {
                labelStackView.addArrangedSubview(additionalLabel)
            }
            labelStackView.axis = .vertical
            labelStackView.spacing = 8
            labelStackView.translatesAutoresizingMaskIntoConstraints = false
            addSubview(labelStackView)
            
            NSLayoutConstraint.activate([
                labelStackView.leadingAnchor.constraint(equalTo: leadingAnchor, constant: 16),
                labelStackView.trailingAnchor.constraint(equalTo: trailingAnchor, constant: -16),
            ])
            titleBottomConstraint = labelStackView.bottomAnchor.constraint(
                equalTo: bottomAnchor,
                constant: -bottomPaddingTitle
            )
            titleBottomConstraint?.isActive = true
        } else {
            imageView.theme = theme
            imageView.imageUI = exhibitViewState.imageViewState.image
            imageView.width = frameWidth
            imageView.height = frameHeight
            imageView.contentScale = exhibitViewState.imageViewState.viewSpec.contentScale
            title.updateWith(text: exhibitViewState.label, theme: theme, colorOverride: .white)

            if let additionalLabelText = exhibitViewState.additionalLabel {
                if additionalLabel == nil {
                    // Create the additionalLabel if it wasn't already present
                    additionalLabel = StyledLabel(text: additionalLabelText, theme: theme, colorOverride: .white)
                    additionalLabel?.translatesAutoresizingMaskIntoConstraints = false
                    labelStackView.addArrangedSubview(additionalLabel!)
                } else {
                    additionalLabel?.updateWith(text: additionalLabelText, theme: theme, colorOverride: .white)
                }
                additionalLabel?.isHidden = false
            } else {
                additionalLabel?.isHidden = true
            }
        }
        
        imageView.setup()
        
        self.addTapGesture {
            viewState.onClick?.invoke()
        }
        
        updateContentOffsetY(contentOffsetY)
        updateContentAlpha(contentAlpha)
        isInitialized = true
    }
    
    func updateContentOffsetY(_ offsetY: CGFloat) {
        titleBottomConstraint?.constant = -bottomPaddingTitle - offsetY
        shadowBottomConstraint?.constant = -bottomPaddingShadow - offsetY
    }
    
    func updateContentAlpha(_ alpha: CGFloat) {
        labelStackView.alpha = alpha
    }
    
    private func setupShadow(_ shadow: ImageViewState) {
        guard shadowView == nil else {
            return
        }
        shadowView = CustomImageView(theme: self.theme, imageViewState: shadow)
        
        // This statement fixed an issue that was faced for 3 hours. Here is the chat with ChatGPT that helped solve it -
        // https://chatgpt.com/share/ef7b3fdb-c482-4be2-a552-37310de2821c
        shadowView.layer.masksToBounds = true
        
        self.addSubview(shadowView)
        shadowView.translatesAutoresizingMaskIntoConstraints = false
        shadowBottomConstraint = shadowView.bottomAnchor.constraint(
            equalTo: bottomAnchor,
            constant: -bottomPaddingShadow
        )
        shadowBottomConstraint?.isActive = true
        shadowView.rotate(angle: 180)
        
        shadowView.setup()
    }
}


class HighlightCarouselConstants {
    static let excessHeight: CGFloat = 100
    static let scrollScaler = 1/1.5
}
