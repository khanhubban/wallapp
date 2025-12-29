//
//  GoogleAuthCoordinatorForIosDefault.swift
//  WallApp
//

import WallApp
import Foundation
import GoogleSignIn

class GoogleAuthCoordinatorForIosDefault : GoogleAuthCoordinatorForIos {
    private var gidSignIn: GIDSignIn {
        GIDSignIn.sharedInstance
    }
    
    func isSignedIn() -> Bool {
        return gidSignIn.hasPreviousSignIn()
    }
    
    func onNewSignIn(result: GoogleAuthSignInResult) -> Bool {
        let currentUser = gidSignIn.currentUser
        return currentUser != nil
    }
    
    func signOut() {
        gidSignIn.signOut()
    }
    
    func getGoogleAuthData() -> GoogleAuthData? {
        let gidUser = gidSignIn.currentUser
        let idToken = gidUser?.idToken?.tokenString
        let accessToken = gidUser?.accessToken.tokenString
        let name = gidUser?.profile?.name
        let photoUrl = gidUser?.profile?.imageURL(withDimension: 100)?.absoluteString
        if let idToken = idToken, let accessToken = accessToken {
            return GoogleAuthData(
                idToken: idToken,
                accessToken: accessToken,
                name: name,
                photoUrl: photoUrl
            )
        } else {
            return nil
        }
    }
}
