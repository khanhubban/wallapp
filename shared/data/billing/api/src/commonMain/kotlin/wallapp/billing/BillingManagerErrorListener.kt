package wallapp.billing

interface BillingManagerErrorListener {

    fun onBillingError(titleSuffix: String?, message: String)
}