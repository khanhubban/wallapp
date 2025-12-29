//
//  SystemPhotoPickeriOS.swift
//  WallApp
//

import WallApp
import SwiftUI
import UIKit
import Photos

class SystemPhotoPickerIos : NSObject, SystemPhotoPicker {
    
    var onComplete : ((SystemPhotoPickerResult) -> Void)?
    
    var enabled: Bool = true
    
    var imagePicker = UIImagePickerController()
    
    func navigateToSystemPhotoPicker(onComplete: @escaping (SystemPhotoPickerResult) -> Void) {
        self.onComplete = onComplete
        openGallery()
    }
    
    private func openGallery() {
        self.imagePicker.sourceType = .photoLibrary
        self.imagePicker.allowsEditing = true
        self.imagePicker.delegate = self
        guard let topViewController = UIApplication.shared.topViewController() else {
            return
        }
        topViewController.present(self.imagePicker, animated: true, completion: nil)
    }
    
}

extension SystemPhotoPickerIos: UIImagePickerControllerDelegate, UINavigationControllerDelegate {
    private static let maxSize = CGSize(width: 512, height: 512)
    
    func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey : Any]) {
        picker.dismiss(animated: true, completion: {
            if let pickedImage = info[.editedImage] as? UIImage {
                Log.d("[SystemPhotoPickerIos] pickedImage, size: \(pickedImage.size)")
                Task.detached {
                    var resizedImage = pickedImage
                    if pickedImage.size.width > SystemPhotoPickerIos.maxSize.width || pickedImage.size.height > SystemPhotoPickerIos.maxSize.height {
                        Log.d("[SystemPhotoPickerIos] resizing image")
                        resizedImage = pickedImage.resizeWith(newSize: SystemPhotoPickerIos.maxSize)
                    }
                    if let imageData = resizedImage.pngData() {
                        let byteArray = imageData.toByteArray()
                        self.onComplete?(.Success(data: byteArray))
                        Log.d("[SystemPhotoPickerIos] Success: \(byteArray.size)")
                    } else {
                        self.onComplete?(.Failure())
                        Log.d("[SystemPhotoPickerIos] failed to convert image to data")
                    }
                }
            } else {
                self.onComplete?(.Failure())
                Log.d("[SystemPhotoPickerIos] failed to get image from info dictionary")
            }
        })
    }
    
    func imagePickerControllerDidCancel(_ picker: UIImagePickerController) {
        picker.dismiss(animated: true, completion: {
            self.onComplete?(.Failure())
            Log.d("[SystemPhotoPickerIos] cancelled")
        })
    }
}
