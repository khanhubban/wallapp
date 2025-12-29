package wallapp.util

expect class WeakReference<T>(value: T) {

    fun get(): T?

    fun clear()
}