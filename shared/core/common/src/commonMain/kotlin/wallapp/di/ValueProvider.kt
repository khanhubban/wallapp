package wallapp.di

interface ValueProvider<T> {
    fun get(): T
}