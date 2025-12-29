//
//  ImagePrefetcherKingfisher.swift
//  WallApp
//

import WallApp
import Kingfisher

class ImagePrefetcherKingfisher : ImagePrefetcherEx {
    
    private var imageDiskCacheForIos: ImageDiskCacheForIos {
        InteropModulesIos.shared.imageDiskCacheForIos
    }
    
    private var lastImagePrefetchers: [ImagePrefetcher] = []
    
    func prefetch(imagePrefetchData: ImagePrefetchData) {
        lastImagePrefetchers.forEach { $0.stop() }
        lastImagePrefetchers.removeAll()
        
        guard let defaultUrls: [URL] = (imagePrefetchData.entries?.filter {
            $0.imageCacheSpec.memoryCachePolicy != .disabled && $0.imageCacheSpec.diskCachePolicy != .disabled
        }.compactMap { entry in
            entry.imageViewState.toUrl()
        }) else { return }
        lastImagePrefetchers.append(
            ImagePrefetcher(urls: defaultUrls) { skipped, failed, completed in
//                Log.d("[ImagePrefetcherKingfisher] default skipped: \(skipped.count), failed: \(failed.count), completed: \(completed.count)")
            }
        )
//        Log.d("[ImagePrefetcherKingfisher] defaultUrls: \(defaultUrls.map { $0.absoluteString })")
        
        guard let diskOnlyUrls = (imagePrefetchData.entries?.filter {
            $0.imageCacheSpec.memoryCachePolicy == .disabled
        }.compactMap { entry in
            entry.imageViewState.toUrl()
        }) else { return }
        lastImagePrefetchers.append(
            ImagePrefetcher(urls: diskOnlyUrls, options: [.memoryCacheExpiration(.expired)]) { skipped, failed, completed in
//                Log.d("[ImagePrefetcherKingfisher] diskOnly skipped: \(skipped.count), failed: \(failed.count), completed: \(completed.count)")
            }
        )
//        Log.d("[ImagePrefetcherKingfisher] diskOnlyUrls: \(diskOnlyUrls.map { $0.absoluteString })")
        
        lastImagePrefetchers.forEach { $0.start() }
    }
    
    func prefetch(imagePrefetchEntry: ImagePrefetchEntry, onCompletion: (() -> Void)?) -> CancellableWork? {
        guard let imageUrl = imagePrefetchEntry.imageViewState.toUrl() else { return nil }
        
        if ImageCache.default.isCached(forKey: imageUrl.absoluteString) {
            Log.d("[ImagePrefetcherKingfisher] image already cached: \(imageUrl.absoluteString)")
            return nil
        }
        
            Log.d("[ImagePrefetcherKingfisher] prefetching image: \(imageUrl.absoluteString)")
        let task = KingfisherManager.shared.retrieveImage(with: imageUrl){ result in
            switch result {
            case .success(let value):
                if value.cacheType == .none {
                    Log.d("[ImagePrefetcherKingfisher] image cached: \(imageUrl.absoluteString)")
                }
            case .failure(_):
                break
            }
            onCompletion?()
        }
        return CancellableWorkDownload(downloadTask: task)
    }
    
    private func saveInCache(imageUrl: URL, data: Data) {
        Task.detached {
            self.imageDiskCacheForIos.put(key: imageUrl.absoluteString, data: data.toByteArray())
        }
    }
}

final class CancellableWorkDownload: CancellableWork {
    
    private var downloadTask: DownloadTask?
    
    init(downloadTask: DownloadTask? = nil) {
        self.downloadTask = downloadTask
    }
    
    func cancel() {
        downloadTask?.cancel()
    }
}
