package wallapp.util

actual class WeakReference<T> actual constructor(value: T) {

    private var value: T? = value

    actual fun get(): T? = value

    actual fun clear() {
        value = null
    }
}

