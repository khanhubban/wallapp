package wallapp.dataverification

data class DataVerificationResults(
    val success: List<DataVerificationResult.Success>,
    val errors: List<DataVerificationResult.Error>,
    val additionalSummary: String? = null,
) {
    val hasErrors: Boolean
        get() = errors.isNotEmpty()

    val total: Int
        get() = success.size + errors.size

    fun logString(
        logPrefix: String = "",
        includeSuccess: Boolean = true,
        includeErrors: Boolean = true,
    ): String {
        val logBuilder = StringBuilder()

        if (includeSuccess) {
            logBuilder.append("Successes (${success.size} / ${total}):\n")
            success.forEach { logBuilder.append("$logPrefix ${it.message}\n") }
            logBuilder.append("\n")
        }
        if (includeErrors) {
            logBuilder.append("Errors (${errors.size} / ${total}):\n")
            errors.forEach { logBuilder.append("$logPrefix ${it.message}\n") }
            logBuilder.append("\n")
        }

        logBuilder.append("Summary:\n")
        logBuilder.append("$logPrefix Success: ${success.size}, errors: ${errors.size}, total: $total\n")
        if (additionalSummary != null) {
            logBuilder.append("$logPrefix $additionalSummary\n")
        }

        return logBuilder.toString()
    }
}