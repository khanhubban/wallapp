#if canImport(UIKit)
import UIKit
#endif

import Foundation

public extension CustomCacheStorage {
  func transformData() -> CustomCacheStorage<Key, Data> {
    let storage = transform(transformer: TransformerFactory.forData())
    return storage
  }


  func transformImage() -> CustomCacheStorage<Key, CacheImage> {
    let storage = transform(transformer: TransformerFactory.forImage())
    return storage
  }

  func transformCodable<U: Codable>(ofType: U.Type) -> CustomCacheStorage<Key, U> {
    let storage = transform(transformer: TransformerFactory.forCodable(ofType: U.self))
    return storage
  }
}
