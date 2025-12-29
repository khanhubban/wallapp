//
//  Log.swift
//  WallApp
//

import Foundation
import WallApp

class Log {
//    private static func formatMessage(_ message: String, with args: [Any?]) -> String {
//        let formatString = message.replacingOccurrences(of: "%@", with: "%@")
//        let mappedArgs = args.map { $0 ?? "nil" }
//        return String(format: formatString, arguments: mappedArgs as [CVarArg])
//    }
    private static func formatMessage(_ message: String, with args: [Any?]) -> String {
        let formatString = message.replacingOccurrences(of: "%@", with: "%@")
        
        // Convert each argument to a string, which is a CVarArg
        let mappedArgs = args.map { "\($0 ?? "nil")" }

        return String(format: formatString, arguments: mappedArgs)
    }

    static func e(_ message: String, _ args: Any?...) {
        LogKt.shared.e(message: formatMessage(message, with: args))
    }

//    static func e(_ t: Error?, _ message: String? = nil, _ args: Any?...) {
//        emitter?.e(t, message, args)
//    }

    static func w(_ message: String, _ args: Any?...) {
        LogKt.shared.w(message: formatMessage(message, with: args))
//        LogKt.shared.w(formatMessage(message, with: args))
    }

//    static func w(_ t: Error?, _ message: String? = nil, _ args: Any?...) {
//        emitter?.w(t, message, args)
//    }

    static func i(_ message: String, _ args: Any?...) {
        LogKt.shared.i(message: formatMessage(message, with: args))
    }

//    static func i(_ t: Error?, _ message: String? = nil, _ args: Any?...) {
//        emitter?.i(t, message, args)
//    }

    static func d(_ message: String, _ args: Any?...) {
        LogKt.shared.d(message: formatMessage(message, with: args))
    }

//    static func d(_ t: Error?, _ message: String? = nil, _ args: Any?...) {
//        emitter?.d(t, message, args)
//    }

    static func v(_ message: String, _ args: Any?...) {
        LogKt.shared.v(message: formatMessage(message, with: args))
    }

//    static func v(_ t: Error?, _ message: String? = nil, _ args: Any?...) {
//        emitter?.v(t, message, args)
//    }
}

