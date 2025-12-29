//
//  BuildConfigIos.swift
//  WallApp
//

import Foundation
import WallApp


class BuildConfigIos: BuildConfig {
    
    var debug: Bool {
        // Set this based on your configuration. Example:
        #if DEBUG
        return true
        #else
        return false
        #endif
    }

    var buildNumber: Int64 {
        // iOS doesn't use a version code like Android, but you can use build number
        guard let buildNumber = Bundle.main.infoDictionary?["CFBundleVersion"] as? String, let number = Int64(buildNumber) else {
            return 0
        }
        return number
    }

    var shortVersion: String {
        // App version
        guard let version = Bundle.main.infoDictionary?["CFBundleShortVersionString"] as? String else {
            return "<unknown shortVersion>"
        }
        return version
    }
    
    var appVersion: AppVersion {
        return AppVersion.AppVersionIos(shortVersion: shortVersion, buildNumber: buildNumber)
    }

    var packageName: String {
        // Bundle Identifier
        guard let bundleIdentifier = Bundle.main.bundleIdentifier else {
            return "<unknown packageName>"
        }
        return bundleIdentifier
    }

    var appName: String {
        // App Name
        guard let appName = Bundle.main.infoDictionary?["CFBundleName"] as? String else {
            return "<unknown appName>"
        }
        return appName
    }

    init() {
        printProperties()
    }
    
    private func printProperties() {
        let tag = "BuildConfig"
        Log.d("\(tag): Debug: \(debug)")
        Log.d("\(tag): App Version: \(appVersion.versionName)")
        Log.d("\(tag): Package Name: \(packageName)")
        Log.d("\(tag): App Name: \(appName)")
    }
}
