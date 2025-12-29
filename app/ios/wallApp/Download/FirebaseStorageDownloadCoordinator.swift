//
//  FirebaseStorageDownloadCoordinator.swift
//  WallApp
//

import WallApp
import FirebaseStorage
import Foundation

class FirebaseStorageDownloadCoordinator : FirebaseStorageDownloadCoordinatorIos {
    func downloadFile(path: String, completion: @escaping (String?, FirebaseStorageDownloadError?) -> Void) -> CancellableWork {
        let storage = Storage.storage()
        let storageRef = storage.reference()
        let fileRef = storageRef.child(path)
        
        let localURL = URL(fileURLWithPath: NSTemporaryDirectory()).appendingPathComponent(path)
        
        Log.d("[FirebaseStorageDownloadCoordinator] Downloading file to: \(localURL.path)")
        let downloadTask = fileRef.write(toFile: localURL) { url, error in
            if let error {
                self.handleError(error: error, completion: completion)
            } else {
                Log.d("[FirebaseStorageDownloadCoordinator] File downloaded successfully: \(url?.path ?? "nil")")
                completion(url?.path, nil)
            }
        }
        return CancellableFirebaseDownload(downloadTask: downloadTask)
    }
    
    private func handleError(error: Error, completion: @escaping (String?, FirebaseStorageDownloadError?) -> Void) {
        let errorCode = (error as NSError).code
        Log.e("[FirebaseStorageDownloadCoordinator] Error downloading file: \(error.localizedDescription), code: \(errorCode)")
        var firebaseError = FirebaseStorageDownloadError(
            message: error.localizedDescription,
            code: Int32(errorCode),
            userCancelled: StorageErrorCode(rawValue: errorCode) == .cancelled
        )
        completion(nil, firebaseError)
    }
}

class CancellableFirebaseDownload : CancellableWork {
    private var downloadTask: StorageDownloadTask?
    
    init(downloadTask: StorageDownloadTask) {
        self.downloadTask = downloadTask
    }
    
    func cancel() {
        downloadTask?.cancel()
    }
}

