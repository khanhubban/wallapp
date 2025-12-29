//
//  SwiftAppViewModel.swift
//  WallApp
//

import WallApp
import FirebaseAuth
import FirebaseCore
import Foundation
import GoogleSignIn
import UIKit

@MainActor
final class GoogleSignInHelper {
    
    private lazy var signInProviderController = InteropModulesIos.shared.signInProviderController
    
    private func handleSignInSuccess() {
        let result = GoogleAuthSignInResult(success: true, error: nil)
        self.signInProviderController.onGoogleSignInResult(result: result)
    }
    
    private func handleSignInError(_ error: Error?, from function: StaticString = #function) {
        guard let error = error else { return }
        Log.e("ⓧ SignIn error in \(function): \(error.localizedDescription)")

        let result = GoogleAuthSignInResult(
            success: false,
            error: error as NSError
        )
        self.signInProviderController.onGoogleSignInResult(result: result)
    }
    
    public func performGoogleSignInFlow() async {
        Log.d("performGoogleSignInFlow() start")
        // [START headless_google_auth]
        guard let clientID = FirebaseApp.app()?.options.clientID else { return }
        
        let config = GIDConfiguration(clientID: clientID)
        GIDSignIn.sharedInstance.configuration = config
        
        guard let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
              let window = windowScene.windows.first,
              let rootViewController = window.rootViewController else {
            Log.w("There is no root view controller!")
            return
        }
        
        do {
            let userAuthentication = try await GIDSignIn.sharedInstance.signIn(withPresenting: rootViewController)
            
            let user = userAuthentication.user
            guard let idToken = user.idToken else {
                throw NSError(
                    domain: "GIDSignInError",
                    code: -1,
                    userInfo: [
                        NSLocalizedDescriptionKey: "Unexpected sign in result: required authentication data is missing.",
                    ]
                )
            }
            handleSignInSuccess()
        }
        catch {
            Log.e(error.localizedDescription)
            handleSignInError(error)
        }
    }
}
