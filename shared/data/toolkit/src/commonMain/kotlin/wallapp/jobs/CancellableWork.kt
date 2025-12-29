package wallapp.jobs

import kotlinx.coroutines.Job
import wallapp.util.CancellableWork

data class CancellableWorkDefault(val job: Job) : CancellableWork {
    override fun cancel() {
        job.cancel()
    }
}