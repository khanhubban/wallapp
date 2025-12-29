//
//  KotlinExtensions.swift
//  WallApp
//

import WallApp
import Foundation
import SwiftUI

extension CommonView: Identifiable {
    public var id: String {
        if let viewState = viewState as? SpacerViewState {
            return viewState.id != nil ? "\(viewState.id!)" : UUID().uuidString
        } else if let viewState = viewState as? FeedAdViewState {
            return viewState.id != nil ? "\(viewState.id!)" : UUID().uuidString
        } else if let viewState = viewState as? ArtistPreviewViewState {
            return viewState.name.string
        }
        return viewState.viewId?.id != nil ? viewState.viewId!.id : UUID().uuidString
    }
}

extension DpOptional {
    func toCGFloat() -> CGFloat {
        return CGFloat(self.dp)
    }
}

extension ResourceHashedImage {
    func toHashString() -> String? {
        (self.imageHash as? ImageHash.ImageHashBlur)?.blurHash
    }
}

extension FeedAdViewState {
    static let nativeAdHeight: CGFloat = 360
}

extension ImageViewState {
    func toUrl() -> URL? {
        if let imageUI = self.image as? ImageUIImageStates, let successImageUI = imageUI.success as? ImageUIImageResource, let imageUrl = (successImageUI.resource as? ResourceUrlImage)?.url {
            let width = self.viewSpec.width
            let height = self.viewSpec.height
            let urlString = CustomImageView.process(imageUrl: imageUrl, width: width, height: height)
            return URL(string: urlString)
        }
        return nil
    }
    
    func toVideoUrl() -> URL? {
        if let imageUI = self.image as? ImageUIImageStates, let successImageUI = imageUI.success as? ImageUIImageResource, let imageUrl = (successImageUI.resource as? ResourceUrlVideo)?.url {
            let width = self.viewSpec.width
            let height = self.viewSpec.height
            let urlString = CustomImageView.process(imageUrl: imageUrl, width: width, height: height)
            return URL(string: urlString)
        }
        return nil
    }
}

extension ScreenArgument {
    var sheetHeight: CGFloat {
        switch onEnum(of: self) {
        case .collectionActionScreenArgument(let argument):
            return argument.modalSheetHeight.toCGFloat()
        case .wallpaperSingleActionScreenArgument(let argument):
            return argument.modalSheetHeight.toCGFloat()
        default:
            return 0
        }
    }
    
    // A persistent sheet would be opened again when returning
    // from a screen that was navigated to from the sheet
    var persistentModalSheet: Bool {
        switch onEnum(of: self) {
        case .wallpaperShowcaseScreenArgument(_):
            return true
        case .wallpaperSingleActionScreenArgument(_):
            return true
        case .collectionActionScreenArgument(_):
            return true
        default:
            return false
        }
    }
}


func lerp(_ a: CGFloat, _ b: CGFloat, _ t: CGFloat) -> CGFloat {
    return a + (b - a) * t
}
func lerp(_ a: Double, _ b: Double, _ t: Double) -> Double {
    return a + (b - a) * t
}
func lerp(_ a: Int, _ b: Int, _ t: Float) -> Int {
    return a + Int(Float(b - a) * t)
}
func lerp(_ a: Float, _ b: Float, _ t: Float) -> Float {
    return a + (b - a) * t
}
