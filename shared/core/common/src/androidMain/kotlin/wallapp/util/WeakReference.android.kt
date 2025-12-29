package wallapp.util

import java.lang.ref.WeakReference as JvmWeakReference

actual class WeakReference<T> actual constructor(value: T) {

    private val jvmWeakReference = JvmWeakReference(value)

    actual fun get(): T? = jvmWeakReference.get()

    actual fun clear() = jvmWeakReference.clear()
}