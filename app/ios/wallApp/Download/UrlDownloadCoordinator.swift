//
//  UrlDownloadCoordinator.swift
//  WallApp
//

import WallApp
import Foundation
import UIKit


import Foundation
import UIKit

struct DownloadInfoHolder {
    var task: URLSessionTask
    var identifier: UIBackgroundTaskIdentifier
    var progress: (KotlinFloat) -> Void
    var completion: (String) -> Void
    var failure: (String) -> Void
    var cancelled: () -> Void
}

class UrlDownloadCoordinatorDefault: NSObject, URLSessionDownloadDelegate, UrlDownloadCoordinatorForIos {
    
    private var downloadInfoMap: [String: DownloadInfoHolder] = [:]
    private let accessQueue = DispatchQueue(label: "wallapp.urlDownloadCoordinator")

    func downloadUrlToFile(url: String,
                           downloadProgress: @escaping (KotlinFloat) -> Void,
                           downloadCompleted: @escaping (String) -> Void,
                           downloadError: @escaping (String) -> Void,
                           downloadCancelled: @escaping () -> Void) {
        
        guard let urlObject = URL(string: url) else {
            downloadError("Invalid URL")
            return
        }

        accessQueue.async { [weak self] in
            guard self?.downloadInfoMap[url] == nil else {
                // A download for this URL is already in progress, ignore this request
                return
            }
            
            let sessionConfig = URLSessionConfiguration.default
            let session = URLSession(configuration: sessionConfig, delegate: self, delegateQueue: OperationQueue())
            let downloadTask = session.downloadTask(with: urlObject)
            
            var backgroundTaskId: UIBackgroundTaskIdentifier = .invalid
            backgroundTaskId = UIApplication.shared.beginBackgroundTask { [weak self] in
                Log.d("[UrlDownloadCoordinator] App went to background. Cancelling download.")
                downloadTask.cancel()
                self?.accessQueue.async {
                    self?.downloadInfoMap[url]?.failure("Download cancelled as app went to background.")
                    self?.downloadInfoMap.removeValue(forKey: url)
                }
                UIApplication.shared.endBackgroundTask(backgroundTaskId)
            }
            
            let downloadInfo = DownloadInfoHolder(
                task: downloadTask,
                identifier: backgroundTaskId,
                progress: downloadProgress,
                completion: downloadCompleted, 
                failure: downloadError,
                cancelled: downloadCancelled
            )
            self?.downloadInfoMap[url] = downloadInfo
            
            downloadTask.resume()
        }
    }

    func cancelDownload(url: String) {
        Log.d("[UrlDownloadCoordinator] Cancelling download for: \(url)")
        accessQueue.async { [weak self] in
            guard let downloadInfo = self?.downloadInfoMap[url] else { return }
            downloadInfo.task.cancel()
        }
    }

    // URLSessionDownloadDelegate methods
    func urlSession(_ session: URLSession, downloadTask: URLSessionDownloadTask, didFinishDownloadingTo location: URL) {
        do {
            let documentsURL = try FileManager.default.url(for: .documentDirectory,
                                                           in: .userDomainMask,
                                                           appropriateFor: nil,
                                                           create: false)
            let savedURL = documentsURL.appendingPathComponent(location.lastPathComponent)
            try FileManager.default.moveItem(at: location, to: savedURL)

            let taskURL = downloadTask.originalRequest?.url?.absoluteString ?? ""
            accessQueue.async { [weak self] in
                Log.d("[UrlDownloadCoordinator] Download completed, saved at \(savedURL.path)")
                if let downloadInfo = self?.downloadInfoMap[taskURL] {
                    downloadInfo.completion(savedURL.path)
                    UIApplication.shared.endBackgroundTask(downloadInfo.identifier)
                    self?.downloadInfoMap.removeValue(forKey: taskURL)
                }
            }
        } catch {
            let taskURL = downloadTask.originalRequest?.url?.absoluteString ?? ""
            Log.d("[UrlDownloadCoordinator] Error moving file: \(error.localizedDescription)")
            accessQueue.async { [weak self] in
                self?.downloadInfoMap[taskURL]?.failure("File error: \(error.localizedDescription)")
                if let downloadInfo = self?.downloadInfoMap[taskURL] {
                    UIApplication.shared.endBackgroundTask(downloadInfo.identifier)
                }
                self?.downloadInfoMap.removeValue(forKey: taskURL)
            }
        }
    }
    
    func urlSession(_ session: URLSession, downloadTask: URLSessionDownloadTask, didWriteData bytesWritten: Int64, totalBytesWritten: Int64, totalBytesExpectedToWrite: Int64) {
        let progress = Float(totalBytesWritten) / Float(totalBytesExpectedToWrite)
        let taskURL = downloadTask.originalRequest?.url?.absoluteString ?? ""
        Log.d("[UrlDownloadCoordinator] Download progress: \(progress)")
        accessQueue.async { [weak self] in
            self?.downloadInfoMap[taskURL]?.progress(progress.toKotlinFloat())
        }
    }
    
    func urlSession(_ session: URLSession, task: URLSessionTask, didCompleteWithError error: Error?) {
        if let error = error {
            let error = error as NSError
            let taskURL = task.originalRequest?.url?.absoluteString ?? ""
            Log.d("[UrlDownloadCoordinator] Download error: \(error.localizedDescription), type: \(error.self)")
            accessQueue.async { [weak self] in
                let downloadInfo = self?.downloadInfoMap[taskURL]
                Log.d("[UrlDownloadCoordinator] didCompleteWithError downloadInfo: \(String(describing: downloadInfo)), errorCode: \(error.code)")
                if error.code == -999 {
                    downloadInfo?.cancelled()
                } else {
                    downloadInfo?.failure("Download error: \(error.localizedDescription)")
                }
                if let downloadInfo = self?.downloadInfoMap[taskURL] {
                    UIApplication.shared.endBackgroundTask(downloadInfo.identifier)
                }
                self?.downloadInfoMap.removeValue(forKey: taskURL)
            }
        }
    }
}

