import WallApp
import Foundation

public class MemoryStorage<Key: Hashable, Value>: StorageAware {
    private var crashTracking: CrashTracking {
        CrashTrackingHolder.shared.crashTracking
    }
    
    final class WrappedKey: NSObject {
        let key: Key

        init(_ key: Key) {
            self.key = key
        }

        override var hash: Int { return key.hashValue }

        override func isEqual(_ object: Any?) -> Bool {
            guard let value = object as? WrappedKey else {
                return false
            }
            return value.key == key
        }
    }

    fileprivate let cache = NSCache<WrappedKey, MemoryCapsule>()
    // Memory cache keys
    fileprivate var keys = Set<WrappedKey>()
    /// Configuration
    fileprivate let config: MemoryConfig
    /// Dispatch queue for synchronizing access to keys
    private let keyAccessQueue = DispatchQueue(label: "com.memoryStorage.keys", attributes: .concurrent)

    public init(config: MemoryConfig) {
        self.config = config
        self.cache.countLimit = Int(config.countLimit)
        self.cache.totalCostLimit = Int(config.totalCostLimit)
    }
}

extension MemoryStorage {
    public var allKeys: [Key] {
        return keyAccessQueue.sync {
            return keys.map { $0.key }
        }
    }

    public var allObjects: [Value] {
        return allKeys.compactMap { try? object(forKey: $0) }
    }

    public func setObject(_ object: Value, forKey key: Key, expiry: Expiry? = nil) {
        let wrappedKey = WrappedKey(key)
        let expiryDate = expiry?.date ?? config.expiry.date
        let capsule = MemoryCapsule(value: object, expiry: .date(expiryDate))
        cache.setObject(capsule, forKey: wrappedKey)
        keyAccessQueue.async(flags: .barrier) {
            self.keys.insert(wrappedKey)
        }
        // Debugging logs
        let keyAddress = Unmanaged.passUnretained(wrappedKey).toOpaque()
        Log.d("[CustomMemoryStorage] Set object for key: \(key), address: \(keyAddress)")
    }

    public func removeAll() {
        cache.removeAllObjects()
        keyAccessQueue.async(flags: .barrier) {
            self.keys.removeAll()
        }
    }

    public func removeExpiredObjects() {
        var keysToRemove = [Key]()
        keyAccessQueue.sync {
            let allKeysCopy = self.keys
            for wrappedKey in allKeysCopy {
                if let capsule = cache.object(forKey: wrappedKey), capsule.expiry.isExpired {
                    keysToRemove.append(wrappedKey.key)
                }
            }
        }
        for key in keysToRemove {
            removeObject(forKey: key)
        }
    }

    public func removeObjectIfExpired(forKey key: Key) {
        let wrappedKey = WrappedKey(key)
        if let capsule = cache.object(forKey: wrappedKey), capsule.expiry.isExpired {
            removeObject(forKey: key)
        }
    }

    public func removeObject(forKey key: Key) {
        let wrappedKey = WrappedKey(key)
        cache.removeObject(forKey: wrappedKey)
        keyAccessQueue.async(flags: .barrier) {
            self.keys.remove(wrappedKey)
        }
    }

    public func removeInMemoryObject(forKey key: Key) throws {
        let wrappedKey = WrappedKey(key)
        cache.removeObject(forKey: wrappedKey)
        keyAccessQueue.async(flags: .barrier) {
            self.keys.remove(wrappedKey)
        }
    }

    public func entry(forKey key: Key) throws -> Entry<Value> {
        let wrappedKey = WrappedKey(key)
        guard let capsule = cache.object(forKey: wrappedKey) else {
            throw StorageError.notFound
        }

        guard let object = capsule.object as? Value else {
            throw StorageError.typeNotMatch
        }

        return Entry(object: object, expiry: capsule.expiry)
    }

    public func object(forKey key: Key) throws -> Value {
        return try entry(forKey: key).object
    }
}

public extension MemoryStorage {
    func transform<U>() -> MemoryStorage<Key, U> {
        let storage = MemoryStorage<Key, U>(config: config)
        return storage
    }
}
