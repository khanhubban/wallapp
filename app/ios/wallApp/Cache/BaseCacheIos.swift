//
//  WallpaperImageCacheIos.swift
//  WallApp
//

import WallApp
import Foundation

final class BaseCacheIos : BaseCache {
    
    static let shared = BaseCacheIos()
    
    let diskConfig = DiskConfig(name: "WallpaperImageCache")
    
    private lazy var memoryStorage = MemoryStorage<String, Data>(config: MemoryConfig())
    private lazy var diskStorage = try! DiskStorage<String, Data>(config: diskConfig, transformer: TransformerFactory.forCodable(ofType: Data.self))

    private lazy var asyncStorage = AsyncStorage(
        storage: HybridStorage(memoryStorage: memoryStorage, diskStorage: diskStorage),
        serialQueue: DispatchQueue(label: "wallapp.baseCacheIos.asyncStorage")
    )
    
    private lazy var syncStorage = SyncStorage(
        storage: HybridStorage(memoryStorage: memoryStorage, diskStorage: diskStorage),
        serialQueue: DispatchQueue(label: "wallapp.baseCacheIos.syncStorage")
    )
    
    private init() {}
    
    var enabled: Bool = true
    
    func putData(key: String,
                            data: KotlinByteArray,
                            completionHandler: @escaping (KotlinBoolean) -> Void) {
        asyncStorage.setObject(data.toNSData(), forKey: key) { result in
            switch result {
            case .success():
                completionHandler(true)
            case .failure(let error):
                Log.e("\(error)")
                completionHandler(false)
            }
        }
    }
    
    func getData(key: String, completionHandler: @escaping (KotlinByteArray?) -> Void) {
        asyncStorage.object(forKey: key) { result in
            switch result {
            case .success(let data):
                completionHandler(data.toByteArray())
            case .failure(let error):
                Log.e("\(error)")
                completionHandler(nil)
            }
        }
    }
    
    func isCached(key: String) -> Bool {
        let exists = try? syncStorage.existsObject(forKey: key)
        if let exists {
            return exists
        } else {
            return false
        }
    }
    
    func clearCache() {
        asyncStorage.removeAll { _ in }
    }
    
    func clearInMemoryCache() {
        memoryStorage.removeAll()
    }
}
