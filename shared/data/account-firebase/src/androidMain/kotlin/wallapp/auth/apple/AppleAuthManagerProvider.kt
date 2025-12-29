package wallapp.auth.apple

actual fun provideAppleAuthManager(): AppleAuthManager {
    return AppleAuthManagerNoOp
}