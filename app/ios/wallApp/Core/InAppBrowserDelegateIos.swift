import SafariServices
import UIKit

class InAppBrowserDelegateIos: NSObject, InAppBrowserDelegate {
    
    private var safariViewController: SFSafariViewController?
    private var callbacks: InAppBrowserDelegateCallbacks?
    private var initialUrl: URL?

    // Function to present the URL using SFSafariViewController, falling back to system browser if necessary
    func show(url: String, callbacks: InAppBrowserDelegateCallbacks) {
        self.callbacks = callbacks

        guard let validUrl = URL(string: url) else {
            // Invalid URL, trigger the error callback
            callbacks.onError()
            return
        }

        self.initialUrl = validUrl  // Store the URL

        // Get the top-most view controller to present the Safari View Controller
        guard let topViewController = getTopViewController() else {
            // Failed to get a valid view controller, trigger the error callback
            callbacks.onError()
            return
        }

        safariViewController = SFSafariViewController(url: validUrl)
        safariViewController?.delegate = self

        if let safariVC = safariViewController {
            topViewController.present(safariVC, animated: true) { [weak self] in
                self?.callbacks?.onShow()
            }
        }
    }

    // Fallback to system browser if SFSafariViewController cannot be used
    private func openSystemBrowser(url: URL) {
        UIApplication.shared.open(url, options: [:]) { [weak self] success in
            if success {
                self?.callbacks?.onOpenToSystemBrowser()
            } else {
                self?.callbacks?.onError()
            }
        }
    }

    // Helper to get the top-most view controller for presenting Safari or system browser
    private func getTopViewController() -> UIViewController? {
        if let scene = UIApplication.shared.connectedScenes.first(where: { $0.activationState == .foregroundActive }) as? UIWindowScene {
            if let topController = scene.windows.first(where: { $0.isKeyWindow })?.rootViewController {
                return getTopPresentedViewController(from: topController)
            }
        }
        return nil
    }

    private func getTopPresentedViewController(from viewController: UIViewController) -> UIViewController {
        var currentController = viewController
        while let presentedViewController = currentController.presentedViewController {
            currentController = presentedViewController
        }
        return currentController
    }
}

// Safari View Controller Delegate to handle dismiss events
extension InAppBrowserDelegateIos: SFSafariViewControllerDelegate {
    // Safari View Controller has been dismissed by the user
    func safariViewControllerDidFinish(_ controller: SFSafariViewController) {
        safariViewController = nil
        callbacks?.onDismiss()
    }

    // Handle any potential load failure or other events and fallback to system browser
    func safariViewController(_ controller: SFSafariViewController, didCompleteInitialLoad didLoadSuccessfully: Bool) {
        if !didLoadSuccessfully, let url = self.initialUrl {
            // Fallback to system browser if the page failed to load
            safariViewController?.dismiss(animated: true) { [weak self] in
                self?.safariViewController = nil  // Reset after dismiss
                self?.openSystemBrowser(url: url)
            }
        }
    }
}
