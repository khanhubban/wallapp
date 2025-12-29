package wallapp.executor

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

private const val NUMBER_OF_THREADS = 4 // TODO: Make this depend on device's hw via Runtime.getRuntime().availableProcessors()

val executorService: ExecutorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS)
    @JvmName("getExecutorService") get() = field