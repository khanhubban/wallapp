//
//  BottomSheetHostingController.swift
//  WallApp
//

import WallApp
import UIKit
import SwiftUI

class BottomSheetHostingController<Content: View>: UIViewController, UIAdaptivePresentationControllerDelegate {

    private let hostingController: UIHostingController<Content>
    private let viewModelStoreOwner: SharedViewModelStoreOwner<ViewModel>
    private var sheetHeight: CGFloat?
    private var isSecondarySheet: Bool
    private var isFullSheet: Bool

    init(isFullSheet: Bool, isSecondarySheet: Bool, viewModelStoreOwner: SharedViewModelStoreOwner<ViewModel>, @ViewBuilder content: () -> Content) {
        self.isFullSheet = isFullSheet
        self.isSecondarySheet = isSecondarySheet
        self.hostingController = UIHostingController(rootView: content())
        self.viewModelStoreOwner = viewModelStoreOwner
        super.init(nibName: nil, bundle: nil)
        setupSheetPreferences()
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        addChild(hostingController)
        view.addSubview(hostingController.view)
        hostingController.view.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint.activate([
            hostingController.view.topAnchor.constraint(equalTo: view.topAnchor),
            hostingController.view.bottomAnchor.constraint(equalTo: view.bottomAnchor),
            hostingController.view.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            hostingController.view.trailingAnchor.constraint(equalTo: view.trailingAnchor)
        ])
        hostingController.didMove(toParent: self)
        
        self.presentationController?.delegate = self
        
        if !isFullSheet {
            observeViewState()
        }
    }

    private func setupSheetPreferences() {
        if let sheet = self.sheetPresentationController {
            if isFullSheet {
                sheet.detents = [.large()]
            } else {
                sheet.detents = [.medium(), .large()]
            }
            sheet.preferredCornerRadius = isFullSheet ? 8 : 28
            sheet.prefersGrabberVisible = true
            sheet.prefersScrollingExpandsWhenScrolledToEdge = false
        }
    }

    private func observeViewState() {
        Task {
            if let viewModel = viewModelStoreOwner.instance as? ScreenViewStateProvider {
                for await viewState in viewModel.viewState {
                    if let newSheetHeight = getSheetHeight(from: viewState) {
                        if self.sheetHeight != newSheetHeight {
                            self.sheetHeight = newSheetHeight
                            updateSheetHeight(to: newSheetHeight)
                        }
                    }
                }
            }
        }
    }

    private func updateSheetHeight(to height: CGFloat) {
        DispatchQueue.main.async { [weak self] in
            guard let self = self else { return }
            let customDetent = UISheetPresentationController.Detent.custom { _ in height }
            if let sheet = self.sheetPresentationController {
                sheet.detents = [customDetent]
            }
        }
    }

    private func getSheetHeight(from viewState: Any) -> CGFloat? {
        if let successState = viewState as? CollectionActionViewState.Success {
            let viewSpec = successState.viewSpec
            let height = CGFloat(viewSpec.height)
            return height
        } else if let successState = viewState as? WallpaperSingleActionViewState.Success {
            let viewSpec = successState.viewSpec
            let height = CGFloat(viewSpec.height)
            return height
        } else {
            return nil
        }
    }
    
    func presentationControllerDidDismiss(_ presentationController: UIPresentationController) {
        DispatchQueue.main.async { [weak self] in
            guard let self = self else { return }
            if self.isSecondarySheet {
                Router.shared.secondarySheetDismissed()
            } else {
                Router.shared.sheetDismissed()
            }
        }
    }
}
