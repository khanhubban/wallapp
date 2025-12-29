package wallapp.content.state.error.rewardad

// Listed in order of priority in which they should be handled.
enum class ErrorRewardAdMode {
    NetworkError,
    AdConsentDenied,
    Other,
}