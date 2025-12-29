//
//  ContentView.swift
//  WallApp
//

import SwiftUI
import WallApp

struct ContentViewCompose: View {
    
    @EnvironmentObject var router: Router
    
    private let screenArgument: ScreenArgument?

    init(screenArgument: ScreenArgument?) {
        self.screenArgument = screenArgument
    }

    var body: some View {
        ContentViewVC(uiViewController: AppUIViewController.create(from: screenArgument))
            .ignoresSafeArea(.all, edges: .all)
            .navigationBarBackButtonHidden()
    }
}

struct ContentViewComposeSingle: View {
    let singleView: SingleView
    
    var body: some View {
        ContentViewVC(uiViewController: InteropModulesIos.shared.composeViewController(singleView: singleView))
    }
}


struct ContentViewVC: UIViewControllerRepresentable {
   @State var uiViewController: UIViewController

   init(uiViewController: UIViewController) {
       self.uiViewController = uiViewController
   }

   func makeUIViewController(context _: Context) -> UIViewController {
       return uiViewController
   }

   func updateUIViewController(_: UIViewController, context _: Context) {}
}

extension UINavigationController: UIGestureRecognizerDelegate {
    override open func viewDidLoad() {
        super.viewDidLoad()
        interactivePopGestureRecognizer?.delegate = self
    }

    public func gestureRecognizerShouldBegin(_ gestureRecognizer: UIGestureRecognizer) -> Bool {
        return viewControllers.count > 1
    }
}

struct ContentViewComposeWithLoading: View {
    @EnvironmentObject var router: Router
    
    private let render: RenderIos
    private let theme: Theme
    private let screenArgument: ScreenArgument
    
    @StateObject var viewModelStoreOwner: SharedViewModelStoreOwner<ViewModel>

    init(render: RenderIos, theme: Theme, screenArgument: ScreenArgument) {
        self.render = render
        self.theme = theme
        self.screenArgument = screenArgument
        _viewModelStoreOwner = StateObject(
            wrappedValue: SharedViewModelStoreOwner<ViewModel>(
                InteropModulesIos.shared.appViewModelFactory.createViewModelFor(argument: screenArgument) as! ViewModel
            )
        )
    }

    var body: some View {
        let uiViewController = ComposeWithLoadingViewController(
            theme: theme,
            render: render,
            screenArgument: screenArgument,
            viewModel: viewModelStoreOwner.instance
        )
        ContentViewVC(uiViewController: uiViewController)
            .ignoresSafeArea(.all, edges: .all)
            .navigationBarBackButtonHidden()
            .onAppear {
                router.screenAppeared(screenArgument)
            }
    }
}

struct PaywallContentView: View {
    @EnvironmentObject var router: Router
    
    let render: RenderIos
    let theme: Theme
    let screenArgument: ScreenArgument

    var body: some View {
        ContentViewComposeWithLoading(
            render: render,
            theme: theme,
            screenArgument: screenArgument
        )
        .navigationBarBackButtonHidden()
        .ignoresSafeArea(.all, edges: .all)
        .onDisappear {
            router.paywallDismissed()
        }
    }
}

struct ArtistScreenView: View {
    let theme: Theme
    let render: RenderIos
    let screenArgument: ScreenArgument.ArtistIdScreenArgument
    
    @StateObject var viewModelStoreOwner: SharedViewModelStoreOwner<ArtistViewModel>
    
    init(theme: Theme, render: RenderIos, screenArgument: ScreenArgument.ArtistIdScreenArgument) {
        self.theme = theme
        self.render = render
        self.screenArgument = screenArgument
        _viewModelStoreOwner = StateObject(
            wrappedValue: SharedViewModelStoreOwner<ArtistViewModel>(
                InteropModulesIos.shared.appViewModelFactory.createViewModelFor(argument: screenArgument) as! ArtistViewModel
            )
        )
    }
    
    var body: some View {
        ContentViewVC(
            uiViewController: ArtistViewController(
                theme: theme,
                render: render,
                screenArgument: screenArgument,
                viewModel: viewModelStoreOwner.instance
            )
        )
        .overlay {
            BottomScrimOverlayView(render: render, theme: theme)
        }
        .navigationBarBackButtonHidden()
        .ignoresSafeArea(.all, edges: .all)
        .navigationBarBackButtonHidden()
    }
}
