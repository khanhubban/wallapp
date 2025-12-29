package wallapp.network

import okhttp3.Interceptor
import okhttp3.Response


class DelayInterceptor(private val delayDuration: Long) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val delay = delayDuration
        if (delay > 0) {
            try {
                Thread.sleep(delay)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
        return chain.proceed(chain.request())
    }

    init {
        require(delayDuration >= 0) { "Duration must be positive." }
    }
}