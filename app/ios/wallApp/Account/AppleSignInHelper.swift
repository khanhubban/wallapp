//
//  AppleSignInHelper.swift
//  WallApp
//

import Foundation
import AuthenticationServices
import WallApp
import CryptoKit

class AppleSignInHelper : NSObject, ASAuthorizationControllerDelegate {
    
    private var currentNonce: String?
    
    private lazy var signInProviderController: SignInProviderController = InteropModulesIos.shared.signInProviderController
    
    func startSignInWithAppleFlow() {
      let nonce = randomNonceString()
      currentNonce = nonce
      let appleIDProvider = ASAuthorizationAppleIDProvider()
      let request = appleIDProvider.createRequest()
      request.requestedScopes = [.fullName, .email]
      request.nonce = sha256(nonce)

      let authorizationController = ASAuthorizationController(authorizationRequests: [request])
      authorizationController.delegate = self
      authorizationController.performRequests()
    }
    
    func authorizationController(controller: ASAuthorizationController, didCompleteWithAuthorization authorization: ASAuthorization) {
        do {
            if let appleIDCredential = authorization.credential as? ASAuthorizationAppleIDCredential {
                guard let nonce = currentNonce else {
                    throw error(msg: "Invalid state: a login callback was received, but no login request was sent.")
                }
                guard let appleIDToken = appleIDCredential.identityToken else {
                    throw error(msg: "Unable to fetch identity token")
                }
                guard let idTokenString = String(data: appleIDToken, encoding: .utf8) else {
                    throw error(msg: "Unable to serialize token string from data: \(appleIDToken.debugDescription)")
                }
                let appleSignInResult = AppleAuthSignInResult(
                    success: true,
                    idToken: idTokenString,
                    rawNonce: nonce,
                    fullName: appleIDCredential.fullName,
                    authorizationCodeString: String(data: appleIDCredential.authorizationCode!, encoding: .utf8),
                    error: nil
                )
                Log.d("[firebase] Signed in with credential: \(appleSignInResult)")
                signInProviderController.onAppleSignInResult(result: appleSignInResult)
            }
        } catch {
            Log.e("[firebase] Error handling result: \(error.localizedDescription)")
            handleError(error: error)
        }
    }
    
    func authorizationController(controller: ASAuthorizationController, didCompleteWithError error: Error) {
        // Handle error.
        print("[firebase] Sign in with Apple errored: \(error)")
        handleError(error: error)
    }
    
    private func handleError(error: Error) {
        let appleAuthSignInResult = AppleAuthSignInResult(
            success: false,
            idToken: nil,
            rawNonce: nil,
            fullName: nil,
            authorizationCodeString: nil,
            error: error as NSError
        )
        signInProviderController.onAppleSignInResult(result: appleAuthSignInResult)
    }
    
    private func error(msg: String) -> NSError {
        return NSError(
            domain: "AppleSignIn",
            code: -2,
            userInfo: [
                NSLocalizedDescriptionKey: msg,
            ]
        )
    }
}

// Adapted from https://auth0.com/docs/api-auth/tutorials/nonce#generate-a-cryptographically-random-nonce
private func randomNonceString(length: Int = 32) -> String {
    precondition(length > 0)
    let charset: [Character] =
    Array("0123456789ABCDEFGHIJKLMNOPQRSTUVXYZabcdefghijklmnopqrstuvwxyz-._")
    var result = ""
    var remainingLength = length
    
    while remainingLength > 0 {
        let randoms: [UInt8] = (0 ..< 16).map { _ in
            var random: UInt8 = 0
            let errorCode = SecRandomCopyBytes(kSecRandomDefault, 1, &random)
            if errorCode != errSecSuccess {
                fatalError(
                    "[firebase] Unable to generate nonce. SecRandomCopyBytes failed with OSStatus \(errorCode)"
                )
            }
            return random
        }
        
        randoms.forEach { random in
            if remainingLength == 0 {
                return
            }
            
            if random < charset.count {
                result.append(charset[Int(random)])
                remainingLength -= 1
            }
        }
    }
    
    return result
}

private func sha256(_ input: String) -> String {
    let inputData = Data(input.utf8)
    let hashedData = SHA256.hash(data: inputData)
    let hashString = hashedData.compactMap {
        String(format: "%02x", $0)
    }.joined()
    
    return hashString
}
