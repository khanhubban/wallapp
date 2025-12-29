//
//  AnyShape.swift
//  WallApp
//

import SwiftUI

struct AnyShape: Shape {
    private let _path: (@Sendable (CGRect) -> Path)
    
    init<S: Shape>(_ wrapped: S) {
        _path = wrapped.path(in:)
    }
    
    func path(in rect: CGRect) -> Path {
        _path(rect)
    }
}
