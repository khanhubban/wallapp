#if canImport(UIKit)
  import UIKit
  public typealias CacheImage = UIImage

#elseif os(OSX)
  import AppKit
  public typealias CacheImage = NSImage
#endif
