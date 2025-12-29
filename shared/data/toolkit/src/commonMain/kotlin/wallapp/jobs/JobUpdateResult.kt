package wallapp.jobs

fun interface JobUpdateResult {

    fun onUpdateFinished(success: Boolean)

}